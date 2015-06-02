package pipe.gui.plugin.concrete;

import pipe.gui.decomposition.Decomposition;
import pipe.gui.plugin.GuiModule;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import java.awt.*;


/**
 * Module for generation equivalent P/T net for Coloured Petri net
 * 
 * @author andrii-usov
 *
 */
public class DecompositionModule implements GuiModule {
    /**
     * Starts the Decomposition module
     * @param petriNet current Petri net to use
     */
    @Override
    public void start(PetriNet petriNet) {
        JFrame frame = new JFrame("Decomposition");
        FileDialog loader = new FileDialog(frame, "Select petri net", FileDialog.LOAD);
        FileDialog saver = new FileDialog(frame, "Select file to save Petri net", FileDialog.SAVE);

        Decomposition pane = new Decomposition(petriNet, loader, saver);

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
        return "Decomposition";
    }
}
