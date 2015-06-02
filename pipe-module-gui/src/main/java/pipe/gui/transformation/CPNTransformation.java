package pipe.gui.transformation;

import pipe.gui.widget.StateSpaceLoader;
import uk.ac.imperial.pipe.io.PetriNetIOImpl;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by andrii-usov on 05.05.2015.
 */
public class CPNTransformation {

    public interface ReplaceAction {
        void replace(PetriNet petriNet);
    }

    private StateSpaceLoader stateSpaceLoader;

    private JPanel mainPanel;

    private JPanel loadPanel;

    private JScrollPane scrollPane;

    private JButton button;

    private JPanel buttonPanel;

    private JRadioButton saveRadioButton;

    private JRadioButton replaceRadioButton;

    private JTextField textField1;

    private JButton closeButton;

    private FileDialog saveDialog;

    private boolean replaceExisting = true;

    private boolean saveToFile = false;

    private File fileToSave;

    private ReplaceAction replaceAction;

    public CPNTransformation(FileDialog loadDialog, FileDialog saveDialog, ReplaceAction replaceAction) {
        this.saveDialog = saveDialog;
        this.stateSpaceLoader = new StateSpaceLoader(loadDialog);
        this.replaceAction = replaceAction;
        setUp();
    }

    public CPNTransformation(PetriNet petriNet, FileDialog loadDialog, FileDialog saveDialog,  ReplaceAction replaceAction) {
        this.saveDialog = saveDialog;
        this.stateSpaceLoader = new StateSpaceLoader(petriNet, loadDialog);
        this.replaceAction = replaceAction;
        setUp();
    }

    /**
     * Sets up the UI
     */
    private void setUp() {
        replaceRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                replaceExisting = true;
                saveToFile = false;
            }
        });

        saveRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDialog.setMode(FileDialog.SAVE);
                saveDialog.setTitle("Select file to save Petri net");
                saveDialog.setVisible(true);

                File[] files = saveDialog.getFiles();
                if (files.length > 0) {
                    fileToSave = files[0];
                    textField1.setText(fileToSave.getName());
                    replaceExisting = false;
                    saveToFile = true;
                }
            }
        });

        loadPanel.add(stateSpaceLoader.getMainPanel(), 0);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PetriNet net = stateSpaceLoader.getPetriNet();
                CPNTransformer transformer = new CPNTransformer(net);
                PetriNet newNet = transformer.getTransformed();

                if (replaceExisting) {
                    if (replaceAction != null) {
                        replaceAction.replace(newNet);
                    }
                } else if (saveToFile && fileToSave != null) {
                    PetriNetIOImpl writer;
                    try {
                        writer = new PetriNetIOImpl();
                        writer.writeTo(fileToSave.getAbsolutePath(), newNet);
                    } catch (JAXBException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getMainPanel().setVisible(false);
            }
        });


    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setResult(JLayeredPane pane) {
        scrollPane.add(pane);
    }

}
