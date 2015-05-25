package pipe.gui.decomposition;

import org.antlr.v4.runtime.atn.LexerATNSimulator;
import uk.ac.imperial.pipe.io.PetriNetIOImpl;
import uk.ac.imperial.pipe.models.petrinet.*;
import uk.ac.imperial.pipe.models.petrinet.name.NameVisitor;
import uk.ac.imperial.pipe.models.petrinet.name.NormalPetriNetName;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetName;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Created by andrii-usov on 23.05.2015.
 */
public class Decomposer {

//    private enum NodeType {
//        INBOUND, OUTBOUND, INTERNAL
//    }

    private class FunctionalSubnet {
        private PetriNet subNet;

        private ArrayList<Transition> inboundTransitions = new ArrayList<>();

        private ArrayList<Transition> outboundTransitions = new ArrayList<>();

        private ArrayList<Transition> internalTransitions = new ArrayList<>();

        private ArrayList<Place> sources = new ArrayList<>();

        private ArrayList<Place> drains = new ArrayList<>();

        private ArrayList<Place> internalPlaces = new ArrayList<>();

        public FunctionalSubnet(String name) {
            this.subNet = new PetriNet();
            subNet.setName(new NormalPetriNetName(name));
            subNet.setPnmlName(petriNet.getPnmlName());
        }

        public void addTransitionAndRaiseNet(Transition transition) {
            subNet.addTransition(transition);
            for (InboundArc ia: petriNet.inboundArcs(transition)) {
                subNet.addPlace(ia.getSource());
                subNet.addArc(ia);
            }

            for (OutboundArc oa: petriNet.outboundArcs(transition)) {
                subNet.addPlace(oa.getTarget());
                subNet.addArc(oa);
            }

            doPreprocessMap();
        }

        public boolean isComplete() {
            Collection<Transition> transitions = subNet.getTransitions();
            for (Transition tr: inboundTransitions) {
                if (!transitions.contains(tr)) {
                    return false;
                }
            }

            for (Transition tr: outboundTransitions) {
                if (!transitions.contains(tr)) {
                    return false;
                }
            }

            for (Transition tr: internalTransitions) {
                if (!transitions.contains(tr)) {
                    return false;
                }
            }

            return true;
        }

        public PetriNet getPetriSubNet() {
            return subNet;
        }

        public  ArrayList<Transition> getInboundTransitions() {
            return this.inboundTransitions;
        }

        public  ArrayList<Transition> getOutboundTransitions() {
            return this.outboundTransitions;
        }

        public  ArrayList<Transition> getInternalTransitions() {
            return this.internalTransitions;
        }

        private void doPreprocessMap() {
            for (Place place: subNet.getPlaces()) {
                if (subNet.outboundArcs(place).size() == 0 && inboundArcs(place).size() != 0) {
                    drains.add(place);
                    for (OutboundArc oa: inboundArcs(place)) {
                        inboundTransitions.add(oa.getSource());
                    }
                } else if (inboundArcs(place).size() == 0 && subNet.outboundArcs(place).size() != 0) {
                    sources.add(place);
                    for (InboundArc ia: subNet.outboundArcs(place)) {
                        outboundTransitions.add(ia.getTarget());
                    }
                } else {
                    internalPlaces.add(place);
                    for (InboundArc ia: subNet.outboundArcs(place)) {
                        internalTransitions.add(ia.getTarget());
                    }

                    for (OutboundArc oa: inboundArcs(place)) {
                        internalTransitions.add(oa.getSource());
                    }
                }
            }
        }

        private Collection<OutboundArc> inboundArcs(Place place) {
            LinkedList<OutboundArc> inbound = new LinkedList<>();

            for (OutboundArc arc : subNet.getOutboundArcs()) {
                if (arc.getTarget().equals(place)) {
                    inbound.add(arc);
                }
            }

            return inbound;
        }

    }





    private HashMap<String, Integer> decomposed = new HashMap<>();

    private PetriNet petriNet;

    public Decomposer(PetriNet net) {
        if (net == null) throw new IllegalArgumentException("Input Petri net shouldn't be null");
        this.petriNet = net;
    }




//    private NodeType isGetNodeType(Connectable node) {
//        NodeType result = null;
//        if (node instanceof Place) {
//            Place p = (Place) node;
//            if (petriNet.outboundArcs(p).size() == 0 && inboundArcs(p).size() != 0) {
//                result = NodeType.OUTBOUND;
//            } else if (inboundArcs(p).size() == 0 && petriNet.outboundArcs(p).size() != 0) {
//                result = NodeType.INBOUND;
//            } else {
//                result = NodeType.INTERNAL;
//            }
//        } else if (node instanceof Transition) {
//            Transition t = (Transition) node;
//            if (petriNet.outboundArcs(t).size() == 0 && petriNet.inboundArcs(t).size() != 0) {
//                result = NodeType.OUTBOUND;
//            } else if (petriNet.inboundArcs(t).size() == 0 && petriNet.outboundArcs(t).size() != 0) {
//                result = NodeType.INBOUND;
//            } else {
//                result = NodeType.INTERNAL;
//            }
//        }
//
//        return result;
//    }



    private void saveSubnet(FunctionalSubnet subnet, String fileFullPath) {
        PetriNetIOImpl writer;
        try {
            writer = new PetriNetIOImpl();
            writer.writeTo(fileFullPath, subnet.getPetriSubNet());
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
    }

    private void doDecompose(FunctionalSubnet subnet, int id) {
        if (!subnet.isComplete()) {
            ArrayList<Transition>  absorbedTransactions = new ArrayList<>();
            Collection<Transition> R = subnet.getPetriSubNet().getTransitions();

            for (Transition tr: subnet.getInboundTransitions()) {
                if (!R.contains(tr)) {
                    absorbedTransactions.add(tr);
                }
            }

            for (Transition tr: subnet.getOutboundTransitions()) {
                if (!R.contains(tr)) {
                    absorbedTransactions.add(tr);
                }
            }

            for (Transition tr: subnet.getInternalTransitions()) {
                if (!R.contains(tr)) {
                    absorbedTransactions.add(tr);
                }
            }

            for (Transition tr: absorbedTransactions) {
                decomposed.put(tr.getName(), id);
                subnet.addTransitionAndRaiseNet(tr);
            }

            doDecompose(subnet, id);
        }
    }

    public void decomposePetriNet() {
        int i = 0;
        for (Transition transition: petriNet.getTransitions()) {
            if (decomposed.get(transition.getName()) == null) {
                FunctionalSubnet subnet = new FunctionalSubnet(petriNet.getName().getName() + "_" + i);
                subnet.addTransitionAndRaiseNet(transition);
                decomposed.put(transition.getName(), i);
                doDecompose(subnet, i);
                saveSubnet(subnet, "E://" + petriNet.getName().getName() + "_" + i + ".xml");
                i++;
            }
        }
    }

}
