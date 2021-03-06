/**
 * Gamecube controller reader
 * Get data from a Gamecube controller and send it raw to serial
 * Original project : https://github.com/brownan/Gamecube-N64-Controller
 */
 
//#include <Wire.h>
#include <Arduino.h>
#include "pins_arduino.h"
#include <SoftwareSerial.h>
#include "PololuQik.h"

PololuQik2s9v1 qik(7, 8, 6);

unsigned char left = 0;

unsigned char right =0;;

int sensorLeftPin = A0;
int sensorRightPin = A1;

#define GC_PIN 2
#define GC_PIN_DIR DDRD
#define GC_HIGH DDRD &= ~0x04
#define GC_LOW DDRD |= 0x04
#define GC_QUERY (PIND & 0x04)
 
char gc_raw_dump[65]; // 1 received bit per byte
 
// 8 bytes of data that we get from the controller
struct {
    // bits: 0, 0, 0, start, y, x, b, a
    unsigned char data1;
    // bits: 1, L, R, Z, Dup, Ddown, Dright, Dleft
    unsigned char data2;
    unsigned char stick_x;
    unsigned char stick_y;
    unsigned char cstick_x;
    unsigned char cstick_y;
    unsigned char left;
    unsigned char right;
} gc_status;
 
// Zero points for the GC controller stick
unsigned char zero_x;
unsigned char zero_y;
 
void serial_handler()
{
  Serial.write((uint8_t*)&gc_status, sizeof(gc_status));
}
 
void setup()
{
  Serial.begin(115200);
    qik.init(9600);  
  /*Wire.begin(0x42);
  Wire.onRequest(&i2c_handler);*/
  
  digitalWrite(GC_PIN, LOW);
  pinMode(GC_PIN, INPUT);
  
  // Initialize the gamecube controller by sending it a null byte.
  // This is unnecessary for a standard controller, but is required for the
  // Wavebird.
  unsigned char initialize = 0x00;
  delay(150);
  noInterrupts();
  // Le temps que le contrôleur s'initialise
  gc_send(&initialize, 1);
  // Stupid routine to wait for the gamecube controller to stop
  // sending its response. We don't care what it is, but we
  // can't start asking for status if it's still responding
  int x;
  for (x=0; x<64; x++) {
      // make sure the line is idle for 64 iterations, should
      // be plenty.
      if (!GC_QUERY)
          x = 0;
  }
 
  // Query for the gamecube controller's status. We do this
  // to get the 0 point for the control stick.
  unsigned char command[] = {0x40, 0x03, 0x00};
  gc_send(command, 3);
  gc_get();
  interrupts();
  translate_raw_data();
  zero_x = gc_status.stick_x;
  zero_y = gc_status.stick_y;
}
 
/**
 * This sends the given byte sequence to the controller
 * length must be at least 1
 * Oh, it destroys the buffer passed in as it writes it
 */
void gc_send(unsigned char *buffer, char length)
{
    // Send these bytes
    char bits;
    
    bool bit;
 
    // This routine is very carefully timed by examining the assembly output.
    // Do not change any statements, it could throw the timings off
    //
    // We get 16 cycles per microsecond, which should be plenty, but we need to
    // be conservative. Most assembly ops take 1 cycle, but a few take 2
    //
    // I use manually constructed for-loops out of gotos so I have more control
    // over the outputted assembly. I can insert nops where it was impossible
    // with a for loop
    
    asm volatile (";Starting outer for loop");
outer_loop:
    {
        asm volatile (";Starting inner for loop");
        bits=8;
inner_loop:
        {
            // Starting a bit, set the line low
            asm volatile (";Setting line to low");
            GC_LOW; // 1 op, 2 cycles
 
            asm volatile (";branching");
            if (*buffer >> 7) {
                asm volatile (";Bit is a 1");
                // 1 bit
                // remain low for 1us, then go high for 3us
                // nop block 1
                asm volatile ("nop\nnop\nnop\nnop\nnop\n");
                
                asm volatile (";Setting line to high");
                GC_HIGH;
 
                // nop block 2
                // we'll wait only 2us to sync up with both conditions
                // at the bottom of the if statement
                asm volatile ("nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              );
 
            } else {
                asm volatile (";Bit is a 0");
                // 0 bit
                // remain low for 3us, then go high for 1us
                // nop block 3
                asm volatile ("nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\nnop\n"  
                              "nop\n");
 
                asm volatile (";Setting line to high");
                GC_HIGH;
 
                // wait for 1us
                asm volatile ("; end of conditional branch, need to wait 1us more before next bit");
                
            }
            // end of the if, the line is high and needs to remain
            // high for exactly 16 more cycles, regardless of the previous
            // branch path
 
            asm volatile (";finishing inner loop body");
            --bits;
            if (bits != 0) {
                // nop block 4
                // this block is why a for loop was impossible
                asm volatile ("nop\nnop\nnop\nnop\nnop\n"  
                              "nop\nnop\nnop\nnop\n");
                // rotate bits
                asm volatile (";rotating out bits");
                *buffer <<= 1;
 
                goto inner_loop;
            } // fall out of inner loop
        }
        asm volatile (";continuing outer loop");
        // In this case: the inner loop exits and the outer loop iterates,
        // there are /exactly/ 16 cycles taken up by the necessary operations.
        // So no nops are needed here (that was lucky!)
        --length;
        if (length != 0) {
            ++buffer;
            goto outer_loop;
        } // fall out of outer loop
    }
 
    // send a single stop (1) bit
    // nop block 5
    asm volatile ("nop\nnop\nnop\nnop\n");
    GC_LOW;
    // wait 1 us, 16 cycles, then raise the line 
    // 16-2=14
    // nop block 6
    asm volatile ("nop\nnop\nnop\nnop\nnop\n"
                  "nop\nnop\nnop\nnop\nnop\n"  
                  "nop\nnop\nnop\nnop\n");
    GC_HIGH;
 
}
 
void translate_raw_data()
{
    // The get_gc_status function sloppily dumps its data 1 bit per byte
    // into the get_status_extended char array. It's our job to go through
    // that and put each piece neatly into the struct gc_status
    int i;
    memset(&gc_status, 0, sizeof(gc_status));
    // line 1
    // bits: 0, 0, 0, start, y, x, b a
    for (i=0; i<8; i++) {
        gc_status.data1 |= gc_raw_dump[i] ? (0x80 >> i) : 0;
    }
    // line 2
    // bits: 1, l, r, z, dup, ddown, dright, dleft
    for (i=0; i<8; i++) {
        gc_status.data2 |= gc_raw_dump[8+i] ? (0x80 >> i) : 0;
    }
    // line 3
    // bits: joystick x value
    // These are 8 bit values centered at 0x80 (128)
    for (i=0; i<8; i++) {
        gc_status.stick_x |= gc_raw_dump[16+i] ? (0x80 >> i) : 0;
    }
    for (i=0; i<8; i++) {
        gc_status.stick_y |= gc_raw_dump[24+i] ? (0x80 >> i) : 0;
    }
    for (i=0; i<8; i++) {
        gc_status.cstick_x |= gc_raw_dump[32+i] ? (0x80 >> i) : 0;
    }
    for (i=0; i<8; i++) {
        gc_status.cstick_y |= gc_raw_dump[40+i] ? (0x80 >> i) : 0;
    }
    for (i=0; i<8; i++) {
        gc_status.left |= gc_raw_dump[48+i] ? (0x80 >> i) : 0;
    }
    for (i=0; i<8; i++) {
        gc_status.right |= gc_raw_dump[56+i] ? (0x80 >> i) : 0;
    }
}
 
void gc_get()
{
    // listen for the expected 8 bytes of data back from the controller and
    // blast it out to the gc_raw_dump array, one bit per byte for extra speed.
    // Afterwards, call translate_raw_data() to interpret the raw data and pack
    // it into the gc_status struct.
    asm volatile (";Starting to listen");
    unsigned char timeout;
    char bitcount = 64;
    char *bitbin = gc_raw_dump;
 
    // Again, using gotos here to make the assembly more predictable and
    // optimization easier (please don't kill me)
read_loop:
    timeout = 0x3f;
    // wait for line to go low
    while (GC_QUERY) {
        if (!--timeout)
            return;
    }
    // wait approx 2us and poll the line
    asm volatile (
                  "nop\nnop\nnop\nnop\nnop\n"  
                  "nop\nnop\nnop\nnop\nnop\n"  
                  "nop\nnop\nnop\nnop\nnop\n"  
                  "nop\nnop\nnop\nnop\nnop\n"  
                  "nop\nnop\nnop\nnop\nnop\n"  
                  "nop\nnop\nnop\nnop\nnop\n"  
            );
    *bitbin = GC_QUERY;
    ++bitbin;
    --bitcount;
    if (bitcount == 0)
        return;
 
    // wait for line to go high again
    // it may already be high, so this should just drop through
    timeout = 0x3f;
    while (!GC_QUERY) {
        if (!--timeout)
            return;
    }
    goto read_loop;
}
 
void loop()
{
    unsigned char command[] = {0x40, 0x03, 0x00};
    noInterrupts();
    gc_send(command, 3);
    gc_get();
    interrupts();
 
    translate_raw_data();
    delay(10);
    
    if (Serial.available()) {
      Serial.read();
      serial_handler();
    }
  left = (gc_status.left < 40) ? 40 : gc_status.left;
  left = (left > 255) ? 255 : left;
  left = ((left - 40) * 127)/215;
  
  right = (gc_status.right < 40) ? 40 : gc_status.right;
  right = (right > 255) ? 255 : right;
  right = ((right - 40) * 127)/215;
  
  qik.setM0Speed(-left * ((gc_status.data1 & 0x1) ? -1 : (analogRead(sensorRightPin) > 15)) );
  qik.setM1Speed(-right * ((gc_status.data1 & 0x1) ? -1 : (analogRead(sensorLeftPin) > 15)) );
}

