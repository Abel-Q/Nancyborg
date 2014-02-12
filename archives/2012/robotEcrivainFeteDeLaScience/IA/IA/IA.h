#ifndef IA_H
#define IA_H

#include <vector>
#include <utility>

#include "mbed.h"
#include "../../config.h"
#include "../../odometrie/Odometrie.h"
#include "../../motorsController/Md22/Md22.h"
#include "../../consignController/ConsignController.h"
#include "../../motorsController/MotorsController.h"
#include "../../commandManager/CommandManager.h"
#include "../../odometrie/Odometrie.h"
#include "../../debug/DebugUDP.h"
#include "Telemetre.h"
#include "../Servo/Servo.h"
#include "../PSX/PSXController.h"

class IA
{
    public :
         IA(CommandManager *p_commandManager, MotorsController *p_motorsContoller, Odometrie *p_odometrie);
         void init();
         void run();
         bool isManualMode();

         struct position {
             int64_t x;
             int64_t y;

             position(int64_t x_, int64_t y_) :
                 x(x_), y(y_)
             {
             }
         };
    private:
        CommandManager* commandManager;
        MotorsController* motorsController;
        Odometrie* odometrie;
        Servo m_servo;
        PSXController m_psx;
        bool m_manualMode;
        std::vector<position> positions;
};

#endif
