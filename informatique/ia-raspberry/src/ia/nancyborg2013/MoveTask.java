package ia.nancyborg2013;

import api.asserv.Asserv;

public class MoveTask extends Task {
	private int x;
	private int y;
	private Asserv asserv;
	private boolean red;
	
	public MoveTask(Asserv asserv, int x, int y, boolean red) {
		this.asserv = asserv;
		this.x = x-130;
		this.y = y-130;
		this.red = red;
	}

	@Override
	public void run() {
		current = true;
		asserv.gotoPosition(x, red?-y:y, true);
		
		notifyEnd();
	}

	@Override
	public void manageDetection(boolean av, boolean ar) {
		if(av)
		{
			System.out.println("ARRET !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			asserv.halt();
		}
		else
		{
			System.out.println("ET ON REDÉMARRE !!!!!!!!!!!!!!!!!!!!!!!!!");
			asserv.resetHalt();
			asserv.sendCommand();
		}
	}
}
