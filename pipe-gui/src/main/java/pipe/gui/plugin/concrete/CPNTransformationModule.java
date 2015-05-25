package pipe.gui.plugin.concrete;

import pipe.gui.plugin.GuiModule;
import pipe.gui.transformation.CPNTransformation;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import javax.swing.*;
import java.awt.FileDialog;


/**
 * Module for generation equivalent P/T net for Coloured Petri net
 * 
 * @author andrii-usov
 *
 */
public class CPNTransformationModule implements GuiModule {
    /**
     * Starts the CPNTransformation analysis module
     * @param petriNet current Petri net to use
     */
    @Override
    public void start(PetriNet petriNet) {
        JFrame frame = new JFrame("CPN Transformation");
        FileDialog loader = new FileDialog(frame, "Select petri net", FileDialog.LOAD);
        FileDialog saver = new FileDialog(frame, "Select file to save Petri net", FileDialog.SAVE);

        CPNTransformation pane = new CPNTransformation(petriNet, loader, saver);

        frame.setContentPane(pane.getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     *
     * @return GSPN Analysis
     */
    @Override
    public String getName() {
        return "CPN Transformation";
    }
}
