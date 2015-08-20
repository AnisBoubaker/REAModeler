package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;

import rea.Agent;
import rea.Event;

public class ConnectionAgentEventCreateFeatureDelegate implements IREALinkCreateFeatureDelegate {
	
	private ICreateConnectionFeature createFeature;
	
	public ConnectionAgentEventCreateFeatureDelegate(ICreateConnectionFeature feature){
		this.createFeature = feature;
	}
	

	@Override
	public Connection create(ICreateConnectionContext context, Object sourceObj, Object targetObj) {
		Connection newConnection = null;
		Agent agent;
		Event event;
		
		if(sourceObj==null || targetObj==null) return null;
		
		if(sourceObj instanceof Agent && targetObj instanceof Event){
			agent = (Agent)sourceObj;
			event = (Event)targetObj;
			event.setProvider(agent);
		} else {
			agent = (Agent)targetObj;
			event = (Event)sourceObj;
			event.setReceiver(agent);
		}
		
		AddConnectionContext addContext =
                new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		//addContext.setNewObject(ReaPackage.Literals.EVENT__PROVIDER);
		newConnection = (Connection)createFeature.getFeatureProvider().addIfPossible(addContext);
       
        return newConnection;
	}
	
	public boolean canCreate(ICreateConnectionContext context, Object sourceObj, Object targetObj) {
		Event event;
		if(sourceObj==null || targetObj==null) return false;
		if(sourceObj instanceof Agent && targetObj instanceof Event){ //Setting the provider agent of the event
			event = (Event)targetObj;
			if(event.getProvider()==null) return true;
		} else if(sourceObj instanceof Event && targetObj instanceof Agent) { //Setting the receiver agent of the event
			event = (Event)sourceObj;
			if(event.getReceiver()==null) return true;
		}
		return false;
	}
	

}
