package com.armedia.acm.services.note.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.data.AcmEntity;

import javax.persistence.*;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Date;


@Entity
@Table(name = "acm_note")
public class Note implements Serializable, AcmEntity

{
    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_note_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "cm_note_text")
    private String note;
    
    @Column(name = "cm_note_type")
    private String type;

    @Column(name = "cm_note_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_note_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_parent_object_id")
    private Long parentId;

    @Column(name = "cm_parent_object_type")
    private String parentType;

    @PrePersist
    public void beforeInsert() {
        Date today = new Date();
        setCreated(today);
        
        if (null == getType()) {
        	setType("GENERAL");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getCreator() {
        return creator;
    }

	@Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

	@Override
	public String getModifier() {
		// Not used. Modifier not exist in the database
		return null;
	}

	@Override
	public void setModifier(String modifier) {
		// Not used. Modifier not exist in the database
	}

	@Override
	public Date getModified() {
		// Not used. Modified not exist in the database
		return null;
	}

	@Override
	public void setModified(Date modified) {
		// Not used. Modified not exist in the database
	}
}
