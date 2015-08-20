package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

public class UtilityNoMoveFeature extends DefaultMoveShapeFeature {

	public UtilityNoMoveFeature(IFeatureProvider fp){
		super(fp);
	}
	
	@Override
	public boolean canExecute(IContext context){
		return false;
	}
	
	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		return false;
	}
	
}
