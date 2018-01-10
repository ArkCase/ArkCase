/**
 * 
 */
package com.armedia.acm.services.timesheet.model;

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
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_timesheet")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmTimesheet implements Serializable, AcmObject, AcmEntity, AcmStatefulEntity {

	private static final long serialVersionUID = 3346214028142786165L;

	@Id
    @TableGenerator(name = "acm_timesheet_gen",
            table = "acm_timesheet_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_timesheet",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_timesheet_gen")
    @Column(name = "cm_timesheet_id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "cm_timesheet_user_id")
	private AcmUser user;
	
	@Column(name = "cm_timesheet_start_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column(name = "cm_timesheet_end_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy="timesheet")
    private List<AcmTime> times = new ArrayList<>();
	
	@Column(name = "cm_timesheet_status")
	private String status;
	
	@Lob
    @Column(name = "cm_timesheet_details")
    private String details;

	@Column(name = "cm_timesheet_title")
	private String title;
	
	@Column(name = "cm_timesheet_creator")
	private String creator;
	
	@Column(name = "cm_timesheet_created")
    @Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name = "cm_timesheet_modifier")
	private String modifier;
	
	@Column(name = "cm_timesheet_modified")
    @Temporal(TemporalType.TIMESTAMP)
	private Date modified;

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = TimesheetConstants.OBJECT_TYPE;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type",referencedColumnName = "cm_object_type")
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
		if (getTimes() != null)
		{
			for (AcmTime time : getTimes())
			{
				time.setTimesheet(this);
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

	public Date getStartDate() 
	{
		return startDate;
	}

	public void setStartDate(Date startDate) 
	{
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<AcmTime> getTimes() {
		return times;
	}

	public void setTimes(List<AcmTime> times) {
		this.times = times;
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

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
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
		return TimesheetConstants.OBJECT_TYPE;
	}

}
