
package org.mule.module.cmis.callback;

import javax.annotation.Generated;


/**
 * Callback returned by methods that are annotated with @Source
 * <p/>
 * It will be executed when the MessageSource is being stopped.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public interface StopSourceCallback {

    void stop() throws Exception;
}
