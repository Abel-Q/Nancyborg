package navigation;

import java.util.ArrayList;

/**
 * Classe abstraite gérant la navigation du robot, il suffit de l'étendre chaque année en remplissant les méthodes qui vont bien
 * @author fprugnie
 *
 */
public abstract class Navigation {
	
	protected DStarLite dStar;
	protected ArrayList<Point> zonesInterdites;
	protected ArrayList<Point> zonesInterditesMobiles;
	
	public Navigation() {
		this.dStar = new DStarLite();
		this.zonesInterdites = new ArrayList<Point>();
		this.zonesInterditesMobiles = new ArrayList<Point>();
		
		this.initZonesInterdites();
	}
	
	/**
	 * Contient les zones interdites fixes
	 * Attention à bien prendre en compte la largeur du robot dans vos calculs !!
	 */
	protected abstract void initZonesInterdites();
	
	public void debugZoneInterdites() {
		System.out.println("================= Debug zones interdites ===================");
		for (Point state : this.zonesInterdites) {
			System.out.println(state.x+";"+state.y);
		}
		System.out.println("================= Fin debug zones interdites ===================");
	}
	
	public void debugZoneInterditesMobiles() {
		System.out.println("================= Debug zones interdites mobiles ===================");
		for (Point state : this.zonesInterditesMobiles) {
			System.out.println(state.x+";"+state.y);
		}
		System.out.println("================= Fin debug zones interdites mobiles ===================");
	}
	
	public class Point {
		public int x, y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

}
