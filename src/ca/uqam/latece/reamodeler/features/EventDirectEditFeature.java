package ca.uqam.latece.reamodeler.features;

import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import ca.uqam.latece.reamodeler.REAModelerUtil;

import rea.Agent;
import rea.Event;
import rea.REAProcess;
import rea.ValueModel;

public class EventDirectEditFeature extends AbstractDirectEditingFeature {

	public EventDirectEditFeature(IFeatureProvider fp){
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
        // support direct editing, if it is an Event, and the user clicked
        // directly on the text and not somewhere else in the rectangle
        if (bo instanceof Event && ga instanceof Text) {
            return true;
        }
        // direct editing not supported in all other cases
        return false;
    }

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		// return the current name of the Event
        PictogramElement pe = context.getPictogramElement();
        Event event = (Event) getBusinessObjectForPictogramElement(pe);
        return event.getName();
	}
	
	@Override
    public String checkValueValid(String value, IDirectEditingContext context) {
        if (value.length() < 1)
            return "Please enter any text as agent name.";
        if (value.contains("\n"))
            return "Line breakes are not allowed in agent names.";
        
        Event currentEvent = (Event) getBusinessObjectForPictogramElement(context.getPictogramElement());
        REAProcess process = (REAProcess)currentEvent.eContainer();
        List<Event> events;
        if(currentEvent.isDecrementEvt()) events = process.getDecrementEvt();
        else events = process.getIncrementEvt();
        
        for(Event e: events){
        	if(e!=currentEvent && e.getName().equals(value)) return "Event name duplication is not permitted within the same REA Process. Please choose another name.";
        } 
        // null means, that the value is valid
        return null;
    }
	
	public void setValue(String value, IDirectEditingContext context) {
        // set the new name for the MOF class
        PictogramElement pe = context.getPictogramElement();
        Event event = (Event) getBusinessObjectForPictogramElement(pe);
        event.setName(value);
 
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
