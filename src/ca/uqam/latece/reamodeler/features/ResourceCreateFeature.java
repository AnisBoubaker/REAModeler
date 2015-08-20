package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import rea.ReaFactory;
import rea.Resource;
import rea.ValueModel;

import ca.uqam.latece.reamodeler.REAImageProvider;
import ca.uqam.latece.reamodeler.REAModelerUtil;

public class ResourceCreateFeature extends AbstractCreateFeature {
	
	public ResourceCreateFeature(IFeatureProvider fp){
		super(fp, "Resource", "Add a new REA Resource instance");
	}
	
	public boolean canCreate(ICreateContext context) {
		if(context.getTargetContainer() instanceof Diagram) return true;
		return false;
	}

	public Object[] create(ICreateContext context) {
		/*String newClassName = ExampleUtil.askString("Create a new resource", "Please enter resource's name", "");
		if(newClassName==null || newClassName.trim().length()==0) 
			return EMPTY;*/
		Resource newClass = ReaFactory.eINSTANCE.createResourceWithDefaultPropertiesAndRights();
				
		ValueModel valueModel = REAModelerUtil.getValueModelObject(getDiagram());
		valueModel.getResources().add(newClass);
		//newClass.setName(newClassName);
        addGraphicalRepresentation(context, newClass);
        
        getFeatureProvider().getDirectEditingInfo().setActive(true);
        
        return new Object[] {newClass};
	}
	
	@Override
	public String getCreateImageId() {
		return REAImageProvider.IMG_RESOURCE;
	}
	
}
