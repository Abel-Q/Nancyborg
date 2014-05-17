package ia.nancyborg2013;

import java.io.IOException;

import api.gpio.Gpio;

public class GpioCodeur {
	private int value;
	private Gpio gpio;
	
	public GpioCodeur(int gpio) {
		value = 0;
		try {
			this.gpio = new Gpio(gpio, true, Gpio.PULL_UP);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					boolean high = false;
					
					while(true) {
						boolean highTmp = GpioCodeur.this.gpio.isHigh();
						if(high != highTmp) {
							
							synchronized(this)
							{
								if(highTmp) ++value;
							}
							
							System.out.println("Tic");
						}
						high = highTmp;
					}
				}
			});
			
			thread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized int getValue() {
		return value;
	}
	
	public synchronized void reset() {
		value = 0;
	}
}
