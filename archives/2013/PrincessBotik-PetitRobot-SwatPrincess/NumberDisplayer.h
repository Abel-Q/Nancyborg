/*
 * NumberDisplayer.h
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

#ifndef NUMBER_DISPLAYER_H
#define NUMBER_DISPLAYER_H

class NumberDisplayer {
  public:
    NumberDisplayer(int p0, int p1, int p2, int p3, int p4);
    void display(int number);
  
  private:
    int _p0;
    int _p1;
    int _p2;
    int _p3;
    int _p4;
};

#endif // NUMBER_DISPLAYER_H
