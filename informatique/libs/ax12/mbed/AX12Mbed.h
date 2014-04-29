/**
 * \file    AX12Mbed.h
 * \author  Mickaël Thomas
 * \version 1.0
 * \date    02/04/2014
 * \brief   MBED implementation of AX12Base
 */
#ifndef AX12_MBED_H
#define AX12_MBED_H

#include "mbed.h"
#include "AX12Base.h"

#ifndef AX12
#define AX12 AX12Mbed
#endif

/** \brief MBED implementation if AX12Base
 */
class AX12Mbed : public AX12Base {
public:
    AX12Mbed(PinName tx, PinName rx, int id, int baud = 1000000)
        : AX12Base(id, baud), ax12(tx, rx)
    {
        ax12.baud(baud);
    }

    void setCurrentBaud(int new_baud) {
        AX12Base::setCurrentBaud(new_baud);

        ax12.baud(new_baud);
    }

private:
    int readBytes(uint8_t *bytes, int n, int timeout) {
        Timer timer;

        timer.start();

        for (int i = 0; i < n; i++) {
            if (timeout >= 0) {
                while (!ax12.readable() && timer.read_ms() <= timeout)
                    ;

                if (!ax12.readable()) {
                    printf("got timeout (%d >= %d)\n", timer.read_ms(),  timeout);
                    setCommError(AX12_COMM_ERROR_TIMEOUT);
                    return -1;
                }
            }
            bytes[i] = ax12.getc();
        }

        return n;
    }

    int writeBytes(const uint8_t *bytes, int n) {
        for (int i = 0; i < n; i++)
            ax12.putc(bytes[i]);

        return n;
    }

    void flushInput() {
        while (ax12.readable())
            ax12.getc();
    }

    Serial ax12;
};

#endif