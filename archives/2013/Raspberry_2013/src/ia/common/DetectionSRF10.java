package ia.common;

import java.util.ArrayList;

import api.sensors.SRF;

/**
 * Détection de l'adversaire à l'aide de 3 SRF10, 2 à l'avant et 1 derrière.
 * Utilisation d'une moyenne glissante pour un résulat exploitable.
 * 
 * @author GaG <francois.prugniel@esail.net>
 * @version 1.0
 */

public class DetectionSRF10 {

	/**
	 * Télémètre SRF10 avant gauche
	 */
	public SRF avantGauche;
	/**
	 * Télémètre SRF10 avant droit
	 */
	public SRF avantDroit;
	/**
	 * Télémètre SRF10 arrière
	 */
	public SRF arriere;
	/**
	 * Moyenne glissante avant gauche
	 */
	public ArrayList<Integer> moyenneAvantGauche;
	/**
	 * Moyenne glissante avant droit
	 */
	public ArrayList<Integer> moyenneAvantDroit;
	/**
	 * Moyenne glissante arrière
	 */
	public ArrayList<Integer> moyenneArriere;
	/**
	 * Nombre d'échantillon maximum de la moyenne glissante
	 */
	public int echantillon;
	/**
	 * Seuil de détection en cm
	 */
	public int seuil;

	/**
	 * Constructeur du système de détection de l'adversaire
	 * 
	 * @param i2cdev Numéro de l'adaptateur I2C
	 * @param avantGauche Adresse du télémètre avant gauche
	 * @param avantDroit Adresse du télémètre avant droit
	 * @param arriere Adresse du télémètre arrière
	 * @param echantillon Nombre d'échantillon de la moyenne glissante
	 * @param seuil Seuil de détection de l'adversaire en cm
	 */
	public DetectionSRF10(int i2cdev, int avantGauche, int avantDroit, int arriere, int echantillon, int seuil) {
		this.avantGauche = new SRF(i2cdev, avantGauche);
		this.avantDroit = new SRF(i2cdev, avantDroit);
		this.arriere = new SRF(i2cdev, arriere);
		this.echantillon = echantillon;
		this.moyenneAvantGauche = new ArrayList<Integer>();
		this.moyenneAvantDroit = new ArrayList<Integer>();
		this.moyenneArriere = new ArrayList<Integer>();
		this.seuil = seuil;
	}

	/**
	 * Déclenche une mesure du télémètre avant gauche
	 */
	public int mesureAvantGauche() {
		int mesure = this.avantGauche.getCentimeters(80);
		this.moyenneAvantGauche.add(mesure);
		if (this.moyenneAvantGauche.size() > this.echantillon) {
			this.moyenneAvantGauche.remove(0);
		}
		return mesure;
	}

	/**
	 * Déclenche une mesure du télémètre avant droit
	 */
	public int mesureAvantDroit() {
		int mesure = this.avantDroit.getCentimeters(80);
		this.moyenneAvantDroit.add(mesure);
		if (this.moyenneAvantDroit.size() > this.echantillon) {
			this.moyenneAvantDroit.remove(0);
		}
		return mesure;
	}

	/**
	 * Déclenche une mesure du télémètre arrière
	 */
	public int mesureArriere() {
		int mesure = this.arriere.getCentimeters(80);
		this.moyenneArriere.add(mesure);
		if (this.moyenneArriere.size() > this.echantillon) {
			this.moyenneArriere.remove(0);
		}
		return mesure;
	}

	/**
	 * Vérifie la présence d'un adversaire à l'avant
	 * 
	 * @return Y-a-til un robot devant nous ?
	 */
	public boolean detectionAvant() {
		int mesureG = this.mesureAvantGauche();
		int mesureD = this.mesureAvantDroit();
		int sommeGauche = 0;
		for (int i : moyenneAvantGauche) {
			sommeGauche += i;
		}
		int sommeDroit = 0;
		for (int i : moyenneAvantDroit) {
			sommeDroit += i;
		}
		System.out.println("Gauche = " + mesureG + " - Droit = " + mesureD);
		return (sommeGauche / this.echantillon <= this.seuil) || (sommeDroit / this.echantillon <= this.seuil);
	}

	/**
	 * Vérifie la présence d'un adversaire à l'arrière
	 * 
	 * @return Y-a-til un robot derrière nous ?
	 */
	public boolean detectionArriere() {
		int mesure = this.mesureArriere();
		System.out.println("Arriere = " + mesure);
		int sommeArriere = 0;
		for (int i : moyenneArriere) {
			sommeArriere += i;
		}
		return (sommeArriere / this.echantillon >= this.seuil);
	}

	/**
	 * @return the avantGauche
	 */
	public SRF getAvantGauche() {
		return avantGauche;
	}

	/**
	 * @param avantGauche the avantGauche to set
	 */
	public void setAvantGauche(SRF avantGauche) {
		this.avantGauche = avantGauche;
	}

	/**
	 * @return the avantDroit
	 */
	public SRF getAvantDroit() {
		return avantDroit;
	}

	/**
	 * @param avantDroit the avantDroit to set
	 */
	public void setAvantDroit(SRF avantDroit) {
		this.avantDroit = avantDroit;
	}

	/**
	 * @return the arriere
	 */
	public SRF getArriere() {
		return arriere;
	}

	/**
	 * @param arriere the arriere to set
	 */
	public void setArriere(SRF arriere) {
		this.arriere = arriere;
	}
}
