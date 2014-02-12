#include "Telemetre.h"

Telemetre::Telemetre(PinName ctrl, PinName analog, int p_id) :
    m_ctrl(ctrl),
    m_analog(analog),
    moy(10),
    curseur(0),
    enCours(false),
    id(p_id)
{
    for(int i=0;i<TAILLE_FENETRE;i++)
    {
        tab[i]=moy;
    }
}

double Telemetre::getValeur()
{
    m_ctrl = 1;
    wait_us(20);
    m_ctrl = 0;
    wait_ms(10);
    
    return m_analog;
}

void Telemetre::update()
{
    if(!enCours)
    {
        enCours = true;
        m_ctrl = 1;
        timeout.attach(this, &Telemetre::setCtrl0, 0.02);
    }
}

void Telemetre::setCtrl0()
{
    m_ctrl = 0;
    timeout.attach(this, &Telemetre::updateMoyenne, 0.1);
}

void Telemetre::updateMoyenne()
{
    double moyBuffered = moy;
    
    curseur = (curseur+1)%TAILLE_FENETRE;
    
    moyBuffered *= TAILLE_FENETRE;
    moyBuffered -= tab[curseur];
    tab[curseur] = m_analog;
    moyBuffered += tab[curseur];
    moyBuffered /= TAILLE_FENETRE;
    
    moy = moyBuffered;
    
    
    enCours = false;
}

double Telemetre::getMoyenne()
{
    return moy;
}


void Telemetre::computeMoyenne()
{
    curseur = (curseur+1)%TAILLE_FENETRE;
    
    moy *= TAILLE_FENETRE;
    moy -= tab[curseur];
    tab[curseur] = getValeur();
    moy += tab[curseur];
    moy /= TAILLE_FENETRE;
}

 bool Telemetre::isMeasuring()
 {
    return enCours;
 }