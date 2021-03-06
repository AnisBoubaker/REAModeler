package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

import ca.uqam.latece.reamodeler.REAImageProvider;

import rea.Agent;
import rea.REAProcess;

public class AgentAddFeature extends AbstractAddFeature {

	private static final IColorConstant CLASS_TEXT_FOREGROUND = new ColorConstant(
			75, 98, 174);

	private static final IColorConstant CLASS_FOREGROUND = new ColorConstant(
			75, 98, 174);

	private static final IColorConstant CLASS_BACKGROUND = new ColorConstant(
			207, 217, 250);

	public AgentAddFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof Agent) {
			// An Event could only be added to an existing REAProcess
			if (getBusinessObjectForPictogramElement(context
					.getTargetContainer().getContainer()) instanceof REAProcess) {
				return true;
			}
		}
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		Agent addedClass = (Agent) context.getNewObject();
		ContainerShape targetShape = context.getTargetContainer();

		// Container shape with rounded rectangle
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(
				targetShape, true);

		// define a default size for the shape
		int width = 100;
		int height = 26;
		IGaService gaService = Graphiti.getGaService();

		// create and set graphics algorithm
		RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(
				containerShape, 5, 5);
		{
			roundedRectangle.setForeground(manageColor(CLASS_FOREGROUND));
			roundedRectangle.setBackground(manageColor(CLASS_BACKGROUND));
			roundedRectangle.setLineWidth(2);
			gaService.setLocationAndSize(roundedRectangle, context.getX(),
					context.getY(), width, height);

			// if added Class has no resource we add it to the resource
			// of the diagram
			// in a real scenario the business model would have its own resource
			if (addedClass.eResource() == null) {
				getDiagram().eResource().getContents().add(addedClass);
			}
			// create link and wire it
			this.link(containerShape, addedClass);
		}

		// Icon
		{
			Shape shape = peCreateService.createShape(containerShape, false);

			Image icon = gaService.createImage(shape,
					REAImageProvider.IMG_ACTOR);
			gaService.setLocationAndSize(icon, 5, 5, 16, 16);

		}

		// SHAPE WITH TEXT
		{
			Shape shape = peCreateService.createShape(containerShape, false);

			Text text = gaService.createDefaultText(getDiagram(), shape,
					addedClass.getName());
			text.setForeground(manageColor(CLASS_TEXT_FOREGROUND));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			gaService.setLocationAndSize(text, 20, 5, width - 21, 16);

			this.link(shape, addedClass);
			
			// provide information to support direct-editing directly
            // after object creation (must be activated additionally)
            IDirectEditingInfo directEditingInfo =
                getFeatureProvider().getDirectEditingInfo();
            // set container shape for direct editing after object creation
            directEditingInfo.setMainPictogramElement(containerShape);
            // set shape and graphics algorithm where the editor for
            // direct editing shall be opened after object creation
            directEditingInfo.setPictogramElement(shape);
            directEditingInfo.setGraphicsAlgorithm(text);
		}

		// add a chopbox anchor to the shape
		peCreateService.createChopboxAnchor(containerShape);

		layoutPictogramElement(containerShape);

		return containerShape;
	}

}
