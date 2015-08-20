package ca.uqam.latece.reamodeler.features;

import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

public class REAProcessLayoutFeature extends AbstractLayoutFeature {

	private static final int MIN_HEIGHT = 30;

	private static final int MIN_WIDTH = 50;

	public REAProcessLayoutFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canLayout(ILayoutContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof ContainerShape)
			return true;
		return false;
	}

	public boolean layout(ILayoutContext context) {
		boolean anythingChanged = true;

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
			int containerHeight = containerShapeGa.getHeight();
			for (Shape shape : containerShape.getChildren()) {
				GraphicsAlgorithm graphicsAlgorithm = shape
						.getGraphicsAlgorithm();
				IGaService gaService = Graphiti.getGaService();
				// IDimension size = gaService.calculateSize(graphicsAlgorithm);

				// if (containerWidth != size.getWidth() || containerWidth !=
				// size.getHeight()) {
				if (Graphiti.getPeService().getPropertyValue(shape, "shape-id")
						.equals("reaProcess_horizontalLine")) {
					Polyline polyline = (Polyline) graphicsAlgorithm;
					Point secondPoint = polyline.getPoints().get(1);
					Point newSecondPoint = gaService.createPoint(
							containerWidth, secondPoint.getY());
					polyline.getPoints().set(1, newSecondPoint);
					shape.setVisible(true);
					anythingChanged = true;
				} else if (Graphiti.getPeService()
						.getPropertyValue(shape, "shape-id")
						.equals("reaProcess_verticalLine")) {
					Polyline polyline = (Polyline) graphicsAlgorithm;
					Point firstPoint = polyline.getPoints().get(0);
					Point newFirstPoint = gaService.createPoint(
							(int) (containerWidth / 2),
							(int) (firstPoint.getY()));
					Point newSecondPoint = gaService.createPoint(
							(int) (containerWidth / 2), containerHeight);
					polyline.getPoints().set(0, newFirstPoint);
					polyline.getPoints().set(1, newSecondPoint);
					shape.setVisible(true);
					anythingChanged = true;
				} else if (Graphiti.getPeService()
						.getPropertyValue(shape, "shape-id")
						.equals("reaProcess_processId")) {
					gaService.setWidth(graphicsAlgorithm, containerWidth - 21);
					anythingChanged = true;
				} else if (Graphiti.getPeService()
						.getPropertyValue(shape, "shape-id")
						.equals("reaProcess_decrementContainer")) {
					gaService.setWidth(graphicsAlgorithm,
							(int) (containerWidth) / 2);
					gaService
							.setHeight(graphicsAlgorithm, containerHeight - 20);
					shape.setVisible(true);
					for (Shape children : ((ContainerShape) shape)
							.getChildren()) {
						setAllConnectionsVisible(children, true);
					}
					anythingChanged = true;
				} else if (Graphiti.getPeService()
						.getPropertyValue(shape, "shape-id")
						.equals("reaProcess_incrementContainer")) {
					Rectangle rectangle = (Rectangle) graphicsAlgorithm;
					rectangle.setX((int) (containerWidth / 2));
					rectangle.setWidth((int) (containerWidth / 2));
					rectangle.setHeight(containerHeight - 20);
					shape.setVisible(true);
					for (Shape children : ((ContainerShape) shape)
							.getChildren()) {
						setAllConnectionsVisible(children, true);
					}
					anythingChanged = true;
				}

				// }
			}
		} else {
			int resizeWidth = 100;
			int resizeHeight = 25;
			containerShapeGa.setHeight(resizeHeight);
			containerShapeGa.setWidth(resizeWidth);

			for (Shape shape : containerShape.getChildren()) {
				GraphicsAlgorithm graphicsAlgorithm = shape
						.getGraphicsAlgorithm();
				IGaService gaService = Graphiti.getGaService();
				if (Graphiti.getPeService().getPropertyValue(shape, "shape-id")
						.equals("reaProcess_horizontalLine")) {
					shape.setVisible(false);
				} else if (Graphiti.getPeService()
						.getPropertyValue(shape, "shape-id")
						.equals("reaProcess_verticalLine")) {
					shape.setVisible(false);
				} else if (Graphiti.getPeService()
						.getPropertyValue(shape, "shape-id")
						.equals("reaProcess_processId")) {
					gaService.setWidth(graphicsAlgorithm, resizeWidth);
					anythingChanged = true;
				} else if (Graphiti.getPeService()
						.getPropertyValue(shape, "shape-id")
						.equals("reaProcess_decrementContainer")) {
					for (Shape children : ((ContainerShape) shape)
							.getChildren()) {
						setAllConnectionsVisible(children, false);
					}
					shape.setVisible(false);
				} else if (Graphiti.getPeService()
						.getPropertyValue(shape, "shape-id")
						.equals("reaProcess_incrementContainer")) {
					for (Shape children : ((ContainerShape) shape)
							.getChildren()) {
						setAllConnectionsVisible(children, false);
					}
					shape.setVisible(false);
				}

			}

			anythingChanged = true;
		}

		return anythingChanged;
	}

	private void setAllConnectionsVisible(Shape shape, boolean visible) {
		List<Anchor> anchors = shape.getAnchors();
		// First container is the inflow/outflow box, the second is the
		// REAProcess box.
		ContainerShape reaProcessBox = shape.getContainer().getContainer();
		for (Anchor anchor : anchors) {
			List<Connection> connections = Graphiti.getPeService()
					.getAllConnections(anchor);
			for (Connection c : connections) {
				Anchor oppositeEnd;
				if (c.getStart() == anchor)
					oppositeEnd = c.getEnd();
				else
					oppositeEnd = c.getStart();
				// check if the opposite end is part of the REAProcess and
				// hide/show the connection if so.
				// If it's part of the REA Process, 2nd container would be
				// REAProcess box (as for thisContainer. Otherwise, the second
				// container would be null
				Shape oppositeEndContainer = ((Shape) oppositeEnd.getParent())
						.getContainer().getContainer();
				if (oppositeEndContainer == reaProcessBox){
					c.setVisible(visible);
				}				
			}
		}
	}

}
