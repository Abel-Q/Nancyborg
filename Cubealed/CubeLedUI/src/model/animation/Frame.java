package model.animation;

import java.io.Serializable;

import model.Model;
import model.cube.Coord;
import model.cube.Cube;
import model.cube.Led;

/**
 * Représente une étape dans une animation.
 */
public class Frame extends Model implements Serializable {
    private static final long serialVersionUID = 3451069190172103454L;

    private Animation animation;
    private int duration = 100;
    private Cube cube;

    public Frame(Animation animation) {
        this.animation = animation;
        cube = new Cube(animation.getCubeSize());
    }

    public Frame(Animation animation, Frame other) {
        this.animation = animation;
        this.duration = other.getDuration();
        this.cube = new Cube(other.getCube());
    }

    /**
     * Renvoie le cube associé à cette étape
     */
    public Cube getCube() {
        return cube;
    }

    /**
     * Renvoie la durée de l'étape
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Définit une nouvelle durée pour l'étape
     */
    public void setDuration(int duration) {
        this.duration = duration;
        update();
    }

    public Led getLed(Coord coord) {
        return cube.getLed(coord);
    }

    public Led getLed(int x, int y, int z) {
        return cube.getLed(x, y, z);
    }

    public Animation getAnimation() {
        return animation;
    }

}
