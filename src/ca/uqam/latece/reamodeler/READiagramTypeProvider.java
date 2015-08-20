package ca.uqam.latece.reamodeler;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;


public class READiagramTypeProvider extends AbstractDiagramTypeProvider {
	
	public READiagramTypeProvider(){
		super();
		setFeatureProvider(new REAFeatureProvider(this));
	}
	
	@Override
	public boolean isAutoUpdateAtRuntime() {
		return true;
	}

	@Override
	public boolean isAutoUpdateAtRuntimeWhenEditorIsSaved() {
		return true;
	}

	@Override
	public boolean isAutoUpdateAtStartup() {
		return true;
	}

	@Override
	public boolean isAutoUpdateAtReset() {
		return true;
	}
	
	
}
