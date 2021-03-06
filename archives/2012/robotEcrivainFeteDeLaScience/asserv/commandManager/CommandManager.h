#ifndef COMMAND_MANAGER
#define COMMAND_MANAGER

#include "mbed.h"
#include "../consignController/ConsignController.h"
#include "CMDList/CMDList.h"
#include "../Utils/Utils.h"
#include "../config.h"

class CommandManager {

  public:
    CommandManager(int capacity , ConsignController *ctrlr, Odometrie *odo);
    ~CommandManager();
  
    bool addStraightLine(int64_t valueInmm);
    bool addTurn(int64_t angleInDeg);
    bool addGoTo(int64_t posXInmm, int64_t posYInmm);
    bool addGoToAngle(int64_t posXInmm, int64_t posYInmm);
    bool perform();
    bool isDone();
        
    // Gestion d'un &#65533;ventuel arret d'urgence
    void setEmergencyStop();
    void resetEmergencyStop();
    
    //Calage bordure
    void calageBordurePetit(int sens);
    
    // GoTo l&#65533; om on veut   
    void computeGoTo();
    void computeGoToAngle();
        
  private:
    CMDList *liste; //File d'attente des commandes
    ConsignController *cnsgCtrl;
    Odometrie *odometrie; // Odom&#65533;trie, pour pouvoir faire les conversion et calculer la cible
    CMD currCMD; //commande courante
    CMD nextCMD; //commande suivante
        
    bool emergencyStop; //arret d'urgence : si le robot adverse est dans les parages
        
    bool currentConsignFinished; //signale si la consigne courante est terminee. Reste a vrai tant qu'une nouvelle consigne n'est pas arrivee.
};

#endif
