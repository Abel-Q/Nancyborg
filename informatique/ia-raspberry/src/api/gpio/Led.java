package api.gpio;

import java.io.IOException;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;

/**
 * Classe gérant une Led
 * 
 * @author GaG <francois.prugniel@esial.net>
 * 
 */

public class Led {

	/**
	 * GPIO de la Led
	 */
	public Gpio gpio;

	/**
	 * Constructeur de la Led
	 * 
	 * @param gpio Numéro de la num GPIO de la Led
	 * @throws IOException
	 */
	public Led(Pin gpio) throws IOException {
		this.gpio = new Gpio(gpio, PinMode.DIGITAL_OUTPUT);
		this.gpio.setLow();
	}

	/**
	 * Allumage de la Led
	 * 
	 * @throws IOException
	 */
	public void allumer() throws IOException {
		this.gpio.setHigh();
	}

	/**
	 * Extinction de la Led
	 * 
	 * @throws IOException
	 */
	public void eteindre() throws IOException {
		this.gpio.setLow();
	}
}
