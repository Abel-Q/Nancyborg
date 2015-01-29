package model;

import java.util.Observable;

public class Model extends Observable {
    protected void update() {
        setChanged();
        notifyObservers();
    }

    protected void update(Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}
