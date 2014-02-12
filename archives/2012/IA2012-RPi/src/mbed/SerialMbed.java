package mbed;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SerialMbed {
	private OutputStream deviceOut;
	private FileInputStream deviceIn;

	public SerialMbed() throws IOException {
		File mbedFile = null;

		File dir = new File("/dev/serial/by-id/");

		for (File file : dir.listFiles()) {
			if (file.getName().startsWith("usb-mbed_Microcontroller")) {
				mbedFile = file;
				break;
			}
		}

		if (mbedFile == null)
			throw new FileNotFoundException();

		deviceIn = new FileInputStream(mbedFile);
		deviceOut = new FileOutputStream(mbedFile);
		Runtime.getRuntime().exec("stty -F " + mbedFile.getAbsolutePath() + " ispeed 115200 ospeed 115200");
		//System.out.println("Mbed sur "+pandaFile.getAbsolutePath());
	}

	/*
	   public String readLine() throws Exception {
	   return deviceIn.readLine();
	   }
	   */

	public char getc() {
		try {
			return (char) deviceIn.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public boolean ready() {
		try {
			return deviceIn.available()>0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void send(String s) throws Exception
	{
		deviceOut.write(s.getBytes());
		//System.out.println("envoyé : '" + s + "'");
		deviceOut.flush();
		System.out.println("flush");

	}

	public void emptyBuffer() {
		while(ready()) {
			try {
				deviceIn.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
