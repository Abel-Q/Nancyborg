/*
 * Qik.cpp
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

#include "Qik.h"

//#define DEBUG

//#define INVERSE_LEFT
#define INVERSE_RIGHT

#ifndef INVERSE_RIGHT
  #define ADDR_RIGHT_POS_SPEED ((char)0x88)
  #define ADDR_RIGHT_NEG_SPEED ((char)0x8A)
#else
  #define ADDR_RIGHT_POS_SPEED ((char)0x8A)
  #define ADDR_RIGHT_NEG_SPEED ((char)0x88)
#endif

#ifndef INVERSE_LEFT
  #define ADDR_LEFT_POS_SPEED ((char)0x8C)
  #define ADDR_LEFT_NEG_SPEED ((char)0x8E)
#else
  #define ADDR_LEFT_POS_SPEED ((char)0x8E)
  #define ADDR_LEFT_NEG_SPEED ((char)0x8C)
#endif

// Constructeur, initialisation de la connexion série
Qik::Qik(void) {
#ifndef DEBUG
  Serial.begin(38400);
#endif
}

// Vitesse moteur gauche (entre -128 et 127)
void Qik::left(char speed) {
#ifdef DEBUG
  Serial.print("ML");
  Serial.print((int)speed);
  Serial.print(" - ");
  return;
#endif
  // La commande diffère suivant que la vitesse est positive ou négative
  if(speed >= 0) {
    Serial.write(ADDR_LEFT_POS_SPEED);
    Serial.write(speed);
  } else {
    Serial.write(ADDR_LEFT_NEG_SPEED);
    Serial.write(-speed); // Et on envoie toujours un nombre positif pour la vitesse
  }
}

// Vitesse moteur droit (entre -128 et 127) : idem que la fonction précédente
void Qik::right(char speed) {
#ifdef DEBUG
  Serial.print("MR");
  Serial.print((int)speed);
  Serial.print(" - ");
  return;
#endif
  if(speed >= 0) {
    Serial.write(ADDR_RIGHT_POS_SPEED);
    Serial.write(speed);
  } else {
    Serial.write(ADDR_RIGHT_NEG_SPEED);
    Serial.write(-speed);
  }
}

