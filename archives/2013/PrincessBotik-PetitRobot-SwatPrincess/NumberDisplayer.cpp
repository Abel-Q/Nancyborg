/*
 * NumberDisplayer.cpp
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

#include "NumberDisplayer.h"
#include "Arduino.h"

NumberDisplayer::NumberDisplayer(int p0, int p1, int p2, int p3, int p4) {
  _p0 = p0;
  _p1 = p1;
  _p2 = p2;
  _p3 = p3;
  _p4 = p4;
  pinMode(_p0, OUTPUT);
  pinMode(_p1, OUTPUT);
  pinMode(_p2, OUTPUT);
  pinMode(_p3, OUTPUT);
  pinMode(_p4, OUTPUT);
  digitalWrite(_p0, LOW);
  digitalWrite(_p1, LOW);
  digitalWrite(_p2, LOW);
  digitalWrite(_p3, LOW);
  digitalWrite(_p4, LOW);
}

void NumberDisplayer::display(int number) {
  digitalWrite(_p0, ((number & 1) ? HIGH : LOW));
  digitalWrite(_p1, ((number & (1<<1)) ? HIGH : LOW));
  digitalWrite(_p2, ((number & (1<<2)) ? HIGH : LOW));
  digitalWrite(_p3, ((number & (1<<3)) ? HIGH : LOW));
  digitalWrite(_p4, ((number & (1<<4)) ? HIGH : LOW));
}

