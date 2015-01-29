/*
 * Qik.h
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

#ifndef QIK_H
#define QIK_H

#include "Arduino.h"

/*
 * Classe permettant de controler une carte moteur Pololu Qik via la liaison série de l'Arduino
 * Celle-ci ne sera donc pas disponible pour autre chose dans ce cas (par ex. pour le débug)
 */
class Qik {

  public:
    // Constructeur (initialise notamment la connexion série)
    Qik(void);
    
    // Vitesse du moteur gauche (de -128 à 127)
    void left(char speed);
    
    // Vitesse du moteur droit (de -128 à 127)
    void right(char speed);

};

#endif // QIK_H
