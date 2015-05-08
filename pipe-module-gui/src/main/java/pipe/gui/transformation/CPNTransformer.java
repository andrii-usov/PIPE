package pipe.gui.transformation;

import uk.ac.imperial.pipe.models.petrinet.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andrii-usov on 06.05.2015.
 */
public class CPNTransformer {

    private PetriNet petriNet;

    private PetriNet newPetriNet;

    private static final Color DEFAULT_COLOR = Color.BLACK;

    private static final String DEFAULT_NAME = "Default";

    private HashMap<String, Boolean> visited;

    public CPNTransformer (PetriNet net) {
        if (net == null) throw new IllegalArgumentException("Petri Net is null");
        this.petriNet = net;
        this.newPetriNet = new PetriNet();
        newPetriNet.setName(petriNet.getName());
        newPetriNet.setPnmlName(petriNet.getPnmlName());
        Token token = new ColoredToken(DEFAULT_NAME, DEFAULT_COLOR);
        newPetriNet.addToken(token);

        visited = new HashMap<>();
    }

    public void transform(Connectable node) {
        if (isVisited(node.getName())) return;
        if (node instanceof Place) {
            Place place = (Place) node;
            List<Place> transformed = transformPlace(place);
            visit(place.getName());

            for(InboundArc inboundArc: petriNet.outboundArcs(place)) {

            }

        } else if (node instanceof  Transition) {
            Transition transition = (Transition) node;
            transformTransition(transition);
            visit(transition.getName());
        }
    }

    private List<Place> transformPlace(Place place) {
        int colorsCounter = 0;
        ArrayList<Place> transformed = new ArrayList<>();
        for(Integer i: place.getTokenCounts().values()) {
            if (i > 0) {
                colorsCounter++;
            }
        }

        if (colorsCounter > 0) {
            int placesCounter = 0;
            int start = (int)(place.getX() -(30.0*colorsCounter + 30.0*(colorsCounter - 1))/2 + 15.0);
            for (String id: place.getTokenCounts().keySet()) {
                if (place.getTokenCount(id) > 0) {
                    Place newPlace = clonePlace(place, place.getId() + "_" + id, place.getName() + "_" + id);
                    newPlace.setTokenCount(DEFAULT_NAME, place.getTokenCount(id));
                    newPlace.setX(start + placesCounter * 45);

                    transformed.add(newPlace);
                    placesCounter++;
                }
            }
        } else {
            Place newPlace = clonePlace(place, place.getId(), place.getName());
            petriNet.addPlace(newPlace);
            transformed.add(newPlace);
        }
        return transformed;
    }

    public void transformTransition(Transition transition) {

    }

    private boolean isVisited(String name) {
        if (visited.get(name) == null || visited.get(name) == Boolean.FALSE) { return false;}
        return true;
    }

    private void visit(String name) {
        visited.put(name, Boolean.TRUE);
    }

    private Place clonePlace(Place source, String name, String id) {
        if (source == null || name == null || id == null) throw new IllegalArgumentException("Input parameters souldn't be null");
        Place dest = new DiscretePlace(id, name);
        dest.setCapacity(source.getCapacity());
        dest.setMarkingXOffset(source.getMarkingXOffset());
        dest.setMarkingYOffset(source.getMarkingYOffset());
        dest.setNameXOffset(source.getNameXOffset());
        dest.setNameYOffset(source.getNameYOffset());
        dest.setY(source.getY());
        dest.setX(source.getX());

        return dest;
    }

    public PetriNet getTransformed() {
        for (Place place: petriNet.getPlaces()) {
            transform(place);
        }

        for (Transition transition: petriNet.getTransitions()) {
            transform(transition);
        }

        return newPetriNet;
    }



}
