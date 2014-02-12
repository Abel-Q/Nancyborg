#include "IA.h"
#include "../../PSX/PSXController.h"
#include "../../Utils/Utils.h"

IA::IA(CommandManager* p_commandManager, MotorsController* p_motorsController, Odometrie* p_odometrie) :
    commandManager(p_commandManager),
    motorsController(p_motorsController),
    odometrie(p_odometrie),
    m_servo(p23),
    m_psx(p22, p21, p17, p18),
    m_manualMode(true)
{
}

void IA::init()
{
    m_psx.init();
    m_psx.setAnalogMode(true, false);
    m_servo.write(1);

    //pin 7: tirette ( 0 => go!)
/*    DigitalIn tirette(p7);
    tirette.mode(PullDown);

    pc.printf("Attente tirette\n");
    while(!tirette)
    {
        wait_ms(20);
    }*/
}

void IA::run()
{
    m_psx.run();

    m_manualMode = m_psx.isAnalogMode();

    // FIXME: il faut appuyer deux fois sur start si décommenté
    /*
    if (!m_manualMode && commandManager->isDone())
        m_psx.setAnalogMode(true, false);
    */

    if (m_psx.hasKeyChanged(m_psx.KEY_TRIANGLE) && m_psx.isKeyPressed(m_psx.KEY_TRIANGLE))
    {
        positions.clear();
        commandManager->setEmergencyStop();
        commandManager->resetEmergencyStop();

        if (!m_manualMode)
            m_psx.setAnalogMode(true, false);
    }

    if (m_manualMode)
    {
        if (m_psx.hasKeyChanged(m_psx.KEY_CIRCLE) && m_psx.isKeyPressed(m_psx.KEY_CIRCLE))
        {
            pc.printf("ajout liste : (%d, %d)\r\n", odometrie->getX(), odometrie->getY());
            positions.push_back(position(odometrie->getX(), odometrie->getY()));

            pc.printf("nb pos = %d\r\n", positions.size());
        }
        else if (m_psx.hasKeyChanged(m_psx.KEY_START) && m_psx.isKeyPressed(m_psx.KEY_START))
        {
            for (int i = 0; i < positions.size(); i++)
            {
                commandManager->addGoTo(Utils::UOTomm(odometrie, positions[i].x),
                                        Utils::UOTomm(odometrie, positions[i].y));
            }

            m_psx.setAnalogMode(false, false);
            return;
        }

        double x = m_psx.getRightX();
        double y = m_psx.getLeftY();

        if (m_psx.isKeyPressed(m_psx.KEY_R1))
            x *= 2;

        if (m_psx.isKeyPressed(m_psx.KEY_L1))
            y *= 2;

        //pc.printf("x = %f   y = %f\r\n", x, y);

        int v = y * 90;
        int vg = v + x * 20;
        int vd = v - x * 20;

        //pc.printf("vg = %d   vd = %d\r\n", vg, vd);

        motorsController->vitesseG(vg);
        motorsController->vitesseD(vd);
    }
}

bool IA::isManualMode()
{
    return m_manualMode;
}
