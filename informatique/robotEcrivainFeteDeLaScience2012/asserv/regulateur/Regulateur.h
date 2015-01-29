#ifndef REGULATEUR
#define REGULATEUR

#include "../filtres/Pid/Pid.h"
#include "../filtres/QuadRampDerivee/QuadRampDerivee.h"
#include "../config.h"

class Regulateur {
  public:
   
    // Constructeur et Destructeur
    /*
    * isDistance est un bool&#65533;en pass&#65533; en param&#65533;tre pour d&#65533;terminer si le r&#65533;gulateur s'occupe de la distance ou de l'angle
    * En fonction du cas, la QuadRampDerivee et le PID &#65533; cr&#65533;er ne sont pas les m&#65533;mes, on passe donc cet argument &#65533; leur constructeur pour
    * les diff&#65533;rencier
    */
    Regulateur(bool isDistance);
    ~Regulateur();
    
    // Permet de calculer l'erreur par rapport &#65533; la consigne gr&#65533;ce aux filtres et &#65533; la position fournit par l'odom&#65533;trie
    int64_t manage(int64_t consigne, int64_t feedback_odometrie); 
    
    // Permet d'activer ou de d&#65533;sactiver la QuadRampDerivee
    void setfiltreQuadRampDeriveeON(bool val) {
      filtreQuadRampDeriveeON = val;
    }
     
    // R&#65533;initialisation de l'accumulateur (distance en UO parcouru depuis l'origine)
    void reset_accumulator() {
      accumulateur = 0;
    }

    int64_t getAccumulateur() {
      return accumulateur;
    }

    // D&#65533;termine si la QuandRampDerivee est termin&#65533;e et donc si le robot &#65533; atteint sa cible
    bool isRampFinished() {
      return filtreQuadRampDerivee.isRampFinished();
    }

    // Permet de modifier dynamiquement la vitesse de la marche arri&#65533;re, pour le callage bordure notamment
    void setVitesseMarcheArriere(int64_t vitesse) {
      filtreQuadRampDerivee.setVitesseMarcheArriere(vitesse);
    }
     
  private:
    // Filtres utilises 
    QuadRampDerivee filtreQuadRampDerivee;
    Pid filtrePid;
    // Accumulateur des UO parcourues depuis le point de d&#65533;part
    int64_t accumulateur;
     
    // Permet de d&#65533;sactiver la QuadRampDerivee
    bool filtreQuadRampDeriveeON;
      
    #ifdef DEBUG
          bool isDistance; //permet de savoir si cette instance de r&#65533;gulateur est utilis&#65533;e pour la r&#65533;gulation de distance
      #endif
    
};


#endif
