package pipe.gui.decomposition;

import pipe.gui.transformation.CPNTransformer;
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
public class Decomposition {

    private StateSpaceLoader stateSpaceLoader;

    private JPanel mainPanel;

    private JPanel loadPanel;

    private JButton button;

    private JPanel buttonPanel;

    private JScrollPane scrollPane;

    private FileDialog saveDialog;

    private File fileToSave;

    public Decomposition(FileDialog loadDialog, FileDialog saveDialog) {
        this.saveDialog = saveDialog;
        this.stateSpaceLoader = new StateSpaceLoader(loadDialog);
        setUp();
    }

    public Decomposition(PetriNet petriNet, FileDialog loadDialog, FileDialog saveDialog) {
        this.saveDialog = saveDialog;
        this.stateSpaceLoader = new StateSpaceLoader(petriNet, loadDialog);
        setUp();
    }

    /**
     * Sets up the UI
     */
    private void setUp() {

        loadPanel.add(stateSpaceLoader.getMainPanel(), 0);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PetriNet net = stateSpaceLoader.getPetriNet();
                Decomposer d = new Decomposer(net);
                d.decomposePetriNet();
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
