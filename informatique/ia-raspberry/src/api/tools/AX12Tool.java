package api.tools;

import api.controllers.AX12;

public class AX12Tool {
	public static void main(String[] args) {
		AX12 ax = new AX12("/dev/ttyAMA0", 1, 115200);
		System.out.println("wait...");
		ax.pingWait();
		System.out.println("ok");

		for (;;) {
			System.out.println("0...");
			ax.setGoalPosition(0, true);

			System.out.println("300...");
			ax.setGoalPosition(300, true);

			System.out.println("pos: " + ax.getPresentPosition());
		}
	}
}
