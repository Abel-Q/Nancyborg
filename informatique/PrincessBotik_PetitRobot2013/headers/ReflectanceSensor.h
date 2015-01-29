/*
 * ReflectanceSensor.h
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


#ifndef REFLECTANCE_SENSOR_H
#define REFLECTANCE_SENSOR_H

#include "Arduino.h"

/*
 * Classe représentant un capteur de réflexion ; permet de détecter des lignes noires
 * Capteur Pololu QTR-1A Reflectance Sensor
 */
class ReflectanceSensor {
  public:
    ReflectanceSensor(int pin_number, int threshold); // constructeur
    void update(); // Mettre à jour la valeur du capteur
    byte read(); // Lire la valeur
    int read_raw(); // Lit la valeur brute
  
  private:
    int _pin_number; // Numéro de la pin analogique
    int _threshold; // Seuil de détection
    byte _state; // Buffer pour l'état du capteur
    int _data; // Données brute du capteur
};


#endif // REFLECTANCE_SENSOR_H
