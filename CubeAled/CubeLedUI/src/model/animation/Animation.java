package model.animation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.Model;

/**
 * Représente une animation pour un cube à LEDs
 */
public class Animation extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Frame> frames = new ArrayList<Frame>();
    private int cubeSize;
    private String name;

    /**
     * Construit une nouvelle animation
     * 
     * @param name nom de l'animation
     * @param cubeSize côté du cube
     */
    public Animation(String name, int cubeSize) {
        this.name = name;
        this.cubeSize = cubeSize;
    }

    public int getCubeSize() {
        return cubeSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFrameCount() {
        return frames.size();
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public Frame getFrame(int index) {
        return frames.get(index);
    }

    public Frame newFrame(Frame other) {
        return newFrame(frames.size(), other);
    }

    public Frame newFrame() {
        return newFrame(frames.size());
    }

    public Frame newFrame(int index, Frame other) {
        Frame newFrame = new Frame(this, other);
        frames.add(index, newFrame);
        update();
        return newFrame;
    }

    public Frame newFrame(int index) {
        Frame newFrame = new Frame(this);
        frames.add(index, newFrame);
        update();
        return newFrame;
    }

    public void deleteFrame(int index) {
        frames.remove(index);
        update();
    }

    public void deleteFrame(Frame frame) {
        frames.remove(frame);
        update();
    }

    public void deleteLastFrame() {
        frames.remove(frames.get(frames.size() - 1));
        update();
    }

    @Override
    public String toString() {
        return name;
    }
}