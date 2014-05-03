package ia.nancyborg2013;

import java.io.IOException;

import api.controllers.qik.Qik2s9v1;

public class MotorTask extends Task {
	
	private Qik2s9v1 qik;
	private GpioCodeur codeur;
	private int targetValue;
	private int speed;
	private int i;
	
	
	public MotorTask(Qik2s9v1 qik, int i, GpioCodeur codeur, int targetValue, int speed) {
		this.qik = qik;
		this.i = i;
		this.codeur = codeur;
		this.speed = speed;
		this.targetValue = targetValue;
	}

	@Override
	public void run() {
		current = true;
		try {
			if (i == 0)
				qik.setM0Speed(speed);
			else
				qik.setM1Speed(speed);
			
			codeur.reset();
			while(targetValue!=codeur.getValue()) {
				System.out.println("valeur codeur : "+codeur.getValue());
				Thread.sleep(50);
			}
			
			if (i == 0)
				qik.setM0Speed(0);
			else
				qik.setM1Speed(0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		notifyEnd();
	}

	@Override
	public void manageDetection(boolean av, boolean ar) {
		// TODO Auto-generated method stub
		
	}
}
