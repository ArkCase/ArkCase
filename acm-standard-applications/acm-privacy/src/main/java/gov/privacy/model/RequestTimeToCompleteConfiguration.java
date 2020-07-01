package gov.privacy.model;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class RequestTimeToCompleteConfiguration
{
    private Integer intake;
    private Integer fulfill;
    private Integer approve;
    private Integer release;
    private Integer totalTimeToComplete;

    public Integer getIntake()
    {
        return intake;
    }

    public void setIntake(Integer intake)
    {
        this.intake = intake;
    }

    public Integer getFulfill()
    {
        return fulfill;
    }

    public void setFulfill(Integer fulfill)
    {
        this.fulfill = fulfill;
    }

    public Integer getApprove()
    {
        return approve;
    }

    public void setApprove(Integer approve)
    {
        this.approve = approve;
    }

    public Integer getRelease()
    {
        return release;
    }

    public void setRelease(Integer release)
    {
        this.release = release;
    }

    public Integer getTotalTimeToComplete()
    {
        return totalTimeToComplete;
    }

    public void setTotalTimeToComplete(Integer totalTimeToComplete)
    {
        this.totalTimeToComplete = totalTimeToComplete;
    }
}
