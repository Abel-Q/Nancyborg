#ifndef MAIN
#define MAIN

#include "mbed.h"
#include "../config/config.h"
#include "../odometrie/Odometrie.h"
#include "../motorsController/VNH5019/VNH5019Accel.h"
#include "../consignController/ConsignController.h"
#include "../commandManager/CommandManager.h"
#include "../debug/DebugUDP.h"

// Ticker pour l'interruption de la boucle d'asserv
Ticker Live;
void Live_isr(void);

// Méthodes utiles
int main();
void initAsserv();
void resetAsserv();
void ecouteSerie();

// Objets qui vont bien pour asservir le bestiau
Odometrie *odometrie;
MotorsController *motorController;
ConsignController *consignController;
CommandManager *commandManager;
DigitalOut refLed(LED2);
DigitalOut liveLed(LED4);
DigitalOut gotoLed(LED3);
#ifdef DEBUG
DigitalOut dataLed(LED1);
char debugLedStatus;
DebugUDP *debugUdp;
UDPSocket udp;
uint64_t temps;
#endif

#endif
