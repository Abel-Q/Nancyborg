/**
 *    @file     PololuQik2.cpp
 *    @author   Edward Wilson (edwilson1989@gmail.com)
 *    @date     Aug 6, 2010
 *    @brief    PololuQik2 motor drivers
 */

/*************************************************************
 PololuQik2 - basic class to control Pololu's Qik2s9v1
 motor controller (http://www.pololu.com/catalog/product/1110)

 This uses the default settings for the motor controller and the
 Compact Protocol to communicate to it.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 Author:  Edward Wilson (edwilson1989@gmail.com)
 History:
 Version: 1.0    August 6, 2010      Initial code
          1.1    December 28, 2010   Composition constructor added
          1.3    April 22, 2011      Ported for MBED
************************************************************/

#include "PololuQik2.h"

PololuQik2::PololuQik2(PinName TxPin, PinName RxPin, PinName RSTPin, PinName errPin, void (*ErrorFunction)(void), bool enCRC):CRC(), resetPin(RSTPin), errorPin(errPin), serialConnection(TxPin,RxPin) {
    serialConnection.baud(38400);
    enableCRC = enCRC;
    errorPin.rise(ErrorFunction);
}

void PololuQik2::begin() {
    serialConnection.baud(38400);
    wait_ms(100); // Allow Serial baud rate to propagate
    resetPin = 0;
    wait_ms(100);
    resetPin = 1;
    wait_ms(10);
    unsigned char message[] = { INITIALPACKET };
    sendMessage(message, 1);
    wait_ms(100);
}

void PololuQik2::setMotor0Speed(int8_t speed) {
    if (speed < 0) {
        unsigned char message[] = {MOTOR0REVERSEPACKET, (-1*speed)};
        sendMessage(message , 2);
    } else if (speed > 0) {
        unsigned char message[] = {MOTOR0FORWARDPACKET, speed};
        sendMessage(message , 2);
    } else {
        unsigned char message[] = {MOTOR0REVERSEPACKET, 0};
        sendMessage(message , 2);
    }
}

void PololuQik2::setMotor1Speed(int8_t speed) {
    if (speed < 0) {
        unsigned char message[] = {MOTOR1REVERSEPACKET, (-1*speed)};
        sendMessage(message , 2);
    } else if (speed > 0) {
        unsigned char message[] = {MOTOR1FORWARDPACKET, speed};
        sendMessage(message , 2);
    } else {
        unsigned char message[] = {MOTOR1REVERSEPACKET, 0};
        sendMessage(message , 2);
    }
}

void PololuQik2::stopBothMotors() {
    setMotor0Speed(0);
    setMotor1Speed(0);
}

void PololuQik2::sendMessage(unsigned char message[], uint8_t length) {
    if (enableCRC) {
        uint8_t chk = CRC.CRC(message, length);
        for (int8_t i = 0; i < length; i++)
            sendByte(message[i]);

        sendByte(chk);
    } else {
        for (int8_t i = 0; i < length; i++)
            sendByte(message[i]);
    }
}

uint8_t PololuQik2::readByte() {
    while (!serialConnection.readable());
    return serialConnection.getc();
}

void PololuQik2::sendByte(uint8_t byte) {
    serialConnection.putc(byte);
}

uint8_t PololuQik2::getFirmwareVersion() {
    unsigned char message[] = { FWVERSIONPACKET };
    sendMessage(message, 1);
    return readByte();
}

bool PololuQik2::hasCRCError() {
    return errorBitSet(CRCERRORBIT);
}

bool PololuQik2::errorBitSet(uint8_t bitToCheck) {
    return (1 << bitToCheck) & errByte;
}

bool PololuQik2::hasFrameError() {
    return errorBitSet(FRAMEERRORBIT);
}

bool PololuQik2::hasDataOverrunError() {
    return errorBitSet(DATAOVERRUNERRORBIT);
}

bool PololuQik2::hasFormatError() {
    return errorBitSet(FORMATERRORBIT);
}

bool PololuQik2::hasTimeoutError() {
    return errorBitSet(TIMEOUTERRORBIT);
}

uint8_t PololuQik2::getError() {
    unsigned char message[] = { ERRORPACKET };
    sendMessage(message, 1);
    return readByte();
}
