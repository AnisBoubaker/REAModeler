package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;

public class UtilityNoResizeFeature extends DefaultResizeShapeFeature {

	public UtilityNoResizeFeature(IFeatureProvider fp){
		super(fp);
	}
	
	
	@Override
	public boolean canExecute(IContext context){
		return false;
	}
	
	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return false;
	}
}
