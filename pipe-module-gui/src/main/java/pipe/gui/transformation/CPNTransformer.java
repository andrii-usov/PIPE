package pipe.gui.transformation;

import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.DiscretePlace;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Token;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by andrii-usov on 06.05.2015.
 */
public class CPNTransformer {

    private PetriNet petriNet;

    private Color defaultColor = Color.BLACK;

    public CPNTransformer (PetriNet net) {
        if (net == null) throw new IllegalArgumentException("Petri Net is null");
        this.petriNet = net;

    }

    public void transformPlaces() {
        ArrayList<Place> placesToAdd = new ArrayList<>();
        ArrayList<Place> placesToDelete = new ArrayList<>();
        for(Place p: petriNet.getPlaces()) {
            int counter = 0;
            for(Integer i: p.getTokenCounts().values()) {
                if (i > 0) {
                    counter++;
                    if (counter > 1) {
                        break;
                    }
                }
            }
            if (counter > 1) {
                placesToDelete.add(p);
                counter = 0;
                int start = (int)(p.getX() -(30.0*p.getTokenCounts().keySet().size() + 15.0*(p.getTokenCounts().keySet().size() - 1))/2 + 15.0);
                for (String id: p.getTokenCounts().keySet()) {
                    if (p.getTokenCount(id) > 0) {
                        Place newPlace = new DiscretePlace(p.getId() + "_" + id, p.getName() + "_" + id);
                        newPlace.setTokenCount(id, p.getTokenCount(id));
                        newPlace.setX(start + counter * 45);
                        copyMiscInformation(p, newPlace);
                        placesToAdd.add(newPlace);
                        counter++;
                    }
                }
            }
        }

        for(Place p: placesToDelete) {
            try {
                petriNet.removePlace(p);
            } catch (PetriNetComponentException e) {
                e.printStackTrace();
            }
        }

        for(Place p: placesToAdd) {
            petriNet.addPlace(p);
        }

    }

    private void copyMiscInformation(Place source, Place dest) {
        dest.setCapacity(source.getCapacity());
        dest.setMarkingXOffset(source.getMarkingXOffset());
        dest.setMarkingYOffset(source.getMarkingYOffset());
        dest.setNameXOffset(source.getNameXOffset());
        dest.setNameYOffset(source.getNameYOffset());
        dest.setY(source.getY());
    }

    public PetriNet getTransformed() {
        transformPlaces();
        return petriNet;
    }



}
