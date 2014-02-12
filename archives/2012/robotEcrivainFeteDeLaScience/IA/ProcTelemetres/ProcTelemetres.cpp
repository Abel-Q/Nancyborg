/*

#include "ProcTelemetres.h"
#include "mbed.h"
#include "Telemetre.h"

ProcTelemetres* ProcTelemetres::instanceProcTelemetres;

void ProcTelemetres::constructTelemetres()
{
    instanceProcTelemetres = new ProcTelemetres();
}

ProcTelemetres::ProcTelemetres() :
    av1(p21, p17),
    av2(p22, p18),
    ar(p20, p19)
{

}

bool ProcTelemetres::isDetecting(Telem noTelem)
{
    ProcTelemetres::instanceProcTelemetres->mutex.lock();
    
    bool res = false;
    if(noTelem == AV1) res = ProcTelemetres::instanceProcTelemetres->av1.getMoyenne() < 0.03;
    else if(noTelem == AV2) res = ProcTelemetres::instanceProcTelemetres->av2.getMoyenne() < 0.03;
    else if(noTelem == AR) res = ProcTelemetres::instanceProcTelemetres->ar.getMoyenne() < 0.03;
    
    
    ProcTelemetres::instanceProcTelemetres->mutex.unlock();
    return res;
}

void procTelemetres(void const *argument)
{
    while(true)
    {
        ProcTelemetres::instanceProcTelemetres->mutex.lock();
        
        ProcTelemetres::instanceProcTelemetres->av1.computeMoyenne();
        ProcTelemetres::instanceProcTelemetres->av2.computeMoyenne();
        ProcTelemetres::instanceProcTelemetres->ar.computeMoyenne();
        
        ProcTelemetres::instanceProcTelemetres->mutex.unlock();
    }
}
*/

