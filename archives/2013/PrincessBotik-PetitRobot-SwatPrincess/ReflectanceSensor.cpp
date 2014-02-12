/*
 * ReflectanceSensor.cpp
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

#include "ReflectanceSensor.h"

/*
 * Constructeur :
 * pin_number : numéro de la pin analogique
 * threshold : seuil de détection (entre 0 et 1024)
 */
ReflectanceSensor::ReflectanceSensor(int pin_number, int threshold) {
  _pin_number = pin_number;
  _threshold = threshold;
}

/*
 * Mise à jour de la valeur du capteur dans le buffer
 */
void ReflectanceSensor::update(void) {
  _data = analogRead(_pin_number);
  _state = _data> _threshold ? 1 : 0;
}


/*
 * Lit la valeur du buffer d'état
 * revoie 1 si on est sur du noir ou dans le vide, et 0 sinon.
 */
byte ReflectanceSensor::read(void) {
  return _state;
}

/*
 * Lit la valeur brute du capteur
 */
int ReflectanceSensor::read_raw(void) {
  return _data;
}

