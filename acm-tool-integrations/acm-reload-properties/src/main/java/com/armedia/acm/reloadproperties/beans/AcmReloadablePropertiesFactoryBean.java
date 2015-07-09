/**
 * 
 */
package com.armedia.acm.reloadproperties.beans;

import java.io.IOException;
import java.util.Properties;

import net.unicon.springframework.addons.properties.ReloadablePropertiesFactoryBean;

/**
 * @author riste.tutureski
 *
 */
public class AcmReloadablePropertiesFactoryBean extends ReloadablePropertiesFactoryBean {	
	
	 public AcmReloadablePropertiesFactoryBean() {
		super();
	}

	@Override 
	protected Properties createProperties() throws IOException 
	{ 
		return (Properties) super.createInstance(); 
	}
	
}
