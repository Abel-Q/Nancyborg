package ia.nancyborg2013;

import java.io.IOException;

import api.controllers.PololuMaestro;
import api.gpio.Gpio;

public class ClipTask extends Task {
	
	private PololuMaestro pololu;
	private int id;
	private boolean close;
	private int value;
	private Gpio switchGpio;

	public ClipTask(PololuMaestro pololu, int id, boolean close, Gpio switchGpio) {
		this.pololu = pololu;
		this.id = id;
		this.close = close;
		this.switchGpio = switchGpio;
		value = 1200;
	}

	@Override
	public void run() {
		current = true;
		try {
			while(!switchGpio.isLow())
			{
				value+=10;
				pololu.setTargetMs(id, value);
				Thread.sleep(10);
			}
			value+=20;
			pololu.setTargetMs(id, value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void manageDetection(boolean av, boolean ar) {
		// TODO Auto-generated method stub
		
	}

}
