package ia.nancyborg2014;

import java.util.ArrayList;

import navigation.Point;
import api.asserv.Asserv;

public class DeplacementTask extends Thread {

	private Asserv asserv;
	private boolean rouge;
	private ArrayList<Point> points;
	private Ia ia;
	
	public DeplacementTask(Asserv asserv, boolean rouge, ArrayList<Point> points, Ia ia) {
		this.asserv = asserv;
		this.rouge = rouge;
		this.points = points;
		this.ia = ia;
	}
	
	private void waitForFinish() {
		boolean finished = false;
		while(!finished) {
			try {
				Thread.sleep(20);
				if(asserv.lastCommandFinished()) {
					finished = true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void run() {
		System.out.println("C'est parti !");
		int mult = rouge ? 1 : -1;
		for (Point p : points) {
			this.asserv.gotoPosition(p.getX(), mult * p.getY(), false);
			this.waitForFinish();
		}
		ia.objectifAtteint(points.get(points.size()-1));
	}
}
