package ia.nancyborg2014;

import java.util.ArrayList;

import navigation.Navigation2014;

public class Ia {

	public static void main(String[] args) {
		Navigation2014 nav = new Navigation2014();
		//nav.debugZoneInterdites();
		nav.setGoal(250, 100);
		long begin = System.currentTimeMillis();
		nav.calculItineraire(20, 160);
		ArrayList<String> commandes = nav.getCommandeAsserv();
		long end = System.currentTimeMillis();
		System.out.println("================= Commandes asserv ===================");
		for (String str : commandes) {
			System.out.println(str);
		}
		System.out.println("Time: " + (end-begin) + "ms");
		System.out.println("================= Fin Commandes asserv ===================");
		
		nav.obstacleMobile(80, 150, 25, 152);
		
		begin = System.currentTimeMillis();
		nav.calculItineraire(20, 160);
		commandes = nav.getCommandeAsserv();
		end = System.currentTimeMillis();
		System.out.println("================= Commandes asserv ===================");
		for (String str : commandes) {
			System.out.println(str);
		}
		System.out.println("Time: " + (end-begin) + "ms");
		System.out.println("================= Fin Commandes asserv ===================");
	}
	
}
