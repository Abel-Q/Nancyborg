#ifndef _PSXCONTROLLER_H
#define _PSXCONTROLLER_H

#include "mbed.h"
#include "../../config.h"

class PSXController {
public:
    enum Key {
        KEY_L2 = 0,
        KEY_R2,
        KEY_L1,
        KEY_R1,
        KEY_TRIANGLE,
        KEY_CIRCLE,
        KEY_CROSS,
        KEY_SQUARE,

        KEY_SELECT,
        KEY_L3,
        KEY_R3,
        KEY_START,
        KEY_UP,
        KEY_RIGHT,
        KEY_DOWN,
        KEY_LEFT,

        KEY_COUNT
    };

    PSXController(PinName pin_dat, PinName pin_cmd, PinName pin_att, PinName pin_clk);
    void init();
    void run();
    unsigned int getKeys();
    bool isKeyPressed(Key key);
    const char *getKeyString(Key key);
    double getLeftX();
    double getLeftY();
    double getRightX();
    double getRightY();
    void setAnalogMode(bool analog, bool locked);
    bool isAnalogMode();
    bool hasKeyChanged(Key key);

private:
    void transmitPacket(unsigned char* toWrite, unsigned char *toRead, int length);
    void transmitByte(unsigned char byteToWrite, unsigned char* byteToRead);
    double translateAnalog(int val);
    DigitalIn _pin_dat;
    DigitalOut _pin_cmd, _pin_att, _pin_clk;
    unsigned int _keys_state;
    unsigned int _left_joy;
    unsigned int _right_joy;
    unsigned char _readBuffer[20];

    bool key_states[16];

    static const char* KEY_STRINGS[16];
};

#endif // _PSXCONTROLLER_H
