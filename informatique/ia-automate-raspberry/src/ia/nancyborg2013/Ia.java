package ia.nancyborg2013;

import api.gpio.Gpio;

public class Ia {
	public static void main(String[] args) throws Exception {
		Gpio gpio = new Gpio(24, true, Gpio.PULL_UP);
		System.out.println("Lu : " + gpio.isHigh());
		gpio.close();
	}
}