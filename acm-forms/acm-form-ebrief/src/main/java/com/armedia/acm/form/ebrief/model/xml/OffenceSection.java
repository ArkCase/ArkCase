/**
 * 
 */
package com.armedia.acm.form.ebrief.model.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
public class OffenceSection {

	private Date firstDate;
	private List<OffenceItem> items;
	
	@XmlElement(name="offenceFirstDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getFirstDate() {
		return firstDate;
	}
	
	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
	}
	
	@XmlElement(name="offenceTableItem")
	public List<OffenceItem> getItems() {
		return items;
	}
	
	public void setItems(List<OffenceItem> items) {
		this.items = items;
	}
	
}
