package ia.princessbotik2013;

import java.io.IOException;

import api.asserv.Asserv;

/**
 * Thread gérant les déplacements du robot
 * Permet de gérer la détection de l'adversaire sans perdre l'étape suivante
 * du déplacement
 * Cette classe est a réécrire chaque fois (en attendant un A*)
 * @author Trowa
 *
 */
public class DeplacementTask extends Thread {
	
	private Asserv asserv;
	private Servomoteurs servos;
	private boolean rouge;
	private boolean marcheAvant;
	private boolean adversaireDetecte;
	private boolean stopped;
	
	/**
	 * @return the stopped
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * @param stopped the stopped to set
	 */
	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	/**
	 * @return the asserv
	 */
	public Asserv getAsserv() {
		return asserv;
	}

	/**
	 * @param asserv the asserv to set
	 */
	public void setAsserv(Asserv asserv) {
		this.asserv = asserv;
	}

	/**
	 * @return the servos
	 */
	public Servomoteurs getServos() {
		return servos;
	}

	/**
	 * @param servos the servos to set
	 */
	public void setServos(Servomoteurs servos) {
		this.servos = servos;
	}

	/**
	 * @return the rouge
	 */
	public boolean isRouge() {
		return rouge;
	}

	/**
	 * @param rouge the rouge to set
	 */
	public void setRouge(boolean rouge) {
		this.rouge = rouge;
	}

	/**
	 * @return the marcheAvant
	 */
	public boolean isMarcheAvant() {
		return marcheAvant;
	}

	/**
	 * @param marcheAvant the marcheAvant to set
	 */
	public void setMarcheAvant(boolean marcheAvant) {
		this.marcheAvant = marcheAvant;
	}

	/**
	 * @return the adversaireDetecte
	 */
	public boolean isAdversaireDetecte() {
		return adversaireDetecte;
	}

	/**
	 * @param adversaireDetecte the adversaireDetecte to set
	 */
	public void setAdversaireDetecte(boolean adversaireDetecte) {
		this.adversaireDetecte = adversaireDetecte;
	}

	public DeplacementTask(Asserv asserv, Servomoteurs servos, boolean rouge) {
		this.asserv = asserv;
		this.servos = servos;
		this.rouge = rouge;
	}
	
	private void waitForFinish() {
		boolean finished = false;
		while(!finished) {
			checkStopped();
			if(asserv.lastCommandFinished()) {
				finished = true;
			}
		}
		
	}

	/**
	 * Si le robot est en mouvement, on déclenche l'arret d'urgence de l'asserv
	 */
	public void stopDeplacements() {
		if(!stopped) {
			stopped = true;
			asserv.halt();
		}
	}
	
	/**
	 * On remet le robot en route en reprenant la dernière commande en cours
	 */
	public void resumeDeplacement() {
		if(stopped) {
			stopped = false;
			asserv.resetHalt();
			asserv.sendCommand();
		}
	}
	
	/**
	 * On attend tranquillement que la détection de l'adversaire ne voit plus personne
	 */
	private void checkStopped() {
		while(stopped) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		System.out.println("C'est parti !");
		try {
			match();
			Thread.sleep(1000*1000);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void match() throws IOException {
		
		int mult = this.rouge?-1:1;
		
		// On va au milieu en esquivant les verres
		marcheAvant = true;
		asserv.gotoPosition(320, mult*200, false);
		this.waitForFinish();
		asserv.gotoPosition(1520, mult*200, false);
		this.waitForFinish();
			
		// On monte sur la première ligne
		asserv.gotoPosition(1520, mult*500, false);
		servos.ouvertureTourVerres();
		this.waitForFinish();
			
		// On revient avec la pince ouverte
		asserv.gotoPosition(370, mult*500, false);
		this.waitForFinish();
			
		// On recule pour faire demi-tour et retour au milieu
		marcheAvant = false;
		asserv.go(-200, false);
		this.waitForFinish();
		servos.fermetureTourVerres();
		marcheAvant = true;
		asserv.gotoPosition(1520, mult*500, false);
		this.waitForFinish();
			
		// On monte sur la seconde ligne
		asserv.gotoPosition(1520, mult*750, false);
		servos.ouvertureTourVerres();
		this.waitForFinish();
			
		// On revient avec la pince ouverte
		asserv.gotoPosition(370, mult*750, false);
		this.waitForFinish();
			
		// On recule pour faire demi-tour et retour au milieu (un poil plus loin pour ne pas pousser le verre du bout)
		marcheAvant = false;
		asserv.go(-200, false);
		this.waitForFinish();
		servos.fermetureTourVerres();
		marcheAvant = true;
		asserv.gotoPosition(1530, mult*750, false);
		this.waitForFinish();
			
		// On monte sur la troisième ligne
		asserv.gotoPosition(1530, mult*1000, false);
		servos.ouvertureTourVerres();
		this.waitForFinish();
			
		// On revient
		asserv.gotoPosition(370, mult*1000, false);
		this.waitForFinish();
			
		// On recule et on ferme la pince
		marcheAvant = false;
		asserv.go(-200, false);
		this.waitForFinish();
		servos.fermetureTourVerres();
	}
}
