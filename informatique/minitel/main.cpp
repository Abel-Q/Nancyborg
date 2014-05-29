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
DigitalIn Couleur(COULEUR_PIN);

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
SRF08 ultraM(SDA, SCL, 0xE0); //SRF08 ranging module 4 capteur milieu petit

/* ################################################################## */

/* ##########  DECLARATION du timer pour la fin de jeu ############## */
Timer timeEnd;
Timer timeOut;
Timer timeAngle;
/* ################################################################## */

/* #############  DECLARATION des variables globales ################ */
int distanceGauche[TAILLE_MAX];
int distanceDroit[TAILLE_MAX];
int distanceMilieu[TAILLE_MAX];
int distanceArr[TAILLE_MAX];
int cptPosition = 0;
int angleGo = 0;
/* ################################################################## */

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
        distanceMilieu[cpt] = 0;
        distanceArr[cpt] = 0;
    }

    init();

    printf("\rfirmware version de la qik pour la porte est %d\n", qikP.getFirmwareVersion());

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
        
        ultraM.startRanging();
        while (!ultraM.rangingFinished()) wait(0.01);
        distanceMilieu[cpt] = ultraM.getRange();
        
        ultraArr.startRanging();
        while (!ultraArr.rangingFinished()) wait(0.01);
        distanceArr[cpt] = ultraArr.getRange();
    }

    qikP.setMotor1Speed(-50);
    wait(3);
    qikP.setMotor1Speed(50);
    wait(3);
    qikP.setMotor1Speed(0);

    printf("\rAttente de tirette\n");
    while(Tirette){} // Boucle d'attente qu'on est tiré sur la tirette
    timeEnd.start();

    /*
     * On part du coin et on avance tant que le robot ne trouvre pas
     * de ligne noir et on éteint les moteurs après.
     */

    qik.setMotor0Speed(AMOTEURD);
    qik.setMotor1Speed(AMOTEURG);
    printf("\rAttente de %f secondes pour rejoindre la ligne\n", HAND_TROLL_TIMEOUT);
    wait(HAND_TROLL_TIMEOUT);

    qik.stopBothMotors();

    match();

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
	int hasTurned = 0;
	printf("\r%f : Début de match !\n", timeEnd.read());
	
	// Lancement timer avant mesure de l'angle
	timeAngle.start();
	
    /*
     * Boucle de match donc on fait les actions tant que le temps
     * imparti timeEnd.read() < 90
     * read() pour un temps en seconde et renvoi un float
     * read_ms() pour un temps en miliseconde et renvoi un int
     * read_us() pour un temps en microseconde et renvoi un int
     */
    while(timeEnd.read() < 90.0)
    {	

        /*
         * On affiche sur les leds l'état des capteurs
         * 	=> allumés : pas de détection
         *  => éteint  : détection
         */
        ledGauche = !capteurGauche;
        ledDroite = !capteurDroit;
		
		/*
		 * Lance détection ennemi
		 */
        ultraG.startRanging();
        while (!ultraG.rangingFinished()) wait(0.01);
        remplirTab(distanceGauche,ultraG.getRange());

        ultraD.startRanging();
        while (!ultraD.rangingFinished()) wait(0.01);
        remplirTab(distanceDroit,ultraD.getRange());

        /*
         * Gestion de la détection des ennemis
         */
		if(moyenne(distanceGauche) < DISTANCE_CAPTEUR || moyenne(distanceDroit) < DISTANCE_CAPTEUR)
		{
			printf("\r%f : Ennemi détecté :o\n", timeEnd.read());
			qik.stopBothMotors();
			continue;
		}
		
		// Gestion de la pose de la fresque
		if(hasTurned)
        {
			// Lancement détection mur à scratch
			ultraM.startRanging();
			while (!ultraM.rangingFinished()) wait(0.01);
			remplirTab(distanceMilieu,ultraM.getRange());
			
            if(moyenne(distanceMilieu) < DISTANCE_FRESQUE)
            {
				printf("\r%f : Fresque détectée, on fait l'accrochage =)\n", timeEnd.read());
                /*
                 * Dépose fresque
                 */
                qikP.setMotor1Speed(-20);
                wait(2);
                qikP.setMotor1Speed(-1);
                wait(1);
                qikP.setMotor1Speed(0);
                
				// On recule, mettez les feux de recul et la sirène !
				printf("\r%f : Mettez les feux de recul et la sirène !\n", timeEnd.read());
                int count = 0;
                while(count < 5) {					
					ultraArr.startRanging();
					while (!ultraArr.rangingFinished()) wait(0.01);
					remplirTab(distanceArr,ultraArr.getRange());
                
                    if(ultraArr.getRange() < DISTANCE_CAPTEUR)
                    {
						printf("\r%f : Ennemi détecté :o\n", timeEnd.read());			
                        qik.stopBothMotors();
                    }
                    else
                    {
                        reculer(2);
                    }
                }
                break;
            }
            else
            {
                avancer(0.80);
            }
        }
        else     
		{
			avancer(1);       
		}
		
        printf("\r%f : Angle %d sur %d \n", timeEnd.read(), cptPosition, SEUIL);
        if(!hasTurned) {
			hasTurned = abs(cptPosition) > SEUIL;
			if(hasTurned)
				printf("\r%f : On a tourné, on peut détecter la Fresque\n", timeEnd.read());
		}
        	
        // Update timer angle
        if(timeAngle.read() >= ANGLE_MAX_TIMER) {
			timeAngle.stop();
			angleGo = 1;
		}

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

int abs(int n)
{
    return n < 0 ? -n : n;
}

void avancer(double rate)
{
    printf("\r%f : Avance\n", timeEnd.read());

    qik.setMotor0Speed((int)AMOTEURD*rate);
    qik.setMotor1Speed((int)AMOTEURG*rate);

    if(!capteurDroit && !capteurGauche)
    {
        qik.setMotor0Speed((int)RMOTEURD*rate);
        qik.setMotor1Speed((int)RMOTEURG*rate);
    }

    /*
     * Tourne à gauche
     */
    if(!capteurDroit)
    {
        printf("\r%f : Gauche !\n", timeEnd.read());
		if(angleGo)
			cptPosition ++;
        qik.setMotor1Speed((int)RMOTEURG*rate);
    }

    /*
     * Troune à droite
     */
    if(!capteurGauche)
    {
        printf("\r%f : Droite !\n", timeEnd.read());
        cptPosition --;
        qik.setMotor0Speed((int)RMOTEURD*rate);
    }
}

void reculer(double rate)
{
    printf("\r%f : Reculer\n", timeEnd.read());

    qik.setMotor0Speed((int)RMOTEURD*rate);
    qik.setMotor1Speed((int)RMOTEURG*rate);

    if(!capteurDroit && !capteurGauche)
    {
        qik.setMotor0Speed((int)AMOTEURD*rate);
        qik.setMotor1Speed((int)AMOTEURG*rate);
    }

    /*
     * Tourne à gauche
     */
    if(!capteurDroit)
    {
        printf("\r%f : Gauche !\n", timeEnd.read());
        cptPosition ++;
        qik.setMotor1Speed((int)AMOTEURG*rate);
    }

    /*
     * Troune à droite
     */
    if(!capteurGauche)
    {
        printf("\r%f : Droite !\n", timeEnd.read());
        if(angleGo)
			cptPosition --;
        qik.setMotor0Speed((int)AMOTEURD*rate);
    }
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
