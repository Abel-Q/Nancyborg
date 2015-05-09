/*
 * BatteringRam.h
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

#ifndef BATTERING_RAM_H
#define BATTERING_RAM_H

// Pff, c'est un peu un #inlcude WTF mais Arduino ne recherche pas dans le dossier des libs
// si l'include n'est pas fait dans le fichier principal...
// D'ailleurs il faut de toute façon faire l'include dans le fichier principal, sinon
// il ne rajoute pas la lib à l'édition de liens...
#include <../../../../libraries/Servo/Servo.h>

#include "Arduino.h"

/*
 * Classe "Bélier" (battering ram en anglais) pour défoncer les cadeaux !
 */
class BatteringRam {
  public:
    BatteringRam(int servo_control_pin); // Constructeur
    void setUnfold(boolean unfold); // paramétrage de la position du bélier
    void doAction(void); // Mise en position du bélier
    
  private:
    boolean _unfolded; // Position réelle du bélier
    boolean _unfold; // Position prévue du bélier
    Servo _servo; // Le servomoteur à controler
    int _us_folded; // Position (en µs) du servo pour un bélier replié
    int _us_unfolded; // Position du servo pour un belier déployé
};

#endif // BATTERING_RAM_H
