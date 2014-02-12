package servomoteur;

public class Pince {
	PololuMicroMaestro p;
	
	public Pince(){
		p = new PololuMicroMaestro("/dev/serial/by-id/usb-Pololu_Corporation_Pololu_Micro_Maestro_6-Servo_Controller_00026569-if00", 
					"/dev/serial/by-id/usb-Pololu_Corporation_Pololu_Micro_Maestro_6-Servo_Controller_00026569-if02", 12);
		p.setMinPosms(0,1224);// Pince AVG
		//p.setMaxPosms(1,2450);
		p.setMaxPosms(0,2142);
		//p.setMinPosms(2,928);  // Pince AVD
		p.setMinPosms(1,1025);
		p.setMaxPosms(1,1952);
		//p.setMinPosms(3,992); //Pince ARG
		p.setMinPosms(2,1118);
		p.setMaxPosms(2,2000);
		p.setMinPosms(3,847); //Pince ARD
		//p.setMaxPosms(4,2000);
		p.setMaxPosms(3,1715);
		p.setMinPosms(4,1050);
		p.setMaxPosms(4,1740);
	}
	
	public void openPinceAv(){
		System.out.println("Ouverture pince avant");
		p.setMaxPosms(0,2450);
		p.setMinPosms(1,800);
		p.setPositionms(0,p.getMaxPosms(0));
		p.setPositionms(1,p.getMinPosms(1));
	}
	
	public void openPinceAvForEject(){
		System.out.println("Ouverture pince avant pour éjection");
		p.setMaxPosms(0,2142);
		p.setMinPosms(1,1025);
		p.setPositionms(0,p.getMaxPosms(0));
		p.setPositionms(1,p.getMinPosms(1));
	}
		
	public void closePinceAv(){
		System.out.println("Fermeture pince avant");
		p.setPositionms(0,p.getMinPosms(0));
		p.setPositionms(1,p.getMaxPosms(1));
	}
	
		
	public void openPinceAr(){
		System.out.println("ouverture pince arrière");
		p.setMaxPosms(3,2000);
		p.setMinPosms(2,992);
		p.setPositionms(2,p.getMinPosms(2));
		p.setPositionms(3,p.getMaxPosms(3));
		
			
	}
	
	public void openPinceArForEject(){
		System.out.println("ouverture pince arrière pour éjection");
		p.setMinPosms(2,1118);
		p.setMaxPosms(3,1715);
		p.setPositionms(2,p.getMinPosms(2));
		p.setPositionms(3,p.getMaxPosms(3));
			
	}
		
	public void closePinceAr(){
		System.out.println("fermeture pince arrière");
		p.setPositionms(2,p.getMaxPosms(2));
		p.setPositionms(3,p.getMinPosms(3));
	}
	
	public void setSpeed(int speed){ // MARCHE PAS !
		p.setmaxSpeed(0,speed);
		p.setmaxSpeed(1,speed);
	}

	public void ejecter(){
		System.out.println("éjection");
		p.setPositionms(4,p.getMaxPosms(4));
	}
	
	public void fermerBenne(){
		System.out.println("fermeture benne");
		p.setPositionms(4,p.getMinPosms(4));
	}
}

