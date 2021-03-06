package pipe.actions.gui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Exports the Petri net as a PNG
 */
public class ExportPNGAction extends GuiAction {
    /**
     * Constructor
     * Sets short cut to ctrl G
     */
    public ExportPNGAction() {
        super("PNG", "Export the net to PNG format", KeyEvent.VK_G, InputEvent.META_DOWN_MASK);
    }

    /**
     * Saves the Petri net as a PNG when selected.
     *
     * Currently this feature has not been implemented.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO
    }
}
