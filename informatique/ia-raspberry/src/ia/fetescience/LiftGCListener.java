package ia.fetescience;

import java.io.IOException;

import api.controllers.gc.GameCubeAdapter;
import api.controllers.gc.GameCubeRemote;
import api.controllers.gc.GameCubeRemote.Button;
import api.controllers.qik.Qik2s9v1;

public class LiftGCListener extends GameCubeAdapter {
	private GameCubeRemote gc;
	private Qik2s9v1 qik;

	public LiftGCListener(Qik2s9v1 qik, GameCubeRemote gc) {
		this.gc = gc;
		this.qik = qik;
	}
	
	@Override
	public void buttonStateChanged(Button button, boolean pressed) {
		try {
			if (button == Button.D_UP) {
				qik.setM1Speed(pressed ? -127 : 0);
			} else if (button == Button.D_DOWN) {
				qik.setM1Speed(pressed ? 127 : 0);
			} else if (button == Button.L) {
				qik.setM0Speed(pressed ? -50 : 0);
			} else if (button == Button.R) {
				qik.setM0Speed(pressed ? 50 : 0);
			}
		} catch (IOException osef) {
			
		}
	} 
}
