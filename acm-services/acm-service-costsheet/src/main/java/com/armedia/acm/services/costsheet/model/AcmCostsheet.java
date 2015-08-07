/**
 * 
 */
package com.armedia.acm.services.costsheet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_costsheet")
public class AcmCostsheet  implements Serializable, AcmObject, AcmEntity, AcmStatefulEntity {

	private static final long serialVersionUID = 6290288826480329085L;

	@Id
    @TableGenerator(name = "acm_costsheet_gen",
            table = "acm_costsheet_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_costsheet",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_costsheet_gen")
    @Column(name = "cm_costsheet_id")
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "cm_costsheet_user_id")
	private AcmUser user;
	
	@Column(name = "cm_costsheet_object_id")
	private Long parentId;
	
	@Column(name = "cm_costsheet_object_type")
	private String parentType;
	
	@Column(name = "cm_costsheet_object_number")
	private String parentNumber;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy="costsheet")
    private List<AcmCost> costs = new ArrayList<>();
	
	@Column(name = "cm_costsheet_status")
	private String status;
	
	@Lob
    @Column(name = "cm_costsheet_details")
    private String details;
	
	@Column(name = "cm_costsheet_creator")
	private String creator;
	
	@Column(name = "cm_costsheet_created")
    @Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "cm_costsheet_modifier")
	private String modifier;
	
	@Column(name = "cm_costsheet_modified")
    @Temporal(TemporalType.TIMESTAMP)
	private Date modified;

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type")
    })
    private List<AcmParticipant> participants = new ArrayList<>();
	
	@OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container = new AcmContainer();
	
	@PrePersist
    protected void beforeInsert()
    {
		setChildPointers();
    }
	
	@PreUpdate
    protected void beforeUpdate()
    {
		setChildPointers();
    }
	
	private void setChildPointers()
	{
		if (getCosts() != null)
		{
			for (AcmCost time : getCosts())
			{
				time.setCostsheet(this);
			}
		}

        if(objectType == null){
            objectType = getObjectType();
        }

		if (getParticipants() != null)
		{
			for (AcmParticipant participant : getParticipants())
			{
				participant.setObjectId(getId());
				participant.setObjectType(getObjectType());
			}
		}
		
		if (getContainer() != null)
		{
			getContainer().setContainerObjectId(getId());
			getContainer().setContainerObjectType(getObjectType());
		}
	}
	
	@Override
	public Long getId() 
	{
		return id;
	}
	
	public void setId(Long id) 
	{
		this.id = id;
	}
	
	public AcmUser getUser() {
		return user;
	}

	public void setUser(AcmUser user) {
		this.user = user;
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

	public String getParentNumber() {
		return parentNumber;
	}

	public void setParentNumber(String parentNumber) {
		this.parentNumber = parentNumber;
	}

	public List<AcmCost> getCosts() {
		return costs;
	}

	public void setCosts(List<AcmCost> costs) {
		this.costs = costs;
	}

	@Override
	public String getStatus() 
	{
		return status;
	}

	@Override
	public void setStatus(String status) 
	{
		this.status = status;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String getCreator() 
	{
		return creator;
	}

	@Override
	public void setCreator(String creator) 
	{
		this.creator = creator;		
	}

	@Override
	public String getModifier() 
	{
		return modifier;
	}

	@Override
	public void setModifier(String modifier) 
	{
		this.modifier = modifier;
	}

	@Override
	public Date getCreated() 
	{
		return created;
	}

	@Override
	public void setCreated(Date created) 
	{
		this.created = created;
	}

	@Override
	public Date getModified() 
	{
		return modified;
	}

	@Override
	public void setModified(Date modified) 
	{
		this.modified = modified;
	}
	
	public List<AcmParticipant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<AcmParticipant> participants) {
		this.participants = participants;
	}

	public AcmContainer getContainer() {
		return container;
	}

	public void setContainer(AcmContainer container) {
		this.container = container;
	}

	@Override
	@JsonIgnore
	public String getObjectType() 
	{
		return CostsheetConstants.OBJECT_TYPE;
	}

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
