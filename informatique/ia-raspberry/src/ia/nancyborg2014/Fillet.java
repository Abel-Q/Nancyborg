package ia.nancyborg2014;

import fr.nancyborg.ax12.AX12Linux;

public class Fillet {
	private Ia ia;
	private AX12Linux ax;
	
	public Fillet(Ia ia) {
		this.ia = ia;
		this.ax = new AX12Linux("/dev/ttyACM0", 8, 115200);
	}
	
	public void lancer() {
		this.ax.setGoalPosition(0, true);
		this.ax.setGoalPosition(50, true);
	}
}
