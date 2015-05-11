package pipe.gui.transformation;

import uk.ac.imperial.pipe.models.petrinet.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by andrii-usov on 06.05.2015.
 */
public class CPNTransformer {

    private PetriNet petriNet;

    private PetriNet newPetriNet;

    private static final Color DEFAULT_COLOR = Color.BLACK;

    private static final String DEFAULT_NAME = "Default";

    private HashMap<String, List<Connectable>> transformed;

    private HashMap<String, Boolean> visited;

    private HashSet<String> colors;

    public CPNTransformer (PetriNet net) {
        if (net == null) throw new IllegalArgumentException("Petri Net is null");
        this.petriNet = net;
        this.newPetriNet = new PetriNet();
        newPetriNet.setName(petriNet.getName());
        newPetriNet.setPnmlName(petriNet.getPnmlName());
        Token token = new ColoredToken(DEFAULT_NAME, DEFAULT_COLOR);
        newPetriNet.addToken(token);
        transformed = new HashMap<>();
        visited = new HashMap<>();
    }

    private void connect(Connectable node) {
        if (isVisited(node.getName())) return;
        visit(node.getName());

        if (node instanceof Place) {
            Place place = (Place) node;
            for (InboundArc ia: petriNet.outboundArcs(place)) {
                Transition adj = ia.getTarget();
                createArcs(place, adj, ia);
                connect(adj);
            }
        } else if (node instanceof  Transition) {
            Transition transition = (Transition) node;
            for (OutboundArc oa: petriNet.outboundArcs(transition)) {
                Place adj = oa.getTarget();
                createArcs(transition, adj, oa);
                connect(adj);
            }
        }
    }

    private void createArcs(Place place, Transition transition, InboundArc arc) {
        for (Connectable p: this.transformed.get(place.getName())) {
            Place pl = (Place) p;
            for (Connectable t: this.transformed.get(transition.getName())) {
                Transition tr = (Transition) t;
                for (String key: arc.getTokenWeights().keySet()) {
                    if (pl.getName().endsWith(key) && tr.getName().endsWith(key)) {
                        InboundArc newArc = null;
                        if (arc.getType() == ArcType.INHIBITOR) {
                            newArc = new InboundInhibitorArc(pl, tr);
                        } else {
                            HashMap<String, String> tokenWeights = new HashMap<>();
                            tokenWeights.put(DEFAULT_NAME, arc.getWeightForToken(key));
                            newArc = new InboundNormalArc(pl, tr, tokenWeights);
                        }
                        newPetriNet.addArc(newArc);
                    }
                }
            }
        }
    }

    private void createArcs(Transition transition, Place place, OutboundArc arc) {
        for (Connectable t: this.transformed.get(transition.getName())) {
            Transition tr = (Transition) t;
            for (Connectable p: this.transformed.get(place.getName())) {
                Place pl = (Place) p;
                for (String key: arc.getTokenWeights().keySet()) {
                    if (tr.getName().endsWith(key) && pl.getName().endsWith(key) ) {
                        HashMap<String, String> tokenWeights = new HashMap<>();
                        tokenWeights.put(DEFAULT_NAME, arc.getWeightForToken(key));
                        OutboundArc newArc = new OutboundNormalArc(tr, pl, tokenWeights);
                        newPetriNet.addArc(newArc);
                    }
                }
            }
        }
    }

    private List<Connectable> transformPlace(Place place) {

        ArrayList<Connectable> transformed = new ArrayList<>();
        HashSet<String> colors = new HashSet<>();
        for(String key: place.getTokenCounts().keySet()) {
            if (place.getTokenCount(key) > 0) {
                colors.add(key);
            }
        }

        for(InboundArc ia: petriNet.outboundArcs(place)) {
            for (String key: ia.getTokenWeights().keySet()) {
                if (!ia.getWeightForToken(key).equals("") && !ia.getWeightForToken(key).equals("0")) {
                    colors.add(key);
                }
            }
        }

        int activeColorsCounter = colors.size();

        if (activeColorsCounter > 0) {
            int placesCounter = 0;
            int start = (int)(place.getX() -(30.0*activeColorsCounter + 30.0*(activeColorsCounter - 1))/2 + 15.0);
            for (String id: colors) {
                Place newPlace = clonePlace(place, place.getId() + "_" + id, place.getName() + "_" + id);
                newPlace.setTokenCount(DEFAULT_NAME, place.getTokenCount(id));
                newPlace.setX(start + placesCounter * 45);
                newPetriNet.addPlace(newPlace);
                transformed.add(newPlace);
                placesCounter++;
            }
        } else {
            Place newPlace = clonePlace(place, place.getId(), place.getName());
            newPetriNet.addPlace(newPlace);
            transformed.add(newPlace);
        }
        return transformed;
    }

    private List<Connectable> transformTransition(Transition transition) {
        HashSet<String> colors = new HashSet<>();
        ArrayList<Connectable> transformed = new ArrayList<>();

        for(OutboundArc oa: petriNet.outboundArcs(transition)) {
            for (String key: oa.getTokenWeights().keySet()) {
                if (!oa.getWeightForToken(key).equals("") && !oa.getWeightForToken(key).equals("0")) {
                    colors.add(key);
                }
            }

        }

        for(InboundArc ia: petriNet.inboundArcs(transition)) {
            for (String key: ia.getTokenWeights().keySet()) {
                if (!ia.getWeightForToken(key).equals("") && !ia.getWeightForToken(key).equals("0")) {
                    colors.add(key);
                }
            }

        }

        if (colors.size() > 0) {
            int transitionCounter = 0;
            int start = (int)(transition.getX() -(30.0*colors.size() + 30.0*(colors.size() - 1))/2 + 15.0);
            for (String id: colors) {
                Transition newTransition = cloneTransition(transition, transition.getId() + "_" + id, transition.getName() + "_" + id);
                newTransition.setX(start + transitionCounter * 45);
                newPetriNet.addTransition(newTransition);
                transformed.add(newTransition);
                transitionCounter++;
            }
        } else {
            Transition newTransition = cloneTransition(transition, transition.getId(), transition.getName());
            newPetriNet.addTransition(newTransition);
            transformed.add(newTransition);
        }
        return transformed;
    }

    private boolean isVisited(String name) {
        if (visited.get(name) == null || visited.get(name) == Boolean.FALSE) { return false;}
        return true;
    }

    private void visit(String name) {
        visited.put(name, Boolean.TRUE);
    }

    private Place clonePlace(Place source, String name, String id) {
        if (source == null || name == null || id == null) throw new IllegalArgumentException("Input parameters shouldn't be null");
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

    private Transition cloneTransition(Transition source, String name, String id) {
        if (source == null || name == null || id == null) throw new IllegalArgumentException("Input parameters should't be null");
        Transition dest = new DiscreteTransition(id, name);
        dest.setNameXOffset(source.getNameXOffset());
        dest.setNameYOffset(source.getNameYOffset());
        dest.setY(source.getY());
        dest.setX(source.getX());
        dest.setAngle(source.getAngle());
        dest.setInfiniteServer(source.isInfiniteServer());
        dest.setPriority(source.getPriority());
        dest.setRate(source.getRate());
        dest.setTimed(source.isTimed());

        dest.disable();
        return dest;
    }

    public PetriNet getTransformed() {
        for (Place place: petriNet.getPlaces()) {
            transformed.put(place.getName(), transformPlace(place));
        }

        for (Transition transition: petriNet.getTransitions()) {
            transformed.put(transition.getName(), transformTransition(transition));
        }

        for (Place place: petriNet.getPlaces()) {
            connect(place);
        }

        for (Transition transition: petriNet.getTransitions()) {
            connect(transition);
        }

        return newPetriNet;
    }



}
