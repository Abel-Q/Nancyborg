package api.asserv;

import java.io.IOException;

import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.UnsupportedCommOperationException;
import api.communication.Serial;

/**
 * Classe permettant de communiquer avec l'asservissement sur MBED et de lui
 * donner tout les ordres nécessaires une fois qu'il est bien réglé.
 * 
 * @author GaG <francois.prugniel@esial.net>
 * 
 */

public class Asserv {
	/**
	 * Liaison série avec la MBED
	 */
	private Serial mbed;
	/**
	 * Commande à executer
	 * TODO On doit pouvoir faire plus propre je pense
	 */
	private String commande; // Dernière commande
	/**
	 * Booléen signalant l'exécution complète de la dernière commande
	 */
	private boolean lastCommandFinished;

	/**
	 * Constructeur
	 * 
	 * @param serie Nom de la liaison serie
	 * @throws UnsupportedCommOperationException 
	 * @throws NoSuchPortException 
	 * @throws PortInUseException 
	 * @throws IOException 
	 */
	public Asserv(String serie) throws IOException, PortInUseException, NoSuchPortException, UnsupportedCommOperationException {
		commande = "";
		mbed = new Serial(serie, 115200);
		mbed.sendBreak(10);
		lastCommandFinished = true;
		while(!mbed.readLine().endsWith("ok"));
		System.out.println("Asserv ready (la salope)");
	}

	/**
	 * Le robot s'aligne avec la position (x,y) puis y va
	 * 
	 * @param x Abscisse en mm
	 * @param y Ordonnée en mm
	 */
	public void gotoPosition(double x, double y) {
		commande = "g"+x+"#"+y+"\n";
		sendCommand();
	}

	/**
	 * Le robot s'aligne avec la position (x,y) 
	 * 
	 * @param x Abscisse en mm
	 * @param y Ordonnée en mm
	 */
	public void face(double x, double y) {
		commande = "f"+x+"#"+y+"\n";
		sendCommand();
	}

	/**
	 * Avance tout droit
	 * @param d Distance à parcourir en mm
	 */
	public void go(double d) {
		commande = "v"+d+"\n";
		sendCommand();
	}

	/**
	 * Tourne
	 * 
	 * @param a Angle à parcourir en degrés
	 */
	public void turn(double a) {
		commande = "t"+a+"\n";
		sendCommand();
	}

	/**
	 * Arrêt du robot (en cas de détection)
	 * Désactive l'asservissement
	 */
	public void halt() {
		try {
			mbed.write("h");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Redémarre l'asservissement après un halt()
	 */
	public void resetHalt() {
		try {
			mbed.write("r");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Envoie un ordre à l'asservissement
	 */
	public void sendCommand() {
		System.out.println("sending : " + commande);
		try {
			synchronized (this) {
				mbed.write(commande);
				lastCommandFinished = false;
				launchFinishedChecker();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Si un adversaire est devant nous, on recule de 50cm
	 */
	public void eviteAdversaireDevant() {
		System.out.println("sending : " + commande);
		try {
			synchronized (this) {
				resetHalt();
				mbed.write("v-50!");
				lastCommandFinished = false;
				launchFinishedChecker();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Si un robot est derrière nous, on avance de 50cm
	 */
	public void eviteAdversaireDerriere() {
		System.out.println("sending : " + commande);
		try {
			synchronized (this) {
				resetHalt();
				mbed.write("v50!");
				lastCommandFinished = false;
				launchFinishedChecker();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ecoute l'asserv pour surveiller la finalisation de la commande
	 */
	private void launchFinishedChecker() {
		Thread checker = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (!lastCommandFinished) {
						//System.out.println("On check");
						if (mbed.ready()) {
							char check = mbed.readChar();
							System.out.println("reçu : " + check);
							if (check == 'd') {
								lastCommandFinished = true;
							}
						} else {
							Thread.sleep(10);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		checker.start();
	}

	/**
	 * On lance le calage bordure
	 * Cette commande est bloquante
	 * @param sens Sens du selecteur de couleur
	 */
	public void calageBordure(boolean sens) {
		try {
			mbed.write("c" + (sens ? "1" : "0") + "g");
			while (mbed.ready()) {
				mbed.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renvoit un booléen signalant si la dernière commande est totalement
	 * exécuté
	 * 
	 * @return lastCommandFinished
	 */
	public boolean lastCommandFinished() {
		return lastCommandFinished;
	}
	
}
