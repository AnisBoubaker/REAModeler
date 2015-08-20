package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import rea.REAProcess;

public class REAProcessUpdateFeature extends AbstractUpdateFeature {

	public REAProcessUpdateFeature(IFeatureProvider fp){
		super(fp);
	}
	
	@Override
	public boolean canUpdate(IUpdateContext context) {
		 Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		 return (bo instanceof REAProcess);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		// retrieve name from pictogram model
        String pictogramName = null;
        PictogramElement pictogramElement = context.getPictogramElement();
        if (pictogramElement instanceof ContainerShape) {
            ContainerShape cs = (ContainerShape) pictogramElement;
            for (Shape shape : cs.getChildren()) {
                if (shape.getGraphicsAlgorithm() instanceof Text
                		&& Graphiti.getPeService().getPropertyValue(shape, "shape-id")!=null
                		&& Graphiti.getPeService().getPropertyValue(shape, "shape-id").equals("reaProcess_processId")) {
                    Text text = (Text) shape.getGraphicsAlgorithm();
                    pictogramName = text.getValue();
                    break;
                }
            }
        }
 
        // retrieve name from business model
        String businessName = null;
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        if (bo instanceof REAProcess) {
        	REAProcess process = (REAProcess) bo;
            businessName = process.getID();
        }
 
        // update needed, if names are different
        boolean updateNameNeeded =
            ((pictogramName == null && businessName != null) || 
                (pictogramName != null && !pictogramName.equals(businessName)));
        if (updateNameNeeded) {
            return Reason.createTrueReason("Process ID is out of date");
        } else {
            return Reason.createFalseReason();
        }
	}

	@Override
	public boolean update(IUpdateContext context) {
		// retrieve name from business model
        String businessName = null;
        PictogramElement pictogramElement = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        if (bo instanceof REAProcess) {
        	REAProcess process = (REAProcess) bo;
            businessName = process.getID();
        }
 
        // Set name in pictogram model
        if (pictogramElement instanceof ContainerShape) {
            ContainerShape cs = (ContainerShape) pictogramElement;
            for (Shape shape : cs.getChildren()) {
            	if (shape.getGraphicsAlgorithm() instanceof Text
                		&& Graphiti.getPeService().getPropertyValue(shape, "shape-id")!=null
                		&& Graphiti.getPeService().getPropertyValue(shape, "shape-id").equals("reaProcess_processId")) {
                    Text text = (Text) shape.getGraphicsAlgorithm();
                    text.setValue(businessName);
                    return true;
                }
            }
        }
 
        return false;
	}

}
