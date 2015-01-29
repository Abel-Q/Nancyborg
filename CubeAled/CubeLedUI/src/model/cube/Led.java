package model.cube;

import java.io.Serializable;

import model.Model;

public class Led extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    private int brightness;
    private Coord coord;

    public Led(Coord coord) {
        this.coord = coord;
    }

    public Led(Led other) {
        this.coord = other.getCoord();
        this.brightness = other.getBrightness();
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        if (brightness > 255 || brightness < 0) {
            return;
        }

        this.brightness = brightness;
        update(this);
    }

    public Coord getCoord() {
        return coord;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Led other = (Led) obj;
        if (brightness != other.brightness) {
            return false;
        }
        if (coord == null) {
            if (other.coord != null) {
                return false;
            }
        } else if (!coord.equals(other.coord)) {
            return false;
        }
        return true;
    }
}
