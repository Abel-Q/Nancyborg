package view;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.animation.Animation;

import comm.AnimationIO;
import comm.CodeGenerator;

public class MainView extends JFrame implements ItemListener {
    private static final long serialVersionUID = 1L;

    private JComboBox<Animation> comboAnim;
    private JButton newButton;
    private JButton removeButton;
    private JButton saveButton;
    private JButton openButton;
    private JButton exportButton;

    private AnimationView animView = null;
    private RealTimeCubeView realTimeView = new RealTimeCubeView();

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainView mainView = new MainView(4);
                // Définit un titre pour notre fenêtre
                mainView.setTitle("Éditeur d'animations pour cube à LEDs");
                // Définit sa taille : 800 pixels de large et 800 pixels de haut
                mainView.setSize(600, 600);
                // Nous demandons maintenant à notre objet de se positionner au centre
                mainView.setLocationRelativeTo(null);
                // Termine le processus lorsqu'on clique sur la croix rouge
                mainView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // Et enfin, la rendre visible
                mainView.setVisible(true);
            }
        });
    }

    public MainView(final int cubeSize)
    {
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel currentAnimPanel = new JPanel();
        currentAnimPanel.add(new JLabel("Animation : "));
        comboAnim = new JComboBox<>();
        comboAnim.addItemListener(this);
        currentAnimPanel.add(comboAnim);

        newButton = new JButton("+");
        currentAnimPanel.add(newButton);

        removeButton = new JButton("-");
        removeButton.setEnabled(false);
        currentAnimPanel.add(removeButton);

        saveButton = new JButton("Enregistrer");
        saveButton.setEnabled(false);
        currentAnimPanel.add(saveButton);

        openButton = new JButton("Ouvrir");
        currentAnimPanel.add(openButton);

        exportButton = new JButton("Exporter");
        exportButton.setEnabled(false);
        currentAnimPanel.add(exportButton);

        getContentPane().add(currentAnimPanel);

        pack();

        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(getContentPane(), "Nom de l'animation :");

                if (name != null) {
                    Animation newAnim = new Animation(name, cubeSize);
                    newAnim.newFrame();
                    comboAnim.addItem(newAnim);
                    comboAnim.setSelectedItem(newAnim);
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboAnim.getSelectedItem() != null) {
                    comboAnim.removeItem(comboAnim.getSelectedItem());
                }
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter("Fichiers d'animation", "cla"));
                if (chooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
                    try {
                        Animation anim = AnimationIO.loadAnimation(chooser.getSelectedFile());
                        comboAnim.addItem(anim);
                        comboAnim.setSelectedItem(anim);
                    } catch (ClassNotFoundException | IOException e1) {
                        JOptionPane.showMessageDialog(getContentPane(), e1.getLocalizedMessage(), "Erreur à l'ouverture", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter("Fichiers d'animation", "cla"));

                if (chooser.showSaveDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = chooser.getSelectedFile();
                        if (!selectedFile.getName().endsWith(".cla")) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + ".cla");
                        }

                        AnimationIO.saveAnimation(selectedFile, (Animation) comboAnim.getSelectedItem());
                        JOptionPane.showMessageDialog(getContentPane(), "Animation sauvegardée");
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(getContentPane(), e1.getLocalizedMessage(), "Erreur à l'ouverture", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = CodeGenerator.generateCode((Animation) comboAnim.getSelectedItem());
                StringSelection selection = new StringSelection(code);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                JOptionPane.showMessageDialog(getContentPane(), "Code copié dans le presse-papier !");
            }
        });

        comboAnim.addItem(new Animation("Animation 1", cubeSize));
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Animation anim = (Animation) comboAnim.getSelectedItem();

        if (animView != null) {
            getContentPane().remove(animView);
        }

        removeButton.setEnabled(anim != null);
        saveButton.setEnabled(anim != null);
        exportButton.setEnabled(anim != null);

        if (anim != null) {
            animView = new AnimationView(anim, realTimeView);
            animView.addItemListener(this);
            getContentPane().add(animView);
        }

        revalidate();
        repaint();
    }

}