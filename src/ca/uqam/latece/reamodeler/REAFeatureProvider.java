package ca.uqam.latece.reamodeler;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

import rea.Agent;
import rea.Event;
import rea.REAProcess;
import rea.Resource;

import ca.uqam.latece.reamodeler.features.*;

public class REAFeatureProvider extends DefaultFeatureProvider {
	
	public REAFeatureProvider(IDiagramTypeProvider dtp){
		super(dtp);
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context){
		if(context instanceof AddConnectionContext){
			Object sourceObj = getBusinessObjectForPictogramElement(((AddConnectionContext) context).getSourceAnchor().getParent());
			Object targetObj = getBusinessObjectForPictogramElement(((AddConnectionContext) context).getTargetAnchor().getParent());
			if( //Agent <-> Event connection
					(sourceObj instanceof Agent && targetObj instanceof Event) ||
					(targetObj instanceof Agent && sourceObj instanceof Event) ){ 
				return new ConnectionAgentEventAddFeature(this);
			}
			if( //Agent <-> Event connection
					(sourceObj instanceof Resource && targetObj instanceof Event) ||
					(targetObj instanceof Resource && sourceObj instanceof Event) ){ 
				return new ConnectionResourceEventAddFeature(this);
			}
		} else {
			if(context.getNewObject() instanceof REAProcess){
				return new REAProcessAddFeature(this);
			}
			if(context.getNewObject() instanceof Resource){
				return new ResourceAddFeature(this);
			}
			if(context.getNewObject() instanceof Agent){
				return new AgentAddFeature(this);
			}
			if(context.getNewObject() instanceof Event){
				return new EventAddFeature(this);
			}
		}
		return super.getAddFeature(context);		
	}
	
	@Override
	public ICreateFeature[] getCreateFeatures(){
		return new ICreateFeature[] {
				new REAProcessCreateFeature(this,REAProcessCreateFeature.CONVERSION),
				new REAProcessCreateFeature(this,REAProcessCreateFeature.EXCHANGE),
				new ResourceCreateFeature(this),
				new EventCreateFeature(this),
				new AgentCreateFeature(this)
				};
	}
	
	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
        return new ICreateConnectionFeature[] {
            new ConnectionCreateFeature(this) 
         	};
    }
	
	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
        PictogramElement pictogramElement = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        if (bo instanceof REAProcess) {
            return new REAProcessLayoutFeature(this);
        }
        if (bo instanceof Resource) {
            return new ResourceLayoutFeature(this);
        }
        if (bo instanceof Agent) {
            return new AgentLayoutFeature(this);
        }
        if (bo instanceof Event) {
            return new AgentLayoutFeature(this);
        }
        return super.getLayoutFeature(context);
    }
	
	@Override
    public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
        Shape shape = context.getShape();
        String shapeIdProperty = Graphiti.getPeService().getPropertyValue(shape, "shape-id");
        if(shapeIdProperty!=null){
	        if(shapeIdProperty.equals("reaProcess_incrementContainer")) return new UtilityNoMoveFeature(this);
	        if(shapeIdProperty.equals("reaProcess_decrementContainer")) return new UtilityNoMoveFeature(this);
        }
        
        return super.getMoveShapeFeature(context);
    }
	
	@Override
    public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		Shape shape = context.getShape();
        String shapeIdProperty = Graphiti.getPeService().getPropertyValue(shape, "shape-id");
        
        if(shapeIdProperty!=null){
	        if(shapeIdProperty.equals("reaProcess_incrementContainer")) return new UtilityNoResizeFeature(this);
	        if(shapeIdProperty.equals("reaProcess_decrementContainer")) return new UtilityNoResizeFeature(this);
        }
        
        return super.getResizeShapeFeature(context);
    }
	
	@Override
    public IRemoveFeature getRemoveFeature(IRemoveContext context){
		PictogramElement shape = context.getPictogramElement();
        String shapeIdProperty = Graphiti.getPeService().getPropertyValue(shape, "shape-id");
        
        if(shapeIdProperty!=null){
	        if(shapeIdProperty.equals("reaProcess_incrementContainer")) return new UtilityNoRemoveFeature(this);
	        if(shapeIdProperty.equals("reaProcess_decrementContainer")) return new UtilityNoRemoveFeature(this);
        }
        
        return super.getRemoveFeature(context);
    }
	
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
	   PictogramElement pictogramElement = context.getPictogramElement();
	   if (pictogramElement instanceof ContainerShape) {
	       Object bo = getBusinessObjectForPictogramElement(pictogramElement);
	       if (bo instanceof Resource) {
	           return new ResourceUpdateFeature(this);
	       }
	       if (bo instanceof Agent) {
	           return new AgentUpdateFeature(this);
	       }
	       if (bo instanceof Event) {
	           return new EventUpdateFeature(this);
	       }
	       if (bo instanceof REAProcess) {
	           return new REAProcessUpdateFeature(this);
	       }
	   }
	   return super.getUpdateFeature(context);
	}
	
	@Override
    public ICopyFeature getCopyFeature(ICopyContext context) {
		final PictogramElement[] pes = context.getPictogramElements();
        if (pes == null || pes.length != 1) {  // we may copy only one pictogram element at a time
            return null;
        }
        for(PictogramElement pe : pes){
        	Object bo = getBusinessObjectForPictogramElement(pe);
        	if(bo instanceof Agent) return new AgentCopyFeature(this);
        	if(bo instanceof Resource) return new ResourceCopyFeature(this);
        }
        return null;
    }
	
	@Override
    public IPasteFeature getPasteFeature(IPasteContext context) {
		PictogramElement[] pes = context.getPictogramElements();
        if (pes.length != 1) return null; //We may paste to only one shape at a time
        return new UtilityPasteFeature(this,context);
    }
	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		if(context.getPictogramElement() instanceof Connection){
			Connection connection = (Connection)context.getPictogramElement();
			Object sourceObj = getBusinessObjectForPictogramElement(connection.getStart().getParent());
			Object targetObj = getBusinessObjectForPictogramElement(connection.getEnd().getParent());
			if(sourceObj instanceof Event && targetObj instanceof Agent) return new ConnectionAgentEventDeleteFeature(this);
			if(sourceObj instanceof Agent && targetObj instanceof Event) return new ConnectionAgentEventDeleteFeature(this);
			if(sourceObj instanceof Event && targetObj instanceof Resource) return new ConnectionResourceEventDeleteFeature(this);
			if(sourceObj instanceof Resource && targetObj instanceof Event) return new ConnectionResourceEventDeleteFeature(this);
		}
		return new DefaultDeleteFeature(this);
	}
	
	@Override
    public IDirectEditingFeature getDirectEditingFeature(
        IDirectEditingContext context) {
        PictogramElement pe = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pe);
        if (bo instanceof Resource) {
            return new ResourceDirectEditFeature(this);
        }
        if (bo instanceof Agent) {
            return new AgentDirectEditFeature(this);
        }
        if (bo instanceof Event) {
            return new EventDirectEditFeature(this);
        }
        if (bo instanceof REAProcess) {
            return new REAProcessDirectEditFeature(this);
        }
        return super.getDirectEditingFeature(context);
    }
	
	@Override
    public ICustomFeature[] getCustomFeatures(ICustomContext context) {
        return new ICustomFeature[] { new UtilityCollapseFeature(this) };
    }
	
}
