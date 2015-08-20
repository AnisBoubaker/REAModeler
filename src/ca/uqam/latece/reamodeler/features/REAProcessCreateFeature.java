package ca.uqam.latece.reamodeler.features;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.internal.util.T;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import rea.BusinessProcessDefinition;
import rea.REAProcess;
import rea.ReaFactory;

import ca.uqam.latece.reamodeler.REAImageProvider;
import ca.uqam.latece.reamodeler.REAModelerUtil;

@SuppressWarnings("restriction")
public class REAProcessCreateFeature extends AbstractFeature implements ICreateFeature {

	public final static int CONVERSION = 1;
	public final static int EXCHANGE = 2;
	
	private String description;
	private String name;
	private int type;
	
	public REAProcessCreateFeature(IFeatureProvider fp, int type){
		super(fp);
		this.type = type;
		switch(type){
		case CONVERSION:
			setName("Conversion Process");
			setDescription("Add a new REA conversion process");
			break;
		case EXCHANGE:
			setName("Exchange Process");
			setDescription("Add a new REA exchange process");
			break;
		}
	}
	
	public String getCreateDescription() {
		return description;
	}

	public String getCreateName() {
		return name;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	private void setDescription(String description) {
		this.description = description;
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public boolean canCreate(ICreateContext context) {
		if(context.getTargetContainer() instanceof Diagram) return true;
		return false;
	}

	public Object[] create(ICreateContext context) {
		String newClassName="";
		REAProcess newClass = null;
		switch(type){
		case CONVERSION:
			//newClassName =ExampleUtil.askString("Create Conversion Process", "Enter an unique identifier (ID) for this REA Conversion Process", "");
			//if(newClassName==null || newClassName.trim().length()==0) return EMPTY;
			newClass = ReaFactory.eINSTANCE.createConversion();
			break;
		case EXCHANGE:
			//newClassName =ExampleUtil.askString("Create Exchange Process", "Enter an unique identifier (ID) for this REA Exchange Process", "");
			//if(newClassName==null || newClassName.trim().length()==0) return EMPTY;
			newClass = ReaFactory.eINSTANCE.createExchange();
			break;
		}
		
		BusinessProcessDefinition bpd = REAModelerUtil.getValueModelObject(getDiagram()).getProcessDefinition();
		bpd.getREAProcesses().add(newClass);
		//REAModelerUtil.saveToModelFile(newClass, getDiagram());
		//newClass.setID(newClassName);
        addGraphicalRepresentation(context, newClass);
        
        getFeatureProvider().getDirectEditingInfo().setActive(true);
        
        return new Object[] {newClass};
	}
	
	public String getCreateImageId() {
		switch (type){
		case CONVERSION: return REAImageProvider.IMG_CONVERSION_PROCESS;
		case EXCHANGE: return REAImageProvider.IMG_EXCHANGE_PROCESS;
		default: return null;
		}
	}

	
	/**
	 * Methods below are added to conform to ICreateFeature Interface
	 * (since we cannot extend AbstractCreateFeature).
	 */
	
	public boolean canExecute(IContext context) {
		final String SIGNATURE = "canExecute(IContext)"; //$NON-NLS-1$
		boolean info = T.racer().info();
		if (info) {
			T.racer().entering(AbstractCreateFeature.class, SIGNATURE, new Object[] { context });
		}
		boolean ret = false;
		if (context instanceof ICreateContext) {
			ret = canCreate((ICreateContext) context);
		}

		if (info) {
			T.racer().exiting(AbstractCreateFeature.class, SIGNATURE, ret);
		}
		return ret;
	}

	public void execute(IContext context) {
		final String SIGNATURE = "execute(IContext)"; //$NON-NLS-1$
		boolean info = T.racer().info();
		if (info) {
			T.racer().entering(AbstractCreateFeature.class, SIGNATURE, new Object[] { context });
		}
		if (context instanceof ICreateContext) {
			create((ICreateContext) context);
		}
		if (info) {
			T.racer().exiting(AbstractCreateFeature.class, SIGNATURE);
		}
	}

	public String getCreateLargeImageId() {
		return getCreateImageId();
	}
	
}
