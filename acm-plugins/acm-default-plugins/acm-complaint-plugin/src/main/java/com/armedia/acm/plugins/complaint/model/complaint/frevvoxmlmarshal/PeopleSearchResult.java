/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint.frevvoxmlmarshal;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.plugins.complaint.model.complaint.SearchResult;

/**
 * @author riste.tutureski
 *
 */
public class PeopleSearchResult extends SearchResult {

	@XmlElement(name="existingPeopleResult")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}
	
}
