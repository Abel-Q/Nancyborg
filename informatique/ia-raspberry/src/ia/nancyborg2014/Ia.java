package ia.nancyborg2014;

import ia.nancyborg2014.Canon.ModeTir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import navigation.Navigation2014;
import navigation.Point;
import api.asserv.Asserv;
import api.chrono.Chrono;
import api.gpio.Gpio;
import api.sensors.DetectionIR;
import api.sensors.DetectionSRF;
import api.sensors.DetectionSRFThread;

import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

import fr.nancyborg.ax12.AX12Linux;

public class Ia {

	public Asserv asserv;
	public Gpio tirette, selecteurCouleur;
	public boolean rouge;
	//public DetectionIR detection;
	public DetectionSRFThread detection;
	public Navigation2014 nav;
	public DeplacementTask deplacement;
	public ArrayList<Point> objectifs;
	public ArrayList<Point> objectifsAtteints;
	public Point objectifCourant;
	public DetectionSRF capteurArriere;
	public Canon canon;
	public Filet filet;

	public Ia() {
		try {
			// On initialise l'asservissement
			asserv = new Asserv("/dev/serial/by-id/usb-mbed_Microcontroller_101000000000000000000002F7F04F94-if01");

			// TODO initialisation des AX12 du canon

			// On initialise les GPIOs
			tirette = new Gpio(RaspiPin.GPIO_03, PinMode.DIGITAL_INPUT, PinPullResistance.PULL_UP); // Mise = low, Enleve = high;
			selecteurCouleur = new Gpio(RaspiPin.GPIO_02, PinMode.DIGITAL_INPUT, PinPullResistance.PULL_UP); // Rouge = high, Jaune = low
			rouge = false;
			objectifsAtteints = new ArrayList<Point>();
			objectifs = new ArrayList<Point>();

			// Détection de l'adversaire
			//this.detection = this.getDetecteur();
			this.detection = new DetectionSRFThread(0xE4, 0xE8, 30, this);
			this.capteurArriere = new DetectionSRF(0xE0, 30, 30);

			//nav = new Navigation2014();
			canon = new Canon(RaspiPin.GPIO_07, this);
			filet = new Filet(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DetectionIR getDetecteur() {
		float[] anglesCapteurs = { -45.0f, 0.0f, 45.0f };
		AX12Linux ax12Detection = new AX12Linux("/dev/ttyAMA0", 1, 115200);
		// Distance capteur - balise = 56cm
		return new DetectionIR(anglesCapteurs, 240.0f, 56.0, ax12Detection, RaspiPin.GPIO_14, RaspiPin.GPIO_13, RaspiPin.GPIO_12, this);
	}

	public Point getPosition() {
		return asserv.getCurrentPosition();
	}
	
	public ArrayList<Point> getCachedCommandesAsserv() {
		return this.nav.getCachedCommandeAsserv();
	}
	
	public Point[] getZoneInterdite(Point adversaire) {
		return this.nav.getExtremeZoneInterdite(adversaire);
	}

	public void detectionAdversaire(Point  adversaire) {
		// La détectionSRF nous a stoppé, l'asserv est déjà coupée
		if (this.deplacement != null) {
			this.deplacement.stop();
		}
		this.deplacement = null;
		
		this.asserv.halt();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.detection.setDetect(false);
		
		// On reset l'arret d'urgence de l'asserv
		this.asserv.resetHalt();
		
		this.asserv.turn(180, true);
		
		this.detection.setDetect(true);
		this.deplacement = this.nouvelObjectif();
		if (this.deplacement != null) {
			this.deplacement.start();
		}
	}
	
	// On a vu quelqu'un mais ça marche pas
	public void detectionAdversaire(Point adversaire, long time) {
		System.out.println("Stooooooop");
		if (this.deplacement != null) {
			this.deplacement.stop();
		}
		System.out.println("stop thread : "+(System.currentTimeMillis()-time)+"ms");
		this.deplacement = null;
		System.out.println("null : "+(System.currentTimeMillis()-time)+"ms");
		// On s'arrête et on attend de perdre notre inertie
		this.asserv.halt();
		System.out.println("Temps avant d'envoyer halt : "+(System.currentTimeMillis()-time)+"ms");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// On reset l'arret d'urgence de l'asserv
		this.asserv.resetHalt();

		System.out.println("fini");
		// On recalcul l'itinéraire ou on trouve un autre objectif
		System.out.println("Nouveau calcul");
		boolean goalReachable = this.nav.obstacleMobile(adversaire, this.asserv.getCurrentPosition());
		if (goalReachable) {
			this.deplacement = new DeplacementTask(this.asserv, this.rouge, this.nav.getCommandeAsserv(), this);
		} else {
			System.out.println("Changement d'objectif");
			this.deplacement = this.nouvelObjectif();
		}
		System.out.println("Fin du calcul : "+(System.currentTimeMillis()-time)+"ms");

		// On se met en route ou si l'on n'a plus d'objectif, on attend et on recommence
		if (this.deplacement != null) {
			this.deplacement.start();
		} else {
			this.detectionAdversaire(adversaire, time);
		}
	}

	// Objectif atteint
	public void objectifAtteint(Point objectif) {
		System.out.println("Je suis arrivé : "+objectif);
		System.out.println("Kill de déplacement");
		this.deplacement = null;
		
		// On coupe la détection, le déplacement est déjà fini et on note l'objectif atteint
		System.out.println("Stop détection");
		this.detection.setDetect(false);
		if (!objectif.equals(new Point(1100, this.fuckingMult() * 900))) {
			System.out.println("Marquage objectif");
			this.objectifsAtteints.add(objectif);
		}
		
		System.out.println(this.objectifs);
		System.out.println(this.objectifsAtteints);
		System.out.println(this.objectifs.indexOf(objectif));
		// On lance la séquence de marquage de point
		switch (this.objectifs.indexOf(objectif)) {
			case 0:
				// On place les fresques
				System.out.println("Pose ta fresque Biatch !!!");
				this.asserv.gotoPosition(1280, this.fuckingMult() * 150, true);
				this.asserv.go(40, false);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
				this.asserv.halt();
				this.asserv.resetHalt();
				this.asserv.go(-550, false);
				while (!this.asserv.lastCommandFinished()) {
					try {
						if (this.capteurArriere.doitStopper()) {
							System.out.println("Stop fresque 2");
							this.asserv.halt();
							Thread.sleep(200);
							this.asserv.reset();
							while (!(this.capteurArriere.peutRepartir()));
							this.asserv.go(-550, false);
						}
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				// On tire sur le mamouth
				System.out.println("Oh oui, tire moi grand fou !!");
				try {
					this.asserv.face(700, 0, true);
					this.asserv.turn(5 * this.fuckingMult(), true);
					this.canon();
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
				break;
			case 2:
				// Feu extérieur
				System.out.println("Feu extérieur : éteind moi !!!");
				this.asserv.face(400, this.fuckingMult() * 2000, true);
				this.asserv.go(300, false);
				while (!this.asserv.lastCommandFinished()) {
					try {
						if (!this.rouge && this.detection.getCapteurDroit().doitStopper() || (this.rouge && this.detection.getCapteurGauche().doitStopper())) {
							System.out.println("Stop feu extérieur 1");
							this.asserv.halt();
							Thread.sleep(200);
							this.asserv.reset();
							while (!(!this.rouge && this.detection.getCapteurDroit().peutRepartir()) || !(this.rouge && this.detection.getCapteurGauche().peutRepartir()));
							this.asserv.go(300, false);
						}
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.asserv.go(-300, false);
				while (!this.asserv.lastCommandFinished()) {
					try {
						if (this.capteurArriere.doitStopper()) {
							System.out.println("Stop feu extérieur 2");
							this.asserv.halt();
							Thread.sleep(200);
							this.asserv.reset();
							while (!(this.capteurArriere.peutRepartir()));
							this.asserv.go(-300, false);
						}
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 3:
				// Feu bas
				System.out.println("Feu bas : éteind moi !!!");
				this.asserv.face(0, this.fuckingMult() * 1600, true);
				this.asserv.go(300, true);
				while (!this.asserv.lastCommandFinished()) {
					try {
						if ((this.rouge && this.detection.getCapteurDroit().doitStopper()) || (!this.rouge && this.detection.getCapteurGauche().doitStopper())) {
							System.out.println("Stop feu bas 1");
							this.asserv.halt();
							Thread.sleep(200);
							this.asserv.reset();
							while (!(this.rouge && this.detection.getCapteurDroit().peutRepartir()) || !(!this.rouge && this.detection.getCapteurGauche().peutRepartir()));
							this.asserv.go(300, false);
						}
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.asserv.go(-300, true);
				while (!this.asserv.lastCommandFinished()) {
					try {
						if (this.capteurArriere.doitStopper()) {
							System.out.println("Stop feu bas 2");
							this.asserv.halt();
							Thread.sleep(200);
							this.asserv.reset();
							while (!(this.capteurArriere.peutRepartir()));
							this.asserv.go(-300, false);
						}
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 4:
				// Foyer
				System.out.println("C'est chaud ça brule, c'est le foyer !!");
				break;
			default:
				// Je sais pas
				System.out.println("Mais qu'est ce que je fou là ??");
				break;
		}
		
		// On cherche un nouvel objectif et on y va
		this.deplacement = this.nouvelObjectif();
		this.detection.setDetect(true);
		if (deplacement != null) {
			this.deplacement.start();
		}
	}

	// On trouve un nouvelle objectif, en éliminant ceux déjà réalisé
	public DeplacementTask nouvelObjectif() {
		System.out.println("Recherche d'objectifs");
		ArrayList<Point> todo = new ArrayList<Point>(this.objectifs);
		todo.removeAll(this.objectifsAtteints);
		todo.remove(this.objectifCourant);
		System.out.println("Objectif courant");
		System.out.println("todo : "+todo);
		
		if (this.objectifs.size() == this.objectifsAtteints.size()) {
			System.out.println("J'ai fini mon taff, je vous emmerde et je rentre à ma maison !!");
			return null; // On insiste pas
		}
		
		double dist = 10000;
		int newObjectif = -1;
		for (int i = 0; i < todo.size(); i++) {
			// Check colision centre et torches
			int xmin = 1150;
			int xmax = 1850;
			int ymin = this.fuckingMult() * 1300;
			int ymax = this.fuckingMult() * 600;
			
			int xa = this.asserv.getCurrentPosition().getX();
			int ya = this.asserv.getCurrentPosition().getY();
			int xb = todo.get(i).getX();
			int yb = todo.get(i).getY();
			double a = (double)(ya-yb)/(double)(xa-xb);
			double b = (double)ya-a*(double)xa;
			
			double xint1 = (ymin-b)/a;
			double xint2 = (ymin-b)/a;
			
			double yint1 = a*xmax+b;
			double yint2 = a*xmax+b;
			
			boolean colisionCentre = ((xint1 > xmin  && xint1 < xmax) || (xint2 > xmin  && xint2 < xmax) ||
					(yint1 > ymin  && yint1 < ymax) || (yint2 > ymin  && yint2 < ymax));
			
			if (!colisionCentre) {
				double newdist = Math.hypot(xb-xa, yb-ya);
				if (newdist < dist) {
					dist = newdist;
					newObjectif = i;
				}
			}
		}
		
		ArrayList<Point> liste = new ArrayList<Point>();
		Point point;
		if (newObjectif != -1) {
			System.out.println("J'ai !!!");
			point = todo.get(newObjectif);
			this.objectifCourant = point; // a retirer demain si tout pète
		} else {
			System.out.println("Fail !!");
			point = new Point(1100, this.fuckingMult() * 900);
			this.objectifCourant = new Point(0, 0); // a retirer demain si tout pète
		}
		liste.add(point);
		return new DeplacementTask(this.asserv, this.rouge, liste, this);
	}

	public static void main(String[] args) throws Exception {

		System.out.println("############################################################## IA #################################################");
		final Ia ia = new Ia();

		// On initialise le chrono
		Chrono chrono_funny = new Chrono(85 * 1000);
		Chrono chrono_stop = new Chrono(91 * 1000);
		
		System.out.println("Attente enlevage tirette");
		// On attend de virer la tirette
		while (ia.tirette.isLow());
		
		ia.rouge = ia.selecteurCouleur.isHigh();
		System.out.println("couleur isHigh = "+ia.selecteurCouleur.isHigh()+" - rouge = "+ia.rouge);

		System.out.println("Callage bordure");
		// On lance le callage bordure
		ia.asserv.calageBordure(!ia.rouge);
		ia.asserv.go(100, true);
		ia.asserv.turn(-90 * ia.fuckingMult(), true);

		System.out.println("Attente remise tirette");
		// On attend de remettre la tirette
		while (ia.tirette.isHigh());
		
		// On initialise les objectifs
		ia.initObjectif();
		
		// On fait la première route
		ArrayList<Point> path = ia.getPath(0);
		ia.objectifCourant = ia.objectifs.get(0);

		System.out.println("Attente enlevage tirette pour départ");
		// On attend de virer la tirette
		while (ia.tirette.isLow());
		System.out.println("Gooo");
		
		// On démarre le chrono et la déplacementTask
		chrono_funny.startChrono(new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				System.out.println("On arrête tout et on se place pour la funny action");
				if (ia.deplacement != null) {
					ia.deplacement.stop();
				}
				ia.detection.setDetect(false);
				ia.asserv.halt();
				ia.asserv.resetHalt();
				if (!ia.rouge) {
					ia.asserv.face(950, ia.fuckingMult() * 800, true);
					ia.asserv.gotoPosition(950, ia.fuckingMult() * 800, false);
				} else {
					ia.asserv.face(630, ia.fuckingMult() * 800, true);
					ia.asserv.gotoPosition(630, ia.fuckingMult() * 800, false);
				}
				while (!ia.asserv.lastCommandFinished()) {
					try {
						if (ia.detection.getCapteurDroit().doitStopper() || ia.detection.getCapteurGauche().doitStopper()) {
							ia.asserv.halt();
							Thread.sleep(200);
							ia.asserv.reset();
							while (!(ia.detection.getCapteurDroit().peutRepartir() && ia.detection.getCapteurGauche().peutRepartir()));
							if (ia.rouge) {
								ia.asserv.gotoPosition(950, ia.fuckingMult() * 600, false);
							} else {
								ia.asserv.gotoPosition(630, ia.fuckingMult() * 600, false);
							}
						}
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!ia.rouge) {
					ia.asserv.face(950, 0, true);
				} else {
					ia.asserv.face(630, 0, true);
				}
				
			}
		});
		
		chrono_stop.startChrono(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Fin du match mais on tire un coup quand même");
				ia.asserv.halt();
				ia.filet.lancer();
				ia.tirette.close();
				ia.selecteurCouleur.close();
				ia.detection.stop();
				
				System.out.println("Fin");
				System.exit(0);
			}
		});
		
		// On lance la détection et le déplacement vers le premier objectif
		System.out.println("Lancement détection");
		ia.detection.start();

		System.out.println("Lancement déplacement");
		ia.deplacement = new DeplacementTask(ia.asserv, ia.rouge, path, ia);
		ia.deplacement.start();
		System.out.println("Deplacement run ok");

		//while(true)

	}

	public void canon() throws IOException, InterruptedException {
		final int angle_delta = 8;
		
		this.asserv.turn(-angle_delta, true);
		this.canon.tir(ModeTir.HAUT);
		
		this.asserv.turn(angle_delta, true);
		this.canon.tir(ModeTir.HAUT);
		
		this.asserv.turn(angle_delta, true);
		this.canon.tir(ModeTir.HAUT);

		this.canon.tir(ModeTir.BAS);
		
		this.asserv.turn(-angle_delta, true);
		this.canon.tir(ModeTir.BAS);
		
		this.asserv.turn(-angle_delta, true);
		this.canon.tir(ModeTir.BAS);
	}
	
	public void initObjectif() {
		this.objectifs.add(new Point(1280, this.fuckingMult() * 600)); // Fresques
		this.objectifs.add(new Point(700, this.fuckingMult() * 600)); // Mamouth
		this.objectifs.add(new Point(400, this.fuckingMult() * 800)); // Feu extérieur
		this.objectifs.add(new Point(1100, this.fuckingMult() * 1600)); // Feu bas
		this.objectifs.add(new Point(700, this.fuckingMult() * 1100)); // Foyer
	}
	
	public ArrayList<Point> getPath(int cas) {
		switch (cas) {
			case 0: // Feu sur ligne noir et fresque
				ArrayList<Point> path = new ArrayList<Point>();
				path.add(new Point(200, this.fuckingMult() * 600));
				path.add(new Point(1280, this.fuckingMult() * 600));
				return path;
		}
		return new ArrayList<Point>();
	}
	
	public int fuckingMult() {
		return this.rouge ? -1 : 1;
	}

}
