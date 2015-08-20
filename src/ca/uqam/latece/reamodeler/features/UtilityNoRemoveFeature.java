package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;

public class UtilityNoRemoveFeature extends DefaultRemoveFeature {
	public UtilityNoRemoveFeature(IFeatureProvider fp){
		super(fp);
	}
	
	@Override
	public boolean canExecute(IContext context){
		return false;
	}
	
	@Override
	public boolean canRemove(IRemoveContext context) {
		return false;
	}
}
