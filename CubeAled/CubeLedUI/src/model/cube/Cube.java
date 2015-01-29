package model.cube;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import model.Model;

/**
 * Représente un cube à LEDs
 */
public class Cube extends Model implements Serializable, Iterable<Led>, Observer {
    private static final long serialVersionUID = 1L;
    private int size;
    private Map<Coord, Led> leds = new HashMap<>();

    private Cube(int size, Cube other) {
        this.size = size;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    Coord coord = new Coord(x, y, z);

                    Led led;

                    if (other == null) {
                        led = new Led(coord);
                    } else {
                        led = new Led(other.getLed(x, y, z));
                    }

                    led.addObserver(this);
                    leds.put(coord, led);
                }
            }
        }
    }

    public Cube(Cube other) {
        this(other.getSize(), other);

    }

    public Cube(int size) {
        this(size, null);

    }

    /**
     * Retourne la LED à la position donnée
     */
    public Led getLed(int x, int y, int z) {
        return leds.get(new Coord(x, y, z));
    }

    /**
     * Retourne la LED à la position donnée
     */
    public Led getLed(Coord coord) {
        return leds.get(coord);
    }

    /**
     * Retourne la taille du cube (côté)
     */
    public int getSize() {
        return size;
    }

    @Override
    public Iterator<Led> iterator() {
        return new LedIterator();
    }

    public class LedIterator implements Iterator<Led> {
        private int x = 0;
        private int y = 0;
        private int z = 0;
        private boolean atEnd = false;

        @Override
        public boolean hasNext() {
            return !atEnd;
        }

        @Override
        public Led next() {
            if (atEnd) {
                return null;
            }

            Led led = getLed(x, y, z);

            if (++x == size) {
                x = 0;
                if (++y == size) {
                    y = 0;
                    if (++z == size) {
                        atEnd = true;
                    }
                }
            }

            return led;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        update(arg);
    }
}
