package api.gpio;

import java.io.Closeable;
import java.io.IOException;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Classe générique de controle d'un GPIO
 * 
 * @author GaG <francois.prugniel@esial.net>
 * @author mickael
 * @version 1.0
 * 
 */

public class Gpio implements Closeable {	
	private GpioPinDigitalMultipurpose gpioPin;

	/**
	 * Constructeur d'un GPIO
	 * @param pin Broche du GPIO (numéro WiringPi : voir http://wiringpi.com/pins/)
	 * @throws IOException
	 */
	public Gpio(Pin pin, PinMode mode, PinPullResistance pull) throws IOException {
		System.out.println("Configuration Gpio " + pin);
		gpioPin = GpioFactory.getInstance().provisionDigitalMultipurposePin(pin, mode, pull);
		System.out.println("Gpio ok");
	}

	/**
	 * Constructeur d'un GPIO
	 * 
	 * @param pin pin
	 * @param entree Sens du GPIO
	 * @throws IOException
	 */
	public Gpio(Pin pin, PinMode mode) throws IOException {
		this(pin, mode, PinPullResistance.OFF);
	}

	/**
	 * Fermeture du GPIO.
	 * <p>
	 * Appelez cette méthode à la fin du programme pour fermez proprement le
	 * GPIO
	 */
	public void close() {
		System.out.println("Fermeture Gpio " + gpioPin);
	}

	/**
	 * @return Le GPIO est-il a 1 ?
	 * @throws IOException
	 */
	public boolean isHigh() {
		return gpioPin.isHigh();
	}

	/**
	 * @return Le GPIO est-il a 0 ?
	 * @throws IOException
	 */
	public boolean isLow() {
		return gpioPin.isLow();
	}

	/**
	 * Met le GPIO a 1
	 * 
	 * @throws IOException
	 */
	public void setHigh() throws IOException {
		gpioPin.setState(true);
	}

	/**
	 * Met le GPIO a 0
	 * 
	 * @throws IOException
	 */
	public void setLow() throws IOException {
		gpioPin.setState(false);
	}

	/**
	 * Permet d'activer les résistances de pull-up et de pull-down sur le GPIO
	 */
	public void setPull(PinPullResistance pull) {
		gpioPin.setPullResistance(pull);
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
