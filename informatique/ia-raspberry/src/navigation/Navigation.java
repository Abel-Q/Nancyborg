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
	protected ArrayList<Point> objectifs;
	
	public Navigation() {
		this.dStar = new DStarLite();
		this.zonesInterdites = new ArrayList<Point>();
		this.zonesInterditesMobiles = new ArrayList<Point>();
		this.objectifs = new ArrayList<>();
		
		this.initZonesInterdites();
		this.initListeObjectifs();
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
	
	/**
	 * Remplit la liste d'objectifs
	 */
	protected abstract void initListeObjectifs();
	
	public void setGoal(int x, int y) {
		this.goal = new Point(x, y);
	}
	
	/**
	 * On vient de rencontrer un obstacle, on met à jours les zones interdites et on recalcule l'itinéraire
	 * @param obstacleX Position en X de l'obstacle (prendre en cm l'entier le plus proche)
	 * @param obstacleY Position en Y de l'obstacle (prendre en cm l'entier le plus proche)
	 * @param robotX Position en X du robot (prendre en cm l'entier le plus proche)
	 * @param robotY Position en Y du robot (prendre en cm l'entier le plus proche)
	 * @return true si l'objectif est encore atteignable, false sinon
	 */
	public boolean obstacleMobile(int obstacleX, int obstacleY, int robotX, int robotY) {
		// On nettoie les anciennes zones interdites
		for (Point p : this.zonesInterditesMobiles) {
			this.dStar.updateCell(p.getX(), p.getY(), 1);
		}
		this.zonesInterditesMobiles.clear();
		
		// On calcule la nouvelle zone et on la stocke
		this.setZonesInterditesMobiles(obstacleX, obstacleY);
		
		// On place la nouvelle zone interdite
		for (Point p : this.zonesInterditesMobiles) {
			this.dStar.updateCell(p.getX(), p.getY(), -1);
		}
		
		return calculItineraire(robotX, robotY);
	}
	
	/**
	 * On déclenche un calcul de l'itinaire à suivre
	 * @param startX Position en X du robot (prendre en cm l'entier le plus proche)
	 * @param startY Position en Y du robot (prendre en cm l'entier le plus proche)
	 * @return true si l'objectif est atteignable, false sinon
	 */
	public boolean calculItineraire(int startX, int startY) {
		this.dStar.updateStart(startX, startY);
		this.dStar.updateGoal(this.goal.getX(), this.goal.getY());
		return this.dStar.replan();
	}
	
	/**
	 * Permet de récupérer la liste des positions à transmettre à l'asservissement
	 * @return ArrayList<Point> des positions du parcours
	 */
	public ArrayList<Point> getCommandeAsserv() {
		ArrayList<Point> commandes = new ArrayList<Point>();
		List<State> path = this.dStar.getPathReduced();
		
		for (State i : path) {
			commandes.add(new Point(i.x,i.y));
		}
		
		return commandes;
		
		// Méthode JBG, semble moins legèrement moins bonne mais plus propre
		//return this.dStar.getRoute();
	}
	
	public void debugZoneInterdites() {
		System.out.println("================= Debug zones interdites ===================");
		for (Point state : this.zonesInterdites) {
			System.out.println(state.getX()+";"+state.getY());
		}
		System.out.println("================= Fin debug zones interdites ===================");
	}
	
	public void debugZoneInterditesMobiles() {
		System.out.println("================= Debug zones interdites mobiles ===================");
		for (Point state : this.zonesInterditesMobiles) {
			System.out.println(state.getX()+";"+state.getY());
		}
		System.out.println("================= Fin debug zones interdites mobiles ===================");
	}

}
