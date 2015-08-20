package ca.uqam.latece.reamodeler.features;

import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import ca.uqam.latece.reamodeler.REAModelerUtil;

import rea.Agent;
import rea.ValueModel;

public class AgentDirectEditFeature extends AbstractDirectEditingFeature {

	public AgentDirectEditFeature(IFeatureProvider fp){
		super(fp);
	}
	
	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}
	
	@Override
    public boolean canDirectEdit(IDirectEditingContext context) {
        PictogramElement pe = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pe);
        GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
        // support direct editing, if it is an Agent, and the user clicked
        // directly on the text and not somewhere else in the rectangle
        if (bo instanceof Agent && ga instanceof Text) {
            return true;
        }
        // direct editing not supported in all other cases
        return false;
    }

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		// return the current name of the EClass
        PictogramElement pe = context.getPictogramElement();
        Agent agent = (Agent) getBusinessObjectForPictogramElement(pe);
        return agent.getName();
	}
	
	@Override
    public String checkValueValid(String value, IDirectEditingContext context) {
        if (value.length() < 1)
            return "Please enter any text as agent name.";
        if (value.contains("\n"))
            return "Line breakes are not allowed in agent names.";
        ValueModel valueModel = REAModelerUtil
				.getValueModelObject(getDiagram());
        List<Agent> agents = valueModel.getAgents();
        Agent currentAgent = (Agent) getBusinessObjectForPictogramElement(context.getPictogramElement());
        for(Agent a: agents){
        	if(a!=currentAgent && a.getName().equals(value)) return "This name have already been set to another agent. Please set another name or consider creating another instance of the agent named "+value+". (by Copy/Paste)";
        } 
        // null means, that the value is valid
        return null;
    }
	
	public void setValue(String value, IDirectEditingContext context) {
        // set the new name for the MOF class
        PictogramElement pe = context.getPictogramElement();
        Agent agent = (Agent) getBusinessObjectForPictogramElement(pe);
        agent.setName(value);
 
        // Explicitly update the shape to display the new value in the diagram
        // Note, that this might not be necessary in future versions of Graphiti
        // (currently in discussion)
        Text displayedName = (Text)pe.getGraphicsAlgorithm();
        displayedName.setValue(value);
 
        // we know, that pe is the Shape of the Text, so its container is the
        // main shape of the EClass
        updatePictogramElement(((Shape) pe).getContainer());
    }

}
