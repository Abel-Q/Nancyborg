# Compilation avec gcc4mbed

## Mise en place de gcc4mbed

Pour commencer, il faut mettre en place gcc4mbed : https://github.com/adamgreen/gcc4mbed#quick-start

## Compilation

Une fois installé, modifier le Makefile en indiquant le chemin vers les sources de gcc4mbed (GCC4MBED_DIR).
Il ne reste plus qu'à exécuter make.

## Exemple
    $ git clone git://github.com/mickael9/IAsservPetitRobot2012.git
    $ git clone git://github.com/adamgreen/gcc4mbed.git
    $ gcc4mbed/linux_install
    $ gcc4mbed/BuildShell
    $ cd ../IAsservPetitRobot2012
    $ make
