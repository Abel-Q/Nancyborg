package api.canon2014;

import java.io.IOException;

import navigation.Point;
import fr.nancyborg.ax12.AX12Linux;
import ia.nancyborg2014.Ia;
import api.gpio.Gpio;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;

public class Canon {
	public enum ModeTir {
		HAUT,
		BAS
	}
	private static final float alphaMin = 218; //en degrés
	private static final float alphaMax = 237; //en degrés
	private static final float angleHaut = 220;
	private static final float angleBas = 226;

	private Ia ia;
	private AX12Linux axElevation;
	private AX12Linux axBarilet;
	private Gpio gpio;
	private int numBalle = 0;

	public Canon(Pin pinCanon, Ia ia) throws IOException {
		this.axElevation = new AX12Linux("/dev/ttyAMA0", 2, 115200);
		this.axBarilet = new AX12Linux("/dev/ttyAMA0", 3, 115200);
		this.ia = ia;

		axElevation.setCWLimit(alphaMin);
		axElevation.setCCWLimit(alphaMax);
		this.axBarilet.setGoalPosition(0, true);

		gpio = new Gpio(pinCanon, PinMode.DIGITAL_OUTPUT);
		gpio.setLow();
	}

	public void lancer(float angleLanceur, int numBalle) throws IOException, InterruptedException{
		System.out.println("lancer: " + angleLanceur);
		this.axElevation.setGoalPosition((float) angleLanceur, true);
		this.axBarilet.setGoalPosition(60.0f * numBalle, true);
		gpio.setHigh();
		Thread.sleep(40);
		gpio.setLow();
	}

	public void tir(ModeTir mode) throws IOException, InterruptedException {
		if (mode == ModeTir.HAUT) {
			// on tire nos 3 balles en haut et 3 en bas
			lancer(angleHaut, numBalle++);
		} else if (mode == ModeTir.BAS) {
			lancer(angleBas, numBalle++);
		}
	}
}
