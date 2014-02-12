/*
 * BatteringRam.cpp
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
 
#include "BatteringRam.h"

#define DEFAULT_US_FOLDED 1000
#define DEFAULT_US_UNFOLDED 2000

/*
 * Constructeur pour le bélier, avec la pin de controle du servomoteur
 */
BatteringRam::BatteringRam(int servo_control_pin) {
  _servo.attach(servo_control_pin); // On attache le servo à la pin de controle
  _us_folded = DEFAULT_US_FOLDED; // Paramétrage des positions min et max du servo
  _us_unfolded = DEFAULT_US_UNFOLDED;
  setUnfold(false); // On commence en mode replié
  doAction(); // et on se met en position
}

/*
 * Paramétrer la position du bélier :
 * unfold = true -> bélier déplié
 * unfold = false -> bélier rangé
 * A noter que la position ne sera pas immédiatement prise, il faut appeler ensuite la méthode doAction()
 */
void BatteringRam::setUnfold(boolean unfold) {
  _unfold = unfold;
}

/*
 * Réalise l'action prévue précédemment : ranger ou déployer le bélier
 */
void BatteringRam::doAction(void) {
  if(_unfolded == _unfold) { // Si on n'a rien à faire...
    return; // ... bah on ne fait rien.
  }

  _unfolded = _unfold;
  if(_unfold) { // On positionne le servo comme demandé
    _servo.writeMicroseconds(_us_unfolded);
  } else {
    _servo.writeMicroseconds(_us_folded);
  }
}
