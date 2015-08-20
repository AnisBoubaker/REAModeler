package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

import rea.Agent;
import rea.Event;


public class ConnectionAgentEventDeleteFeature extends DefaultDeleteFeature {

	
	public ConnectionAgentEventDeleteFeature(IFeatureProvider fp){
		super(fp);
	}
	
	@Override
	public void preDelete(IDeleteContext context) {
		Connection connection = (Connection)context.getPictogramElement();
		Object sourceObj = getBusinessObjectForPictogramElement(connection.getStart().getParent());
		Object targetObj = getBusinessObjectForPictogramElement(connection.getEnd().getParent());
		if(sourceObj instanceof Event && targetObj instanceof Agent){
			((Event)sourceObj).setReceiver(null);
		}
		if(sourceObj instanceof Agent && targetObj instanceof Event){
			((Event)targetObj).setProvider(null);
		}
	}
}
