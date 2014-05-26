package navigation;

public class Navigation2014 extends Navigation {

	@Override
	protected void initZonesInterdites() {
		// On interdit les bords de la table pour que le robot ne se coince pas sur une bordure comme un con
		for (int i = 17; i <= 283; i++) {
			this.zonesInterdites.add(new Point(i, 17));
			this.zonesInterdites.add(new Point(i, 183));
		}
		for (int i = 17; i <= 183; i++) {
			this.zonesInterdites.add(new Point(17, i));
			this.zonesInterdites.add(new Point(283, i));
		}
		
		// On bloque les quarts de cercle de dépose dans les coins (on bloque un carré, c'est moins chiant)
		for (int i = 0; i <= 42; i++) {
			this.zonesInterdites.add(new Point(i, 42));
			this.zonesInterdites.add(new Point(42, i));
			this.zonesInterdites.add(new Point(258+i, 42));
			this.zonesInterdites.add(new Point(258, i));
		}
		
		// On bloque le cercle central (encore un carré)
		for (int i = 0; i <= 64; i++) {
			this.zonesInterdites.add(new Point(118+i, 73));
			this.zonesInterdites.add(new Point(118, 73+i));
			this.zonesInterdites.add(new Point(118+i, 137));
			this.zonesInterdites.add(new Point(182, 73+i));
		}
		
		// On bloque les dépôts de fruits
		for (int i = 0; i <= 104; i++) {
			this.zonesInterdites.add(new Point(23+i, 153));
			this.zonesInterdites.add(new Point(173+i, 153));
		}
		for (int i = 0; i <= 47; i++) {
			this.zonesInterdites.add(new Point(23, 200-i));
			this.zonesInterdites.add(new Point(127, 200-i));
			this.zonesInterdites.add(new Point(173, 200-i));
			this.zonesInterdites.add(new Point(277, 200-i));
		}
		
		// On remplit DStar
		for (Point p : this.zonesInterdites) {
			this.dStar.updateCell(p.getX(), p.getY(), -1);
		}
	}

	@Override
	protected void setZonesInterditesMobiles(int x, int y) {
		for (int i = 0; i <= 72; i++) {
			this.zonesInterditesMobiles.add(new Point(x-36, y-36+i));
			this.zonesInterditesMobiles.add(new Point(x-36+i, y-36));
			this.zonesInterditesMobiles.add(new Point(x+36, y-36+i));
			this.zonesInterditesMobiles.add(new Point(x-36+i, y+36));
		}
	}
	
	@Override
	public Point[] getExtremeZoneInterdite(Point adversaire) {
		Point[] res = new Point[2];
		res[0] = new Point(adversaire.getX()-36, adversaire.getY()-36);
		res[1] = new Point(adversaire.getX()+36, adversaire.getY()+36);
		return res;
	}

	@Override
	protected void initListeObjectifs() {
		// TODO à remplir
		this.objectifs.add(new Point(100, 100));
	}

}
