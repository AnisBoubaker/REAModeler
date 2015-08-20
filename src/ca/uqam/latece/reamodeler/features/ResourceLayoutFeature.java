package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.mm.algorithms.Image;

public class ResourceLayoutFeature extends AbstractLayoutFeature {

	private static final int MIN_HEIGHT = 30;

	private static final int MIN_WIDTH = 50;

	public ResourceLayoutFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canLayout(ILayoutContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof ContainerShape)
			return true;
		return false;
	}

	public boolean layout(ILayoutContext context) {
		boolean anythingChanged = false;

		ContainerShape containerShape = (ContainerShape) context
				.getPictogramElement();
		GraphicsAlgorithm containerShapeGa = containerShape
				.getGraphicsAlgorithm();

		int isCollapsed = 0;
		String isCollapsedProperty = Graphiti.getPeService().getPropertyValue(
				containerShape, "isCollapsed");

		if (isCollapsedProperty != null)
			isCollapsed = Integer.parseInt(isCollapsedProperty);

		if (isCollapsed == 0) {

			String widthProperty = Graphiti.getPeService().getPropertyValue(
					containerShape, "initial_width");
			String heightProperty = Graphiti.getPeService().getPropertyValue(
					containerShape, "initial_height");

			if (widthProperty != null && heightProperty != null) { // collapse
																	// the REA
																	// Process
				containerShapeGa.setWidth(Integer.parseInt(widthProperty));
				containerShapeGa.setHeight(Integer.parseInt(heightProperty));
				Graphiti.getPeService().setPropertyValue(containerShape,
						"initial_width", null);
				Graphiti.getPeService().setPropertyValue(containerShape,
						"initial_height", null);
			}

			// height
			if (containerShapeGa.getHeight() < MIN_HEIGHT) {
				containerShapeGa.setHeight(MIN_HEIGHT);
				anythingChanged = true;
			}

			// width
			if (containerShapeGa.getWidth() < MIN_WIDTH) {
				containerShapeGa.setWidth(MIN_WIDTH);
				anythingChanged = true;
			}

			int containerWidth = containerShapeGa.getWidth();
			for (Shape shape : containerShape.getChildren()) {
				GraphicsAlgorithm graphicsAlgorithm = shape
						.getGraphicsAlgorithm();
				IGaService gaService = Graphiti.getGaService();
				IDimension size = gaService.calculateSize(graphicsAlgorithm);
				shape.setVisible(true);
				if (containerWidth != size.getWidth()) {
					if (graphicsAlgorithm instanceof Polyline) {
						Polyline polyline = (Polyline) graphicsAlgorithm;
						Point secondPoint = polyline.getPoints().get(1);
						Point newSecondPoint = gaService.createPoint(
								containerWidth, secondPoint.getY());
						polyline.getPoints().set(1, newSecondPoint);
						anythingChanged = true;
					} else if (graphicsAlgorithm instanceof Text) {
						gaService.setWidth(graphicsAlgorithm,
								containerWidth - 26);
						anythingChanged = true;
					} else if (!(graphicsAlgorithm instanceof Image)) {
						gaService.setWidth(graphicsAlgorithm, containerWidth);
						anythingChanged = true;
					}
				}
			}
		} else {// isCollapsed true
			int resizeWidth = 100;
			int resizeHeight = 25;
			containerShapeGa.setHeight(resizeHeight);
			containerShapeGa.setWidth(resizeWidth);

			for (Shape shape : containerShape.getChildren()) {
				String shapeId = Graphiti.getPeService().getPropertyValue(
						shape, "shape-id");
				if (shapeId == null
						|| (!shapeId.equals("reaResource_name") && !shapeId
								.equals("reaResource_icon"))) {
					shape.setVisible(false);
					;
				} else {
					shape.setVisible(true);
				}
			}
			anythingChanged = true;
		}

		return anythingChanged;
	}

}
