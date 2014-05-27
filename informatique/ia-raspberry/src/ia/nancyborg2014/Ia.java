package ia.nancyborg2014;

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

			//nav = new Navigation2014();
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
		if (!objectif.equals(new Point(1100, 1100))) {
			System.out.println("Marquage objectif");
			this.objectifsAtteints.add(objectif);
		}
		
		int mult = this.rouge ? 1 : -1;

		System.out.println(this.objectifs);
		System.out.println(this.objectifsAtteints);
		System.out.println(this.objectifs.indexOf(objectif));
		// On lance la séquence de marquage de point
		switch (this.objectifs.indexOf(objectif)) {
			case 0:
				// On place les fresques
				System.out.println("Pose ta fresque Biatch !!!");
				this.asserv.gotoPosition(1280, mult * 1800, true);
				this.asserv.go(-400, true);
				break;
			case 1:
				// On tire sur le mamouth
				System.out.println("Oh oui, tire moi grand fou !!");
				break;
			case 2:
				// Feu extérieur
				System.out.println("Feu extérieur : éteind moi !!!");
				break;
			case 3:
				// Feu bas
				System.out.println("Feu bas : éteind moi !!!");
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
		System.out.println("todo : "+todo);
		
		// TODO plus d'objectifs
		if (this.objectifs.size() == this.objectifsAtteints.size()) {
			System.out.println("J'ai fini mon taff, je vous emmerde et je rentre à ma maison !!");
		}
		
		double dist = 10000;
		int newObjectif = -1;
		for (int i = 0; i < todo.size(); i++) {
			// Check colision centre et torches
			int xmin = 1150;
			int xmax = 1850;
			int ymin = 700;
			int ymax = 1400;
			
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
			
			xmin = 700;
			xmax = 1100;
			ymin = 700;
			ymax = 1100;
			
			xint1 = (ymin-b)/a;
			xint2 = (ymin-b)/a;
			
			yint1 = a*xmax+b;
			yint2 = a*xmax+b;
			
			boolean colisionTorche = ((xint1 > xmin  && xint1 < xmax) || (xint2 > xmin  && xint2 < xmax) ||
					(yint1 > ymin  && yint1 < ymax) || (yint2 > ymin  && yint2 < ymax));
			
			if (!colisionCentre && !colisionTorche) {
				double newdist = Math.hypot(xb-xa, yb-ya);
				if (newdist < dist) {
					dist = newdist;
					newObjectif = i;
				}
			}
		}
		
		ArrayList<Point> liste = new ArrayList<Point>();
		if (newObjectif != -1) {
			System.out.println("J'ai !!!");
			liste.add(todo.get(newObjectif));
		} else {
			System.out.println("Fail !!");
			liste.add(new Point(1100, 1100));
		}
		return new DeplacementTask(this.asserv, this.rouge, liste, this);
	}
	
	public static void mainFuu(String[] args) {
		//System.out.println("Atteint");
		try {
			Ia ia = new Ia();
			System.out.println(ia.getPosition());
			DetectionSRF detectionGauche = new DetectionSRF(0xE4, 30, 30);
			DetectionSRF detectionDroite = new DetectionSRF(0xE8, 30, 30);
			ia.asserv.gotoPosition(1000, 0, false);
			while (true) {
				if (detectionDroite.doitStopper() || detectionGauche.doitStopper()) {
					ia.asserv.halt();System.out.println("STOOOOOOOOOOOOOOOOOOOOOP");
					System.exit(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		final Ia ia = new Ia();

		// On initialise le chrono
		Chrono chrono = new Chrono(89 * 1000);

		System.out.println("Attente enlevage tirette");
		// On attend de virer la tirette
		while (ia.tirette.isLow());
		
		ia.rouge = ia.selecteurCouleur.isHigh();
		System.out.println("couleur isHigh = "+ia.selecteurCouleur.isHigh()+" - rouge = "+ia.rouge);

		System.out.println("Callage bordure");
		// On lance le callage bordure
		ia.asserv.calageBordure(!ia.rouge);

		System.out.println("Attente remise tirette");
		// On attend de remettre la tirette
		while (ia.tirette.isHigh());
		System.out.println("Attente enlevage tirette");
		// On attend de virer la tirette
		while (ia.tirette.isLow());

		System.out.println(ia.getPosition());
		
		System.out.println("Mise en position");
		int mult = ia.rouge ? 1 : -1;
		ia.asserv.gotoPosition(200, mult * 1700, true);
		ia.asserv.face(200, 0, true);
		while (!ia.asserv.lastCommandFinished());
		
		System.out.println("Attente remise tirette");
		// On attend de remettre la tirette
		while (ia.tirette.isHigh());
		
		// On initialise les objectifs
		ia.initObjectif();
		
		// On fait la première route
		ArrayList<Point> path = ia.getPath(0);

		System.out.println("Attente enlevage tirette pour départ");
		// On attend de virer la tirette
		while (ia.tirette.isLow());
		System.out.println("Gooo");
		
		// On démarre le chrono et la déplacementTask
		chrono.startChrono(new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				ia.asserv.halt();
				ia.tirette.close();
				ia.selecteurCouleur.close();
				ia.detection.stop();
				if (ia.deplacement != null) {
					ia.deplacement.stop();
				}
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
		
		while(true);

	}
	
	public void initObjectif() {
		int mult = this.rouge ? 1 : -1;
		this.objectifs.add(new Point(1280, mult * 1400)); // Fresques
		this.objectifs.add(new Point(750, mult * 1500)); // Mamouth
		this.objectifs.add(new Point(400, mult * 1200)); // Feu extérieur
		this.objectifs.add(new Point(1100, mult * 400)); // Feu bas
		this.objectifs.add(new Point(700, mult * 900)); // Foyer
	}
	
	public ArrayList<Point> getPath(int cas) {
		int mult = this.rouge ? 1 : -1;
		switch (cas) {
			case 0: // Feu sur ligne noir et fresque
				ArrayList<Point> path = new ArrayList<Point>();
				path.add(new Point(200, mult * 1400));
				path.add(new Point(1280, mult * 1400));
				return path;
		}
		return new ArrayList<Point>();
	}

}
