package api.canon2014;

import java.io.IOException;

import navigation.Point;
import fr.nancyborg.ax12.AX12Linux;
import ia.nancyborg2014.Ia;
import api.gpio.Gpio;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;

public class Canon {

	public enum DirectionTir {
		HAUT,
		MILIEU,
		BAS
	}
	
	private float alphaMin = 142; //en degré
	private float alphaMax = 300; //en degré
	private int hauteurLanceur = 20 * 10; //en mm
	private Ia ia;
	private Pin pinCanon;
	private AX12Linux axRotation;
	private AX12Linux axBarilet;
	private boolean collaboration = true; // Si true on collabore, sinon on tire nos 6 balles dans le tas !
	private Gpio gpio;
	private Point currentPosition;
	private Tir[] objectifs; //[0],[1],[2] pour le haut; [3],[4],[5] pour milieu et [6],[7],[8] pour bas
	private Tir centreMammouth;
	private Tir centreLanceur;
	
	public Canon(Pin pinCanon, Ia ia) throws IOException {
		this.axRotation = new AX12Linux("/dev/ttyAMA0", 2, 115200);
		this.axBarilet = new AX12Linux("/dev/ttyAMA0", 3, 115200);
		this.ia = ia;
		this.pinCanon = pinCanon;
		axRotation.setCWLimit(alphaMin); // A VERIFIER SI LE SENS EST CORRECT !!!
		axRotation.setCCWLimit(alphaMax); // A VERIFIER SI LE SENS EST CORRECT !!!
		gpio = new Gpio(pinCanon, PinMode.DIGITAL_OUTPUT);
		centreMammouth = new Tir(750, 2000, 370);
		for(int k=0;k<3;k++) {
			for(int i=0;i<3;i++) {
				objectifs[i + 3*k] = new Tir(centreMammouth.getX() + (i-1) * 10, centreMammouth.getY(), centreMammouth.getZ() + (k-1) * 10);
			}
		}
	}
	
	public void lancer(double angleLanceur) throws IOException, InterruptedException{
		this.axRotation.setGoalPosition((float) angleLanceur + alphaMin, true);
		this.axBarilet.setGoalPosition(60.0f, true);
		gpio.setHigh();
		Thread.sleep(toMillis(1));
		gpio.setLow();
		//this.axRotation.setGoalPosition(-angleLanceur, true);
		this.axRotation.setGoalPosition(alphaMin, true);
	}
	
	public long toMillis(long second) {
		return second * 1000;
	}
	
	public void tirSurMammouthCible(DirectionTir directionTir, boolean collaboration) throws IOException, InterruptedException {
		boolean rouge = ia.rouge; //si True => y >= 0
		this.currentPosition = ia.getPosition();
		this.centreLanceur.setX(currentPosition.getX());
		this.centreLanceur.setY(currentPosition.getY());
		this.centreLanceur.setZ(hauteurLanceur);
		double normeDistance;
		if(rouge) {
			if(collaboration) {
				switch (directionTir) {
				case BAS:
					for(int i=0;i<3;i++) {
						//ia.asserv.turn(angleTir(objectifs[i], currentPosition) - currentPosition.getCap(), false);
						ia.asserv.face(objectifs[i].getX(), objectifs[i].getY(), false);
						normeDistance = Math.sqrt(Math.pow((objectifs[i].getX()-currentPosition.getX()),2)+Math.pow((objectifs[i].getY()-currentPosition.getY()),2));
						lancer(Math.atan((objectifs[i].getZ()-hauteurLanceur)/normeDistance));
					}
					break;
				case HAUT:
					for(int i=6;i<9;i++) {
						ia.asserv.face(objectifs[i].getX(), objectifs[i].getY(), false);
						normeDistance = Math.sqrt(Math.pow((objectifs[i].getX()-currentPosition.getX()),2)+Math.pow((objectifs[i].getY()-currentPosition.getY()),2));
						lancer(Math.atan((objectifs[i].getZ()-hauteurLanceur)/normeDistance));
					}
					break;
				case MILIEU:
					for(int i=3;i<6;i++) {
						ia.asserv.face(objectifs[i].getX(), objectifs[i].getY(), false);
						normeDistance = Math.sqrt(Math.pow((objectifs[i].getX()-currentPosition.getX()),2)+Math.pow((objectifs[i].getY()-currentPosition.getY()),2));
						lancer(Math.atan((objectifs[i].getZ()-hauteurLanceur)/normeDistance));
					}
					break;
				default:
					break;

				}
			}
			else {
				for(int i=0;i<9;i++) {
					ia.asserv.face(objectifs[i].getX(), objectifs[i].getY(), false);
					normeDistance = Math.sqrt(Math.pow((objectifs[i].getX()-currentPosition.getX()),2)+Math.pow((objectifs[i].getY()-currentPosition.getY()),2));
					lancer(Math.atan((objectifs[i].getZ()-hauteurLanceur)/normeDistance));
				}
			}
		}
	}
	
	public double angleTir(Tir cible, Point positionRobot) {
		return Math.acos((cible.getX()-positionRobot.getX())/Math.sqrt(Math.pow((cible.getX()-positionRobot.getX()),2)+Math.pow((cible.getY()-positionRobot.getY()),2)));
	}
	
}
