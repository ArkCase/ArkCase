/**
 * 
 */
package com.armedia.acm.plugins.report.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Report implements Serializable{

	private static final long serialVersionUID = -8652555531179054044L;
	
	private String id;
	private String name;
	private String propertyName;
	private String title;
	private String description;
	private Long fileSize;
	
	@XmlElement(name="createdDate")
	private Date created;
	
	@XmlElement(name="lastModifiedDate")
	private Date modified;
	
	private boolean folder;
	private boolean hidden;
	private boolean locked;
	private String path;
	private String propertyPath;
	private String versionId;
	private boolean versioned;
	private String locale;
	private boolean injected;
	
	@XmlElement(name="localePropertiesMapEntries")
	private ReportProperties properties;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		
		setPropertyName(null);
	}
	
	public String getPropertyName() {
		if (propertyName == null) {
			setPropertyName(null);
		}
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		if (propertyName == null) {
			if (getName() != null && getName().length() > 5) {
				String str = getName().replace(" ", "_").substring(0, getName().length()-5);
				if (!str.equals(str.toUpperCase())) {
					str = str.replaceAll("(?<!^)([a-z])([A-Z])", "$1_$2");
				}
				this.propertyName = str.toUpperCase();
			}
		} else {
			this.propertyName = propertyName;			
		}
		
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getModified() {
		return modified;
	}
	
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public boolean isFolder() {
		return folder;
	}
	
	public void setFolder(boolean folder) {
		this.folder = folder;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
		
		setPropertyPath(null);
	}
	
	public String getPropertyPath() {
		if (propertyPath == null) {
			setPropertyPath(null);
		}
		return propertyPath;
	}

	public void setPropertyPath(String propertyPath) {
		if (propertyPath == null) {
			if (getPath() != null) {
				this.propertyPath = getPath().replace("/", ":");
			}
		} else {
			this.propertyPath = propertyPath;
		}
	}

	public String getVersionId() {
		return versionId;
	}
	
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	
	public boolean isVersioned() {
		return versioned;
	}
	
	public void setVersioned(boolean versioned) {
		this.versioned = versioned;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean isInjected() {
		return injected;
	}

	public void setInjected(boolean injected) {
		this.injected = injected;
	}

	public ReportProperties getProperties() {
		return properties;
	}

	public void setProperties(ReportProperties properties) {
		this.properties = properties;
	}
}
