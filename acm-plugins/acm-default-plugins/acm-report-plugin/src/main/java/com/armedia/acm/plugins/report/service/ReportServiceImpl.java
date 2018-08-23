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

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.pentaho.config.PentahoReportUrl;
import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.model.Reports;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService
{
    private static final String PENTAHO_DASHBOARD_REPORT_EXTENSION = ".xdash";
    private static final String PENTAHO_ANALYSIS_REPORT_EXTENSION = ".xanalyzer";
    private static final String PENTAHO_INTERACTIVE_REPORT_EXTENSION = ".prpti";
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final String PENTAHO_REPORT_URL_TEMPLATE = "PENTAHO_REPORT_URL_TEMPLATE";
    private final String PENTAHO_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer";
    private final String PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE = "PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE";
    private final String PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/prpti.view";
    private final String PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE = "PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE";
    private final String PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer?ts={timestamp}";
    private final String PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE = "PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE";
    private final String PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer";
    private String reportsPropertiesFileLocation;
    private String reportToRolesMapPropertiesFileLocation;
    private String reportServerConfigPropertiesFileLocation;
    private Map<String, String> reportToRolesMapProperties;
    private Map<String, String> reportPluginProperties;
    private MuleContextManager muleContextManager;
    private PropertyFileManager propertyFileManager;
    private PentahoReportUrl reportUrl;
    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;
    private Properties applicationRolesProperties;

    @Override
    public List<Report> getPentahoReports() throws Exception
    {
        Reports reports = null;

        String username = "";
        Authentication authentication = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                : null;
        if (authentication != null)
        {
            username = StringUtils.substringBeforeLast(authentication.getName(), "@");
        }

        String fullReportUrl = getReportUrl().getReportsUrl();
        String reportListUrl = fullReportUrl.replace("http://", "").replace("https://", "");

        String muleEndPoint = fullReportUrl.startsWith("http://") ? "vm://getPentahoReports.in" : "vm://getPentahoReportsSecure.in";

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        MuleMessage received = getMuleContextManager().send(muleEndPoint, reportListUrl, properties);

        String xml = received.getPayload(String.class);

        MuleException e = received.getInboundProperty("getPantehoReportsException");
        if (e != null)
        {
            throw e;
        }

        if (xml != null)
        {
            String utf8XML = new String(xml.getBytes(), StandardCharsets.UTF_8);
            reports = (Reports) convertFromXMLToObject(utf8XML, Reports.class);

            if (reports == null)
            {
                throw new RuntimeException(xml);
            }
        }
        else
        {
            throw new RuntimeException("Taking Pentaho reports failed.");
        }

        return reports.getValue();
    }

    @Override
    public List<Report> getAcmReports()
    {
        List<Report> reports = new ArrayList<>();
        if (getReportPluginProperties() != null)
        {
            reports.addAll(getReportPluginProperties().entrySet().stream().map(entry -> {
                Report report = new Report();
                report.setPropertyName(entry.getKey());
                report.setPropertyPath(entry.getValue());
                report.setTitle(createReportTitleFromKey(entry.getKey()));
                return report;
            }).collect(Collectors.toList()));
        }

        return reports;
    }

    private String createReportTitleFromKey(String key)
    {
        String retval = "";

        if (key != null && !key.isEmpty())
        {
            String[] keyArray = key.split("_");
            List<String> keyList = Arrays.asList(keyArray);
            retval = keyList.stream().map(element -> StringUtils.capitalize(element.toLowerCase())).collect(Collectors.joining(" "));
        }

        return retval;
    }

    @Override
    public List<Report> getAcmReports(String userId)
    {
        List<Report> userReports = new ArrayList<>();
        if (userId != null && !userId.isEmpty())
        {
            List<Report> reports = getAcmReports();

            if (reports != null)
            {
                userReports = reports.stream().filter(report -> checkReportAuthorization(report, userId)).collect(Collectors.toList());
            }
        }

        return userReports;
    }

    @Override
    public Map<String, String> getAcmReportsAsMap(List<Report> reports)
    {
        Map<String, String> retval = new HashMap<>();

        if (reports != null)
        {
            reports.forEach(report -> retval.put(report.getTitle(), getReportUrl().getReportUrlPath(report.getPropertyName())));
        }

        return retval;
    }

    private boolean checkReportAuthorization(Report report, String userId)
    {
        boolean authorized = false;

        Map<String, List<String>> reportsToRolesMap = getReportToRolesMap();

        if (report != null && reportsToRolesMap != null && !reportsToRolesMap.isEmpty())
        {
            if (reportsToRolesMap.containsKey(report.getPropertyName()))
            {
                List<String> reportToRoles = reportsToRolesMap.get(report.getPropertyName());
                List<String> userRoles = getApplicationRoles();
                if (reportToRoles != null && userRoles != null)
                {
                    try
                    {
                        Optional<String> optional = reportToRoles.stream().filter(reportRoles -> userRoles.contains(reportRoles))
                                .findAny();

                        authorized = optional.isPresent();
                    }
                    catch (Exception e)
                    {
                        LOG.warn("Element found is null. Proceed with execution.");
                    }
                }
            }
        }

        LOG.debug("Report authorization: {}", authorized);
        return authorized;
    }


    private List<String> getApplicationRoles()
    {
        List<String> applicationRoles = new ArrayList<>();

        try
        {
            Properties roleProperties = getApplicationRolesProperties();
            applicationRoles = Arrays.asList(roleProperties.getProperty("application.roles").split(","));
        }
        catch (Exception e)
        {
            LOG.error("Cannot read application roles from configuration.", e);
        }

        return applicationRoles;
    }

    @Override
    public List<Report> sync() throws Exception
    {
        List<Report> reports = getPentahoReports();
        if (reports != null)
        {
            List<String> propertiesToDelete = new ArrayList<>();
            for (Entry<String, String> entry : reportPluginProperties.entrySet())
            {
                Report found = reports.stream().filter(item -> entry.getKey().equals(item.getPropertyName())).findFirst().orElse(null);
                if (found == null)
                {
                    propertiesToDelete.add(entry.getKey());
                }
            }

            propertiesToDelete.forEach(item -> reportPluginProperties.remove(item));

            getPropertyFileManager().removeMultiple(propertiesToDelete, getReportsPropertiesFileLocation());
            getPropertyFileManager().removeMultiple(propertiesToDelete, getReportToRolesMapPropertiesFileLocation());
        }

        return reports;
    }

    @Override
    public boolean saveReports(List<Report> reports) throws AcmEncryptionException
    {

        if (reports != null && reports.size() > 0)
        {
            Map<String, String> propertiesToUpdate = new HashMap<>();
            List<String> propertiesToDelete = new ArrayList<>();
            for (Report report : reports)
            {
                String key = report.getPropertyName();

                if (report.isInjected())
                {
                    String value = createPentahoReportUri(report);
                    propertiesToUpdate.put(key, value);
                    reportPluginProperties.put(key, value);
                }
                else
                {
                    propertiesToDelete.add(key);
                    reportPluginProperties.remove(key);
                }
            }

            getPropertyFileManager().storeMultiple(propertiesToUpdate, getReportsPropertiesFileLocation(), false);
            getPropertyFileManager().removeMultiple(propertiesToDelete, getReportsPropertiesFileLocation());
        }
        return true;
    }

    private String createPentahoReportUri(Report report) throws AcmEncryptionException
    {
        if (report == null)
        {
            return null;
        }

        String url = null;
        if (report.getName() != null && report.getName().endsWith(PENTAHO_INTERACTIVE_REPORT_EXTENSION))
        {
            url = getPropertyFileManager().load(getReportServerConfigPropertiesFileLocation(), PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE,
                    PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE_DEFAULT);
        }
        else if (report.getName() != null && report.getName().endsWith(PENTAHO_ANALYSIS_REPORT_EXTENSION))
        {
            url = getPropertyFileManager().load(getReportServerConfigPropertiesFileLocation(), PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE,
                    PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE_DEFAULT);
        }
        else if (report.getName() != null && report.getName().endsWith(PENTAHO_DASHBOARD_REPORT_EXTENSION))
        {
            url = getPropertyFileManager().load(getReportServerConfigPropertiesFileLocation(), PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE,
                    PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE_DEFAULT);
        }
        else
        {
            url = getPropertyFileManager().load(getReportServerConfigPropertiesFileLocation(), PENTAHO_REPORT_URL_TEMPLATE,
                    PENTAHO_REPORT_URL_TEMPLATE_DEFAULT);
        }

        if (url != null)
        {
            url = url.replace("{path}", report.getPropertyPath());
            url = url.replace("{timestamp}", String.valueOf(System.currentTimeMillis()));
        }

        return url;
    }

    private Object convertFromXMLToObject(String xml, Class<?> c)
    {
        Object obj = null;
        try
        {
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Element element = document.getDocumentElement();
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<?> jaxbElement = unmarshaller.unmarshal(element, c);
            obj = jaxbElement.getValue();
        }
        catch (Exception e)
        {
            LOG.error("Error while creating Object from XML. ", e);
        }

        return obj;
    }


    private Map<String, String> prepareReportToRolesMapForSaving(Map<String, List<String>> reportsToRolesMap)
    {
        Map<String, String> retval = new HashMap<>();

        if (reportsToRolesMap != null && reportsToRolesMap.size() > 0)
        {
            reportsToRolesMap.forEach((key, value) -> retval.put(key, StringUtils.join(value, ",")));
        }

        return retval;
    }

    private Map<String, List<String>> prepareReportToRolesMapForRetrieving(Map<String, String> reportsToRolesMap)
    {
        return reportsToRolesMap.entrySet().stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .collect(Collectors.toMap(Entry::getKey,
                        entry -> Arrays.stream(entry.getValue().split(","))
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> getReportToRolesMap()
    {
        Map<String, String> reportsToRolesMap = getReportToRolesMapProperties();
        return prepareReportToRolesMapForRetrieving(reportsToRolesMap);
    }

    @Override
    public List<String> getReportToRoles(String sortDirection, Integer startRow, Integer maxRows, String filterName) throws IOException
    {
        Properties reportsToRoles = propertyFileManager.readFromFile(new File(getReportToRolesMapPropertiesFileLocation()));
        List<String> result = new ArrayList<>(reportsToRoles.stringPropertyNames());

        if (sortDirection.contains("DESC"))
        {
            Collections.sort(result, Collections.reverseOrder());
        }
        else
        {
            Collections.sort(result);
        }

        if (startRow > result.size())
        {
            return result;
        }
        maxRows = maxRows > result.size() ? result.size() : maxRows;

        if (!filterName.isEmpty())
        {
            result.removeIf(report -> !(report.toLowerCase().contains(filterName.toLowerCase())));
        }

        return result.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
    }

    @Override
    public List<String> getReportToRolesPaged(String sortDirection, Integer startRow, Integer maxRows) throws IOException
    {
        return getReportToRoles(sortDirection, startRow, maxRows, "");
    }

    @Override
    public List<String> getReportToRolesByName(String sortDirection, Integer startRow, Integer maxRows, String filterQuery)
            throws IOException
    {
        return getReportToRoles(sortDirection, startRow, maxRows, filterQuery);
    }

    @Override
    public boolean saveReportToRolesMap(Map<String, List<String>> reportToRolesMap, Authentication auth)
    {
        boolean success;
        try
        {
            Map<String, String> prepared = prepareReportToRolesMapForSaving(reportToRolesMap);
            getPropertyFileManager().storeMultiple(prepared,
                    getReportToRolesMapPropertiesFileLocation(), true);
            setReportToRolesMapProperties(prepared);
            success = true;
        }
        catch (Exception e)
        {
            LOG.error("Cannot save report to roles map", e);
            success = false;
        }
        return success;
    }

    @Override
    public List<String> saveRolesToReport(String reportName, List<String> roles, Authentication auth)
    {
        String reportUpdated = "";
        try
        {
            reportUpdated += getPropertyFileManager().load(getReportToRolesMapPropertiesFileLocation(), reportName, null);
            reportUpdated += (reportUpdated.isEmpty() ? "" : ",") + roles.stream().collect(Collectors.joining(","));
            getPropertyFileManager().store(reportName, reportUpdated, getReportToRolesMapPropertiesFileLocation(), false);
        }
        catch (AcmEncryptionException e)
        {
            LOG.warn("Cannot save roles to report", e);
        }

        return Arrays.asList(reportUpdated.split(","));

    }

    @Override
    public List<String> removeRolesToReport(String reportName, List<String> roles, Authentication auth) throws Exception
    {
        String reportUpdated = "";
        List<String> rolesForReport = new ArrayList<>(
                Arrays.asList(propertyFileManager.load(getReportToRolesMapPropertiesFileLocation(), reportName, "").split(",")));

        for (String role : roles)
        {
            rolesForReport.remove(role);
        }

        reportUpdated += rolesForReport.stream().collect(Collectors.joining(","));

        getPropertyFileManager().store(reportName, reportUpdated, getReportToRolesMapPropertiesFileLocation(), false);

        return Arrays.asList(reportUpdated.split(","));
    }

    @Override
    public List<String> getRolesForReport(Boolean authorized, String reportId) throws AcmEncryptionException
    {

        List<String> rolesForReport = new ArrayList<>(
                Arrays.asList(propertyFileManager.load(getReportToRolesMapPropertiesFileLocation(), reportId, "").split(",")));

        if (!authorized)
        {
            return getApplicationRoles().stream()
                    .filter(role -> rolesForReport.stream().noneMatch(r -> r.trim().equals(role)))
                    .collect(Collectors.toList());
        }

        return rolesForReport;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public PentahoReportUrl getReportUrl()
    {
        return reportUrl;
    }

    public void setReportUrl(PentahoReportUrl reportUrl)
    {
        this.reportUrl = reportUrl;
    }

    public String getReportsPropertiesFileLocation()
    {
        return reportsPropertiesFileLocation;
    }

    public void setReportsPropertiesFileLocation(String reportsPropertiesFileLocation)
    {
        this.reportsPropertiesFileLocation = reportsPropertiesFileLocation;
    }

    public String getReportServerConfigPropertiesFileLocation()
    {
        return reportServerConfigPropertiesFileLocation;
    }

    public void setReportServerConfigPropertiesFileLocation(String reportServerConfigPropertiesFileLocation)
    {
        this.reportServerConfigPropertiesFileLocation = reportServerConfigPropertiesFileLocation;
    }

    public Map<String, String> getReportPluginProperties()
    {
        return reportPluginProperties;
    }

    public void setReportPluginProperties(Map<String, String> reportPluginProperties)
    {
        this.reportPluginProperties = reportPluginProperties;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public String getReportToRolesMapPropertiesFileLocation()
    {
        return reportToRolesMapPropertiesFileLocation;
    }

    public void setReportToRolesMapPropertiesFileLocation(String reportToRolesMapPropertiesFileLocation)
    {
        this.reportToRolesMapPropertiesFileLocation = reportToRolesMapPropertiesFileLocation;
    }

    public Map<String, String> getReportToRolesMapProperties()
    {
        return reportToRolesMapProperties;
    }

    public void setReportToRolesMapProperties(Map<String, String> reportToRolesMapProperties)
    {
        this.reportToRolesMapProperties = reportToRolesMapProperties;
    }

    public Properties getApplicationRolesProperties()
    {
        return applicationRolesProperties;
    }

    public void setApplicationRolesProperties(Properties applicationRolesProperties)
    {
        this.applicationRolesProperties = applicationRolesProperties;
    }
}
