package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;

import rea.Agent;
import rea.Resource;

/**
 * Delegation class. An instance of this class gets created by the FeatureProvider on Paste action. 
 * The delegate class will be decided upon the business object in the clipboard (see constructor)
 * @author Anis Boubaker
 */
public class UtilityPasteFeature extends AbstractPasteFeature {

	IPasteFeature delegate=null;
	
	public UtilityPasteFeature(IFeatureProvider fp, IPasteContext context){
		super(fp);
		//Decide which paste delegate to call depending on the context
		Object[] fromClipboard = getFromClipboard();
        if (fromClipboard != null && fromClipboard.length == 1) {
            if(fromClipboard[0] instanceof Agent) delegate = new AgentPasteFeatureDelegate(fp);
            if(fromClipboard[0] instanceof Resource) delegate = new ResourcePasteFeatureDelegate(fp);
        }
	}
	
	@Override
	public void paste(IPasteContext context) {
		if(delegate==null) return;
		delegate.paste(context);
	}

	@Override
	public boolean canPaste(IPasteContext context) {
		if(delegate==null) return false;
		return delegate.canPaste(context);
	}

}
