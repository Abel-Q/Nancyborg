package api.gpio;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

import com.pi4j.wiringpi.GpioUtil;

/**
 * Classe générique de controle d'un GPIO
 * 
 * @author GaG <francois.prugniel@esial.net>
 * @author mickael
 * @version 1.0
 * 
 */

public class Gpio implements Closeable {
	/**
	 * L'entrée est en l'air (flottante)
	 */
	public static final int FLOATING = com.pi4j.wiringpi.Gpio.PUD_OFF;

	/**
	 * L'entrée est reliée à la masse par une résistance interne
	 */
	public static final int PULL_DOWN = com.pi4j.wiringpi.Gpio.PUD_DOWN;

	/**
	 * L'entrée est reliée au 3.3 V par une résistance interne
	 */
	public static final int PULL_UP = com.pi4j.wiringpi.Gpio.PUD_UP;

	/**
	 * Numéro du GPIO (<a
	 * href="http://elinux.org/RPi_Low-level_peripherals#Introduction"
	 * >http://elinux.org/RPi_Low-level_peripherals#Introduction</a>)
	 */
	private int num;

	/**
	 * Constructeur d'un GPIO
	 * 
	 * @param num Numéro du GPIO
	 * @param entree Sens du GPIO
	 * @param pull une des constantes {@link #PULL_UP}, {@link #PULL_DOWN} et
	 *            {@link #FLOATING}
	 * @see #setPull(int)
	 * @throws IOException
	 */
	public Gpio(int num, boolean entree, int pull) throws IOException {
		System.out.println("Configuration Gpio " + num);
		GpioUtil.export(num, entree ? GpioUtil.DIRECTION_IN : GpioUtil.DIRECTION_OUT);
		System.out.println("Gpio ok");
		this.num = num;
		this.setPull(pull);
	}

	/**
	 * Constructeur d'un GPIO
	 * 
	 * @param num Numéro du GPIO (not header pin number; not wiringPi pin number but GPIO number : https://projects.drogon.net/raspberry-pi/wiringpi/pins/)
	 * @param entree Sens du GPIO
	 * @throws IOException
	 */
	public Gpio(int num, boolean entree) throws IOException {
		this(num, entree, FLOATING);
	}

	/**
	 * Fermeture du GPIO.
	 * <p>
	 * Appelez cette méthode à la fin du programme pour fermez proprement le
	 * GPIO
	 */
	public void close() {
		System.out.println("Fermeture Gpio " + this.num);
		GpioUtil.unexport(this.num);
	}

	/**
	 * @return Le GPIO est-il a 1 ?
	 * @throws IOException
	 */
	public boolean isHigh() {
		boolean res = false;
		res = com.pi4j.wiringpi.Gpio.digitalRead(this.num) == 1;
		return res;
	}

	/**
	 * @return Le GPIO est-il a 0 ?
	 * @throws IOException
	 */
	public boolean isLow() {
		boolean res = false;
		res = !this.isHigh();
		return res;
	}

	/**
	 * Met le GPIO a 1
	 * 
	 * @throws IOException
	 */
	public void setHigh() throws IOException {
		com.pi4j.wiringpi.Gpio.digitalWrite(this.num, 1);
	}

	/**
	 * Met le GPIO a 0
	 * 
	 * @throws IOException
	 */
	public void setLow() throws IOException {
		com.pi4j.wiringpi.Gpio.digitalWrite(this.num, 0);
	}

	/**
	 * Permet d'activer les résistances de pull-up et de pull-down sur le GPIO
	 * 
	 * @param pull une des constantes {@link #PULL_UP}, {@link #PULL_DOWN} et
	 *            {@link #FLOATING}
	 */
	public void setPull(int pull) {
		com.pi4j.wiringpi.Gpio.pullUpDnControl(this.num, pull);
	}

	/*public static void main(String[] args) throws IOException {
		System.out.println("-- Test GPIO --");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		for (String arg : args) {
			int n = Integer.parseInt(arg);

			Gpio gpio = new Gpio(n, true);

			System.out.println("Pull up...");
			gpio.setPull(Gpio.PULL_UP);
			in.readLine();

			System.out.println("Pull down...");
			gpio.setPull(Gpio.PULL_DOWN);
			in.readLine();

			System.out.println("En l'air...");
			gpio.setPull(Gpio.FLOATING);
			in.readLine();

			gpio.close();
		}
	}*/
}
