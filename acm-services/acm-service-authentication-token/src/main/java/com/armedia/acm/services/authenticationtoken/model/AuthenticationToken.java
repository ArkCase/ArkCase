package com.armedia.acm.services.authenticationtoken.model;


import com.armedia.acm.data.AcmEntity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "acm_authentication_token")
public class AuthenticationToken implements Serializable, AcmEntity

{
    private static final long serialVersionUID = -1154137631399833851L;

    @Id
    @TableGenerator(name = "acm_authentication_token_gen",
            table = "acm_authentication_token_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_authentication_token",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_authentication_token_gen")
    @Column(name = "cm_authentication_token_id")
    private Long id;

    @Column(name = "cm_authentication_token_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_authentication_token_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_authentication_token_modified", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_authentication_token_modifier", nullable = false, insertable = true, updatable = true)
    private String modifier;

    @Column(name = "cm_authentication_token_key", nullable = false, insertable = true, updatable = false)
    private String key;

    @Column(name = "cm_authentication_token_email", nullable = false, insertable = true, updatable = false)
    private String email;

    @Column(name = "cm_authentication_token_password", nullable = true, insertable = true, updatable = false)
    private String password;

    @Column(name = "cm_authentication_token_status", nullable = true, insertable = true, updatable = true)
    private String status;

    @Column(name = "cm_authentication_token_file_id", nullable = true, insertable = true, updatable = true)
    private Long fileId;

    @PrePersist
    public void beforeInsert() {
        Date today = new Date();
        setCreated(today);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
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
