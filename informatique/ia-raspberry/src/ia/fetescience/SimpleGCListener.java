package ia.fetescience;

import api.asserv.Asserv;
import api.controllers.gc.GameCubeAdapter;
import api.controllers.gc.GameCubeRemote;
import api.controllers.gc.GameCubeRemote.AnalogButton;
import api.controllers.gc.GameCubeRemote.Button;

public class SimpleGCListener extends GameCubeAdapter {
	private Asserv asserv;
	private GameCubeRemote gc;
	private boolean enabled = false;

	public SimpleGCListener(Asserv asserv, GameCubeRemote gc) {
		this.asserv = asserv;
		this.gc = gc;
	}

	@Override
	public void dataUpdated() {
		if (gc.hasChanged(Button.START) && gc.isPressed(Button.START)) {
			enabled = !enabled;
			asserv.setEnabled(enabled);

			if (enabled)
				asserv.calageBordure(true);
		}

		if (gc.hasChanged(Button.L)) {
			asserv.setMotorSpeed('G', gc.isPressed(Button.L) ? 127 : 0);
			asserv.setMotorSpeed('D', gc.isPressed(Button.L) ? 127 : 0);
		} else if (gc.hasChanged(Button.R)) {
			asserv.setMotorSpeed('G', gc.isPressed(Button.R) ? -127 : 0);
			asserv.setMotorSpeed('D', gc.isPressed(Button.R) ? -127 : 0);
		} else if (gc.hasValueChanged(AnalogButton.STICK_X) || gc.hasValueChanged(AnalogButton.STICK_Y)) {

			int x = gc.getValue(AnalogButton.STICK_X) - 119;
			int y = gc.getValue(AnalogButton.STICK_Y) - 125;

			System.out.println("Xrel = " + x + "  --  Yrel = " + y);

			if (Math.abs(x) < 10)
				x = 0;

			if (Math.abs(y) < 10)
				y = 0;

			int vitesse_base = y;
			int direction = x / 3;

			asserv.setMotorSpeed('G', Math.max(Math.min(vitesse_base + direction, 127), -127));
			asserv.setMotorSpeed('D', Math.max(Math.min(vitesse_base - direction, 127), -127));
		}
	}
}
