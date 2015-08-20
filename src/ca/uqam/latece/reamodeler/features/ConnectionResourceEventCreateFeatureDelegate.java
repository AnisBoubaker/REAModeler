package ca.uqam.latece.reamodeler.features;

import java.util.List;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import rea.Event;
import rea.Resource;

public class ConnectionResourceEventCreateFeatureDelegate implements
		IREALinkCreateFeatureDelegate {
	
	private ICreateConnectionFeature createFeature;
	
	public ConnectionResourceEventCreateFeatureDelegate(ICreateConnectionFeature feature){
		this.createFeature = feature;
	}

	@Override
	public Connection create(ICreateConnectionContext context, Object sourceObj, Object targetObj) {
		Connection newConnection = null;
		Resource resource;
		Event event;
		AddConnectionContext addContext;
		ContainerShape resourceShape;
		
		if(sourceObj==null || targetObj==null) return null;
		
		if(sourceObj instanceof Resource && targetObj instanceof Event){
			resource = (Resource)sourceObj;
			resourceShape = (ContainerShape)context.getSourceAnchor().getParent();
			event = (Event)targetObj;
			if(event.isDecrementEvt()){ //Connection Should go from Resource to Event if it's an increment
				addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
			} else {  //Otherwise: Connection goes from Event to Resource
				addContext = new AddConnectionContext(context.getTargetAnchor(), context.getSourceAnchor());
			}
		} else {
			resource = (Resource)targetObj;
			resourceShape = (ContainerShape)context.getTargetAnchor().getParent();
			event = (Event)sourceObj;
			//addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
			if(event.isDecrementEvt()) {//Connection Should go from Resource to Event if it's an increment
				addContext = new AddConnectionContext(context.getTargetAnchor(), context.getSourceAnchor());
			} else { //Otherwise: Connection goes from Event to Resource
				addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
			}
		}
		
		//addContext.setNewObject(ReaPackage.Literals.EVENT__PROVIDER);
		newConnection = (Connection)createFeature.getFeatureProvider().addIfPossible(addContext);
        if(newConnection!=null){
        	event.setResource(resource);
        	setPredecessorProcess(resourceShape);
        }
        return newConnection;
	}

	private void setPredecessorProcess(ContainerShape resourceShape){
		//EList<PictogramLink> plinks = diagram.getPictogramLinks();
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
				Event e = (Event)this.createFeature.getFeatureProvider().getBusinessObjectForPictogramElement(oppositeEndContainer);
				if(e.isIncrementEvt()) incrementEvent = e;
				else if(e.isDecrementEvt()) decrementEvent = e;
			}
		}
		if(incrementEvent!=null && decrementEvent!=null){
			decrementEvent.getREAProcess().getPredecessors().add(incrementEvent.getREAProcess());
		} 
	}
	
	@Override
	public boolean canCreate(ICreateConnectionContext context, Object sourceObj, Object targetObj) {
		Event event=null;
		Resource resource = null;
		Anchor resourceAnchor =  null;
		
		if(sourceObj==null || targetObj==null) return false;
		
		if(sourceObj instanceof Resource && targetObj instanceof Event){
			event = (Event)targetObj;
			resource = (Resource)sourceObj;
			resourceAnchor = context.getSourceAnchor();
		} else if (sourceObj instanceof Event && targetObj instanceof Resource){
			event = (Event)sourceObj;
			resource = (Resource)targetObj;
			resourceAnchor = context.getTargetAnchor();
		}
		
		if(event==null || resource==null) return false;
		
		//Check if the resource has already been connected to the same event type
		//Loop through all the connections of the resource and return false if 
		//we are adding a connection to an inflow event from a resource that already 
		//has such a connection (same applies to outflow events)
		//Note: We are NOT preventing the same resource from being connected to 
		//multiple inflow events but this restriction will allow us to establish REA
		//processes order. Thus the restriction is applied to resource shapes rather
		//than to the actual resource instance. 
		List<Connection> connections = Graphiti.getPeService()
				.getAllConnections(resourceAnchor);
		for (Connection c : connections) {
			Anchor oppositeEnd;
			if (c.getStart() == resourceAnchor)
				oppositeEnd = c.getEnd();
			else
				oppositeEnd = c.getStart();
			Shape oppositeEndContainer = ((Shape) oppositeEnd.getParent());
			Event e = (Event)this.createFeature.getFeatureProvider().getBusinessObjectForPictogramElement(oppositeEndContainer);
			if(e.isIncrementEvt() && event.isIncrementEvt()) return false;
			if(e.isDecrementEvt() && event.isDecrementEvt()) return false;
		}
		
		
		//The event must not have a Resource already set (connection must be deleted from model first)
		return event.getResource()==null;
	}

}
