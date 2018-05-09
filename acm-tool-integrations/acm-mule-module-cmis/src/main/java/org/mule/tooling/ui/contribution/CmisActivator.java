
package org.mule.tooling.ui.contribution;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import javax.annotation.Generated;

/**
 * The activator class controls the plug-in life cycle
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CmisActivator
        extends AbstractUIPlugin
{

    public final static String PLUGIN_ID = "org.mule.tooling.ui.contribution.cmis";
    private static org.mule.tooling.ui.contribution.CmisActivator plugin;

    public static org.mule.tooling.ui.contribution.CmisActivator getDefault()
    {
        return plugin;
    }

    public void start(BundleContext context)
            throws Exception
    {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context)
            throws Exception
    {
        plugin = null;
        super.stop(context);
    }

}
