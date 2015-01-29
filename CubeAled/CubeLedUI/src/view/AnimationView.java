package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.animation.Animation;
import model.animation.Frame;

public class AnimationView extends JPanel implements Observer {
    private static final long serialVersionUID = 1L;

    private Animation anim;

    private JPanel panelButtons = new JPanel();
    private JButton moveLeftButton = new JButton("«« Début");
    private JButton moveRightButton = new JButton("Fin »»");
    private JSpinner currentFrameSpinner = new JSpinner();
    private JLabel frameCountLabel = new JLabel(" / 0");
    private JButton deleteButton = new JButton("-");
    private JButton newButton = new JButton("+");

    private JSlider slider = new JSlider(1, 1, 1);
    private SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 0, 1);

    private FrameView frameView;

    public AnimationView(final Animation anim, final RealTimeCubeView realTimeView) {
        this.anim = anim;

        anim.addObserver(this);

        setLayout(new BorderLayout());

        currentFrameSpinner.setModel(spinnerModel);
        currentFrameSpinner.setMaximumSize(currentFrameSpinner.getPreferredSize());

        Box box = Box.createVerticalBox();
        panelButtons.add(moveLeftButton);
        panelButtons.add(currentFrameSpinner);
        panelButtons.add(frameCountLabel);
        panelButtons.add(deleteButton);
        panelButtons.add(newButton);
        panelButtons.add(moveRightButton);

        box.add(panelButtons);

        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        box.add(slider);

        add(box, BorderLayout.NORTH);
        add(realTimeView, BorderLayout.SOUTH);

        moveLeftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spinnerModel.setValue(1);
            }
        });

        moveRightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spinnerModel.setValue(spinnerModel.getMaximum());
            }
        });

        currentFrameSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (frameView != null) {
                    remove(frameView);
                }

                slider.setValue((int) spinnerModel.getValue());

                if (getCurrentFrame() != null) {
                    frameView = new FrameView(getCurrentFrame(), realTimeView);
                    add(frameView, BorderLayout.CENTER);
                } else {
                    frameView = null;
                }

                revalidate();
                repaint();
            }
        });

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentFrameSpinner.setValue(slider.getValue());
            }
        });

        MouseWheelListener listener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int newVal = slider.getValue() - e.getWheelRotation();
                if (newVal > 0 && newVal <= slider.getMaximum()) {
                    slider.setValue(newVal);
                }
            }
        };

        slider.addMouseWheelListener(listener);
        currentFrameSpinner.addMouseWheelListener(listener);

        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentIndex = spinnerModel.getNumber().intValue() - 1;

                if (getCurrentFrame() == null || (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
                    anim.newFrame(currentIndex + 1);
                } else {
                    anim.newFrame(currentIndex + 1, getCurrentFrame());
                }

                currentFrameSpinner.setValue(currentIndex + 2);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (anim.getFrameCount() > 0) {
                    anim.deleteFrame(getCurrentFrame());
                }
            }
        });

        update(null, null);
        if (anim.getFrameCount() > 0) {
            currentFrameSpinner.setValue(1);
        }
    }

    public Frame getCurrentFrame() {
        int index = spinnerModel.getNumber().intValue() - 1;
        if (index >= 0 && index < anim.getFrameCount()) {
            return anim.getFrame(spinnerModel.getNumber().intValue() - 1);
        } else {
            return null;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        slider.setMinimum(anim.getFrameCount() > 0 ? 1 : 0);
        slider.setMaximum(anim.getFrameCount());
        spinnerModel.setMinimum(slider.getMinimum());
        spinnerModel.setMaximum(slider.getMaximum());
        frameCountLabel.setText(" / " + anim.getFrameCount());

        if (spinnerModel.getNumber().intValue() > anim.getFrameCount()) {
            spinnerModel.setValue(anim.getFrameCount());
        }
    }

    public void addItemListener(ItemListener listener) {
        listenerList.add(ItemListener.class, listener);
    }

    public void removeItemListener(ItemListener listener) {
        listenerList.remove(ItemListener.class, listener);
    }

    public Animation getAnimation() {
        return anim;
    }
}
