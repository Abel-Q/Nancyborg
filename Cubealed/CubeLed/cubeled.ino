/*
 * Commande d'un cube à LED
 */

#include "Tlc5940.h"
#include "animation_stream.h"
#include "commands.h"

/* Tableau de correspondance x, y -> Canal TLC :
   tlc_order[y][x] = Canal TLC
 */
const PROGMEM byte tlc_order[][4] = {
  { 15, 14, 13, 11 }, /* y = 0 */
  { 12, 10,  9,  8 }, /* y = 1 */
  {  4,  5,  6,  7 }, /* y = 2 */
  {  0,  1,  2,  3 }, /* y = 3 */
};

/* Tableau de correspondance z -> pin transistor
   transistor_order[z] = pin transistor
 */
const PROGMEM byte transistor_order[] = {
  5, /* z = 0 */
  7, /* z = 1 */
  4, /* z = 2 */
  6, /* z = 3 */
};

/* Valeur des LEDS (0 - 255)
   values[z][y][x] = val
 */
byte values[4][4][4];

#define TEST_ANIMATION "\x43\x4c\x00\xff\x53\x0b\x4c\x01\xff\x53\x0b\x4c\x02\xff\x53\x0b\x4c\x03\xff\x53\x0b\x4c\x04\xff\x53\x0b\x4c\x05\xff\x53\x0b\x4c\x06\xff\x53\x0b\x4c\x07\xff\x53\x0b\x4c\x08\xff\x53\x0b\x4c\x09\xff\x53\x0b\x4c\x0a\xff\x53\x0b\x4c\x0b\xff\x53\x0b\x4c\x0c\xff\x53\x0b\x4c\x0d\xff\x53\x0b\x4c\x0e\xff\x53\x0b\x4c\x0f\xff\x53\x0b\x4c\x10\xff\x53\x0b\x4c\x11\xff\x53\x0b\x4c\x12\xff\x53\x0b\x4c\x13\xff\x53\x0b\x4c\x14\xff\x53\x0b\x4c\x15\xff\x53\x0b\x4c\x16\xff\x53\x0b\x4c\x17\xff\x53\x0b\x4c\x18\xff\x53\x0b\x4c\x19\xff\x53\x0b\x4c\x1a\xff\x53\x0b\x4c\x1b\xff\x53\x0b\x4c\x1c\xff\x53\x0b\x4c\x1d\xff\x53\x0b\x4c\x1e\xff\x53\x0b\x4c\x1f\xff\x53\x0b\x4c\x20\xff\x53\x0b\x4c\x21\xff\x53\x0b\x4c\x22\xff\x53\x0b\x4c\x23\xff\x53\x0b\x4c\x24\xff\x53\x0b\x4c\x25\xff\x53\x0b\x4c\x26\xff\x53\x0b\x4c\x27\xff\x53\x0b\x4c\x28\xff\x53\x0b\x4c\x29\xff\x53\x0b\x4c\x2a\xff\x53\x0b\x4c\x2b\xff\x53\x0b\x4c\x2c\xff\x53\x0b\x4c\x2d\xff\x53\x0b\x4c\x2e\xff\x53\x0b\x4c\x2f\xff\x53\x0b\x4c\x30\xff\x53\x0b\x4c\x31\xff\x53\x0b\x4c\x32\xff\x53\x0b\x4c\x33\xff\x53\x0b\x4c\x34\xff\x53\x0b\x4c\x35\xff\x53\x0b\x4c\x36\xff\x53\x0b\x4c\x37\xff\x53\x0b\x4c\x38\xff\x53\x0b\x4c\x39\xff\x53\x0b\x4c\x3a\xff\x53\x0b\x4c\x3b\xff\x53\x0b\x4c\x3c\xff\x53\x0b\x4c\x3d\xff\x53\x0b\x4c\x3e\xff\x53\x0b\x4c\x3f\xff\x53\x0b\x43\x4c\x3f\xff\x53\x0b\x4c\x3e\xff\x53\x0b\x4c\x3d\xff\x53\x0b\x4c\x3c\xff\x53\x0b\x4c\x3b\xff\x53\x0b\x4c\x3a\xff\x53\x0b\x4c\x39\xff\x53\x0b\x4c\x38\xff\x53\x0b\x4c\x37\xff\x53\x0b\x4c\x36\xff\x53\x0b\x4c\x35\xff\x53\x0b\x4c\x34\xff\x53\x0b\x4c\x33\xff\x53\x0b\x4c\x32\xff\x53\x0b\x4c\x31\xff\x53\x0b\x4c\x30\xff\x53\x0b\x4c\x2f\xff\x53\x0b\x4c\x2e\xff\x53\x0b\x4c\x2d\xff\x53\x0b\x4c\x2c\xff\x53\x0b\x4c\x2b\xff\x53\x0b\x4c\x2a\xff\x53\x0b\x4c\x29\xff\x53\x0b\x4c\x28\xff\x53\x0b\x4c\x27\xff\x53\x0b\x4c\x26\xff\x53\x0b\x4c\x25\xff\x53\x0b\x4c\x24\xff\x53\x0b\x4c\x23\xff\x53\x0b\x4c\x22\xff\x53\x0b\x4c\x21\xff\x53\x0b\x4c\x20\xff\x53\x0b\x4c\x1f\xff\x53\x0b\x4c\x1e\xff\x53\x0b\x4c\x1d\xff\x53\x0b\x4c\x1c\xff\x53\x0b\x4c\x1b\xff\x53\x0b\x4c\x1a\xff\x53\x0b\x4c\x19\xff\x53\x0b\x4c\x18\xff\x53\x0b\x4c\x17\xff\x53\x0b\x4c\x16\xff\x53\x0b\x4c\x15\xff\x53\x0b\x4c\x14\xff\x53\x0b\x4c\x13\xff\x53\x0b\x4c\x12\xff\x53\x0b\x4c\x11\xff\x53\x0b\x4c\x10\xff\x53\x0b\x4c\x0f\xff\x53\x0b\x4c\x0e\xff\x53\x0b\x4c\x0d\xff\x53\x0b\x4c\x0c\xff\x53\x0b\x4c\x0b\xff\x53\x0b\x4c\x0a\xff\x53\x0b\x4c\x09\xff\x53\x0b\x4c\x08\xff\x53\x0b\x4c\x07\xff\x53\x0b\x4c\x06\xff\x53\x0b\x4c\x05\xff\x53\x0b\x4c\x04\xff\x53\x0b\x4c\x03\xff\x53\x0b\x4c\x02\xff\x53\x0b\x4c\x01\xff\x53\x0b\x4c\x00\xff\x53\x0b"
//#define TEST_ANIMATION "C"

byte animation[1500] = TEST_ANIMATION;
int animation_size = sizeof(TEST_ANIMATION);
AnimationStream anim(animation_size, animation);

void setup()
{
  Serial.begin(115200);

  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);

  digitalWrite(4, HIGH);
  digitalWrite(5, HIGH);
  digitalWrite(6, HIGH);
  digitalWrite(7, HIGH);

  Tlc.init();
/*
  Serial.print("free ram = ");
  Serial.println(availableMemory());
*/
}

void setLed(byte x, byte y, byte z, byte val)
{
  values[z][y][x] = val;
}

byte getLed(byte x, byte y, byte z)
{
  return values[z][y][x];
}

/* Parcourt les 4 étages */
void refresh()
{
  for (byte z = 0; z < 4; z++) {
    byte tr = pgm_read_byte(&transistor_order[z]);

    for (byte y = 0; y < 4; y++) {
      for (byte x = 0; x < 4; x++) {
        byte val = getLed(x, y, z);
        byte order = pgm_read_byte(&tlc_order[y][x]);
        Tlc.set(order, map(val, 0, 255, 0, 4095));
      }
    }

    Tlc.update();
    delay(1);
    digitalWrite(tr, LOW);
    delay(2);
    digitalWrite(tr, HIGH);
  }
}

void discard() {
  while (Serial.available()) {
    char poked = Serial.peek();

    if (poked >= 'A' && poked <= 'Z')
      break;

    Serial.read();
  }
}

void loop()
{
  static Commands c1(Serial);
  static Commands c2(anim);

  refresh();
  discard();
  c1.update();
  c2.update();
}

// this function will return the number of bytes currently free in RAM
// written by David A. Mellis
// based on code by Rob Faludi http://www.faludi.com
int availableMemory() {
  int size = 4096; // Use 2048 with ATmega328
  byte *buf;

  while ((buf = (byte *) malloc(--size)) == NULL)
    ;

  free(buf);

  return size;
}
