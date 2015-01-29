/*
 * USRangeSensor.cpp
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

#include "headers/USRangeSensor.h"

/*
 * Instance d'un télémètre US.
 * nécessite une pin analogIn pour la lecture des données, et une pin Digital
 * pour l'activation du sonar.
 */
USRangeSensor::USRangeSensor(int range_analog_pin, int enable_pin) {
  pinMode(enable_pin, OUTPUT); // Pin d'activation en OUTPUT
  digitalWrite(enable_pin, LOW); // On désactive pour le moment
  
  // Initialisation des attributs
  _range_analog_pin = range_analog_pin;
  _last_range = 0;
  _threshold = 0;
  _enable_pin = enable_pin;
}

/*
 * Mise à jour de la distance depuis le télémètre.
 */
void USRangeSensor::update(void) {
  _last_range = analogRead(_range_analog_pin); // Lecture de la distance
}

/*
 * Lecture de la distance (donnée entre 0 et 1024)
 */
int USRangeSensor::read(void) {
  return _last_range;
}

/*
 * Définit un seuil en deçà duquel on considère qu'un objet est devant nous
 */
void USRangeSensor::setRangeThreshold(int range)  {
  _threshold = range;
}

/*
 * Retourne vrai si la distance de l'objet est inférieure à la distance
 * définie précédemment
 */
byte USRangeSensor::isObjectDetected(void) {
  return _last_range<_threshold;
};

/*
 * Activation ou désactivation du téléèmtre US
 */
void USRangeSensor::setEnabled(boolean enabled) {
  digitalWrite(_enable_pin, enabled ? HIGH : LOW); // On active ou désactive
}

