/*
 * MotorControl.cpp
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

#include "headers/MotorControl.h"

// Vitesses maximales
#define MAX_FORWARD ((char)64)
#define MAX_BACKWARD ((char)-64)


// Vitesses maximales à appliquer
// Vers l'avant
#define FULL_FORWARD_LEFT MAX_FORWARD
#define FULL_FORWARD_RIGHT MAX_FORWARD

// Vers l'arrière

#define FULL_BACKWARD_LEFT MAX_BACKWARD
#define FULL_BACKWARD_RIGHT MAX_BACKWARD

// Rotation vers la droite
#define TURN_RIGHT_LEFT MAX_FORWARD
#define TURN_RIGHT_RIGHT MAX_BACKWARD

// Rotation vers la droite
#define TURN_LEFT_LEFT MAX_BACKWARD
#define TURN_LEFT_RIGHT MAX_FORWARD

/*
 * Constructeur
 */
MotorControl::MotorControl(void) {
  _current_action = NULL_ACTION;
  setAction(IDLE);
  doAction();
}


/*
 * Enregistre la prochaine action à effectuer
 */
void MotorControl::setAction(motors_action_t action) {
  _action_to_do = action;
}


/*
 * Réalise l'action à effectuer
 */
void MotorControl::doAction(void) {
  _current_action = _action_to_do;
  switch(_current_action) {
    case IDLE: // Arret complet
      _motors.left(0);
      _motors.right(0);
    break;
    
    case GO_FORWARD: // Marche avant toute
      _motors.left(FULL_FORWARD_LEFT);
      _motors.right(FULL_FORWARD_RIGHT);
    break;
    
    case GO_BACKWARD: // Marche arrière toute
      _motors.left(FULL_BACKWARD_LEFT);
      _motors.right(FULL_BACKWARD_RIGHT);
    break;
    
    case GO_LEFT: // Avance vers la gauche
      _motors.left(0);
      _motors.right(MAX_FORWARD);
    break;
    
    case GO_RIGHT: // Avance vers la droite
      _motors.left(MAX_FORWARD);
      _motors.right(0);
    break;
    
    case TURN_LEFT: // Tourne sur place vers la gauche
      _motors.left(TURN_LEFT_LEFT);
      _motors.right(TURN_LEFT_RIGHT);
    break;
    
    case TURN_RIGHT: // Tourne sur place vers la droite
      _motors.left(TURN_RIGHT_LEFT);
      _motors.right(TURN_RIGHT_RIGHT);
    break;
    
    default:
      _motors.left(0);
      _motors.right(0);
  }
}
