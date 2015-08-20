package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

import rea.REAProcess;
import rea.Resource;

public class UtilityCollapseFeature extends AbstractCustomFeature {

	private boolean hasDoneChanges = false;
	
	public UtilityCollapseFeature(IFeatureProvider fp){
		super(fp);
	}
	
	@Override
    public String getName() {
        return "Expand/Collapse";
    }
 
    @Override
    public String getDescription() {
        return "Expands or Collapses the enclosing box.";
    }
    
    @Override
    public boolean canExecute(ICustomContext context) {
        // allow rename if exactly one pictogram element
        // representing a REAProcess is selected
        boolean ret = false;
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1) {
            Object bo = getBusinessObjectForPictogramElement(pes[0]);
            if (bo instanceof REAProcess || bo instanceof Resource) {
                ret = true;
            }
        }
        return ret;
    }
	
	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		PictogramElement pe = pes[0];
		int isCollapsed = 0;
		String isCollapsedProperty = Graphiti.getPeService().getPropertyValue(pe, "isCollapsed");
		if(isCollapsedProperty!=null) isCollapsed = Integer.parseInt(isCollapsedProperty);
		
		if(isCollapsed==0){
			int width = pe.getGraphicsAlgorithm().getWidth();
			int height = pe.getGraphicsAlgorithm().getHeight();
			Graphiti.getPeService().setPropertyValue(pe, "initial_width", String.valueOf(width));
			Graphiti.getPeService().setPropertyValue(pe, "initial_height", String.valueOf(height));
			Graphiti.getPeService().setPropertyValue(pe, "isCollapsed", "1");
		} else {
			Graphiti.getPeService().setPropertyValue(pe, "isCollapsed", "0");
		}
		
		LayoutContext lContext = new LayoutContext(pe);
		ILayoutFeature layoutFeature = getFeatureProvider().getLayoutFeature(lContext);
		if(layoutFeature.canExecute(lContext)) layoutFeature.execute(lContext);
		this.hasDoneChanges = true;
	}

	@Override
    public boolean hasDoneChanges() {
           return this.hasDoneChanges;
    }
}
