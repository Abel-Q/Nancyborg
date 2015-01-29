/*
 * Timer.h
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
 
 #ifndef TIMER_H
 #define TIMER_H
 
 
#include "Arduino.h"
/*
 * Classe Timer simple, permettant d'en armer un et de vérifier s'il est arrivé à expiration
 */
class Timer {
  public:
    Timer(void); // Constructeur
    void arm(unsigned long delay_ms); // Armement du timer avec un délai en millisecondes
    byte triggered(void); // Le timer a-t-il expiré ?
  
  private:
    unsigned long _ms_to_reach; // Le nombre de millisecondes à atteindre ( à comparer avec millis() )
};

#endif

