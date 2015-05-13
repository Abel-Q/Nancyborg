package ia.nancyborg2015;

import api.asserv.Asserv;
import api.chrono.Chrono;
import api.communication.Serial;
import api.controllers.PololuMaestro;
import ia.common.DeplacementTask;
import ia.common.DetectionRPLidar;
import navigation.Navigation2014;
import navigation.Point;
import org.mbed.RPC.*;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.TimerTask;

public class Ia {
	public Asserv asserv;
	Navigation2014 nav;

	MbedRPC rpc;
	Tirette tirette;
    SelecteurCouleur selecteurCouleur;
	PololuMaestro maestro;
	RPCVariable<Float> consigneEtage;
	RPCVariable<Float> positionEtage;
    ArrayList<Point> objectifs;
    ArrayList<Point> objectifsAtteints;
    Point objectifCourant;
    DeplacementTask deplacement;
	DetectionRPLidar rplidar;

	public TeamColor teamColor;
	private int ymult;

	public enum TeamColor {
		GREEN,
		YELLOW
	}

	public Ia() {
        while (true) {
            try {
                // On initialise l'asservissement
	            System.out.println("****** Init asserv");
	            asserv = new Asserv("/dev/serial/by-id/usb-MBED_MBED_CMSIS-DAP_101068a5cbdd92814f89f87e9a3fcdbac7ba-if01");

	            System.out.println("****** Init MBED IO");
	            // On initialise la MBED pour les IO et l'étage
                rpc = new SerialMbedRPC(FileSystems.getDefault().getPath("/dev/serial/by-id/usb-MBED_MBED_CMSIS-DAP_1010b17ca0c1d1b67081b01c87816f0123b1-if01").toRealPath().toString(), 115200);

	            System.out.println("****** Init maestro");
	            maestro = new PololuMaestro(new Serial("/dev/serial/by-id/usb-Pololu_Corporation_Pololu_Micro_Maestro_6-Servo_Controller_00046907-if00", 115200)); // if02

	            System.out.println("****** Init GPIO");
	            tirette = new Tirette(new DigitalIn(rpc, MbedRPC.p27), new DigitalOut(rpc, MbedRPC.p28));
                selecteurCouleur  = new SelecteurCouleur(new DigitalIn(rpc, MbedRPC.p29), new DigitalOut(rpc, MbedRPC.p30));

	            System.out.println("****** Init moteur etage");
	            consigneEtage = new RPCVariable<>(rpc, "SetPoint");
	            positionEtage = new RPCVariable<>(rpc, "Position");

	            System.out.println("****** Init RPLidar");

	            rplidar = new DetectionRPLidar(this,
			            FileSystems.getDefault().getPath("/dev/serial/by-id/usb-Silicon_Labs_CP2102_USB_to_UART_Bridge_Controller_0001-if00-port0").toRealPath().toString(), 115200);

	            System.out.println("******* ALL INIT DONE");
	            objectifsAtteints = new ArrayList<Point>();
                objectifs = new ArrayList<Point>();
                return;
                //nav = new Navigation2014();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Nouvelle tentative d'init dans 3 secondes...");
	            sleep(3000);
            }
        }
	}

	public Point getPosition() {
		return asserv.getCurrentPosition();
	}

	/*
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
		sleep(200);
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
		System.out.println("Temps avant d'envoyer halt : " + (System.currentTimeMillis() - time) + "ms");
		sleep(500);

		// On reset l'arret d'urgence de l'asserv
		this.asserv.resetHalt();

		System.out.println("fini");
		// On recalcul l'itinéraire ou on trouve un autre objectif
		System.out.println("Nouveau calcul");
		boolean goalReachable = this.nav.obstacleMobile(adversaire, this.asserv.getCurrentPosition());
		if (goalReachable) {
			this.deplacement = new DeplacementTask(this, this.nav.getCommandeAsserv());
		} else {
			System.out.println("Changement d'objectif");
			this.deplacement = this.nouvelObjectif();
		}
		System.out.println("Fin du calcul : " + (System.currentTimeMillis() - time) + "ms");

		// On se met en route ou si l'on n'a plus d'objectif, on attend et on recommence
		if (this.deplacement != null) {
			this.deplacement.start();
		} else {
			this.detectionAdversaire(adversaire, time);
		}
	}
*/
	// Objectif atteint
	public void objectifAtteint(Point objectif) {
/*
		System.out.println("Je suis arrivé : "+objectif);
		System.out.println("Kill de déplacement");
		this.deplacement = null;
		
		// On coupe la détection, le déplacement est déjà fini et on note l'objectif atteint
		System.out.println("Stop détection");
		this.detection.setDetect(false);
		if (!objectif.equals(new Point(1100, this.ymult * 900))) {
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
				this.asserv.gotoPosition(1280, this.ymult * 150, true);
				this.asserv.go(40, false);
				sleep(1000);
				this.asserv.halt();
				this.asserv.resetHalt();
				this.asserv.go(-550, false);
				while (!this.asserv.lastCommandFinished()) {
					try {
						if (this.capteurArriere.doitStopper()) {
							System.out.println("Stop fresque 2");
							this.asserv.halt();
							sleep(200);
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
					this.asserv.turn(5 * this.ymult, true);
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
				break;
			case 2:
				// Feu extérieur
				System.out.println("Feu extérieur : éteind moi !!!");
				this.asserv.face(400, this.ymult * 2000, true);
				this.asserv.go(300, false);
				while (!this.asserv.lastCommandFinished()) {
					try {
						if (!this.teamColor == TeamColor.GREEN && this.detection.getCapteurDroit().doitStopper() || (this.teamColor == TeamColor.GREEN && this.detection.getCapteurGauche().doitStopper())) {
							System.out.println("Stop feu extérieur 1");
							this.asserv.halt();
							sleep(200);
							this.asserv.reset();
							while (!(!this.teamColor == TeamColor.GREEN && this.detection.getCapteurDroit().peutRepartir()) || !(this.teamColor == TeamColor.GREEN && this.detection.getCapteurGauche().peutRepartir()));
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
							sleep(200);
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
				this.asserv.face(0, this.ymult * 1600, true);
				this.asserv.go(300, true);
				while (!this.asserv.lastCommandFinished()) {
					try {
						if ((this.teamColor == TeamColor.GREEN && this.detection.getCapteurDroit().doitStopper()) || (!this.teamColor == TeamColor.GREEN && this.detection.getCapteurGauche().doitStopper())) {
							System.out.println("Stop feu bas 1");
							this.asserv.halt();
							sleep(200);
							this.asserv.reset();
							while (!(this.teamColor == TeamColor.GREEN && this.detection.getCapteurDroit().peutRepartir()) || !(!this.teamColor == TeamColor.GREEN && this.detection.getCapteurGauche().peutRepartir()));
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
							sleep(200);
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
		*/
	}
/*
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
			int ymin = this.ymult * 1300;
			int ymax = this.ymult * 600;
			
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
			point = new Point(1100, this.ymult * 900);
			this.objectifCourant = new Point(0, 0); // a retirer demain si tout pète
		}
		liste.add(point);
		return new DeplacementTask(this.asserv, this.teamColor == TeamColor.GREEN, liste, this);
	}
	*/

	public static void main(String[] args) throws Exception {

		System.out.println("############################################################## IA #################################################");
		final Ia ia = new Ia();
		ia.start();
	}

	public void start() throws Exception {
		// On initialise le chrono
		Chrono chrono_stop = new Chrono(91 * 1000);

		System.out.println("Attente enlevage tirette");
		// On attend de virer la tirette
		tirette.wait(true);
		this.teamColor = selecteurCouleur.getTeamColor();
		this.ymult = this.teamColor == TeamColor.GREEN ? -1 : 1;

		System.out.println("IA initialisée. Couleur : " + (this.teamColor == TeamColor.GREEN ? "vert" : "jaune"));

		// On lance le callage bordure
		System.out.println("Calage bordure");

		this.asserv.calageBordure(this.teamColor != TeamColor.GREEN);
		this.asserv.go(100, true);
		this.asserv.turn(-90 * this.ymult, true);

		System.out.println("Attente remise tirette");
		// On attend de remettre la tirette
		tirette.wait(false);

		// On initialise les objectifs
		this.initObjectif();

		// On fait la première route
		ArrayList<Point> path = this.getPath(0);
		this.objectifCourant = this.objectifs.get(0);

		// On attend de virer la tirette
		System.out.println("Attente enlevage tirette pour départ");
		tirette.wait(true);
		System.out.println("Gooo");

		chrono_stop.startChrono(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Fin du match mais on tire un coup quand même");
				asserv.halt();
				System.out.println("Fin");
				System.exit(0);
			}
		});

		// On lance la détection et le déplacement vers le premier objectif
		System.out.println("Lancement détection");
		//this.detection.start();

		System.out.println("Lancement déplacement");
		/*this.deplacement = new DeplacementTask(this, path);
		this.deplacement.start();*/
		System.out.println("Deplacement run ok");
	}

	public void sleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void initObjectif() {
		this.objectifs.add(new Point(1280, this.ymult * 600)); // Fresques
		this.objectifs.add(new Point(700, this.ymult * 600)); // Mamouth
		this.objectifs.add(new Point(400, this.ymult * 800)); // Feu extérieur
		this.objectifs.add(new Point(1100, this.ymult * 1600)); // Feu bas
		this.objectifs.add(new Point(700, this.ymult * 1100)); // Foyer
	}
	
	public ArrayList<Point> getPath(int cas) {
		switch (cas) {
			case 0: // Feu sur ligne noir et fresque
				ArrayList<Point> path = new ArrayList<Point>();
				path.add(new Point(200, this.ymult * 600));
				path.add(new Point(1280, this.ymult * 600));
				return path;
		}
		return new ArrayList<Point>();
	}
}
