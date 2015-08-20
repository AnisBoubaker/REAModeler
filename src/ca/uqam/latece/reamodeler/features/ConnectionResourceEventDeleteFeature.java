package ca.uqam.latece.reamodeler.features;

import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import rea.Event;
import rea.Resource;

public class ConnectionResourceEventDeleteFeature extends DefaultDeleteFeature {

	public ConnectionResourceEventDeleteFeature(IFeatureProvider fp){
		super(fp);
	}
	
	@Override
	public void preDelete(IDeleteContext context) {
		Connection connection = (Connection)context.getPictogramElement();
		Object sourceObj = getBusinessObjectForPictogramElement(connection.getStart().getParent());
		Object targetObj = getBusinessObjectForPictogramElement(connection.getEnd().getParent());
		if(sourceObj instanceof Event && targetObj instanceof Resource){
			unsetPredecessorProcess((ContainerShape)connection.getEnd().getParent());
			((Event)sourceObj).setResource(null);
		}
		if(sourceObj instanceof Resource && targetObj instanceof Event){
			unsetPredecessorProcess((ContainerShape)connection.getStart().getParent());
			((Event)targetObj).setResource(null);
		}
	}
	
	private void unsetPredecessorProcess(ContainerShape resourceShape){
		Event incrementEvent = null;
		Event decrementEvent = null;
		List<Anchor> resourceAnchors = resourceShape.getAnchors();
		for (Anchor anchor : resourceAnchors) {
			List<Connection> connections = Graphiti.getPeService()
					.getAllConnections(anchor);
			for (Connection c : connections) {
				Anchor oppositeEnd;
				if (c.getStart() == anchor)
					oppositeEnd = c.getEnd();
				else
					oppositeEnd = c.getStart();
				Shape oppositeEndContainer = ((Shape) oppositeEnd.getParent());
				Event e = (Event)getBusinessObjectForPictogramElement(oppositeEndContainer);
				if(e.isIncrementEvt()) incrementEvent = e;
				else if(e.isDecrementEvt()) decrementEvent = e;
			}
		}
		if(incrementEvent!=null && decrementEvent!=null){
			decrementEvent.getREAProcess().getPredecessors().remove(incrementEvent.getREAProcess());
		} 
	}
	
}
