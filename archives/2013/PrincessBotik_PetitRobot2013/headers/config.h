/*
 * config.h
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

#ifndef CONFIG_H
#define CONFIG_H

/*
 * Fichiers de configuration des pins d'I/O
 * pour les capteurs et les actionneurs
 */
 

// Interrupteurs
#define FRONT_SWITCH_PIN 11
#define TIRETTE_SWITCH_PIN 10

// Capteurs de réflexion (lignes noires)
// Pins
#define LEFT_SIDE_REFLECT_PIN 3
#define RIGHT_SIDE_REFLECT_PIN 5
#define LEFT_FRONT_REFLECT_PIN 4
#define RIGHT_FRONT_REFLECT_PIN 1
// Seuils de détection
#define LEFT_SIDE_THRESHOLD 64
#define RIGHT_SIDE_THRESHOLD 256
#define LEFT_FRONT_THRESHOLD 256
#define RIGHT_FRONT_THRESHOLD 256

// Télémètre ultrason
#define ANALOG_READ_SONAR_PIN 0
#define DIGITAL_ENABLE_SONAR_PIN 9

// La led, si c'est utile...
#define LED_PIN_NUM 13

// Le bélier
#define BATTERING_RAM_PIN 3

// Un afficheur du numéro d'état
#define NB_PIN0 6
#define NB_PIN1 7
#define NB_PIN2 5
#define NB_PIN3 4
#define NB_PIN4 LED_PIN_NUM

#endif // CONFIG_H
