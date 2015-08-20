package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;

import rea.Resource;

public class ResourceCopyFeature extends AbstractCopyFeature {

	public ResourceCopyFeature(IFeatureProvider fp){
		super(fp);
	}
	
	@Override
	public void copy(ICopyContext context) {
        PictogramElement[] pes = context.getPictogramElements();
        //We already know (from canCopy) that the business object is an Agent
        Resource resource = (Resource)getBusinessObjectForPictogramElement(pes[0]);
        // put business object to the clipboard
        putToClipboard(new Object[]{resource});
	}

	@Override
	public boolean canCopy(ICopyContext context) {
		final PictogramElement[] pes = context.getPictogramElements();
        if (pes == null || pes.length != 1) {  // we may copy only one pictogram element at a time
            return false;
        }
        if(getBusinessObjectForPictogramElement(pes[0]) instanceof Resource) return true;
        return false;
	}

}
