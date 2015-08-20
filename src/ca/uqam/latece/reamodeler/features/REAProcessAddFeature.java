package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider; 
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

import ca.uqam.latece.reamodeler.REAImageProvider;

import rea.Exchange;
import rea.REAProcess;

public class REAProcessAddFeature extends AbstractAddFeature {

	private static final IColorConstant CLASS_TEXT_FOREGROUND = new ColorConstant(156, 65, 156);

	private static final IColorConstant CLASS_FOREGROUND = new ColorConstant(156, 65, 156);

	private static final IColorConstant CLASS_BACKGROUND = new ColorConstant(232, 190, 218);

	public REAProcessAddFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof REAProcess) {
			// An REA Process could only be added to the diagram (No REA Process
			// nesting)
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		REAProcess addedClass = (REAProcess) context.getNewObject();
		Diagram targetDiagram = (Diagram) context.getTargetContainer();

		// Container shape with rounded rectangle
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(
				targetDiagram, true);

		// define a default size for the shape
		int width = context.getWidth() <= 0 ? 200 : context.getWidth();
		int height = context.getHeight() <= 0 ? 100 : context.getWidth();
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
			Graphiti.getPeService().setPropertyValue(containerShape, "isCollapsed", "0");
			// create link and wire it
			this.link(containerShape, addedClass);
		}

		// Horizontal line
		{
			// create shape for line
			Shape shape = peCreateService.createShape(containerShape, false);

			// create and set graphics algorithm
			Polyline polyline = gaService.createPolyline(shape, new int[] { 0,
					20, width, 20 });
			polyline.setForeground(manageColor(CLASS_FOREGROUND));
			polyline.setLineWidth(2);
			Graphiti.getPeService().setPropertyValue(shape, "shape-id","reaProcess_horizontalLine");
		}
		
		//Vertical line separating inflow and outflow events
		{
			// create shape for line
			Shape shape = peCreateService.createShape(containerShape, false);

			// create and set graphics algorithm
			Polyline polyline = gaService.createPolyline(shape, new int[] { (int)(width/2), 20, (int)(width/2), height });
			polyline.setForeground(manageColor(CLASS_FOREGROUND));
			polyline.setLineWidth(2);
			Graphiti.getPeService().setPropertyValue(shape, "shape-id","reaProcess_verticalLine");
		}
		//Invisible rectangles for dropping in outflow events
		{
			ContainerShape shape = peCreateService.createContainerShape(containerShape, true);
			Rectangle rectangle = gaService.createInvisibleRectangle(shape);
			rectangle.setWidth((int)(width/2));
			rectangle.setHeight(height-20);
			rectangle.setX(0);
			rectangle.setY(20);
			Graphiti.getPeService().setPropertyValue(shape, "shape-id","reaProcess_decrementContainer");
			
			//this.link(shape, addedClass);
			
		}
		//Invisible rectangles for dropping in inflow events
		{
			ContainerShape shape = peCreateService.createContainerShape(containerShape, true);
			Rectangle rectangle = gaService.createInvisibleRectangle(shape);
			rectangle.setWidth((int)(width/2));
			rectangle.setHeight(height-20);
			rectangle.setX((int)(width/2));
			rectangle.setY(20);
			Graphiti.getPeService().setPropertyValue(shape, "shape-id","reaProcess_incrementContainer");
			
			//this.link(shape, addedClass);
		}
		
		//Icon
		{
			Shape shape = peCreateService.createShape(containerShape, false);
			
			Image icon;
			if(addedClass instanceof Exchange) icon  = gaService.createImage(shape, REAImageProvider.IMG_EXCHANGE_PROCESS);
			else icon  = gaService.createImage(shape, REAImageProvider.IMG_CONVERSION_PROCESS);
			gaService.setLocationAndSize(icon, 5, 5, 16, 16);	
			Graphiti.getPeService().setPropertyValue(shape, "shape-id","reaProcess_icon");
		}

		// SHAPE WITH TEXT
		{
			Shape shape = peCreateService.createShape(containerShape, false);

			Text text = gaService.createDefaultText(getDiagram(), shape,
					addedClass.getID());
			text.setForeground(manageColor(CLASS_TEXT_FOREGROUND));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			gaService.setLocationAndSize(text, 20, 5, width-21, 16);
			Graphiti.getPeService().setPropertyValue(shape, "shape-id","reaProcess_processId");

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
		/*ChopboxAnchor chopbox = peCreateService
				.createChopboxAnchor(containerShape);*/

		layoutPictogramElement(containerShape);

		return containerShape;
	}

}
