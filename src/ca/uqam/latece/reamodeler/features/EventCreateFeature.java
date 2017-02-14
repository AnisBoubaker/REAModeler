package ca.uqam.latece.reamodeler.features;


import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;

import rea.Event;
import rea.REAProcess;
import rea.impl.ReaFactoryImpl;

import ca.uqam.latece.reamodeler.REAImageProvider;

public class EventCreateFeature extends AbstractCreateFeature{	
	
	public EventCreateFeature(IFeatureProvider fp){
		super(fp, "Event", "Ass a new REA Event");
	}
	
	public boolean canCreate(ICreateContext context) {
		ContainerShape targetContainer = context.getTargetContainer();
		String propertyValue = Graphiti.getPeService().getPropertyValue(targetContainer, "shape-id");
		if(
				propertyValue!=null && 
				
				( propertyValue.equals("reaProcess_incrementContainer") ||
						propertyValue.equals("reaProcess_decrementContainer")
						)
				) 
			return true;
		return false;
	}

	public Object[] create(ICreateContext context) {
		//String newClassName=ExampleUtil.askString("Create REA Even", "Enter the name of this event", "");;
		Event newEvent = ReaFactoryImpl.eINSTANCE.createEvent();
		//newEvent.setName(newClassName);
		
		//Retrieve the REA Process
		ContainerShape targetContainer = context.getTargetContainer();
		String targetContainerPropertyValue = Graphiti.getPeService().getPropertyValue(targetContainer, "shape-id");
		ContainerShape processContainer = targetContainer.getContainer();
		REAProcess reaProcess = (REAProcess)getBusinessObjectForPictogramElement(processContainer);
		
		//Add the new created event to the corresponding EList (increment or decrement) depending on
		//the containing shape the event is positioned on
		if(targetContainerPropertyValue.equals("reaProcess_decrementContainer")){ reaProcess.getDecrementEvt().add(newEvent);}
		else if(targetContainerPropertyValue.equals("reaProcess_incrementContainer")){ reaProcess.getIncrementEvt().add(newEvent);}
        addGraphicalRepresentation(context, newEvent);
        getFeatureProvider().getDirectEditingInfo().setActive(true);
        return new Object[] {newEvent};
	}
	
	public String getCreateImageId() {
		return REAImageProvider.IMG_EVENT;
	}
	
}
