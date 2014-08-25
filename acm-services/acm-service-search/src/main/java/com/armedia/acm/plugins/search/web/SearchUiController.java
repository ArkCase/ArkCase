package com.armedia.acm.plugins.search.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.armedia.acm.web.AcmPageDescriptor;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Properties;
import java.util.Enumeration;

@RequestMapping("/plugin/search")
public class SearchUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmPageDescriptor pageDescriptor;
    private Properties searchDefProperties;

    public static JSONObject toJSONObject(java.util.Properties properties) throws JSONException {
        JSONObject jo = new JSONObject();
        if(properties!=null && !properties.isEmpty()) {
            Enumeration<?> enumProperties = properties.propertyNames();
            while(enumProperties.hasMoreElements()) {
                String name = (String)enumProperties.nextElement();
                jo.put( name, properties.getProperty( name) );
            }
        }
        return jo;

    }


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView search()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("search");
        retval.addObject("pageDescriptor", getPageDescriptor());

        JSONObject propJSON = toJSONObject(getSearchDefProperties());
        retval.addObject("searchDef", propJSON);
        return retval;
    }

    public AcmPageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }

    public void setPageDescriptor(AcmPageDescriptor pageDescriptor) {
        this.pageDescriptor = pageDescriptor;
    }

    public Properties getSearchDefProperties() {
        return searchDefProperties;
    }

    public void setSearchDefProperties(Properties searchDefProperties) {
        this.searchDefProperties = searchDefProperties;
    }
}
