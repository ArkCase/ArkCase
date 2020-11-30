package gov.foia.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@JsonIdentityInfo(generator = JSOGGenerator.class)
@Table(name = "acm_response_installment")
public class ResponseInstallment implements Serializable
{

    @Id
    @TableGenerator(name = "response_installment_gen", table = "acm_response_installment_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_response_installment", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "response_installment_gen")
    @Column(name = "cm_response_installment_id")
    Long id;

    @Column(name = "cm_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(name = "cm_max_download_attempts")
    private Integer maxDownloadAttempts;

    @Column(name = "cm_parent_number")
    private String parentNumber;

    @Column(name = "cm_num_download_attempts")
    private Integer numDownloadAttempts = 0;


    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Integer getMaxDownloadAttempts()
    {
        return maxDownloadAttempts;
    }

    public void setMaxDownloadAttempts(Integer maxDownloadAttempts)
    {
        this.maxDownloadAttempts = maxDownloadAttempts;
    }

    public String getParentNumber()
    {
        return parentNumber;
    }

    public void setParentNumber(String parentNumber)
    {
        this.parentNumber = parentNumber;
    }

    public Integer getNumDownloadAttempts()
    {
        return numDownloadAttempts;
    }

    public void setNumDownloadAttempts(Integer numDownloadAttempts)
    {
        this.numDownloadAttempts = numDownloadAttempts;
    }
}
