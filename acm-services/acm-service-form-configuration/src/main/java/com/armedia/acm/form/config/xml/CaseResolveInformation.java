/**
 * 
 */
package com.armedia.acm.form.config.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
public class CaseResolveInformation extends ResolveInformation {

	@XmlElement(name="caseId")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}

	@XmlElement(name="caseNumber")
	@Override
	public String getNumber() {
		return super.getNumber();
	}

	@Override
	public void setNumber(String number) {
		super.setNumber(number);
	}

	@XmlElement(name="changeDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	@Override
	public Date getDate() {
		return super.getDate();
	}

	@Override
	public void setDate(Date date) {
		super.setDate(date);
	}

	@XmlElement(name="status")
	@Override
	public String getOption() {
		return super.getOption();
	}

	@Override
	public void setOption(String option) {
		super.setOption(option);
	}
	
}
