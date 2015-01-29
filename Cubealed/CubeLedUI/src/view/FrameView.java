package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import model.animation.Frame;
import model.cube.Coord;
import model.cube.Led;

public class FrameView extends JPanel {
    private static final long serialVersionUID = 1L;
    private Frame frame;
    private JSpinner durationSpinner;

    private MouseHandler mouseHandler = new MouseHandler();
    private Map<Coord, LedView> ledViews = new HashMap<>();

    public FrameView(Frame frame, RealTimeCubeView realTimeView) {
        this.frame = frame;
        realTimeView.setFrame(frame);

        setLayout(new BorderLayout());

        int dim = frame.getAnimation().getCubeSize();
        int w = (int) Math.ceil(Math.sqrt(dim));
        int h = (int) Math.round(Math.sqrt(dim));

        JPanel ledsPanel = new JPanel();
        ledsPanel.setLayout(new GridLayout(w, h));

        for (Led led : frame.getCube()) {
            LedView ledView = new LedView(led);
            ledViews.put(led.getCoord(), ledView);
            ledView.addMouseMotionListener(mouseHandler);
        }

        for (int z = 0; z < dim; z++) {
            JPanel levelPanel = new JPanel();
            levelPanel.setBorder(new TitledBorder("z = " + z));
            levelPanel.setLayout(new GridLayout(dim, dim));

            for (int y = 0; y < dim; y++) {
                for (int x = 0; x < dim; x++) {
                    levelPanel.add(ledViews.get(new Coord(x, y, z)));
                }
            }

            ledsPanel.add(levelPanel);
        }

        add(ledsPanel, BorderLayout.CENTER);

        JPanel durationPanel = new JPanel();

        durationPanel.add(new JLabel("Durée :"));

        SpinnerAdapter adapter = new SpinnerAdapter();
        durationSpinner = new JSpinner(adapter);
        durationPanel.add(durationSpinner);

        durationPanel.add(new JLabel("ms"));

        add(durationPanel, BorderLayout.SOUTH);
    }

    private final class SpinnerAdapter extends SpinnerNumberModel implements Observer {
        private static final long serialVersionUID = 1L;

        public SpinnerAdapter() {
            super(frame.getDuration(), 0, 0x3FFF, 10);
            frame.addObserver(this);
        }

        @Override
        public void setValue(Object value) {
            super.setValue(value);
            frame.setDuration((Integer) value);
        }

        @Override
        public Object getValue() {
            return frame.getDuration();
        }

        @Override
        public void update(Observable o, Object arg) {
            fireStateChanged();
        }
    }

    public Frame getFrame() {
        return frame;
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);

            LedView startView = (LedView) e.getSource();

            Point p = SwingUtilities.convertPoint(startView, e.getPoint(), FrameView.this);

            Component end = findComponentAt(p);

            if (!(end instanceof LedView)) {
                return;
            }

            LedView endView = (LedView) end;

            p = SwingUtilities.convertPoint(FrameView.this, p, endView);

            if (endView.mouseCoordCheck(p)) {
                endView.getLed().setBrightness(startView.getLed().getBrightness());
            }
        }
    }
}
