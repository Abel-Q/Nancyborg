package api.tools;


import fr.nancyborg.ax12.*;

public class AX12Tool {
	public static void main(String[] args) {
		AX12Linux ax = new AX12Linux("/dev/ttyAMA0", 1, 115200);
		System.out.println("wait...");
		ax.pingWait();
		System.out.println("ok");

		for (;;) {
			System.out.println("0...");
			ax.setGoalPosition(0, true);

			System.out.println("300...");
			ax.setGoalPosition(300, true);
		}
	}
}
