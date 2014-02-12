/*
 * USRangeSensor.h
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

#ifndef US_RANGE_SENSOR
#define US_RANGE_SENSOR

#include "Arduino.h"

class USRangeSensor {
  
  public:
    USRangeSensor(int range_analog_pin, int enable_pin);
    
    void update(void);
    int read(void);
    void setEnabled(boolean enabled);
    
    void setRangeThreshold(int range);
    byte isObjectDetected(void);
    
  private:
    int _last_range;
    int _range_analog_pin;
    int _threshold;
    int _enable_pin;
};

#endif // US_RANGE_SENSOR

