package api.sensors;

import ia.nancyborg2014.Ia;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import navigation.Point;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;

import fr.nancyborg.ax12.AX12Linux;

//TODO: - mettre a jour l'angle de notre robot
// - mettre a jour les GPIO
// - metre a jour les angles des capteurs et de la rotation

public class DetectionIR extends Thread {
	private float[] anglesCapteurs;
	private float angle0;
	private AX12Linux ax12;
	private Ia ia; // Histoire de signaler à l'IA qu'il y a un adversaire
	public double distanceDetection;

	public DetectionIR(float[] anglesCapteurs, float angle0, double distanceDetection, AX12Linux ax12, Pin pinCapteur1, Pin pinCapteur2, Pin pinCapteur3, Ia ia) {
		this.anglesCapteurs = anglesCapteurs;
		this.ax12 = ax12;
		this.distanceDetection = distanceDetection;
		this.angle0 = angle0;
		this.ia = ia;

		ax12.setCWLimit(angle0 - 45);
		ax12.setCCWLimit(angle0 + 45);

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		// provision gpio pin #02 as an input pin with its internal pull down resistor enabled
		final GpioPinDigitalInput capteur0 = gpio.provisionDigitalInputPin(pinCapteur1, PinPullResistance.PULL_DOWN);
		final GpioPinDigitalInput capteur1 = gpio.provisionDigitalInputPin(pinCapteur2, PinPullResistance.PULL_DOWN);
		final GpioPinDigitalInput capteur2 = gpio.provisionDigitalInputPin(pinCapteur3, PinPullResistance.PULL_DOWN);

		//add trigger event
		capteur0.addTrigger(new GpioCallbackTrigger(PinState.LOW, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				DetectionIR.this.detected(0);
				return null;
			}
		}));

		capteur1.addTrigger(new GpioCallbackTrigger(PinState.LOW, new Callable<Void>() {
			public Void call() throws Exception {
				DetectionIR.this.detected(1);
				return null;
			}
		}));

		capteur2.addTrigger(new GpioCallbackTrigger(PinState.LOW, new Callable<Void>() {
			public Void call() throws Exception {
				DetectionIR.this.detected(2);
				return null;
			}
		}));
	}

	public void run() {
		while (true) {
			this.ax12.setGoalPosition(this.angle0 - 45.0f, true);
			this.ax12.setGoalPosition(this.angle0 + 45.0f, true);
		}
	}

	public void detected(int capteur) {
		System.out.println("************************** detected "+capteur+" : "+this.isAlive());
		long time = System.currentTimeMillis();
		if (!this.isAlive()) {
			return;
		}

		Point nous = ia.getPosition();
		float pos = ax12.getPresentPosition();
		System.out.println("capteur = "+capteur+" - pos = " + pos+" - nous = "+nous);
		float angle = pos - angle0 + this.anglesCapteurs[capteur] - (float)nous.getCap();

		// Position de l'adversaire en mm
		int x = (nous.getX() + ((int) (Math.cos(Math.toRadians(angle)) * this.distanceDetection * 10)));
		int y = (nous.getY() + ((int) (Math.sin(Math.toRadians(angle)) * this.distanceDetection * 10)));
		System.out.println("x = "+x+" - y = "+y);
		Point adversaire = new Point((int)Math.rint((double)x/100.0), (int)Math.rint((double)y/100.0));
		System.out.println("Nous = "+nous+" - Adversaire = "+adversaire);
		//if (checkColision(adversaire)) {
			//ia.detectionAdversaire(adversaire, time);
		//}
		
	}
	
	public boolean checkColision(Point adversaire) {
		ArrayList<Point> commandes = this.ia.getCachedCommandesAsserv();
		Point[] zi = this.ia.getZoneInterdite(adversaire);
		int xmin = zi[0].getX();
		int xmax = zi[1].getX();
		int ymin = zi[0].getY();
		int ymax = zi[1].getY();
		for (int i = 1; i < commandes.size(); i++) {
			if (commandes.get(i-1).getX() == commandes.get(i).getX()) {
				if (commandes.get(i-1).getX() > xmin && commandes.get(i-1).getX() < xmax) {
					return true;
				}
			} else {
				int xa = commandes.get(i-1).getX();
				int ya = commandes.get(i-1).getY();
				int xb = commandes.get(i).getX();
				int yb = commandes.get(i).getY();
				double a = (double)(ya-yb)/(double)(xa-xb);
				double b = (double)ya-a*(double)xa;
				
				double xint1 = (ymin-b)/a;
				double xint2 = (ymin-b)/a;
				
				double yint1 = a*xmax+b;
				double yint2 = a*xmax+b;
				
				if ((xint1 > xmin  && xint1 < xmax) || (xint2 > xmin  && xint2 < xmax) ||
						(yint1 > ymin  && yint1 < ymax) || (yint2 > ymin  && yint2 < ymax)) {
					return true;
				}
			}
		}
		return false;
	}
}