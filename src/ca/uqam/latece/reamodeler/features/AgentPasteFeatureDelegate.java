package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;

import rea.Agent;

public class AgentPasteFeatureDelegate extends AbstractPasteFeature {

	public AgentPasteFeatureDelegate(IFeatureProvider fp){
		super(fp);
	}
	
	@Override
	public void paste(IPasteContext context) {
		// we already verified, that we paste directly in the diagram
        PictogramElement[] pes = context.getPictogramElements();
        ContainerShape destinationShape = (ContainerShape) pes[0];
        // get the EClasses from the clipboard without copying them
        // (only copy the pictogram element, not the business object)
        // then create new pictogram elements using the add feature
        Object[] objects = getFromClipboard();
        for (Object object : objects) {
            AddContext ac = new AddContext();
            ac.setTargetContainer(destinationShape);
            ac.setLocation(0,0); // for simplicity paste at (0, 0)
            addGraphicalRepresentation(ac, object);
        }
	}

	@Override
	public boolean canPaste(IPasteContext context) {
		PictogramElement[] pes = context.getPictogramElements();
        if (pes.length != 1) {
        	return false; //We may paste to only one shape at a time
        }
        //We can only paste on an REA Process (either on the increment container or the decrement container)
        String propertyValue = Graphiti.getPeService().getPropertyValue(pes[0], "shape-id");
        if(
				propertyValue==null || 
				
				( !propertyValue.equals("reaProcess_incrementContainer") &&
						!propertyValue.equals("reaProcess_decrementContainer")
						)
				) {
        	return false; //We may only paste agents on REA Processes
        }
        //Make sure we have an Agent in the clipboard
        Object[] fromClipboard = getFromClipboard();
        if (fromClipboard == null || fromClipboard.length != 1) {
            return false;
        }
        for (Object object : fromClipboard) {
            if (!(object instanceof Agent)) {
                return false;
            }
        }
		return true;
	}

}
