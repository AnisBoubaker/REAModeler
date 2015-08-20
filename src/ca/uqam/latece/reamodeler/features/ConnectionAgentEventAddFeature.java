package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

public class ConnectionAgentEventAddFeature extends AbstractAddFeature {
	
	public ConnectionAgentEventAddFeature(IFeatureProvider fp){
		super(fp);
	}
	
	
	@Override
	public boolean canAdd(IAddContext context) {
		//TODO: should we add some checks here?  
		return true;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		IAddConnectionContext addConContext = (IAddConnectionContext) context;
        //EReference addedEReference = (EReference) context.getNewObject();
        IPeCreateService peCreateService = Graphiti.getPeCreateService();
       
        // CONNECTION WITH POLYLINE
        Connection connection = peCreateService
            .createFreeFormConnection(getDiagram());
        connection.setStart(addConContext.getSourceAnchor());
        connection.setEnd(addConContext.getTargetAnchor());
 
        IGaService gaService = Graphiti.getGaService();
        Polyline polyline = gaService.createPolyline(connection);
        polyline.setLineWidth(2);
        polyline.setForeground(manageColor(IColorConstant.BLACK));
 
        
        //Add the arrow
  		ConnectionDecorator arrowDecorator = peCreateService.createConnectionDecorator(connection, false, 1, true);
  		createArrow(arrowDecorator);
        
        // create link and wire it
        //link(connection, addedEReference);
 
        return connection;
	}
	
	private Polyline createArrow(GraphicsAlgorithmContainer gaContainer) {
	       
        IGaService gaService = Graphiti.getGaService();
        Polyline polyline =
            gaService.createPolyline(gaContainer, new int[] { -5, 5, 0, 0, -5,-5 });
        
        polyline.setForeground(manageColor(IColorConstant.BLACK));
        polyline.setLineWidth(2);
        return polyline;
    }

}
