package ca.uqam.latece.reamodeler;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

public class REAImageProvider extends AbstractImageProvider {

	// The prefix for all identifiers of this image provider
    protected static final String PREFIX ="ca.uqam.latece.reamodeler.";
 
    // The image identifiers
    public static final String IMG_ACTOR= PREFIX + "agent";
    public static final String IMG_EXCHANGE_PROCESS= PREFIX + "conversionProcess";
    public static final String IMG_CONVERSION_PROCESS= PREFIX + "exchangeProcess";
    public static final String IMG_EVENT= PREFIX + "event";
    public static final String IMG_RESOURCE= PREFIX + "resource";
    public static final String IMG_CONNECTION= PREFIX + "connection";
	
	@Override
	protected void addAvailableImages() {
		// register the path for each image identifier
        addImageFilePath(IMG_ACTOR, "icons/agent.gif");
        addImageFilePath(IMG_EXCHANGE_PROCESS, "icons/exchangeProcess.gif");
        addImageFilePath(IMG_CONVERSION_PROCESS, "icons/conversionProcess.gif");
        addImageFilePath(IMG_EVENT, "icons/event.gif");
        addImageFilePath(IMG_RESOURCE, "icons/resource.gif");
        addImageFilePath(IMG_CONNECTION, "icons/connection.gif");
	}

}
