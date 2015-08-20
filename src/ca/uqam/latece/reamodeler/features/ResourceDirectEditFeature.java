package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import rea.Resource;

public class ResourceDirectEditFeature extends AbstractDirectEditingFeature {

	public ResourceDirectEditFeature(IFeatureProvider fp) {
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
        // support direct editing, if it is a Resource, and the user clicked
        // directly on the text and not somewhere else in the rectangle
        String shapeId = Graphiti.getPeService().getPropertyValue(pe, "shape-id");
        if (bo instanceof Resource && ga instanceof Text && shapeId.equals("reaResource_name")) {
            return true;
        }
        // direct editing not supported in all other cases
        return false;
    }

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		// return the current name of the EClass
        PictogramElement pe = context.getPictogramElement();
        Resource resource = (Resource) getBusinessObjectForPictogramElement(pe);
        return resource.getName();
	}
	
	@Override
    public String checkValueValid(String value, IDirectEditingContext context) {
        if (value.length() < 1)
            return "Please enter any text as resource name.";
        if (value.contains("\n"))
            return "Line breakes are not allowed in resource names.";
        // null means, that the value is valid
        return null;
    }
	
	public void setValue(String value, IDirectEditingContext context) {
        // set the new name for the MOF class
        PictogramElement pe = context.getPictogramElement();
        Resource resource = (Resource) getBusinessObjectForPictogramElement(pe);
        resource.setName(value);
 
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
