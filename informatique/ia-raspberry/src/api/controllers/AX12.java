package api.controllers;

import fr.nancyborg.ax12.AX12Linux;

public class AX12 extends AX12Linux {
	public AX12(String devpath, int id, int baud) {
		super(devpath, id, baud);
		//setDebug(true);
	}
}
