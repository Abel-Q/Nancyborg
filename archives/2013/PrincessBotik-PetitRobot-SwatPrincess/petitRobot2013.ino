/*
 * petitRobot.ino
 * 
 * Copyright 2013 Jean-Baptiste HERVE <jean-baptiste.herve@laposte.net>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */


/*
 * IA du petit robot de l'équipe Princess'Botik (NancyBorg - TELECOM Nancy)
 * pour la Coupe de France de Robotique 2013.
 * Robot suiveur de ligne et pousseur de cadeaux.
 * 
 * Nécessite :
 * - 4 capteurs de réflexion, pour détecter correctement les lignes noires,
 * - 1 interrupteur à l'avant, pour détecter le rebord de la table avant de taper dans
 *   le cadeau. Pourra etre doublé si nécéssaire, mais ne garder qu'une pin d'entrée.
 * - un capteur ultra-son pour détecter un éventuel adversaire,
 * - un servomoteur pour lancer le bélier qui tape dans les cadeaux,
 * - une Qik, pour controler 2 moteurs,
 * - les 2 moteurs, évidemment,
 * - une tirette, avec interrupteur
 *
 * Les éléments suivants sont aussi nécessaires, mais pas reliés directement à l'Arduino :
 * - Un bouton d'arret d'urgence,
 * - Une batterie 7.2V (petite batterie)
 *
 * Deux capteurs de réflexion sont placés à l'avant du robot pour suivre correctement
 * une ligne noire. Deux autres sont placés à droite et à gauche, dans l'axe des moteurs,
 * pour détecter les lignes noires partant sur les cotés.
 */

#include "ReflectanceSensor.h"
#include "MotorControl.h"
#include "Timer.h"
#include "BatteringRam.h"
#include "USRangeSensor.h"
#include "config.h"
#include "NumberDisplayer.h"
#include <Servo.h>

//#define DEBUG

/* ----- CAPTEURS ----- */

// Les quatres capteurs de réflexion
ReflectanceSensor* reflect_front_left;
ReflectanceSensor* reflect_front_right;
ReflectanceSensor* reflect_left;
ReflectanceSensor* reflect_right;

// L'interrupteur à l'avant du robot (0 : interrupteur fermé - 1 : interrupteur ouvert)
byte front_switch_state;

// L'interrupteur de la tirette (0 : interrupteur fermé - 1 : interrupteur ouvert)
byte tirette_switch_state;

// Un télémètre ultrason pour détecter les robots adverses (ou le notre !)
USRangeSensor* us_range_sensor;


/* ----- ACTIONNEURS ----- */
// La led
byte led_state;

// Les moteurs
MotorControl* motors;

// Le bélier
BatteringRam* ram;

// Un afficheur du numéro d'état
NumberDisplayer* number_displayer;

/* ----- AUTRES TRUCS UTILES ----- */

// Enumération des états possibles pour la machine à états
// cf. README (TODO: Pas encore écrit !) pour plus de détails
typedef enum {
  READY, // Pret, en attente de retirer la tirette - 0
  STARTING, // Sortie case départ pour aller chercher la première ligne - 1
  FOLLOW_FIRST_LINE, // Suivie de la première ligne après la sortie de la case départ - 2
  FOLLOW_STRAIGHT_NOT_TURN, // A la première bifurcation, on ne tourne pas - 3
  FOLLOW_STRAIGHT_LINE, // Suivie de la ligne après la première bifurcation - 4
  GO_HIT, // Avance vers le cadeau pour le taper - 5
  REVERSE, // Recule un peu pour éviter la bordure en tournant - 6
  TURN_FROM_PRESENT, // Se détourne du cadeau - 7
  TURN_TO_INTER_LINE, // Se tourner en direction de la ligne intermédiaire - 8
  GO_INTER_LINE, // Aller chercher la ligne intermédiaire - 9
  FOLLOW_INTER_LINE, // Suit une ligne intermédiaire - 10
  TURN_FROM_LINE, // Tourne pour éviter une ligne - 11
  TURN_TO_LINE, // Tourner jusqu'à arriver sur une ligne - 12
  HALTED, // Attente que le robot adverse laisse la place - 13
  ENDING, // Finissage du match - 14
  END, // Match terminé ! - 15
} state_t;
state_t state; // Etat courant
state_t old_state; // Sauvegarde de l'état pour les cas d'évitement

// Coté où l'on se trouve
// Déterminé par la ligne noire détectée à l'état FOLLOW_FIRST_LINE.
// vu depuis le coté de la table où est le gateau
enum { LEFT_SIDE, RIGHT_SIDE } side;

// Des timers pour controler le temps d'exécution de certaines taches
Timer match_timer; // Un permier pour controler la durée d'un match
Timer temp_timer; // Un deuxieme pour mesurer le temps d'exécution de diverses taches, comme les rotations et autres

// Retenons le nombre de cadeaux déjà tapé
byte hit_presents;

/*
 * Mise à jour des capteurs et écriture des états dans les buffers
 */
void update_sensors(void) {
  front_switch_state = digitalRead(FRONT_SWITCH_PIN) == HIGH ? 1 : 0;
  tirette_switch_state = digitalRead(TIRETTE_SWITCH_PIN) == HIGH ? 1 : 0;
  reflect_front_left->update();
  reflect_front_right->update();
  reflect_left->update();
  reflect_right->update();
  us_range_sensor->update();
}

#ifdef DEBUG
/*
 * Affichage de l'état des capteurs sur la série
 */
void print_sensor_state(void) {
  // capteurs couleurs
  Serial.print("CL");
  Serial.print(reflect_front_left->read_raw());
  Serial.print(" - CR");
  Serial.print(reflect_front_right->read_raw());
  Serial.print(" - L");
  Serial.print(reflect_left->read_raw());
  Serial.print(" - R");
  Serial.print(reflect_right->read_raw());
  Serial.print(" - US");
  Serial.print(us_range_sensor->read());
  
  // Interrupteurs
  Serial.print(" - T");
  Serial.print(tirette_switch_state);
  Serial.print(" - F");
  Serial.print(front_switch_state);
  Serial.print(" - ");
}
#endif // DEBUG

void update_actuators(void) {
  motors->doAction();
  number_displayer->display((int)state);
}

/*
 * Vérifiaction du timer de fin de match, et arret du robot si nécessaire
 */
void check_match_timer(void) {
  if(match_timer.triggered()) {
    state = ENDING;
  }
}

/*
 * Suivre une ligne (action de base...)
 */
void follow_line(void) {
  if(!reflect_front_left->read() && reflect_front_right->read()) { // On ne voit plus la ligne à gauche ?
    motors->setAction(GO_LEFT); // On va à gauche !
  } else if(!reflect_front_right->read() && reflect_front_left->read()) { // On ne voit plus la ligne à droite ?
    motors->setAction(GO_RIGHT); // On va à droite !
  } else if(reflect_front_right->read() && reflect_front_left->read()) { // Si on voit la ligne,
    motors->setAction(GO_FORWARD); // On va tout droit...
  }
  // Dans le cas où d'un coup, on ne voit plus la ligne du tout, on conserve la dernière
  // action en cours. Avec un peu de chance, ça peut récupérer le coup, sinon, tant pis !
}

void setup(void) {
  
#ifdef DEBUG
  // En mode débug, on met le baudrate de la série à fond
  Serial.begin(115200);
#endif // DEBUG
  
  // Mode input pullup pour les interrupteurs
  pinMode(FRONT_SWITCH_PIN, INPUT_PULLUP);
  pinMode(TIRETTE_SWITCH_PIN, INPUT_PULLUP);
  
  // Initialisation des capteurs et actionneurs
  ram = new BatteringRam(BATTERING_RAM_PIN);
  motors = new MotorControl();
  us_range_sensor = new USRangeSensor(ANALOG_READ_SONAR_PIN, DIGITAL_ENABLE_SONAR_PIN);
  us_range_sensor->setEnabled(true);
  reflect_front_left = new ReflectanceSensor(LEFT_FRONT_REFLECT_PIN, LEFT_FRONT_THRESHOLD);
  reflect_front_right = new ReflectanceSensor(RIGHT_FRONT_REFLECT_PIN, RIGHT_FRONT_THRESHOLD);
  reflect_left = new ReflectanceSensor(LEFT_SIDE_REFLECT_PIN, LEFT_SIDE_THRESHOLD);
  reflect_right = new ReflectanceSensor(RIGHT_SIDE_REFLECT_PIN, RIGHT_SIDE_THRESHOLD);
  number_displayer = new NumberDisplayer(NB_PIN0, NB_PIN1, NB_PIN2, NB_PIN3, NB_PIN4);
  
  ram->setUnfold(false);
  motors->setAction(IDLE);
  us_range_sensor->setRangeThreshold(256);
  
  // On a encore rien foutu...
  hit_presents = 0;
  
  // On attend la tirette
  state = READY;
}

void loop(void) {
  // Mise à jour des capteurs
  update_sensors();
  
  //Affichage de l'état des capteurs
#ifdef DEBUG
  print_sensor_state();
#endif // DEBUG

  // Vérification du timer de fin de match et de l'adversaire
  if(state != READY) {
    check_match_timer();
    
    // On regarde s'il n'y a rien devant nous...
    if(us_range_sensor->isObjectDetected() && state != HALTED) {
      motors->setAction(IDLE); // STOOOOP !
      old_state = state; // On sauvegarde l'état courant
      state = HALTED; // On va attendre, comme on ne sait faire que ça...
    }
  }
  
  // Gros switch pour la machine à états de l'IA
  switch(state) {
    
    // Attente de retrait de la tirette
    case READY:
      if(tirette_switch_state == 1) { // On a enlevé la tirette !
        state = STARTING; // C'est parti !
        match_timer.arm(89500);
      }
    break;
    
    case STARTING:
      // On avance tout droit jusqu'à ce qu'on rencontre la ligne devant nous
      motors->setAction(GO_FORWARD);
      if(reflect_front_left->read() || reflect_front_right->read()) {
        state = FOLLOW_FIRST_LINE;
      }
    break;
    
    case FOLLOW_FIRST_LINE:
      // On suit la ligne jusqu'à détecter une ligne partant sur la gauche ou la droite
      // Cela nous donnera le coté de la table où on est.
      follow_line();
      if(reflect_left->read()) {
        side = RIGHT_SIDE;
        state = FOLLOW_STRAIGHT_NOT_TURN;
      } else if (reflect_right->read()) {
        side = LEFT_SIDE;
        state = FOLLOW_STRAIGHT_NOT_TURN;
      }
    break;
    
    case FOLLOW_STRAIGHT_NOT_TURN:
      // Il y a une première bifurcation, que l'on ne prendra pas. Il faut cependant détecter la ligne noire
      // sur le coté et attendre qu'elle disparaisse, parce qu'on va utiliser la prochaine ligne noire
      // pour savoir si on est pas loin du cadeau
      follow_line();
      if(!reflect_left->read() && !reflect_right->read()) {
        state = FOLLOW_STRAIGHT_LINE;
      }
    break;
      
    case FOLLOW_STRAIGHT_LINE:
      // Suivi de la ligne droite en direction du premier cadeau
      follow_line();
      if(reflect_left->read() || reflect_right->read()) { // Si on détecte la ligne qui part sur le coté, ...
        state = GO_HIT; // ... on se prépare à taper le cadeau
      }
    break;
    
    case GO_HIT:
      // On fonce vers le cadeau avec le bélier déplié
      if(front_switch_state==0) { // Si on touche le bord de la table, c'est bon !
        hit_presents++; // On incrémente le compteur de cadeau tapés
        if(hit_presents < 4) { // Il y a encore du boulot
          state = REVERSE; // On va pouvoir reculer ...
          temp_timer.arm(500); // Pendant un temps défini.
        } else { // Bon, bah, on a fini...
          state = ENDING;
        }
        motors->setAction(IDLE); // En attendant, on va s'arreter
        ram->setUnfold(false); // Et ranger le bélier
      } else {
        follow_line(); // Sinon, on suit la ligne, ...
        ram->setUnfold(true); // ... avec le bélier déployé
      }
    break;
    
    case REVERSE:
      // Reculer un peu pour ne pas taper dans la bordure au demi-tour
      if(temp_timer.triggered()) { // Si on a reculé assez,
        state = TURN_FROM_PRESENT; // on commence à tourner
      } else {
        motors->setAction(GO_BACKWARD); // Sinon, on recule encore...
      }
    break;
    
    case TURN_FROM_PRESENT:
      // On se détourne du cadeau, dans un premier temps pour etre perpendiculaire à la ligne y allant
      if(reflect_left->read() && reflect_right->read()) { // Si les roues sont sur la ligne
        state = TURN_TO_INTER_LINE; // On tournera encore un peu
      } else {
        motors->setAction(side == RIGHT_SIDE ? TURN_LEFT : TURN_RIGHT ); // Sinon, on tourne (dans le bon sens !)
      }
    break;
    
    case TURN_TO_INTER_LINE:
      // On part avec les roues alignées sur la petite ligne allant au cadeau, on tourne vers la ligne
      // principale de telle sorte de ne plus détecter la ligne en dessous des roues. La direction devrait
      // alors etre bonne pour attraper la ligne principale
      if(!reflect_left->read() || !reflect_right->read()) { // Si on ne voit plus la ligne,
        state = GO_INTER_LINE; // On va attraper la ligne
      } else {
        motors->setAction(side == RIGHT_SIDE ? TURN_LEFT : TURN_RIGHT ); // Sinon, on tourne (dans le bon sens !)
      }
    break;
    
    case GO_INTER_LINE:
      // Avancer jusqu'à rencontrer la ligne intermédiaire
      motors->setAction(GO_FORWARD);
      if(reflect_front_left->read() || reflect_front_right->read()) { // Si on voit la ligne,
        state = FOLLOW_INTER_LINE; // On la suit !
      }
    break;
    
    case FOLLOW_INTER_LINE:
      // Suivre la ligne entre les cadeaux, jusqu'à voir la ligne sur le coté pour y aller
      follow_line();
      // On vérifie si on a une ligne qui part du coté où on doit aller
      if((reflect_left->read() && side == LEFT_SIDE) || (reflect_right->read() && side == RIGHT_SIDE)) {
        // Oh, une ligne sur le coté !
        // On va y aller, du coup !
        state = TURN_FROM_LINE;
      }

    case TURN_FROM_LINE:
      // Tourner jusqu'à ne plus etre en face d'une ligne noire pour se diriger vers un cadeau
      if(!reflect_front_left->read() && !reflect_front_right->read()) { // Si on ne voit plus la ligne
        state = TURN_TO_LINE; // On continue à tourner
      } else {
        motors->setAction(side == RIGHT_SIDE ? TURN_RIGHT : TURN_LEFT ); // Sinon, on tourne (dans le bon sens !)
      }
    break;
    
    case TURN_TO_LINE:
      // Tourner jusqu'à rencontrer une nouvelle ligne pour etre en face d'un cadeau
      if(reflect_front_left->read() || reflect_front_right->read()) { // Ah, une ligne !
        state = GO_HIT; // On fonce vers le cadeau !
      } else {
        motors->setAction(side == RIGHT_SIDE ? TURN_RIGHT : TURN_LEFT ); // Sinon, on continue à tourner...
      }
    break;
    
    case HALTED:
      // Ah, on a quelque chose devant nous... On va attendre que ça s'en aille tout seul !
      motors->setAction(IDLE);
      if(!us_range_sensor->isObjectDetected()) { // C'est bon, on peut repartir !
        state = old_state;
      }
    
    break;
    
    case ENDING:
      // Arret du robot pour fin de match, on coupe tout !
      motors->setAction(IDLE);
      state = END;
    break;
    
    case END:
      // Bon, bah, on arrete tout, en suivant la méthodologie de La RACHE :
      while(1);
    break; 
     
    default:
      // Euh... on ne devrait pas etre là en prod!
      // Dans le doute, on va tout arreter !
      state = ENDING;
  }
  
  // On met à jour les actionneurs
  update_actuators();
  
  delay(1);
  
#ifdef DEBUG
  // En mode débug, on va aussi afficher l'état :
  Serial.print("S");
  Serial.println(state);
  Serial.flush();
  // 0n met une grosse tempo pour avoir le temps de lire les capteurs
  delay(500);
#endif // DEBUG
  
}

