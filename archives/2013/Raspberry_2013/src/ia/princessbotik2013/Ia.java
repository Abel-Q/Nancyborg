package ia.princessbotik2013;

import ia.common.DetectionSRF10;

import java.io.IOException;
import java.util.TimerTask;

import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.UnsupportedCommOperationException;
import api.asserv.Asserv;
import api.chrono.Chrono;
import api.gpio.Gpio;

public class Ia {

	/**
	 * @param args Useless
	 * @throws UnsupportedCommOperationException 
	 * @throws NoSuchPortException 
	 * @throws PortInUseException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
			// On initialise l'asservissement
			final Asserv asserv = new Asserv("/dev/serial/by-id/usb-mbed_Microcontroller_101000000000000000000002F7F0AFA6-if01");
			// On intialise les servomoteurs
			Servomoteurs servos = new Servomoteurs("/dev/serial/by-id/usb-Pololu_Corporation_Pololu_Micro_Maestro_6-Servo_Controller_00026569-if00");
			
			// On initialise les GPIOs
			final Gpio tirette = new Gpio(11, true); // Mise = high, Enleve = low;
			final Gpio selecteurCouleur = new Gpio(10, true); // Bleu = low, Rouge = high
			boolean rouge = false;
			
			// On initialise le chrono
			Chrono chrono = new Chrono(88*1000);
			
			// On intialise la deplacementTask
			final DeplacementTask deplacementTask = new DeplacementTask(asserv, servos, rouge);
			
			//  On initialise la détection
			DetectionSRF10 detection = new DetectionSRF10(1, 0x72, 0x70, 0x74, 3, 45);
			
			System.out.println("Attente enlevage tirette");
			// On attend de virer la tirette
			while(tirette.isHigh());
			rouge = selecteurCouleur.isHigh();
			
			System.out.println("Callage bordure");
			// On lance le callage bordure
			asserv.calageBordure(rouge);
			
			System.out.println("Attente remise tirette");
			// On attend de remettre la tirette
			while(tirette.isLow());
			System.out.println("Attente enlevage tirette");
			// On attend de virer la tirette
			while(tirette.isHigh());
			
			System.out.println("Mise en position");
			asserv.go(-100);
			while(!asserv.lastCommandFinished());
			
			System.out.println("Attente remise tirette");
			// On attend de remettre la tirette
			while(tirette.isLow());
			
			System.out.println("Attente enlevage tirette");
			// On attend de virer la tirette
			while(tirette.isHigh());
			System.out.println("Gooo");
			
			// On démarre le chrono et la déplacementTask
			chrono.startChrono(new TimerTask() {
				@Override
				public void run() {
					asserv.halt();
					tirette.close();
					selecteurCouleur.close();
					deplacementTask.stopDeplacements();
					System.out.println("Fin");
					System.exit(0);
				}
			});
			deplacementTask.start();
			
			// Maintenant on ne fait que vérifier la présence de l'adversaire
			while(true) {
				if (deplacementTask.isMarcheAvant()) {
					//  Si on est en marche avant on regarde devant
					if (detection.detectionAvant()) {
						// Si il y a quelqu'un on s'arrête
						deplacementTask.stopDeplacements();
					} else if (deplacementTask.isStopped()) {
						// Si il n'y a personne et qu'on été a l'arret, on repart
						deplacementTask.resumeDeplacement();
					}
				} else {
					// Si on est en marche arrière on regarde derrière
					if (detection.detectionArriere()) {
						// Si il y a quelqu'un on s'arrête
						deplacementTask.stopDeplacements();
					} else if (deplacementTask.isStopped()) {
						// Si il n'y a personne et qu'on été a l'arret, on repart
						deplacementTask.resumeDeplacement();
					}
				}
			}
			
		
		//		Gpio relai = null; // fail
//		try {
//			relai = new Gpio(17, false);
//			relai.close();
//			System.out.println("-----------------------------------------------");
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
	}

}