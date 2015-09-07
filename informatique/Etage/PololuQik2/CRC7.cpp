/**
 *    @file        CRC7.cpp
 *    @author        Edward Wilson (edwilson1989@gmail.com)
 *  @date         Dec 16, 2010
 *  @brief        The CRC7 checksum code.
 *
 *
 */

#include "CRC7.h"

CRC7::CRC7()
{
    GenerateCRCTable();

}

unsigned char CRC7::GetCRC(unsigned char val)
{
    int j;
    for (j = 0; j < 8; j++) {
        if (val & 1)
            val ^= CRC7_POLY;
        val >>= 1;
    }
    return val;
}

unsigned char CRC7::CRC(unsigned char message[], unsigned int length)
{
    unsigned char i, crc = 0;

    for (i = 0; i < length; i++)
        crc = CRCTable[crc ^ message[i]];
    return crc;
}

void CRC7::GenerateCRCTable()
{
    int i;
    // generate a table value for all 256 possible byte values
    for (i = 0; i < 256; i++) {
        CRCTable[i] = GetCRC(i);
    }
}

