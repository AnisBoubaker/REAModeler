package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

public class AgentLayoutFeature extends AbstractLayoutFeature {

	private static final int HEIGHT = 26;
	 
    private static final int MIN_WIDTH = 50;

	public AgentLayoutFeature(IFeatureProvider fp){
		super(fp);
	}
	
	public boolean canLayout(ILayoutContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof ContainerShape) return true;
			return false;
	}

	public boolean layout(ILayoutContext context) {
		boolean anythingChanged = false;
		
		ContainerShape containerShape = (ContainerShape)context.getPictogramElement();
		GraphicsAlgorithm containerShapeGa = containerShape.getGraphicsAlgorithm();
		// height
        if (containerShapeGa.getHeight() != HEIGHT) {
        	containerShapeGa.setHeight(HEIGHT);
            anythingChanged = true;
        }
 
        // width
        if (containerShapeGa.getWidth() < MIN_WIDTH) {
        	containerShapeGa.setWidth(MIN_WIDTH);
            anythingChanged = true;
        }
		
        int containerWidth = containerShapeGa.getWidth();
        for (Shape shape : containerShape.getChildren()){
            GraphicsAlgorithm graphicsAlgorithm = shape.getGraphicsAlgorithm();
            IGaService gaService = Graphiti.getGaService();
            IDimension size = 
                 gaService.calculateSize(graphicsAlgorithm);
            if (containerWidth != size.getWidth()-21) {
                if (graphicsAlgorithm instanceof Text) {
                    gaService.setWidth(graphicsAlgorithm, containerWidth-21);
                    anythingChanged = true;
                }
            }
        }
		
		return anythingChanged;
	}

}
