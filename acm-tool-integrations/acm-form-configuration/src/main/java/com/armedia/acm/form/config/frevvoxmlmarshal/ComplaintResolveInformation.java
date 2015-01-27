/**
 * 
 */
package com.armedia.acm.form.config.frevvoxmlmarshal;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
public class ComplaintResolveInformation extends ResolveInformation {

	@XmlElement(name="complaintId")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}

	@XmlElement(name="complaintNumber")
	@Override
	public String getNumber() {
		return super.getNumber();
	}

	@Override
	public void setNumber(String number) {
		super.setNumber(number);
	}

	@XmlElement(name="closeDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	@Override
	public Date getDate() {
		return super.getDate();
	}

	@Override
	public void setDate(Date date) {
		super.setDate(date);
	}

	@XmlElement(name="disposition")
	@Override
	public String getOption() {
		return super.getOption();
	}

	@Override
	public void setOption(String option) {
		super.setOption(option);
	}
	
}
