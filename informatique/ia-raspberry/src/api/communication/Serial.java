package api.communication;

import java.io.IOException;
import com.pi4j.io.serial.SerialFactory;

/**
 * Représente un port série.
 * 
 * @author mickael
 * 
 */
public class Serial {
	private com.pi4j.io.serial.Serial serial;

	/**
	 * Ouvre un port série
	 * 
	 * @param path chemin du port série (/dev/ttyAMA0 par exemple)
	 * @param speed vitesse en bauds
	 * @throws IOException
	 * @throws PortInUseException
	 * @throws NoSuchPortException
	 * @throws UnsupportedCommOperationException
	 */
	public Serial(String path, int speed) {
		serial = SerialFactory.createInstance();
		serial.open(path, speed);
	}

	/**
	 * Écrit un ou plusieurs octets dans le port série.
	 * 
	 * @param bytes
	 */
	public synchronized void write(byte... bytes) {
		serial.write(bytes);
	}

	/**
	 * Écrit un ou plusieurs octets dans le port série.
	 * 
	 * @param bytes
	 */
	public synchronized void write(int... bytes) {
		for (int b : bytes) {
			serial.write((byte) (b & 0xFF));
		}
	}

	/**
	 * Écrit une chaine de caractère dans le port série.
	 * 
	 * @param str
	 */
	public synchronized void write(String str) {
		serial.write(str);
	}

	/**
	 * Lit un octet depuis le port série.
	 */
	public synchronized byte readByte() {
		return (byte) serial.read();
	}

	/**
	 * Lit un octet depuis le port série (résultat dans un entier).
	 */
	public synchronized int read() {
		return (int) serial.read();
	}

	/**
	 * Lit plusieurs octets depuis le port série
	 * 
	 * @throws IOException
	 */
	public byte[] read(int bytes) throws IOException {
		byte[] arr = new byte[bytes];

		for (int i = 0; i < bytes; i++) {
			arr[i] = readByte();
		}

		return arr;
	}

	/**
	 * Lit un caractère depuis le port série.
	 */
	public synchronized char readChar() {
		return serial.read();
	}

	/**
	 * Lit une ligne depuis le port série.
	 */
	public synchronized String readLine() {
		String ret = "";

		while (true) {
			char c = readChar();

			if (c == '\n' || c == 0) {
				break;
			}

			ret += c;
		}

		return ret;
	}

	/**
	 * Indique si il y a au moins un octet en attente d'être lu
	 */
	public synchronized boolean ready() {
		return serial.availableBytes() > 0;
	}
}
