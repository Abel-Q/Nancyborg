#ifndef TELEMETRE_H
#define TELEMETRE_H

// MB1200 & Cie

#include "mbed.h"
#include "config.h"

const int TAILLE_FENETRE = 5;

class Telemetre
{
    public :
        Telemetre(PinName ctrl, PinName analog, int p_id);
        
        void computeMoyenne();
        double getMoyenne();
        
        bool isMeasuring();
        
        void update();
        
    private :
         DigitalOut m_ctrl;
         AnalogIn m_analog;
         
         double tab[TAILLE_FENETRE];
         double moy;
         int curseur;
         
         double getValeur(); // Obtenir une valeur : priv&#65533;e car l&#65533;interface publique n&#65533;a besoin que de la valeur moyenne
         void setCtrl0();
         void updateMoyenne();
         
         Timeout timeout;
         
         bool enCours;
         
         int id;
};

#endif

