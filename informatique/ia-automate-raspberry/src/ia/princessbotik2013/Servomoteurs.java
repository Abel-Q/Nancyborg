package ia.princessbotik2013;

import java.io.IOException;

import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.UnsupportedCommOperationException;
import api.communication.Serial;
import api.controllers.PololuMaestro;

/**
 * Classe de gestion des 6 servomoteurs du robot avec une micro-maestro Servo 0
 * : Rotation Bras Servo 1 : Inclinaison bras Servo 2 : Articulation bras Servo
 * 3 : Pince Servo 4 : Tour à tasse Servo 5 : Bougie
 * 
 * @author Trowa
 * 
 */
public class Servomoteurs {

	public void test() throws IOException {
		try {
			this.eteindreBougie();
			Thread.sleep(1000);
			this.eteindreBougie();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Liaison série avec la micro-maestro : /dev/ttyACM0 pour le port USB du
	 * bas
	 */
	public Serial serial;

	/**
	 * Interface micro-maestro pour contrôler le bordel
	 */
	public PololuMaestro maestro;

	/**
	 * 
	 * @param serie
	 * @throws IOException
	 * @throws PortInUseException
	 * @throws NoSuchPortException
	 * @throws UnsupportedCommOperationException
	 */
	public Servomoteurs(String serie) throws IOException, PortInUseException, NoSuchPortException, UnsupportedCommOperationException {
		this.serial = new Serial(serie, 115200);
		this.maestro = new PololuMaestro(serial);
		this.maestro.init();
	}

	public void ouvertureTourVerres() throws IOException {
		this.maestro.setTargetMs(4, 2400);
	}

	public void fermetureTourVerres() throws IOException {
		this.maestro.setTargetMs(4, 500);
	}

	public void ouvrirPince() throws IOException {
		maestro.setTargetMs(3, 2400);
	}

	public void fermerPince() throws IOException {
		maestro.setTargetMs(3, 500);
	}

	public void deplierBras() throws IOException {
		maestro.setTargetMs(2, 1200);
	}

	public void plierBras() throws IOException {
		maestro.setTargetMs(2, 500);
	}

	public void descendreBras() throws IOException {
		maestro.setTargetMs(1, 1200);
	}

	public void leverBras() throws IOException {
		maestro.setTargetMs(1, 2000);
	}

	public void rotationGaucheBras() throws IOException {
		maestro.setTargetMs(0, 1200);
	}

	public void rotationDroiteBras() throws IOException {
		maestro.setTargetMs(0, 2000);
	}

	public void eteindreBougie() throws IOException {
		this.maestro.setTargetMs(5, 2000);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.maestro.setTargetMs(5, 400);
	}
}
