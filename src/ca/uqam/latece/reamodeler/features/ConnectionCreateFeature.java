package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;

import ca.uqam.latece.reamodeler.REAImageProvider;

import rea.Agent;
import rea.Event;
import rea.Resource;

public class ConnectionCreateFeature extends
		AbstractCreateConnectionFeature {

	private IREALinkCreateFeatureDelegate delegate = null;
	
	public ConnectionCreateFeature(IFeatureProvider fp){
		super(fp,"Connection","Link two REA concepts");
	}
	
	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		if(context.getSourceAnchor()==null || context.getTargetAnchor()==null) return false;
		
		//Set the delegate depending on the connection type (i.e. linked concepts)
		Object sourceObj = getBusinessObjectForPictogramElement(context.getSourceAnchor().getParent());
		Object targetObj = getBusinessObjectForPictogramElement(context.getTargetAnchor().getParent());
		if( (sourceObj instanceof Event && targetObj instanceof Agent) || (sourceObj instanceof Agent && targetObj instanceof Event) ){
			this.delegate = new ConnectionAgentEventCreateFeatureDelegate(this);
		}
		
		if( (sourceObj instanceof Resource && targetObj instanceof Event) || (sourceObj instanceof Event && targetObj instanceof Resource) ){
			this.delegate = new ConnectionResourceEventCreateFeatureDelegate(this);
		}
		
		//Ask the delegate if creation is permitted and return the result.
		if(this.delegate!=null) return this.delegate.canCreate(context, sourceObj, targetObj);
		
		//No delegate found, creation not permitted. 
		return false;
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		
		Object sourceObj = getBusinessObjectForPictogramElement(context.getSourceAnchor().getParent());
		Object targetObj = getBusinessObjectForPictogramElement(context.getTargetAnchor().getParent());
		
		Connection conn =  delegate.create(context, sourceObj, targetObj);
		return conn;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		if(context.getSourceAnchor()==null) return false;
		
		Object sourceObj = getBusinessObjectForPictogramElement(context.getSourceAnchor().getParent());
		
		//Can start a connection from any of the REA concepts.
		if(!(sourceObj instanceof Event) && !(sourceObj instanceof Resource) && !(sourceObj instanceof Agent))
			return false;
		
		return true;
	}
	
	public String getCreateImageId() {
		return REAImageProvider.IMG_CONNECTION;
	}

}
