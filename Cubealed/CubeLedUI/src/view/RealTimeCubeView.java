package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.animation.Frame;
import model.cube.Coord;
import model.cube.Led;

import comm.CodeGenerator;
import comm.CommCubeLed;

public class RealTimeCubeView extends JPanel implements Observer {
    private static final long serialVersionUID = 1L;

    private Frame frame = null;
    private Frame prevFrame = null;
    private CommCubeLed comm = null;

    private JCheckBox enabledCheckbox = new JCheckBox("Afficher sur le cube");

    public RealTimeCubeView() {
        add(enabledCheckbox);

        enabledCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setActivated(enabledCheckbox.isSelected());
            }
        });
    }

    public void setFrame(Frame frame) {
        if (this.frame != null) {
            this.frame.getCube().deleteObserver(this);
        }
        this.prevFrame = this.frame;

        this.frame = frame;
        this.frame.getCube().addObserver(this);

        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg == null) {
            for (Led led : frame.getCube()) {
                Coord coord = led.getCoord();
                Led prevLed = prevFrame != null ? prevFrame.getLed(coord) : null;

                if (!led.equals(prevLed)) {
                    updateLed(led);
                }
            }
        } else {
            updateLed((Led) arg);
        }
    }

    private void updateLed(Led led) {
        if (comm != null) {
            int cmd = CodeGenerator.generateLedChange(led);
            System.out.format("%0#6x%n", cmd);
            try {
                comm.sendCommands(cmd);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Impossible de communiquer avec le cube : " + e.getMessage(), "Erreur de communication", JOptionPane.ERROR_MESSAGE);
                enabledCheckbox.setSelected(false);
            }
        }
    }

    private void setActivated(boolean activated) {
        if (!activated) {
            if (comm != null) {
                try {
                    comm.setAutomaticMode();
                    comm.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                comm = null;
            }
        } else {
            try {
                comm = new CommCubeLed();
                comm.reset();

                prevFrame = null;
                update(null, null);
            } catch (IOException e) {
                enabledCheckbox.setSelected(false);
                JOptionPane.showMessageDialog(this, "Impossible de communiquer avec le cube : " + e.getMessage(), "Erreur de communication", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
