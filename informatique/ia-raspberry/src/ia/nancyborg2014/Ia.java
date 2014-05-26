package ia.nancyborg2014;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import navigation.Navigation;
import navigation.Navigation2014;
import navigation.Point;
import api.asserv.Asserv;
import api.chrono.Chrono;
import api.gpio.Gpio;
import api.sensors.DetectionIR;

import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

import fr.nancyborg.ax12.AX12Linux;

public class Ia {

	public Asserv asserv;
	public Gpio tirette, selecteurCouleur;
	public boolean rouge;
	public DetectionIR detection;
	public Navigation2014 nav;
	public DeplacementTask deplacement;
	public ArrayList<Point> objectifsAtteints;

	public Ia() {
		try {
			// On initialise l'asservissement
			// TODO vérifier adresse
			asserv = new Asserv("/dev/serial/by-id/usb-mbed_Microcontroller_101000000000000000000002F7F04F94-if01");

			// TODO initialisation des AX12 du canon

			// On initialise les GPIOs
			tirette = new Gpio(RaspiPin.GPIO_03, PinMode.DIGITAL_INPUT, PinPullResistance.PULL_UP); // Mise = low, Enleve = high;
			selecteurCouleur = new Gpio(RaspiPin.GPIO_02, PinMode.DIGITAL_INPUT, PinPullResistance.PULL_UP); // Rouge = high, Jaune = low
			rouge = false;
			objectifsAtteints = new ArrayList<Point>();

			// Détection de l'adversaire
			float[] anglesCapteurs = { -45.0f, 0.0f, 45.0f };
			AX12Linux ax12Detection = new AX12Linux("/dev/ttyAMA0", 1, 115200);
			// Distance capteur - balise = 56cm
			detection = new DetectionIR(anglesCapteurs, 240.0f, 56.0, ax12Detection, RaspiPin.GPIO_14, RaspiPin.GPIO_12, RaspiPin.GPIO_13, this);

			nav = new Navigation2014();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	// On a vu quelqu'un
	public void detectionAdversaire(Point adversaire) {
		System.out.println("Stooooooop");
		if (this.deplacement != null) {
			this.deplacement.stop();
		}
		this.deplacement = null;
		// On s'arrête et on attend de perdre notre inertie
		this.asserv.halt();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// On reset l'arret d'urgence de l'asserv
		this.asserv.resetHalt();

		// On recalcul l'itinéraire ou on trouve un autre objectif
		System.out.println("Nouveau calcul");
		long time = System.currentTimeMillis();
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
			this.deplacement.run();
		} else {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.nav.resetObstacleMobile();
			this.detection.run();
			this.deplacement = this.nouvelObjectif();
			if (this.deplacement != null) {
				this.deplacement.run();
			}
		}
	}

	// Objectif atteint
	public void objectifAtteint(Point objectif) {
		// On coupe la détection, le déplacement est déjà fini et on note l'objectif atteint
		this.detection.stop();
		this.objectifsAtteints.add(objectif);

		// On lance la séquence de marquage de point
		switch (this.nav.getObjectifs().indexOf(objectif)) {
		case 0:
			// On tire les lances
			break;
		}

		// On cherche un nouvel objectif et on y va
		this.deplacement = this.nouvelObjectif();
		this.detection.run();
		this.deplacement.run();
	}

	// On trouve un nouvelle objectif, en éliminant ceux déjà réalisé
	public DeplacementTask nouvelObjectif() {
		ArrayList<Point> liste = this.nav.getObjectifs();
		liste.removeAll(this.objectifsAtteints);
		for (Point point : liste) {
			this.nav.setGoal(point);
			boolean goalReachable = this.nav.calculItineraire(this.asserv.getCurrentPosition());
			if (goalReachable) {
				return new DeplacementTask(this.asserv, this.rouge, this.nav.getCommandeAsserv(), this);
			}
		}
		return null;
	}
	
	public static void mainLol(String[] args) {
		Navigation nav = new Navigation2014();
		nav.setGoal(new Point(10,10));
		System.out.println(nav.calculItineraire(new Point(3,14)));
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

		System.out.println("Mise en position");
		ia.asserv.gotoPosition(200, ia.rouge ? 1700 : -1700, true);
		ia.asserv.face(200, 0, true);
		while (!ia.asserv.lastCommandFinished());

		System.out.println("Attente remise tirette");
		// On attend de remettre la tirette
		while (ia.tirette.isHigh());

		System.out.println("Attente enlevage tirette pour départ");
		// On attend de virer la tirette
		while (ia.tirette.isLow());
		System.out.println("Gooo");

		ia.asserv.gotoPosition(300, ia.rouge ? 1200 : -1200, true);
		
		ia.nav.setGoal(ia.nav.getObjectifs().get(0));
		System.out.println("Calcul itinéraire départ");
		long time = System.currentTimeMillis();
		ia.nav.calculItineraire(ia.asserv.getCurrentPosition());
		System.out.println("Fin du calcul : "+(System.currentTimeMillis() - time)+"ms");
		
		// On démarre le chrono et la déplacementTask
		chrono.startChrono(new TimerTask() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				ia.asserv.halt();
				ia.tirette.close();
				ia.selecteurCouleur.close();
				ia.detection.stop();
				System.out.println("Fin");
				System.exit(0);
			}
		});

		// On lance la détection et le déplacement vers le premier objectif
		ia.detection.run();
		ia.deplacement = new DeplacementTask(ia.asserv, ia.rouge, ia.nav.getCommandeAsserv(), ia);
		ia.deplacement.run();

		/*
		//nav.debugZoneInterdites();
		ia.nav.setGoal(250, 100);
		long begin = System.currentTimeMillis();
		ia.nav.calculItineraire(20, 160);
		ArrayList<Point> commandes = ia.nav.getCommandeAsserv();
		long end = System.currentTimeMillis();
		System.out.println("================= Commandes asserv ===================");
		for (Point str : commandes) {
			System.out.println(str);
		}
		System.out.println("Time: " + (end-begin) + "ms");
		System.out.println("================= Fin Commandes asserv ===================");
		
		ia.nav.obstacleMobile(80, 150, 25, 152);
		
		begin = System.currentTimeMillis();
		ia.nav.calculItineraire(20, 160);
		commandes = ia.nav.getCommandeAsserv();
		end = System.currentTimeMillis();
		System.out.println("================= Commandes asserv ===================");
		for (Point str : commandes) {
			System.out.println(str);
		}
		System.out.println("Time: " + (end-begin) + "ms");
		System.out.println("================= Fin Commandes asserv ===================");*/
	}

}
