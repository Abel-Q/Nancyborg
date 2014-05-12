package navigation;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite gérant la navigation du robot, il suffit de l'étendre chaque année en remplissant les méthodes qui vont bien
 * @author fprugnie
 *
 */
public abstract class Navigation {
	
	protected DStarLite dStar;
	protected ArrayList<Point> zonesInterdites;
	protected ArrayList<Point> zonesInterditesMobiles;
	protected Point goal;
	
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
	
	/**
	 * Remplit la zone interdites mobiles à partir de la position de l'adversaire
	 * @param x Position en X de l'obstacle (prendre en cm l'entier le plus proche)
	 * @param y Position en Y de l'obstacle (prendre en cm l'entier le plus proche)
	 */
	protected abstract void setZonesInterditesMobiles(int x, int y);
	
	public void setGoal(int x, int y) {
		this.goal = new Point(x, y);
	}
	
	/**
	 * On vient de rencontrer un obstacle, on met à jours les zones interdites et on recalcule l'itinéraire
	 * @param obstacleX Position en X de l'obstacle (prendre en cm l'entier le plus proche)
	 * @param obstacleY Position en Y de l'obstacle (prendre en cm l'entier le plus proche)
	 * @param robotX Position en X du robot (prendre en cm l'entier le plus proche)
	 * @param robotY Position en Y du robot (prendre en cm l'entier le plus proche)
	 * @return
	 */
	public boolean obstacleMobile(int obstacleX, int obstacleY, int robotX, int robotY) {
		// On nettoie les anciennes zones interdites
		for (Point p : this.zonesInterditesMobiles) {
			this.dStar.updateCell(p.x, p.y, 1);
		}
		this.zonesInterditesMobiles.clear();
		
		// On calcule la nouvelle zone et on la stocke
		this.setZonesInterditesMobiles(obstacleX, obstacleY);
		
		// On place la nouvelle zone interdite
		for (Point p : this.zonesInterditesMobiles) {
			this.dStar.updateCell(p.x, p.y, -1);
		}
		
		return calculItineraire(robotX, robotY);
	}
	
	public boolean calculItineraire(int startX, int startY) {
		this.dStar.updateStart(startX, startY);
		this.dStar.updateGoal(this.goal.x, this.goal.y);
		return this.dStar.replan();
	}
	
	public ArrayList<String> getCommandeAsserv() {
		ArrayList<String> commandes = new ArrayList<String>();
		List<State> path = this.dStar.getPathReduced();
		
		for (State i : path) {
			commandes.add(i.x+";"+i.y);
		}
		
		return commandes;
	}
	
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
