package com.armedia.acm.plugins.report.service;

/*-
 * #%L
 * ACM Default Plugin: report
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.report.model.Report;

import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public interface ReportService
{

    public List<Report> getPentahoReports() throws Exception, MuleException;

    public List<Report> getAcmReports();

    public List<Report> getAcmReports(String userId);

    public Map<String, String> getAcmReportsAsMap(List<Report> reports);

    public boolean saveReports(List<Report> reports) throws AcmEncryptionException;

    public Map<String, List<String>> getReportToRolesMap();

    public List<String> getReportToRoles(String sortDirection, Integer startRow, Integer maxRows, String filterName) throws IOException;

    public List<String> getReportToRolesPaged(String sortDirection, Integer startRow, Integer maxRows) throws IOException;

    public List<String> getReportToRolesByName(String sortDirection, Integer startRow, Integer maxRows, String filterQuery)
            throws IOException;

    public boolean saveReportToRolesMap(Map<String, List<String>> reportToRolesMap, Authentication auth);

    public List<String> saveRolesToReport(String reportName, List<String> roles, Authentication auth) throws AcmEncryptionException;

    public List<String> removeRolesToReport(String reportName, List<String> roles, Authentication auth) throws Exception;

    public List<String> getRolesForReport(Boolean authorized, String reportId) throws AcmEncryptionException;

    public List<Report> sync() throws Exception;

    public List<String> getRolesForReport(Boolean authorized, String reportId, int startRow, int maxRows, String sortBy, String sortDirection);

}
