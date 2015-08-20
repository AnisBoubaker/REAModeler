package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;

public interface IREALinkCreateFeatureDelegate {
	
	public Connection create(ICreateConnectionContext context, Object sourceObj, Object targetObj);
	
	public boolean canCreate(ICreateConnectionContext context, Object sourceObj, Object targetObj);
	
}
