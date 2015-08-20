package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;

import rea.Agent;
import rea.ReaFactory;
import rea.ValueModel;

import ca.uqam.latece.reamodeler.REAImageProvider;
import ca.uqam.latece.reamodeler.REAModelerUtil;

public class AgentCreateFeature extends AbstractCreateFeature {

	public AgentCreateFeature(IFeatureProvider fp) {
		super(fp, "Agent", "Add a new Agent");
	}

	public boolean canCreate(ICreateContext context) {
		ContainerShape targetContainer = context.getTargetContainer();
		String propertyValue = Graphiti.getPeService().getPropertyValue(
				targetContainer, "shape-id");
		if (propertyValue != null
				&&

				(propertyValue.equals("reaProcess_incrementContainer") || propertyValue
						.equals("reaProcess_decrementContainer")))
			return true;
		return false;
	}

	public Object[] create(ICreateContext context) {
		/*String newClassName = ExampleUtil.askString("Create a new agent",
				"Please enter agent's name", "");
		if (newClassName == null || newClassName.trim().length() == 0)
			return EMPTY;*/
		Agent newClass = ReaFactory.eINSTANCE.createAgent();

		ValueModel valueModel = REAModelerUtil
				.getValueModelObject(getDiagram());
		valueModel.getAgents().add(newClass);
		//newClass.setName(newClassName);
		addGraphicalRepresentation(context, newClass);
		
		getFeatureProvider().getDirectEditingInfo().setActive(true);
		
		return new Object[] { newClass };
	}

	public String getCreateImageId() {
		return REAImageProvider.IMG_ACTOR;
	}

}
