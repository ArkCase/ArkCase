/**
 *
 */
package com.armedia.acm.frevvo.config;

/*-
 * #%L
 * ACM Service: Form Configuration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.frevvo.model.Details;
import com.armedia.acm.frevvo.model.Options;
import com.armedia.acm.frevvo.model.OptionsAndDetailsByType;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.search.model.SearchConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public abstract class FrevvoFormChargeAbstractService extends FrevvoFormAbstractService
{

    private AcmContainerDao AcmContainerDao;

    /**
     * This method will return all charge codes by type and their details (like title) Details are configured in the
     * properties files and
     * they are Solr properties names
     * 
     * @param formName
     * @param types
     * @return
     */
    @Deprecated
    public OptionsAndDetailsByType getCodeOptionsAndDetails(String formName, List<String> types)
    {
        OptionsAndDetailsByType optionsAndDetailsByType = new OptionsAndDetailsByType();

        Map<String, Options> optionsByType = new HashMap<>();
        Map<String, Map<String, Details>> optionsDetailsByType = new HashMap<>();

        if (types != null)
        {
            for (String type : types)
            {
                String[] typeArray = type.split("=");
                if (typeArray != null && typeArray.length == 2)
                {
                    // Get objects from Solr by type
                    String jsonResults = getSolrResponse(typeArray[0]);

                    // Get details properties from .properties file
                    List<String> properties = convertToList((String) getProperties().get(formName + "." + typeArray[0] + ".details"), ",");

                    // Create Options object for given type and objects taken from Solr
                    // These will be value-label for dropdowns
                    Options options = getOptions(typeArray[0], jsonResults);

                    // Create map - for each object id keep details
                    Map<String, Details> optionsDetails = getOptionsDetails(typeArray[0], jsonResults, properties);

                    // Add Options and Details to the container object for given type
                    optionsByType.put(typeArray[0], options);
                    optionsDetailsByType.put(typeArray[0], optionsDetails);
                }
            }
        }

        optionsAndDetailsByType.setOptionsByType(optionsByType);
        optionsAndDetailsByType.setOptionsDetailsByType(optionsDetailsByType);

        return optionsAndDetailsByType;
    }

    /**
     * Return Options object that keeps value-label pairs for Frevvo dropdowns This is separate method because it can be
     * overriden in
     * appropriate module
     * 
     * @param type
     * @param source
     * @return
     */
    public Options getOptions(String type, String source)
    {
        Options options = getCodeOptionsByObjectType(type, source);

        return options;
    }

    /**
     * Return Options object that keeps value-label pairs for Frevvo dropdowns for given type
     * 
     * @param objectType
     * @param source
     * @return
     */
    public Options getCodeOptionsByObjectType(String objectType, String source)
    {
        Options codeOptions = new Options();

        if (source == null)
        {
            // Take Solr response for given object type if 'source' is null.
            // With this we are giving possibility this method to be invoked from other places where we don't have Solr
            // response
            source = getSolrResponse(objectType);
        }

        if (source != null)
        {
            // Get documents
            JSONArray objects = getSearchResults().getDocuments(source);

            if (objects != null)
            {
                for (int i = 0; i < objects.length(); i++)
                {
                    // For each document, find id, name and create the option that recognize Frevvo dropdown
                    JSONObject object = objects.getJSONObject(i);
                    if (object != null)
                    {
                        String id = getSearchResults().extractString(object, SearchConstants.PROPERTY_OBJECT_ID_S);
                        String name = getSearchResults().extractString(object, SearchConstants.PROPERTY_NAME);

                        codeOptions.add(id + "=" + name);
                    }
                }
            }
        }

        return codeOptions;
    }

    /**
     * For each option (where we have object id), take details that will help to show more information on Frevvo side
     * 
     * @param objectType
     * @param source
     * @param properties
     * @return
     */
    public Map<String, Details> getOptionsDetails(String objectType, String source, List<String> properties)
    {
        Map<String, Details> objectsDetails = new HashMap<>();

        if (source == null)
        {
            // Take Solr response for given object type if 'source' is null.
            // With this we are giving possibility this method to be invoked from other places where we don't have Solr
            // response
            source = getSolrResponse(objectType);
        }

        if (source != null)
        {
            // Get documents
            JSONArray objects = getSearchResults().getDocuments(source);

            if (objects != null)
            {
                for (int i = 0; i < objects.length(); i++)
                {
                    // For each document, find id and take details for that object
                    JSONObject object = objects.getJSONObject(i);
                    if (object != null)
                    {
                        String id = getSearchResults().extractString(object, SearchConstants.PROPERTY_OBJECT_ID_S);

                        // Get details for given object. 'properties' keeps which information we should take
                        Details details = getDetails(object, properties);

                        objectsDetails.put(id, details);
                    }
                }
            }
        }

        return objectsDetails;
    }

    /**
     * Get details for given object. 'properties' are properties in the Solr document
     * 
     * @param object
     * @param properties
     * @return
     */
    public Details getDetails(JSONObject object, List<String> properties)
    {
        Details details = new Details();

        if (properties != null)
        {
            for (String property : properties)
            {
                String value = getSearchResults().extractString(object, property);
                details.put(property, value);
            }
        }

        return details;
    }

    public AcmContainer createContainer(String rootFolder, String userId, Long objectId, String objectType, String name)
            throws AcmCreateObjectFailedException
    {
        String path = rootFolder + "/" + userId + "/" + getFolderAndFilesUtils().buildSafeFolderName(name);
        AcmContainer container = getAcmContainerDao().findByObjectTypeAndIdOrCreate(objectType, objectId, path, name);

        if (container != null)
        {
            container.setContainerObjectId(objectId);

            if (container.getFolder() != null && container.getFolder().getCmisFolderId() == null)
            {
                String cmisFolderId = getEcmFileService().createFolder(path);
                container.getFolder().setCmisFolderId(cmisFolderId);
            }

            return container;
        }

        return null;
    }

    public abstract String getSolrResponse(String objectType);

    public AcmContainerDao getAcmContainerDao()
    {
        return AcmContainerDao;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        AcmContainerDao = acmContainerDao;
    }
}
