package api.sensors;

import ia.nancyborg2014.Ia;

import java.util.concurrent.Callable;

import navigation.Point;
import api.ax12.AX12Base;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;

//TODO: - mettre a jour l'angle de notre robot
// - mettre a jour les GPIO
// - metre a jour les angles des capteurs et de la rotation

public class DetectionIR extends Thread {
	private float[] anglesCapteurs;
	private AX12Base ax12;
	private Ia ia; // Histoire de signaler à l'IA qu'il y a un adversaire

	public static double DISTANCE = 30.0;

	public DetectionIR(float[] anglesCapteurs, AX12Base ax12, Pin pinCapteur1, Pin pinCapteur2, Pin pinCapteur3){
		this.anglesCapteurs = anglesCapteurs;
		this.ax12 = ax12;

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

	public void run(){
		while(true){
			// TODO timer pour la rotation
			this.ax12.setGoalPosition(0.0f, true);
			this.ax12.setGoalPosition(45.0f, true);
		}
	}

	public void detected(int capteur) {
		Point nous = ia.getPosition();
		// TODO ça mélange des degrés et des radians, à fixer !!
		float angle = this.ax12.getPresentPosition() + this.anglesCapteurs[capteur] - nous.getCap();
		int x = (nous.getX() + (int) (Math.cos(Math.PI * angle) * DetectionIR.DISTANCE));
		int y = (nous.getY() + (int) (Math.sin(Math.PI * angle) * DetectionIR.DISTANCE));
		ia.detectionAdversaire(new Point(x, y));
	}
}