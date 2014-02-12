/*#ifndef PROC_TELEMETRES_H
#define PROC_TELEMETRES_H



#include "Telemetre.h"
#include "rtos.h"

enum Telem {AV1, AV2, AR};

class ProcTelemetres
{
    public :
        static void constructTelemetres();
       
        static ProcTelemetres* instanceProcTelemetres;
        
        static bool isDetecting(Telem noTelem);

        ProcTelemetres();
        
        Telemetre av1;
        Telemetre av2;
        Telemetre ar;
        
        Mutex mutex;
};

void procTelemetres(void const *argument);



#endif
*/