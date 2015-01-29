package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import model.cube.Led;

public class LedView extends JComponent implements Observer {
    private static final long serialVersionUID = 1L;

    private Led led;
    private MouseHandler mouseHandler = new MouseHandler();

    public LedView(Led led) {
        this.led = led;
        led.addObserver(this);

        addMouseListener(mouseHandler);
        addMouseWheelListener(mouseHandler);

        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        setForeground(new Color(0, 0, 255, led.getBrightness()));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(10, 10);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(getForeground());
        g.fillOval(getWidth() / 2 - 10, getHeight() / 2 - 10, 20, 20);

        g.setColor(Color.black);
        g.drawOval(getWidth() / 2 - 10, getHeight() / 2 - 10, 20, 20);
    }

    public boolean mouseCoordCheck(Point p) {
        return p.getX() >= ((getWidth() / 2) - 10) &&
                p.getY() >= ((getHeight() / 2) - 10) &&
                p.getX() <= ((getWidth() / 2) + 10) &&
                p.getY() <= ((getHeight() / 2) + 10);
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!mouseCoordCheck(e.getPoint())) {
                return;
            }

            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                int amount = -e.getWheelRotation() * 5;

                led.setBrightness(led.getBrightness() + amount);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            if (!mouseCoordCheck(e.getPoint())) {
                return;
            }

            if (e.getButton() == MouseEvent.BUTTON1) {
                led.setBrightness(led.getBrightness() == 0 ? 255 : 0);
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                String result = JOptionPane.showInputDialog("Quelle luminosité ? (entre 0 et 255)", led.getBrightness());

                if (result != null) {
                    int val = Integer.parseInt(result);

                    if (val >= 0 && val < 256) {
                        led.setBrightness(val);
                    }
                }
            }
        }
    }

    public Led getLed() {
        return led;
    }
}
