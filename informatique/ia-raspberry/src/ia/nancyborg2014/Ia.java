package ia.nancyborg2014;

import java.util.ArrayList;

import navigation.Navigation2014;

public class Ia {

	public static void main(String[] args) {
		Navigation2014 nav = new Navigation2014();
		//nav.debugZoneInterdites();
		nav.setGoal(140, 140);
		nav.calculItineraire(20, 160);
		ArrayList<String> commandes = nav.getCommandeAsserv();
		System.out.println("================= Commandes asserv ===================");
		for (String str : commandes) {
			System.out.println(str);
		}
		System.out.println("================= Fin Commandes asserv ===================");
		nav.obstacleMobile(80, 150, 25, 152);
		nav.calculItineraire(20, 160);
		commandes = nav.getCommandeAsserv();
		System.out.println("================= Commandes asserv ===================");
		for (String str : commandes) {
			System.out.println(str);
		}
		System.out.println("================= Fin Commandes asserv ===================");
	}
	
}
