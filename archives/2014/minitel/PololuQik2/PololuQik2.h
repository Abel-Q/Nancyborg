/**
 *    @file         PololuQik2.h
 *    @author       Edward Wilson (edwilson1989@gmail.com)
 *    @author       Eric Fialkowski (eric.fialkowski@gmail.com)
 *    @date         Aug 6, 2010
 *    @brief        PololuQik2 motor drivers.
 *
 *  @details This class is a derivative work based on work of Mr Fialkowski.
 *
 *  PololuQik2 - basic class to control Pololu's Qik2s9v1
 *    motor controller (http://www.pololu.com/catalog/product/1110)
 *
 * This uses the default settings for the motor controller and the
 * Compact Protocol to communicate to it.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  @version 1.0    August 6, 2010      Initial code
 *  @version 1.2    December 28, 2010   Composition constructor added
 *  @version 1.3    April 22, 2011      Ported for MBED
 */

#ifndef POLOLUQIK2_H_
#define POLOLUQIK2_H_

#include "CRC7.h"
#include "mbed.h"
#include <inttypes.h>

#define INITIALPACKET 0xAA /**< The packet used to initialise the motor controller*/
#define MOTOR0FORWARDPACKET 0x88 /**< The packet used to set motor 0 forward */
#define MOTOR1FORWARDPACKET 0x8C /**< The packet used to set motor 1 forward */
#define MOTOR0REVERSEPACKET 0x8A /**< The packet used to set motor 0 in reverse */
#define MOTOR1REVERSEPACKET 0x8E /**< The packet used to set motor 1 in reverse */
#define MOTOR0COASTPACKET 0x86 /**< The packet used to all motor 0 to coast */
#define MOTOR1COASTPACKET 0x87 /**< The packet used to all motor 1 to coast */
#define FWVERSIONPACKET 0x81 /**< The packet to query the firmware version of the motor controller */
#define ERRORPACKET 0x82 /**< The packet to request the error state from the motor controller */

#define DATAOVERRUNERRORBIT 3 /**< The bit which signifies a data over run error */
#define FRAMEERRORBIT 4 /**< The bit which signifies a frame error */
#define CRCERRORBIT 5 /**< The bit which signifies CRC error */
#define FORMATERRORBIT 6 /**< The bit which signifies a format error */
#define TIMEOUTERRORBIT 7 /**< The bit which signifies time out error */

/**
 * This is the driver for the motor controller. This is to control a Qik2s9v1.
 * It will not control multiple motor and it does not support daisy chaining motor controllers.
 *
 * The specification for the motor controller can be found http://www.pololu.com/catalog/product/1110/
 *
 * This motor controller is based on the work of Mr Fialkowski.
 * This motor controller requires the newSoftSerial library be installed on the build path.
 * This motor controller also requires the CRC7 class.
 *
 * @see CRC7
 */
class PololuQik2 {
public:

    /**
     * The parameterised constructor for the pololuQik2 motor controller.
     *
     * @attention It is important to call the begin() method as this initialises the controller.
     * @see begin()
     *
     * @param txPin the pin on the controller to use for transmitting serial bytes.
     * @param rxPin the pin on the controller to use for recieving serial bytes.
     * @param errPin the digital output pin to use to reset the motor controller.
     * @param EnableCRC true if CRC should be used when communicating with the motor controller.
     * @param reset the pin on the controller to use to reset the motor controller.
     */
    PololuQik2(PinName TxPin, PinName RxPin, PinName RSTPin, PinName errPin, void (*errorFunction)(void), bool enCRC);

    /**
     * This method initialises and begins communication with the motor controller.
     * This method is also called when an error is exhibited.
     *
     * @see PololuQik2()
     */
    void begin();

    /**
     * sets the speed of motor 0. This command has superseded motor0Forwards and motor0Reverse.
     * If the speed is less than 0 then the motor will reverse.
     * If the speed is greater than 0 then the motor will run forward
     * If the speed is 0 then the motor will be stopped.
     *
     * The bound for the speed are -127 to 127.
     * @since 1.2
     * @param speed the speed of the motor. If this value is negative the motor will reverse to the
     * appropriate speed.
     */
    void setMotor0Speed(int8_t speed);

    /**
     * sets the speed of motor 1. This command has superseded motor1Forwards and motor1Reverse.
     * If the speed is less than 0 then the motor will reverse.
     * If the speed is greater than 0 then the motor will run forward
     * If the speed is 0 then the motor will be stopped.
     *
     * The bound for the speed are -127 to 127.
     * @since 1.2
     * @param speed the speed of the motor. If this value is negative the motor will reverse to the
     * appropriate speed.
     */
    void setMotor1Speed(int8_t speed);

    /**
     * instructs the motor controller to halt both motors.
     * This is equivalent to called any of the forward or reverse methods
     * with the speed as zero (0).
     */
    void stopBothMotors();

    /**
     * retrieves the firmware version from the motor controller. This will return one of two values.
     * Either the ASCII representation of 1 or the ASCII representation of 2.
     *
     * The return from this method will be either 49 or 50. If otherwise there is an error.
     *
     * @return the firmware version of the motor controller.
     */
    uint8_t getFirmwareVersion();

    /**
     * Checks if the motor controller has a Data over run error
     *
     * returns true if it has a data over run error
     */
    bool hasDataOverrunError();

    /**
     * Checks if the motor controller has a Frame error
     *
     * returns true if it has a Frame error
     */
    bool hasFrameError();

    /**
     * Checks if the motor controller has a CRC error
     *
     * returns true if it has a CRC error
     */
    bool hasCRCError();

    /**
     * Checks if the motor controller has a Format error
     *
     * returns true if it has a Format error
     */
    bool hasFormatError();

    /**
     * Checks if the motor controller has a time out error
     *
     * returns true if it has a time out error
     */
    bool hasTimeoutError();

private:

    void errorCall();
    
    /**
     * This actually sends the data to the motor controller.
     *
     * @param message[] the message to be sent
     * @param length the length of the message to be sent
     */
    void sendMessage(unsigned char message[], uint8_t length);

    /**
     * Checks if a specific bit in the error byte is set.
     * @param bitToCheck the bit number to check. Assume that the least significant bit is bit 1.
     * @return true if the bit is set.
     */
    bool errorBitSet(uint8_t bitToCheck);

    /**
     * retrieves the error value from the motor controller and stores it in the
     * class internal workings.
     *
     * @see errorBitSet()
     * @see error()
     */
    uint8_t getError();
    
    /**
     * sends a single byte to the motor controller
     *
     * @param byte the byte of data to send
     */
    void sendByte(uint8_t byte);

    /**
     * Reads a byte from the motor controller. This is a blocking call.
     * If the motor controller is not sending anything then the program will
     * halt indefinitely.
     *
     * @return the byte from the motor controller.
     * @bug There is a suspected bug in this method.
     */
    uint8_t readByte();

    Serial serialConnection; /**< Serial connection to the motor controller */
    DigitalOut resetPin; /**< The digital output on the controller which is connected to the motor controllers reset pin. */
    InterruptIn errorPin; /**< The digital output on the controller which is connected to the motor controllers error pin. This must be an interrrupt pin on the controller */
    uint8_t errByte; /**< A temporary store for the error code received from the motor controller. */
    uint8_t firmwareVersion; /**< The motor controller firmware version */
    bool enableCRC; /**< this value will be true if CRCs are expected by the motor controller. */
    CRC7 CRC; /**< The CRC object used to generate the CRC checksums */

};

#endif /* POLOLUQIK2_H_ */
