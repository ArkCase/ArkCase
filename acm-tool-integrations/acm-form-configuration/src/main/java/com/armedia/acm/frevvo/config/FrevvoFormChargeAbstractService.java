/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.service.SearchResults;

/**
 * @author riste.tutureski
 *
 */
public abstract class FrevvoFormChargeAbstractService extends FrevvoFormAbstractService{

	private SearchResults searchResults;
	private AcmContainerDao AcmContainerDao;
	
	public Map<String, List<String>> getCodeOptions(List<String> types)
	{
		Map<String, List<String>> codeOptions = new HashMap<String, List<String>>();
		
		if (types != null)
		{
			for (String type : types)
			{
				String[] typeArray = type.split("=");
				if (typeArray != null && typeArray.length == 2)
				{
					List<String> options = getOptions(typeArray[0]);
					codeOptions.put(typeArray[0], options);
				}
			}
		}
		
		return codeOptions;
	}
	
	public List<String> getOptions(String type)
	{
		List<String> options = getCodeOptionsByObjectType(type);
		
		return options;
	}
	
	public List<String> getCodeOptionsByObjectType(String objectType)
	{
		List<String> codeOptions = new ArrayList<>();
		
		String jsonResults = getSolrResponse(objectType);
		
		if (jsonResults != null)
		{
			JSONArray objects = getSearchResults().getDocuments(jsonResults);
			
			List<String> ids = getSearchResults().getListForField(objects, SearchConstants.PROPERTY_OBJECT_ID_S);
			List<String> names = getSearchResults().getListForField(objects, SearchConstants.PROPERTY_NAME);
			
			if (ids != null)
			{
				for (int i = 0; i < ids.size(); i++)
				{
					// This check is only for safe execution. "ids" and "names" always will have the same size but
					// check that before invoking "get(index)" method
					if (i < names.size())
					{
						codeOptions.add(ids.get(i) + "=" + names.get(i));
					}
				}
			}
		}
		
		return codeOptions;
	}
	
	public AcmContainer createContainer(String rootFolder, String userId, Long objectId, String objectType, String name) throws AcmCreateObjectFailedException
	{
		String path = rootFolder + "/" + userId + "/" + getEcmFileService().buildSafeFolderName(name);
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

	public SearchResults getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(SearchResults searchResults) {
		this.searchResults = searchResults;
	}

	public AcmContainerDao getAcmContainerDao() {
		return AcmContainerDao;
	}

	public void setAcmContainerDao(AcmContainerDao acmContainerDao) {
		AcmContainerDao = acmContainerDao;
	}	
}
