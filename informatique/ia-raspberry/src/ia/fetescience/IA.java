package ia.fetescience;

import api.asserv.Asserv;
import api.communication.Serial;
import api.controllers.PololuMaestro;
import api.controllers.gc.GameCubeRemote;
import api.controllers.gc.GameCubeRemote.AnalogButton;
import api.controllers.qik.Qik2s9v1;
import api.gpio.Gpio;

public class IA {
	public static void main(String[] args) throws Exception {
		Asserv asserv = new Asserv("/dev/serial/by-id/usb-mbed_Microcontroller_101000000000000000000002F7F04F94-if01");
		Qik2s9v1 qik = new Qik2s9v1(new Serial("/dev/ttyAMA0", 38400));
		//PololuMaestro pololu = new PololuMaestro(new Serial("/dev/serial/by-id/usb-Pololu_Corporation_Pololu_Micro_Maestro_6-Servo_Controller_00046907-if00", 115200));

		try {
			asserv.setEnabled(false);

			System.out.println("Go!");
			GameCubeRemote gc = new GameCubeRemote();
			gc.addListener(new DebugGCListener());

			gc.addListener(new SimpleGCListener(asserv, gc));
			gc.addListener(new LiftGCListener(qik, gc));
			
			while (true) {
				gc.update();
				System.out.println("X = " + gc.getValue(AnalogButton.STICK_X) + " Y = " +  gc.getValue(AnalogButton.STICK_Y));
				Thread.sleep(50);
			}
			//gc.addListener(new LiftGCListener(qik, motor, gc));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		} finally {
			qik.setCoasts();
			asserv.reset();
		}
	}
}
