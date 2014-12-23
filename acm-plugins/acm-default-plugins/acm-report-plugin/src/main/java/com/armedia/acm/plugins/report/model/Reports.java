/**
 * 
 */
package com.armedia.acm.plugins.report.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Reports {

	@XmlElement(name="repositoryFileDto")
	private List<Report> value;

	public List<Report> getValue() {
		return value;
	}

	public void setValue(List<Report> value) {
		this.value = value;
	}
	
}
