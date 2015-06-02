package pipe.gui.decomposition;

import uk.ac.imperial.pipe.io.PetriNetIOImpl;
import uk.ac.imperial.pipe.models.petrinet.*;
import uk.ac.imperial.pipe.models.petrinet.name.NormalPetriNetName;

import javax.xml.bind.JAXBException;
import java.util.*;


/**
 * Created by andrii-usov on 23.05.2015.
 */
public class Decomposer {

    private class FunctionalSubnet {

        private PetriNet subNet;

        public HashSet<Place> inputPlaces = new HashSet<>();

        public HashSet<Place> outputPlaces = new HashSet<>();

        public HashSet<Place> internalPlaces = new HashSet<>();

        public HashSet<Transition> X = new HashSet<>();

        public HashSet<Transition> Y = new HashSet<>();

        public HashSet<Transition> Q = new HashSet<>();

        public FunctionalSubnet(String name) {
            this.subNet = new PetriNet();
            subNet.setName(new NormalPetriNetName(name));
            subNet.setPnmlName(petriNet.getPnmlName());
        }

        public void addTransitionAndRaiseNet(Transition transition) {
            subNet.addTransition(transition);
            decomposed.add(transition.getName());
            for (InboundArc ia: petriNet.inboundArcs(transition)) {
                subNet.addPlace(ia.getSource());
                subNet.addArc(ia);
            }

            for (OutboundArc oa: petriNet.outboundArcs(transition)) {
                subNet.addPlace(oa.getTarget());
                subNet.addArc(oa);
            }

            doPreprocessNet();
        }

        private void doPreprocessNet() {
            for (Place place: subNet.getPlaces()) {
                if (subNet.outboundArcs(place).size() == 0 && inboundArcs(place, subNet).size() != 0) {
                    outputPlaces.add(place);
                } else if (inboundArcs(place, subNet).size() == 0 && subNet.outboundArcs(place).size() != 0) {
                    inputPlaces.add(place);
                } else {
                    internalPlaces.add(place);
                }
            }

            for (Place p: inputPlaces) {
                X.addAll(outboundNodes.get(p.getName()));
            }

            for (Place p: outputPlaces) {
                Y.addAll(inboundNodes.get(p.getName()));
            }

            for (Place p: internalPlaces) {
                Q.addAll(inboundNodes.get(p.getName()));
                Q.addAll(outboundNodes.get(p.getName()));
            }
        }

        public boolean isComplete() {
            Collection<Transition> R = getTransitions();
            if (R.containsAll(X) && R.containsAll(Y) && R.containsAll(Q)) return true;

            return false;
        }

        public Collection<Transition> getTransitions() {
            return subNet.getTransitions();
        }

        public PetriNet getPetriSubNet() {
            return subNet;
        }

        public void absorbTransitions() {
            Collection<Transition> R = getTransitions();
            HashSet<Transition> absorbed = new HashSet<>();
            absorbed.addAll(X);
            absorbed.addAll(Y);
            absorbed.addAll(Q);
            absorbed.removeAll(R);
            for (Transition t: absorbed) {
                addTransitionAndRaiseNet(t);
            }
        }
    }

    private HashSet<String> decomposed = new HashSet<>();

    public HashMap<String, HashSet<Transition>> inboundNodes = new HashMap<>();

    public HashMap<String, HashSet<Transition>> outboundNodes = new HashMap<>();

    private PetriNet petriNet;

    public Decomposer(PetriNet net) {
        if (net == null) throw new IllegalArgumentException("Input Petri net shouldn't be null");
        this.petriNet = net;

        for (Place place: petriNet.getPlaces()) {
            for (OutboundArc oa: inboundArcs(place, petriNet)) {
                if (inboundNodes.get(place.getName()) == null) {
                    inboundNodes.put(place.getName(), new HashSet<Transition>());
                }
                inboundNodes.get(place.getName()).add(oa.getSource());
            }
            for (InboundArc ia: petriNet.outboundArcs(place)) {
                if (outboundNodes.get(place.getName()) == null) {
                    outboundNodes.put(place.getName(), new HashSet<Transition>());
                }
                outboundNodes.get(place.getName()).add(ia.getTarget());
            }
        }
    }

    private void saveSubnet(FunctionalSubnet subnet, String fileFullPath) {
        PetriNetIOImpl writer;
        try {
            writer = new PetriNetIOImpl();
            writer.writeTo(fileFullPath, subnet.getPetriSubNet());
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
    }

    private Collection<OutboundArc> inboundArcs(Place place, PetriNet net) {
        LinkedList<OutboundArc> inbound = new LinkedList<>();

        for (OutboundArc arc : net.getOutboundArcs()) {
            if (arc.getTarget().equals(place)) {
                inbound.add(arc);
            }
        }

        return inbound;
    }

    private void doDecompose(FunctionalSubnet subnet, int id) {
        if (!subnet.isComplete()) {
            ArrayList<Transition>  absorbedTransactions = new ArrayList<>();
            Collection<Transition> R = subnet.getPetriSubNet().getTransitions();

            subnet.absorbTransitions();

            doDecompose(subnet, id);
        }
    }

    public void decomposePetriNet() {
        int i = 0;
        for (Transition transition: petriNet.getTransitions()) {
            if (!decomposed.contains(transition.getName())) {
                FunctionalSubnet subnet = new FunctionalSubnet(petriNet.getName().getName() + "_" + i);
                subnet.addTransitionAndRaiseNet(transition);
                doDecompose(subnet, i);
                saveSubnet(subnet, "E://" + petriNet.getName().getName() + "_" + i + ".xml");
                i++;
            }
        }
    }


}
