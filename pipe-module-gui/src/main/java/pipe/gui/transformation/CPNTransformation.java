package pipe.gui.transformation;

import pipe.gui.widget.StateSpaceLoader;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.Place;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * Created by andrii-usov on 05.05.2015.
 */
public class CPNTransformation {

    public static final String HTML_STYLE = "body{font-family:Arial,Helvetica,sans-serif;text-align:center;" +
            "background:#ffffff}" +
            "td.colhead{font-weight:bold;text-align:center;" +
            "background:#ffffff}" +
            "td.rowhead{font-weight:bold;background:#ffffff}" +
            "td.cell{text-align:center;padding:5px,0}" +
            "tr.even{background:#a0a0d0}" +
            "tr.odd{background:#c0c0f0}" +
            "td.empty{background:#ffffff}";

    private StateSpaceLoader stateSpaceLoader;

    //private LayoutForm layoutForm;

    private JPanel mainPanel;
    private JPanel loadPanel;
    private JScrollPane scrollPane;
    private JButton button;
    private JPanel buttonPanel;

    public CPNTransformation(FileDialog fileDialog) {
        stateSpaceLoader = new StateSpaceLoader(fileDialog);
        setUp();
    }

    public CPNTransformation(PetriNet petriNet, FileDialog fileDialog) {
        stateSpaceLoader = new StateSpaceLoader(petriNet, fileDialog);
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

            }
        });
//        resultsPanel.add(resultsPane);
//        GenerateResultsForm generateResultsForm = new GenerateResultsForm(new GenerateResultsForm.GoAction() {
//            @Override
//            public void go(int threads) {
//                showSteadyState(threads);
//            }
//        });
//        generatePanel.add(generateResultsForm.getPanel());
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setResult(JLayeredPane pane) {
        scrollPane.add(pane);
    }
}
