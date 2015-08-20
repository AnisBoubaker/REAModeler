package ca.uqam.latece.reamodeler;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;

import rea.BusinessProcessDefinition;
import rea.ReaFactory;
import rea.ValueModel;

public class REAModelerUtil {

	@SuppressWarnings("rawtypes")
	public static void saveToModelFile(final EObject obj, final Diagram d){
		URI uri = d.eResource().getURI();
		uri = uri.trimFragment();
		uri = uri.trimFileExtension();
		uri = uri.appendFileExtension("rea");
		ResourceSet rSet = d.eResource().getResourceSet();
		// rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("rea",
		// new ReaResourceFactoryImpl());

		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace()
				.getRoot();

		IResource file = workspaceRoot.findMember(uri.toPlatformString(true));
		if (file == null || !file.exists()) {
			Resource createResource = rSet.createResource(uri);
			try{
				createResource.save(new HashMap());
			} catch (Exception e){
				Activator.log(1, "Error occured while saving the rea Resource: "+e.getMessage());
			}
			createResource.setTrackingModification(true);
		}
		final Resource resource = rSet.getResource(uri, true);
		resource.getContents().add(obj);
	}

	public static ValueModel getValueModelObject(Diagram diagram) {
		PictogramLink link = diagram.getLink();

		if (link == null) {
			ValueModel valueModel = ReaFactory.eINSTANCE.createValueModel();
			REAModelerUtil.saveToModelFile(valueModel, diagram);
			BusinessProcessDefinition bpd = ReaFactory.eINSTANCE.createBusinessProcessDefinition();
			valueModel.setProcessDefinition(bpd);

			// Create the pictogram link object
			link = PictogramsFactory.eINSTANCE.createPictogramLink();
			link.getBusinessObjects().add(valueModel);
			diagram.setLink(link);
		}

		if (link.getBusinessObjects().size() == 1 && link.getBusinessObjects().get(0) instanceof ValueModel) {
			ValueModel vm = (ValueModel) link.getBusinessObjects().get(0);
			// Return the automaton object
			return vm;
		} else {
			throw new IllegalStateException();
		}
	}
	
}
