#ifndef CONFIG_H
#define CONFIG_H

#define PI 3.14159265358979

#include "mbed.h"
#include "parameter.h"

//Débug ou pas ?
//#define DEBUG

class Config
{
public:
    // On définit un attribut pour chaque paramètre (cf params.h)
#   define PARAM(type, nom) static type nom;
#   include "params.h"

    static const Parameter *getParam(std::string name);
    static void parseConfigLine(std::string line);
    static void loadFile(const char *filename);
    static std::string dumpConfig();

private:
    static const Parameter params[];
};

#endif
