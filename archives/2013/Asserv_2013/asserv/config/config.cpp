#include <fstream>

#include "config.h"

#define PARAM(type, nom) type Config::nom;
#include "params.h"

const Parameter Config::params[] = {
#   define PARAM(type, nom) Parameter(#nom, &Config::nom),
#   include "params.h"
};

const Parameter *Config::getParam(std::string name)
{
    for (size_t i = 0; i < sizeof(Config::params) / sizeof(*Config::params); i++) {
        const Parameter *p = &(Config::params[i]);

        if (p->is(name))
            return p;
    }

    return NULL;
}

void Config::parseConfigLine(std::string line)
{
    bool isName = true;
    std::string name;
    std::string val;

    for (std::string::const_iterator it = line.begin(); it != line.end(); ++it) {
        if (*it == ' ' || *it == '\n' || *it == '\r')
            continue;

        if (isName && *it == '=') {
            isName = false;
            continue;
        }

        if (isName)
            name.push_back(*it);
        else
            val.push_back(*it);
    }

    if (name.empty()) {
        return;
    }

    const Parameter *p = Config::getParam(name);

    if (p == NULL) {
        printf("Attention : Le paramètre '%s' n'existe pas !\n", name.c_str());
        return;
    }

    p->setFromString(val);
}

void Config::loadFile(const char *filename)
{
    printf("Chargement de '%s'...\n", filename);

    std::string line;
    std::ifstream file(filename);

    if (!file) {
        printf("Erreur : Impossible de charger le fichier '%s'\n", filename);
        return;
    }

    while (!file.eof()) {
        getline(file, line);

        if (!line.empty() && line[0] != '#' && line[0] != '/')
            parseConfigLine(line);
    }

    file.close();
}
