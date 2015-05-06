package pipe.gui.plugin.concrete;

import pipe.gui.PetriNetTab;
import pipe.gui.plugin.GuiModule;
import pipe.gui.transformation.CPNTransformation;
import pipe.gui.transformation.CPNTransformer;
import uk.ac.imperial.pipe.io.PetriNetIOImpl;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import javax.xml.bind.JAXBException;

import java.awt.FileDialog;
import java.io.File;

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
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);

        CPNTransformation pane = new CPNTransformation(petriNet, selector);
        CPNTransformer transformer = new CPNTransformer(petriNet);
        petriNet = transformer.getTransformed();
        PetriNetIOImpl writer;
        try {
            writer = new PetriNetIOImpl();
            File file = new File("D:\\new.xml");
            writer.writeTo(file.getAbsolutePath(), petriNet);
        } catch (JAXBException e) {
            e.printStackTrace();
        }


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
