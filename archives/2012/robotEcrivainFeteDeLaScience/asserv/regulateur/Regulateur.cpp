#include "Regulateur.h"

#ifdef DEBUG
#include "../debug/DebugUDP.h"
    extern DebugUDP *debugUdp;
#endif

// Constructeur
Regulateur::Regulateur(bool isDistance) : filtreQuadRampDerivee(isDistance), filtrePid(isDistance) {
  filtreQuadRampDeriveeON = true;
  accumulateur = 0;
  #ifdef DEBUG
      this->isDistance = isDistance;
  #endif
}

// Destructeur
Regulateur::~Regulateur() {
}

// Calcul de l'erreur
int64_t Regulateur::manage(int64_t consigne, int64_t feedback_odometrie) {
                          
  // On ajoute dans l'accumulateur la variation de d&#65533;placement mesur&#65533; par l'odom&#65533;trie
  accumulateur += feedback_odometrie;
    
  // On filtre la consigne a l'aide de la QuadRampeDerivee pour savoir si l'on est en phase d'acc&#65533;l&#65533;ration, constante ou de d&#65533;c&#65533;l&#65533;ration
  // Cette &#65533;tape permet d'avoir un comportement lin&#65533;aire au lieu de binaire (= soit &#65533; l'arr&#65533;t, soit &#65533; fond) qui provoque des secouses et peut 
  // renverser le robot. A la place, on acc&#65533;l&#65533;re et d&#65533;cc&#65533;l&#65533;re tranquillement et &#65533;a, c'est beau :p
  int64_t consigneFiltree = filtreQuadRampDerivee.filtre(consigne, accumulateur, feedback_odometrie); 
    
  // On calcul l'erreur, c'est &#65533; dire la diff&#65533;rence entre la consigne &#65533; suivre et la position r&#65533;elle stock&#65533;e dans l'accumulateur
  int64_t erreur;
  if ( filtreQuadRampDeriveeON ) {
    erreur = consigneFiltree - accumulateur;
  } else {
    erreur = consigne - accumulateur;  
  }
  
  // On envoit l'erreur au PID qui va la filtrer par rapport aux param&#65533;tres du robot et aux coefficients d'asservissement
  // On obtient alors la vitesse a envoyer aux moteurs pour corriger l'erreur courante
  int64_t erreurFiltree = filtrePid.filtre(erreur, feedback_odometrie);
  
  //d&#65533;bug
  #ifdef DEBUG

    char name[32];

    strcpy(name, "consigne");
    name[8]=(isDistance?'D':'A');
    name[9]=0;
    debugUdp->addData(name, (double) consigne );
    
    strcpy(name, "accu");
    name[4]=(isDistance?'D':'A');
    name[5]=0;
    debugUdp->addData(name, (double) accumulateur );
    
    strcpy(name, "consigneFiltree");
    name[15]=(isDistance?'D':'A');
    name[16]=0;
    debugUdp->addData(name, (double) consigneFiltree );
    
    strcpy(name, "erreurFiltree");
    name[13]=(isDistance?'D':'A');
    name[14]=0;
    debugUdp->addData(name, (double) erreurFiltree );

  #endif
  
  return erreurFiltree;
}
