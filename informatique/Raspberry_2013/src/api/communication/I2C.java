package api.communication;

import java.io.Closeable;

/**
 * Interface de communication I2C
 * 
 * @author mickael
 */
public class I2C implements Closeable {
	private static final int I2C_SLAVE = 0x0703; /* Use this slave address */
	private int fd = 0;

	/**
	 * Vérifie la validité d'une adresse (après l'avoir converti en 7 bits si
	 * nécessaire)
	 * 
	 * @param addr l'adresse à convertir
	 * @param min_addr l'adresse minimale valide (sur 7 bits)
	 * @param max_addr l'adresse maximale valide (sur 7 bits)
	 * @return l'adresse sur 7 bits
	 * @throws IllegalArgumentException
	 */
	public static int fixAddress(int addr, int min_addr, int max_addr) {
		if (addr >= (min_addr << 1) && addr <= (max_addr << 1) && (addr % 2) == 0) {
			return addr >> 1;
		} else if (addr == 0 || (addr >= min_addr && addr <= max_addr)) {
			return addr;
		} else {
			throw new IllegalArgumentException("Adresse non valide");
		}
	}

	/**
	 * Établit une connexion I2C à l'adresse donnée
	 * 
	 * @param dev numéro de l'adaptateur I2C (0, 1, ...)
	 * @param devAddr adresse du périphérique sur <strong>7 bits</strong> : le
	 *            bit de poids faible indiquant si il s'agit d'une lecture ou
	 *            d'une écriture ne doit pas être inclus (souvent les
	 *            documentations donnent une adresse sur 8 bits !)
	 */
	public void open(int dev, int devAddr) {
		fd = LibC.open("/dev/i2c-" + dev, LibC.O_RDWR);
		LibC.ioctl(fd, I2C_SLAVE, devAddr);
	}

	/**
	 * Lit un octet
	 * 
	 * @return octet lu
	 */
	public synchronized int readByte() {
		byte[] buff = new byte[1];

		LibC.read(fd, buff, 1);

		return buff[0] & 0xff;
	}

	/**
	 * Écrit un ou plusieurs octets
	 * 
	 * @param bytes les octets à écrire
	 * @return nombre d'octets écrits
	 */
	public synchronized int writeBytes(byte... bytes) {
		return LibC.write(fd, bytes, bytes.length);
	}

	/**
	 * Ferme la connexion I2C
	 */
	public void close() {
		LibC.close(fd);
	}

	/**
	 * Écrit des octets dans un registre
	 * 
	 * @param regaddr adresse du registre
	 * @param vals octets à écrire
	 */
	public void writeRegister(int regaddr, byte... vals) {
		byte[] towrite = new byte[vals.length + 1];
		towrite[0] = (byte) regaddr;
		System.arraycopy(vals, 0, towrite, 1, vals.length);
		writeBytes(towrite);
	}

	/**
	 * Lit un registre de 16 bits (2 octets)
	 * 
	 * @param msb_addr adresse de l'octet de poids fort
	 * @param lsb_addr adresse de l'octet de poids faible
	 */
	public int readRegister16(int msb_addr, int lsb_addr) {
		return readRegister(lsb_addr) | (readRegister(msb_addr) << 8);
	}

	/**
	 * Lit un registre
	 * 
	 * @param addr adresse du registre
	 * @return octet lu
	 */
	public synchronized int readRegister(int addr) {
		writeBytes((byte) addr);
		return readByte();
	}
}
