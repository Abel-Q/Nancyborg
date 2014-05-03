package ia.fetescience;

import api.controllers.gc.GameCubeListener;
import api.controllers.gc.GameCubeRemote.AnalogButton;
import api.controllers.gc.GameCubeRemote.Button;

public class DebugGCListener implements GameCubeListener {

	@Override
	public void dataUpdated() {
		//System.out.println("Update!");

	}

	@Override
	public void buttonStateChanged(Button button, boolean pressed) {
		System.out.println("Bouton " + button.name() + " : " + pressed);

	}

	@Override
	public void buttonAnalogValueChanged(AnalogButton button, int value) {
		//System.out.println("Stick " + button.name() + " : " + value);

	}

}
