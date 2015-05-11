/*
 * MotorControl.h
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

#ifndef MOTOR_CONTROL_H
#define MOTOR_CONTROL_H

#include "Qik.h"

/*
 * Etats dans lesquels peuvent etre les moteurs
 */
typedef enum {
  NULL_ACTION, // Non initialisé
  IDLE, // Arret complet
  GO_FORWARD, // Marche avant toute
  GO_BACKWARD, // Marche arrière toute
  GO_LEFT, // Avance vers la gauche
  GO_RIGHT, // Avance vers la droite
  TURN_LEFT, // Tourne sur place vers la gauche
  TURN_RIGHT, // Tourne sur place vers la droite
} motors_action_t;

/*
 * Classe controlant les moteurs à un niveau d'abstraction assez élevé
 */
class MotorControl {
  
  public:
    MotorControl(void); // Constructeur
    void setAction(motors_action_t action); // Paramétrer l'action à effectuer
    void doAction(void); // Réaliser l'action
  
  private:
    motors_action_t _action_to_do; // Action à effectuer
    motors_action_t _current_action; // Action en cours
    Qik _motors; // La carte moteur
};

#endif
