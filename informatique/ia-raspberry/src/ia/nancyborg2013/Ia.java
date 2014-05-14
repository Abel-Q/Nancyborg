package ia.nancyborg2013;

import java.util.TimerTask;

import api.asserv.Asserv;
import api.chrono.Chrono;
import api.communication.Serial;
import api.controllers.PololuMaestro;
import api.controllers.qik.Qik2s9v1;
import api.gpio.Gpio;
import api.sensors.DetectionSRF;

public class Ia {
	public static void main(String[] args) throws Exception {
		
		int val = 830;
		
		// On initialise l'asservissement
		final Asserv asserv = new Asserv("/dev/serial/by-id/usb-mbed_Microcontroller_101000000000000000000002F7F04F94-if01");

		// On initialise les GPIOs
		final Gpio tirette = new Gpio(18, true, Gpio.PULL_UP);
		final Gpio selecteurCouleur = new Gpio(24, true, Gpio.PULL_UP);

		boolean rouge = false;
		
		Qik2s9v1 qik = new Qik2s9v1(new Serial("/dev/ttyAMA0", 38400));
		PololuMaestro pololu = new PololuMaestro(new Serial("/dev/serial/by-id/usb-Pololu_Corporation_Pololu_Micro_Maestro_6-Servo_Controller_00016872-if00", 115200));
		
		GpioCodeur elevatorSensor = new GpioCodeur(17);
		
		GpioCodeur codeurRotation = new GpioCodeur(4); 

		// On initialise le chrono
		Chrono chrono = new Chrono(88 * 1000);

		System.out.println("Attente mise tirette");
		
		while (tirette.isHigh()) {
			Thread.sleep(50);
		}
		
		rouge = selecteurCouleur.isHigh();
		
		
		//  On initialise la détection
		// à tester : range de  15
		// gain : ~ 8 - 12

		final DetectionSRF detAVG = new DetectionSRF(0xE2, 30, 0x1F, 40, 40);
		final DetectionSRF detAVD = new DetectionSRF(0xE4, 30, 0x1F, 40, 40);
		final DetectionSRF detAR = new DetectionSRF(0xE0, 30, 0x1F, 40, 40);
		
		Thread threadDetection = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean halted = false;
				while (true) {
					try
					{
						if (!halted && (detAVG.doitStopper() || detAVD.doitStopper())) {
							asserv.halt();
							halted = true;
							System.out.println("----------------- Arrêt ! --------------------");
						} else if (halted && detAVG.peutRepartir() && detAVD.peutRepartir()) {
							asserv.resetHalt();
							asserv.sendCommand();
							halted = false;
							System.out.println("--------------- Redémarrage ------------------");
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		System.out.println("Attente enlevage tirette");
		
		

		while (tirette.isLow()) {
			Thread.sleep(50);
		}
		
		
		System.out.println("Calage bordure");
		asserv.calageBordure(rouge);
		
		/*asserv.gotoPosition(0, 0);
		while (!asserv.lastCommandFinished())
			Thread.sleep(10);*/
		
		asserv.gotoPosition(305, rouge ? -val : val, true);
		
		asserv.face(900, rouge ? -val : val, true);

		asserv.go(-300, true);

		
		System.out.println("Attente remise tirette");
		while (tirette.isHigh()) {
			Thread.sleep(50);
		}
		
		System.out.println("Attente enlevage tirette");
		while (tirette.isLow()) {
			Thread.sleep(50);
		}
		
		chrono.startChrono(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Timer fin match");
				asserv.halt();
				System.exit(0);
			}
		});
		
		System.out.println("Gooo");
		Gpio clip1 = new Gpio(11, true, Gpio.PULL_UP);
		
		// Carré
		/*final Task t1 = new MoveTask(asserv, 1000, 0, rouge);
		Task t2 = new MoveTask(asserv, 1000, 1000, rouge);
		Task t3 = new MoveTask(asserv, 0, 1000, rouge);
		Task t4 = new MoveTask(asserv, 0, 0, rouge);
		//Task t5 = null;
		t4.setNext(t1);*/
		
		//IA homologation
		final Task t1 = new MoveTask(asserv, 400, 300, rouge);
		Task t2 = new MotorTask(qik, 1, elevatorSensor, 5, 127);
		//Task t3 = new MotorTask(qik, 0, codeurRotation, 1, 40);
		//Task t4 = new MotorTask(qik, 1, elevatorSensor, 3, 127);
		Task t5 = new StartDetectionTask(threadDetection);
		Task t6 = new MoveTask(asserv, 1500, 300, rouge);
		Task t7 = new MoveTask(asserv, 1500, 800, rouge);
		Task t8 = new MoveTask(asserv, 400, 800, rouge);
		
		/* Vraie IA 
		Task t1 = new MoveTask(asserv, 400, 800, rouge);
		Task t2 = new MoveTask(asserv, 950, 800, rouge);
		Task t3 = new MotorpeutRepartirTask(qik, elevatorSensor, 4, -127);
		Task t4 = new ClipTask(pololu, 1, true, clip1);
		Task t5 = null;
		*/
		
		t1.setNext(t2);
		t2.setNext(t5);
		//t3.setNext(t4);
		//t4.setNext(t5);
		t5.setNext(t6);
		t6.setNext(t7);
		t7.setNext(t8);

		t1.run();
		
		System.out.println("End");
	}
	
	public static void main2(String[] args) throws Exception {
		//  On initialise la détection
		final DetectionSRF detAVG = new DetectionSRF(0xE2, 30, 0x1F, 40, 40);
		final DetectionSRF detAVD = new DetectionSRF(0xE4, 30, 0x1F, 40, 40);
		final DetectionSRF detAR = new DetectionSRF(0xE0, 30, 0x1F, 40, 40);
		
		while (true) {
			//detAVG.mesure();
			detAVD.mesure();
			//detAR.mesure();
			System.out.println();
			Thread.sleep(10);
		}
	}
}
