#include <mbed.h>

#include "PololuQik2/PololuQik2.h"
#include "SRF08/SRF08.h"

#include "main.h"

/* ################# Informations générales sur le robot ############ */
/* 
 * Si on se met dans le sens de l'avancer du robot
 * 		qik.setMotor0Speentrôle qui contrôle le moteur droite
 *  	qik.setMotor1Speed qui contrôle le moteur gauche
 * 
 * 		qikP.setMotor1Speed qui contrôle le moteur de la porte
 * 		Pour actionner le moteur il faut mettre une valeur positive pour 
 * le monter et une valeur négative pour le descendre mais attention
 * si on a fait descendre entièrement le clavier il se peut 
 * que ce soit inversé
 * 		
 * 		le moteur de la porte est sur le canal 1 de la QIK
 */
/* ################################################################## */

/* ##################   DECLARATION DES INPUTS ###################### */
DigitalIn Tirette(TIRETTE_PIN);
DigitalIn ModeMaintenance(MODE_PIN);

/*
 * Si détection : 1
 * sinon : 0
 */
DigitalIn capteurDroit(CAPTEUR_DROIT);
DigitalIn capteurGauche(CAPTEUR_GAUCHE);

/* ################################################################## */

/* ###################  DECLARATION DES OUTPUTS ##################### */
DigitalOut ledGauche(LED_GAUCHE);
DigitalOut ledDroite(LED_DROITE);

DigitalOut reset_qik(RESET_QIK);
DigitalOut error_qik(ERROR_QIK);

DigitalOut reset_qik_p(RESET_QIK_P);
DigitalOut error_qik_p(ERROR_QIK_P);

/* ################################################################## */

/* ###################  DECLARATION objets QIK ###################### */
PololuQik2 qik(TX_QIK,RX_QIK,RESET_QIK,ERROR_QIK,NULL,true);
PololuQik2 qikP(TX_QIK_P,RX_QIK_P,RESET_QIK_P,ERROR_QIK_P,NULL,true);

/* ################################################################## */

/* ##############  DECLARATION objets capteurs ###################### */
SRF08 ultraArr(SDA, SCL, 0xE2); //SRF08 ranging module 1 capteur arrière
SRF08 ultraG(SDA, SCL, 0xE4); //SRF08 ranging module 2 capteur gauche
SRF08 ultraD(SDA, SCL, 0xE6); //SRF08 ranging module 3 capteur droit

/* ################################################################## */

/* ##########  DECLARATION du timer pour la fin de jeu ############## */
Timer timeEnd;
Timer timeOut;
/* ################################################################## */

int distanceGauche[TAILLE_MAX];
int distanceDroit[TAILLE_MAX];;


int main()
{   
	int cpt = 0;
	
    printf("\r\n");
    printf("\r\n");
    printf("\r\n");
    printf("\rDebut de la fonction main\n");

	for(cpt=0;cpt<TAILLE_MAX;cpt++)
	{
		distanceGauche[cpt] = 0;
		distanceDroit[cpt] = 0;
	}

    init();

	printf("\rfirmware version de la qik pour la porte est %d\n", qikP.getFirmwareVersion());
	

	if(!ModeMaintenance)
    {
        printf("\rJe ne suis pas en maintenance\n");
          
        /* 
         * On rempli une première fois les tableaux de distance
         * avec des valeurs non fictives
         */
        for(cpt=0;cpt<TAILLE_MAX;cpt++)
		{
			ultraG.startRanging();
			while (!ultraG.rangingFinished()) wait(0.01);
			distanceGauche[cpt] = ultraG.getRange();
			
			ultraD.startRanging();
			while (!ultraD.rangingFinished()) wait(0.01);
			distanceDroit[cpt] = ultraD.getRange();
		}
		printf("\rAttente de tirette\n");
        while(Tirette){} // Boucle d'attente qu'on est tiré sur la tirette
        timeEnd.start();
        
        /*
         * On ouvre le clavier
         */
        actionClavier(40,5);
        
        /*
         * On part du coin et on avance tant que le robot ne trouvre pas
         * de ligne noir et on éteint les moteurs après.
         */
        while(!capteurDroit && !capteurGauche)
        {
			qik.setMotor0Speed(AMOTEURD);
			qik.setMotor1Speed(AMOTEURG);
		}
        
        qik.stopBothMotors();
        
        match();
    }
    else
    {
        printf("\rJe suis en maintenance\n");
        maintenance();
    }

    printf("\rFin du match et du code\n");
    return EXIT_SUCCESS;
}

void init()
{
    printf("\rInitialisation des QIK\n");

    qik.begin();
    qikP.begin();

    while(qik.hasFrameError() || qik.hasDataOverrunError() || qik.hasTimeoutError())
    {
        qik.begin();
    }

    while(qikP.hasFrameError() || qikP.hasDataOverrunError() || qikP.hasTimeoutError())
    {
        qikP.begin();
    }

    printf("\rInitialisation des QIK OK\n");
}

/*
 * 
 * name: match permet de gérer les actions pour le match
 * @param
 * @return
 * 
 */
void match()
{
    
    /*
     * Boucle de match donc on fait les actions tant que le temps
     * imparti timeEnd.read() < 90
     * read() pour un temps en seconde et renvoi un float
     * read_ms() pour un temps en miliseconde et renvoi un int
     * read_us() pour un temps en microseconde et renvoi un int
     */
    while(timeEnd.read() <= 89)
    {	
		printf("\rTemps de jeu : %f\n",timeEnd.read());		
		
        /*
         * On affiche sur les leds l'état des capteurs
         * 	=> allumés : pas de détection
         *  => éteint  : détection
         */
        ledGauche = !capteurGauche;
        ledDroite = !capteurDroit;
         
		ultraG.startRanging();
		while (!ultraG.rangingFinished()) wait(0.01);
		remplirTab(distanceGauche,ultraG.getRange());
			
		ultraD.startRanging();
		while (!ultraD.rangingFinished()) wait(0.01);
		remplirTab(distanceDroit,ultraD.getRange());

        while(moyenne(distanceGauche) < DISTANCE_CAPTEUR && moyenne(distanceDroit) < DISTANCE_CAPTEUR && timeOut.read() < 20)
        {
			timeOut.start();
			
            qik.stopBothMotors(); 
            
            ultraG.startRanging();
			while (!ultraG.rangingFinished()) wait(0.01);
			remplirTab(distanceGauche,ultraG.getRange());
			
			ultraD.startRanging();
			while (!ultraD.rangingFinished()) wait(0.01);
			remplirTab(distanceDroit,ultraD.getRange());
        }        
		avancer();       
    }

	qikP.setMotor1Speed(-20);
	wait(5);
	
	qikP.setMotor1Speed(0);
    qik.stopBothMotors(); 
}

/*
 * name: actionClavier
 * @param
 * @return
 */
void actionClavier(int vitesse, int tempsAttente)
{
	if(vitesse > -127 && vitesse < 127)
	{
		qikP.setMotor1Speed(vitesse);
		wait(tempsAttente);
		qikP.setMotor1Speed(0);
	}
}

void avancer()
{
	printf("\rje suis dans la fonction avancer\n");
	
	qik.setMotor0Speed(AMOTEURD);
    qik.setMotor1Speed(AMOTEURG);
	
	if(!capteurDroit && !capteurGauche)
	{
		qik.setMotor0Speed(RMOTEURD);
		qik.setMotor1Speed(RMOTEURG);
	}
	
    if(!capteurDroit)
    {
		printf("\rje n'ai pas le capteur droit\n");
        qik.setMotor1Speed(0);
    }

    if(!capteurGauche)
    {
		printf("\rje n'ai pas le capteur gauche\n");
        qik.setMotor0Speed(0);
    }
}

void maintenance()
{
    qikP.setMotor1Speed(50);
    wait(3);
    qikP.setMotor1Speed(0);
    /* 
     * Tant qu'on est en maintenance on attend d'avoir fini
     * la réparation puis on referme le clavier
     */
    while(ModeMaintenance){}

    qikP.setMotor1Speed(-50);
    wait(3);
    
    qikP.setMotor1Speed(0);
}


int moyenne(int tab[])
{
	int calcule = 0, i = 0;
	for(i=0;i<TAILLE_MAX;i++)
	{
		calcule += tab[i];
	}
	
	return (calcule/TAILLE_MAX);
}

/*
 * 
 * name: remplirTab
 * @param: tab est le tableau dans lequel on souhaite mettre une nouvelle valeur
 * @param: nvlElt est le nouvel élement qu'on veut mettre en début de tableau
 * @return
 * 
 */
void remplirTab(int tab[], int nvlElt)
{
	int i = 0;
	
	for(i=0;i<(TAILLE_MAX-1);i++)
	{
		tab[i+1] = tab[i];
	}
	
	tab[0] = nvlElt;
}
