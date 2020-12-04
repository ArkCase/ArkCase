package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
