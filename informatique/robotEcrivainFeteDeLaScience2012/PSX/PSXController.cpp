#include "PSXController.h"

const char* PSXController::KEY_STRINGS[16] = {
    "L2",
    "R2",
    "L1",
    "R1",
    "/_\\",
    "O",
    "X",
    "[]",

    "Select",
    "L3",
    "R3",
    "Start",
    "Up",
    "Right",
    "Down",
    "Left",
};

PSXController::PSXController(PinName pin_dat, PinName pin_cmd, PinName pin_att, PinName pin_clk)
    : _pin_dat(DigitalIn(pin_dat, "DAT")),
      _pin_cmd(DigitalOut(pin_cmd, "CMD")),
      _pin_att(DigitalOut(pin_att, "ATT")),
      _pin_clk(DigitalOut(pin_clk, "CLK"))
{
    _pin_dat.mode(PullUp);
    memset(key_states, 0, 16);
}

void PSXController::init()
{
    // Configure vibration motors (FIXME: not working)
    //transmitPacket((unsigned char*) "\x01\x4D\x00\x00\x01\xFF\xFF\xFF\xFF", _readBuffer, 9);

}

void PSXController::run()
{
    transmitPacket((unsigned char*) "\x01\x42\x00\xFF\xFF\x00\x00\x00\x00", _readBuffer, 9);

    _keys_state = ~(_readBuffer[3] << 8 | _readBuffer[4]);

    _right_joy = (_readBuffer[5] << 8 | _readBuffer[6]);
    _left_joy = (_readBuffer[7] << 8 | _readBuffer[8]);

    printf("LEFT : (%x %x)    RIGHT : (%x, %x)\r\n ", _readBuffer[7], _readBuffer[8], _readBuffer[5], _readBuffer[6]);
}

bool PSXController::isKeyPressed(Key key)
{
    return _keys_state & (1 << key);
}

double PSXController::translateAnalog(int val)
{
    val = val & 0xFF;
    val -= 0x80; // décalage du 0

    int absval = abs(val);
    int sig = val < 0 ? -1 : 1;

    return sig * (absval - 8.) / (128. - 8.);
}

double PSXController::getLeftX()
{
    return translateAnalog(_left_joy >> 8);
}

double PSXController::getLeftY()
{
    return -translateAnalog(_left_joy);
}

double PSXController::getRightX()
{
    return translateAnalog(_right_joy >> 8);
}

double PSXController::getRightY()
{
    return -translateAnalog(_right_joy);
}

const char *PSXController::getKeyString(Key key)
{
    return KEY_STRINGS[(int) key];
}

void PSXController::transmitPacket(unsigned char* toWrite, unsigned char *toRead, int length)
{
    _pin_att = 0;

    for (int i = 0; i < length; i++)
    {
        transmitByte(*(toWrite++), toRead++);
        wait_us(50);
    }

    _pin_att = 1;
}

void PSXController::transmitByte(unsigned char byteToWrite, unsigned char* byteToRead)
{
    *byteToRead = 0;

    for (int i = 0; i < 8; i++)
    {
        _pin_cmd = (byteToWrite >> i) & 1;

        wait_us(1);

        _pin_clk = 0;
        wait_us(1);
        _pin_clk = 1;
        wait_us(5);

        *byteToRead |= _pin_dat << i;
    }

    wait_us(50);
}

bool PSXController::isAnalogMode()
{
    return (_readBuffer[1] & 0xF0) == 0x70;
}

bool PSXController::hasKeyChanged(Key key)
{
    bool pressed = isKeyPressed(key);

    if (pressed != key_states[(int) key])
    {
        key_states[(int) key] = pressed;
        return true;
    }

    return false;
}

void PSXController::setAnalogMode(bool analog, bool locked)
{
    unsigned char buffer[] = "\x01\x44\x00\x00\x00";

    if (analog)
        buffer[3] = 1;
    if (locked)
        buffer[4] = 3;
    
    // Enter config (escape) mode
    transmitPacket((unsigned char*) "\x01\x43\x00\x01\x00", _readBuffer, 5);

    // Set analog mode
    transmitPacket((unsigned char*) buffer, _readBuffer, 5);

    // Exit config mode
    transmitPacket((unsigned char*) "\x01\x43\x00\x00\x00", _readBuffer, 5);
}
