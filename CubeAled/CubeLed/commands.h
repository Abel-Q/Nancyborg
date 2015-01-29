#include <Arduino.h>

extern byte values[4][4][4];
extern byte animation[];
extern int animation_size;
extern AnimationStream anim;

void setLed(byte x, byte y, byte z, byte val);
byte getLed(byte x, byte y, byte z);

class Commands {
  public:
    Commands(Stream &stream) :
      stream(stream),
      delay_endmillis(0)
    {
    }

    void update()
    {
      /* Si on a un délai en attente, on vérifie s'il est dépassé, sinon on ne fait rien */
      if (delay_endmillis) {
        if (millis() >= delay_endmillis) {
          delay_endmillis = 0;
          stream.write('S'); // on annonce la fin du délai
        } else {
          return;
        }
      }

      if (stream.available()) {
        char cmd = readBlocking();

        /* T : met à jour toutes les LEDs */
        if (cmd == 'T') {
          for (unsigned z = 0; z < 4; z++) {
            stream.write('?'); // on indique être prêt à recevoir les données de la ligne
            stream.flush();

            for (unsigned y = 0; y < 4; y++) {
              for (unsigned x = 0; x < 4; x++) {
                values[z][y][x] = readBlocking();
              }
            }
          }
          stream.write('T');
        }
        /* L : change l'état d'une LED */
        else if (cmd == 'L') {
          byte xyz, val;
          xyz = readBlocking();
          val = readBlocking();

          setLed(xyz % 4, (xyz / 4) % 4, (xyz / 16) % 4, val);
          stream.write('L');
        }
        /* C : éteint toutes les LEDs */
        else if (cmd == 'C') {
          memset(values, 0, sizeof(values));
          stream.write('C');
        }
        /* S : lancement d'un délai (0-255 ms) */
        else if (cmd == 'S') {
          delay_endmillis = millis() + readBlocking();
        }
        /* A : enregistre une nouvelle animation */
        else if (cmd == 'A') {
          animation_size = readBlocking() * 256 + readBlocking();
          for (int i = 0; i < animation_size; i++) {
            if ((i % 32) == 0) {
              stream.write('?');
              stream.flush();
            }

            animation[i] = readBlocking();
          }
          anim.setData(animation_size, animation);
          stream.write('A');
        }
        else {
          stream.write('0');
        }

        stream.flush();
      }
    }


  private:
    byte readBlocking() {
      while (!stream.available());
      return stream.read() & 0xFF;
    }

    Stream &stream;
    unsigned long delay_endmillis;
};
