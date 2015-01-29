package api.gpio;

import java.io.IOException;

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
	public Led(int gpio) throws IOException {
		this.gpio = new Gpio(gpio, false);
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
