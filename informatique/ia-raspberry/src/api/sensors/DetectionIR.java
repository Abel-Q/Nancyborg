package api.sensors;

import java.util.concurrent.Callable;

import navigation.Point;
import api.ax12.AX12Base;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;

//TODO: - mettre a jour l'angle de notre robot
// - mettre a jour les GPIO
// - metre a jour les angles des capteurs et de la rotation

public class DetectionIR extends Thread {
	private final float[] angles = {-15.0f, 0.0f, 15.0f};
	private Point nous, adversaire;
	private float angle;
	private AX12Base ax12;

	public static double DISTANCE = 30.0;

	public DetectionIR(Point nous, float angle, Point adversaire, AX12Base ax12){
		this.nous = nous;
		this.angle = angle;
		this.adversaire = adversaire;
		this.ax12 = ax12;

		// create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput capteur0 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput capteur1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput capteur2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
        

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
			this.ax12.setGoalPosition(0.0f, true);
			this.ax12.setGoalPosition(45.0f, true);
		}
	}

	public void detected(int capteur){
		float angle = this.ax12.getPresentPosition() + this.angles[capteur] - this.angle;
		this.adversaire.setX(this.nous.getX() + (int) (Math.cos(Math.PI * angle) * DetectionIR.DISTANCE));
		this.adversaire.setY(this.nous.getY() + (int) (Math.sin(Math.PI * angle) * DetectionIR.DISTANCE));
	}
}