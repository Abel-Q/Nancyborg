/*
 * Timer.cpp
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
 
#include "Timer.h"

 
/*
 * Un beau constructeur vide...
 */
Timer::Timer(void) {} 
 
/*
 * Armement du timer, avec un délai en milliseconde
 */
void Timer::arm(unsigned long delay_ms) {
  _ms_to_reach = millis() + delay_ms;
}

/*
 * Le timer a-t-il expiré ?
 */
byte Timer::triggered(void) {
  return _ms_to_reach <= millis();
}
