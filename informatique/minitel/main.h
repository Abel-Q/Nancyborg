
/* ##################  Vitesses pour les moteurs  ################### */
/*
 * Vitesse positive pour mettre les moteurs en marche avant 
 * et vitesse négative pour mettre les moteurs en marche arrière
 */
#define AMOTEURD 27
#define AMOTEURG 32
#define RMOTEURD -27
#define RMOTEURG -27

/* ################################################################## */

/* #####################  AUTRES DEFINE  ############################ */
#define TAILLE_MAX 5
#define DISTANCE_CAPTEUR 50

/* ################################################################## */

/* ##################  ENTREES SUR L'MBED  ########################## */
#define SDA p9
#define SCL p10

/* ======== define pour la qik de contrôle des moteurs  ============= */
#define TX_QIK p13
#define RX_QIK p14
#define RESET_QIK p15
#define ERROR_QIK p16

/* ======= define pour la qik de contrôle du moteur clavier  ======== */
#define TX_QIK_P p28
#define RX_QIK_P p27
#define RESET_QIK_P p25
#define ERROR_QIK_P p26

/* ======= define des autres entrées sur la Mbed  =================== */
#define MODE_PIN p7
#define TIRETTE_PIN p18
#define CAPTEUR_DROIT p19
#define CAPTEUR_GAUCHE p20

/* ################################################################## */

/* ###################  SORTIES SUR L'MBED  ######################### */
#define LED_DROITE p22
#define LED_GAUCHE p21

/* ################################################################## */

/* ###################  DECLARATION DES FONCTIONS ################### */

/**
 * Permet de faire descendre ou de remonter le clavier
 */
void actionClavier(int vitesse, int tempsAttente);

/**
 * Fait avancer le robot en suivant la ligne
 */
void avancer();

/**
 * Initialise les QIK au démarrage du robot pour qu'elles ne soient pas
 * en défaut
 */
void init();

/**
 * Fait reculer le robot en suivant la ligne
 */
void reculer();

/**
 * Fonction qui gére la maintenance du robot hors d'un match
 */
void maintenance();

/**
 * Fonction qui gére le match
 */
void match();

/**
 * Calcule et renvoie la moyenne d'un tableau
 */
 int moyenne(int tab[]);
 
 /**
  * Rempli un tableau en décalant et supprimant le premier élément
  */
 /// tab est le tableau dans lequel il faut mettre le nouvelle elet
 /// nvlElt le nouvelle elet à mettre dans le tableau
 void remplirTab(int tab[], int nvlElt);
/* ################################################################## */
