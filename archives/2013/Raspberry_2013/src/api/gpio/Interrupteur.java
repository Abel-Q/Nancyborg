package api.gpio;

import java.io.IOException;

/**
 * Classe gérant un interrupteur
 * 
 * @author GaG <francois.prugniel@esial.net>
 * 
 */

public class Interrupteur {

	/**
	 * GPIO de l'interrupteur
	 */
	public Gpio gpio;

	/**
	 * Constructeur de l'interrupteur
	 * 
	 * @param gpio Numéro de la num GPIO de l'interrupteur
	 * @throws IOException
	 */
	public Interrupteur(int gpio) throws IOException {
		this.gpio = new Gpio(gpio, true);
	}

	/**
	 * @return L'interrupteur est-il ouvert ?
	 * @throws IOException
	 */
	public boolean isOuvert() throws IOException {
		return this.gpio.isLow();
	}

	/**
	 * @return L'interrupteur est-il fermé ?
	 * @throws IOException
	 */
	public boolean isFerme() throws IOException {
		return this.gpio.isHigh();
	}
}
