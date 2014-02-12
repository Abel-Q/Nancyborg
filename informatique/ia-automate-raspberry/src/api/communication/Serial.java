package api.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

/**
 * Représente un port série.
 * 
 * @author mickael
 * 
 */
public class Serial {

	private CommPortIdentifier identifier;
	private SerialPort port;
	private OutputStream out;
	private InputStream in;
	private PrintStream print;

	public PrintStream getPrintStream() {
		return print;
	}

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
	public Serial(String path, int speed) throws IOException, PortInUseException, NoSuchPortException, UnsupportedCommOperationException {
		identifier = CommPortIdentifier.getPortIdentifier(path);
		port = (SerialPort) identifier.open("TheGame", 0);
		port.setSerialPortParams(speed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		in = port.getInputStream();
		out = port.getOutputStream();

		print = new PrintStream(out);
	}

	/**
	 * Change les paramètres du port.
	 * 
	 * @param speed vitesse en bauds
	 * @param dataBits nombre de bits de données (constantes {@link SerialPort}
	 *            .DATABITS_*)
	 * @param stopBits nombre de bits de stop (constantes {@link SerialPort}
	 *            .STOPBITS_*)
	 * @param parity parité (constantes {@link SerialPort}.PARITY_*)
	 * @throws UnsupportedCommOperationException
	 */
	public void setSerialPortParams(int speed, int dataBits, int stopBits, int parity) throws UnsupportedCommOperationException {
		port.setSerialPortParams(speed, dataBits, stopBits, parity);
	}

	/**
	 * Envoie un break sur le port série pendant une durée donnée.
	 * <p>
	 * Cela revient à mettre la ligne Tx au niveau bas, ce qui provoque
	 * généralement une remise à zéro du périphérique connecté.
	 * 
	 * @param millis durée du break
	 */
	public void sendBreak(int millis) {
		port.sendBreak(millis);
	}

	/**
	 * Écrit un ou plusieurs octets dans le port série.
	 * 
	 * @param bytes
	 * @throws IOException
	 */
	public void write(byte... bytes) throws IOException {
		out.write(bytes);
	}

	/**
	 * Écrit un ou plusieurs octets dans le port série.
	 * 
	 * @param bytes
	 * @throws IOException
	 */
	public void write(int... bytes) throws IOException {
		for (int b : bytes) {
			out.write(b);
		}
	}

	/**
	 * Écrit une chaine de caractère dans le port série.
	 * 
	 * @param str
	 * @throws IOException
	 */
	public void write(String str) throws IOException {
		write(str.getBytes());
	}

	/**
	 * Lit un octet depuis le port série.
	 * 
	 * @throws IOException
	 */
	public byte readByte() throws IOException {
		return (byte) in.read();
	}

	/**
	 * Lit un octet depuis le port série (résultat dans un entier).
	 * 
	 * @throws IOException
	 */
	public int read() throws IOException {
		return in.read();
	}

	/**
	 * Lit un caractère depuis le port série.
	 * 
	 * @throws IOException
	 */
	public char readChar() throws IOException {
		return (char) in.read();
	}

	/**
	 * Lit une ligne depuis le port série.
	 * 
	 * @throws IOException
	 */
	public String readLine() throws IOException {
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
	 * 
	 * @throws IOException
	 */
	public boolean ready() throws IOException {
		return in.available() > 0;
	}
}
