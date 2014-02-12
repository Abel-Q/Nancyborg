#ifndef CONFIG
#define CONFIG

#include "mbed.h"

  // Retour PC
  static Serial pc(USBTX, USBRX);


  //D&#65533;bug ou pas ?
//#define DEBUG

/***************************
*       CommandManager     *
****************************/
  static double angleThreshold = 0.15; // Fen&#65533;tre de l'angle dans lequel on consid&#65533;re que le GoTo peut commencer &#65533; avancer
  static int64_t returnThreshold = 50; // Distance en dessous de laquelle on revient en marche arriere si on depasse, sinon demi-tour
  
/***************************
*       ODOMETRIE          *
****************************/
  
  #define PI 3.14159265358979

  // D&#65533;finition des variables pour l'odom&#65533;trie
  static double frontParMetreCodeurG = 15294; // Nombre de tics codeurs en 1m pour codeur gauche
  static double frontParMetreCodeurD = 15356; // Nombre de tics codeurs en 1m pour codeur droite
  static int64_t distRoues = 200; // Distance entre les roues codeuses en mm
  static int64_t uOParFront = 512; // Nombre d'UO pour un tic de codeur

  //Booleens gerant une eventuelle inversion des codeurs
  static bool inverseCodeurG = false;
  static bool inverseCodeurD = true;
  static bool swapCodeurs = false;

/***************************
*         Moteurs          *
****************************/
  static int64_t V_MAX_POS_MOTOR = 100;   // MD22 : 1 a 127, vitesse maximum positive
  static int64_t V_MAX_NEG_MOTOR = -100;  // MD22 : -1 a -128, vitesse maximum negative

  //Booleens gerant une eventuelle inversion des moteurs
  static bool inverseMoteurG = false;
  static bool inverseMoteurD = false;
  static bool swapMoteurs = true;
  
  
/***************************
*           PID            *
****************************/
  // PID en distance
  static int64_t DIST_KP = 60; // Coeff proportionelle
  static int64_t DIST_KI = 0; // Coeff int&#65533;grale
  static int64_t DIST_KD = 500; // Coeff d&#65533;riv&#65533;e
  static double DIST_OUT_RATIO = 0.00001; // Coeff permettant de diminuer les valeurs du PID
  static int64_t DIST_MAX_OUTPUT = 120; // Valeur de sortie maximum pour le moteur
  static int64_t DIST_MAX_INTEGRAL = 0; // Valeur maximum de l'int&#65533;grale (0 = filtre PD)


  static int64_t ANGLE_KP = 60; // Coeff proportionelle
  static int64_t ANGLE_KI = 0; // Coeff int&#65533;grale
  static int64_t ANGLE_KD = 1100; // Coeff d&#65533;riv&#65533;e
  static double ANGLE_OUT_RATIO = 0.000005; // Coeff permettant de diminuer les valeurs du PID
  static int64_t ANGLE_MAX_OUTPUT = 90; // Valeur de sortie maximum pour le moteur
  static int64_t ANGLE_MAX_INTEGRAL = 0; // Valeur maximum de l'int&#65533;grale (0 = filtre PD)

  // QUADRAMPDerivee                        
  static int64_t DIST_QUAD_1ST_POS = 800000; // Vitesse max en marche avant
  static int64_t DIST_QUAD_1ST_NEG = 800000; // Vitesse max en marche arri&#65533;re
  static int64_t DIST_QUAD_AV_2ND_ACC = 6000; // Acc&#65533;l&#65533;ration max en marche avant
  static int64_t DIST_QUAD_AV_2ND_DEC = 4000; // D&#65533;c&#65533;l&#65533;ration max en marche avant
  static int64_t DIST_QUAD_AV_ANTICIPATION_GAIN_COEF = 115; // Coeff d&#65533;terminant le d&#65533;but de la rampe de d&#65533;c&#65533;l&#65533;ration en marche avant
  static int64_t DIST_QUAD_AR_2ND_ACC = 3000; // Acc&#65533;l&#65533;ration max en marche arri&#65533;re
  static int64_t DIST_QUAD_AR_2ND_DEC = 3000; // D&#65533;c&#65533;l&#65533;ration max en marche arri&#65533;re
  static int64_t DIST_QUAD_AR_ANTICIPATION_GAIN_COEF = 95; // Coeff d&#65533;terminant le d&#65533;but de la rampe de d&#65533;c&#65533;l&#65533;ration en marche arri&#65533;re
  static int64_t DIST_TAILLE_FENETRE_ARRIVEE = 200000; // Largeur de la zone o&#65533; l'on consid&#65533;re &#65533;tre arriv&#65533; (UO)

  static int64_t ANGLE_QUAD_1ST_POS = 700000; // Vitesse max en rotation
  static int64_t ANGLE_QUAD_2ND_ACC = 6000; // Acc&#65533;l&#65533;ration max en rotation
  static int64_t ANGLE_QUAD_2ND_DEC = 6000; // D&#65533;c&#65533;l&#65533;ration max en rotation
  static int64_t ANGLE_QUAD_ANTICIPATION_GAIN_COEF = 35; // Coeff d&#65533;terminant le d&#65533;but de la rampe de d&#65533;c&#65533;l&#65533;ration en rotatio=
  static int64_t ANGLE_TAILLE_FENETRE_ARRIVEE = 100000; // Largeur de la zone o&#65533; l'on consid&#65533;re &#65533;tre arriv&#65533; (UO)

#endif
