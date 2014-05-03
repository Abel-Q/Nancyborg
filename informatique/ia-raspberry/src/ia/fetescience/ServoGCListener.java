package ia.fetescience;

import java.io.IOException;

import api.controllers.PololuMaestro;
import api.controllers.gc.GameCubeAdapter;
import api.controllers.gc.GameCubeRemote;
import api.controllers.gc.GameCubeRemote.Button;

public class ServoGCListener extends GameCubeAdapter {
	private GameCubeRemote gc;
	private PololuMaestro pololu;
	public ServoGCListener(GameCubeRemote gc, PololuMaestro pololu) {
		this.gc = gc;
		this.pololu = pololu;
	}
	
	@Override
	public void buttonStateChanged(Button button, boolean pressed) {
		boolean start = gc.isPressed(Button.START);
		
		try {
			if (button == Button.B) {
				if (start)
					pololu.setTarget(0, 1035/4.);
				else
					pololu.setTarget(0, 1350/4.);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
