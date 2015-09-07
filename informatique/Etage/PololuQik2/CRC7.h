/**
 *	@file		CRC7.h
 *	@author		Edward Wilson (edwilson1989@gmail.com)
 *  @date 		Dec 16, 2010
 *  @brief		Cyclic Redundancy Check Class. This class computes a CRC7 of the provided data.
 *
 *
 */

#ifndef CRC7_H_
#define CRC7_H_

#define CRC7_POLY 0x91 /**< The Chosen Polynomial for CRC7. This is a standard value */


/**
 * This is a CRC7 implementation for checking validity of messages. It will compute
 * a checksum which can be used to determine if a message has been recieved unaltered.
 * This is used particularly over wireless tranmission or noisy serial lines.
 *
 * This implements a stored CRC table. This will taken up 256 bytes of RAM when the object
 * is instantiated. The reason for using a stored CRC table is speed. This is a compromise
 * between speed and storage spaces.
 *
 * This could possibly be static object.
 */
class CRC7
{
public:
    /**
     * Default constructor. This method sets up the CRC table with pre-hashed values.
     */
    CRC7();
    /**
     * this method will generate the CRC7 checksum for the supplied message. It will be returned
     * from this method.
     *
     * @param message[] the message to be hashed.
     * @param length the length of the message to be hashed in characters.
     * @return the checksum computed.
     */
    unsigned char CRC(unsigned char message[], unsigned int length);

private:
    /**
     * Used to retrieve the value from the CRCTable.
     *
     * @see GenerateCRCTable()
     * @param val the character which the hash is required for.
     * @return the precomputed hash.
     */
    unsigned char GetCRC(unsigned char val);
    /**
     * Generates the CRC table which is used for lookups during operation.
     * This method is faster than having to compute the hash code each time
     * however it comes at a cost of using up valuable RAM.
     */
    void GenerateCRCTable();

    /**
     * The array of characters which stores the precomputed hash codes.
     */
    unsigned char CRCTable[256];
};

#endif /* CRC7_H_ */
