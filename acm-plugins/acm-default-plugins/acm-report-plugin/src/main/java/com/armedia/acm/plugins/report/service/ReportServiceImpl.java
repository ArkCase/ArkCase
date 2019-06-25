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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.pentaho.config.PentahoReportUrl;
import com.armedia.acm.pentaho.config.PentahoReportsConfig;
import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.model.Reports;
import com.armedia.acm.report.config.ReportsToRolesConfig;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.model.ApplicationRolesConfig;
import com.armedia.acm.services.users.service.AcmUserRoleService;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService
{
    private static final String PENTAHO_DASHBOARD_REPORT_EXTENSION = ".xdash";
    private static final String PENTAHO_ANALYSIS_REPORT_EXTENSION = ".xanalyzer";
    private static final String PENTAHO_INTERACTIVE_REPORT_EXTENSION = ".prpti";
    private final Logger LOG = LogManager.getLogger(getClass());
    private final String PENTAHO_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer";
    private final String PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/prpti.view";
    private final String PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer?ts={timestamp}";
    private final String PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer";
    private MuleContextManager muleContextManager;
    private PentahoReportUrl reportUrl;
    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;
    private PentahoReportsConfig reportsConfig;
    private ReportsToRolesConfig reportsToRolesConfig;
    private ConfigurationPropertyService configurationPropertyService;
    private AcmUserRoleService userRoleService;
    private ApplicationRolesConfig rolesConfig;

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
        return reportsConfig.getReportToUrlMap().entrySet()
                .stream()
                .map(entry -> {
                    Report report = new Report();
                    report.setPropertyName(entry.getKey());
                    report.setPropertyPath(entry.getValue());
                    report.setTitle(createReportTitleFromKey(entry.getKey()));
                    return report;
                }).collect(Collectors.toList());
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

    private String createReportKeyFromTitle(String title) throws RuntimeException
    {
        String reportKey = "";

        if(title != null && !title.isEmpty())
        {
            String[] titleArray =  title.replaceAll("\\s","").split("(?=[A-Z])");
            List<String> titleList = Arrays.asList(titleArray);
            reportKey = titleList.stream().map(element -> StringUtils.capitalize(element.toUpperCase())).collect(Collectors.joining("_"));
        }
        else
        {
            throw new RuntimeException("Report title must not be empty");
        }
        return reportKey;
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
                userReports = reports.stream()
                        .filter(report -> checkReportAuthorization(report, userId))
                        .collect(Collectors.toList());
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

        Set<String> userRoles = userRoleService.getUserRoles(userId);

        Map<String, List<String>> reportsToRolesMap = getReportToRolesMap();

        if (report != null && reportsToRolesMap != null && !reportsToRolesMap.isEmpty())
        {
            if (reportsToRolesMap.containsKey(report.getPropertyName()))
            {
                List<String> reportToRoles = reportsToRolesMap.get(report.getPropertyName());
                if (reportToRoles != null)
                {
                    try
                    {
                        authorized = reportToRoles.stream().anyMatch(userRoles::contains);
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

    @Override
    public List<Report> sync() throws Exception
    {
        List<Report> reports = getPentahoReports();
        if (reports != null)
        {
            List<String> propertiesToDelete = new ArrayList<>();
            Map<String, String> reportToUrlMapping = reportsConfig.getReportToUrlMap();
            List<Report> missgingReports = reports.stream()
                    .filter(item ->!reportToUrlMapping.containsKey(item.getPropertyName()))
                    .collect(Collectors.toList());
            for (Report report : missgingReports)
            {
                updateReportsConfig(report, report.getTitle());
            }
            for (Entry<String, String> entry : reportToUrlMapping.entrySet())
            {
                Report found = reports.stream()
                        .filter(item -> entry.getKey().equals(item.getPropertyName()))
                        .findFirst()
                        .orElse(null);
                if (found == null)
                {
                    propertiesToDelete.add(entry.getKey());
                }
            }

            Map<String, String> reportsToRolesMapping = reportsToRolesConfig.getReportsToRolesMap();

            propertiesToDelete.forEach(item -> {
                reportToUrlMapping.remove(item);
                reportsToRolesMapping.remove(item);
            });

            configurationPropertyService.updateProperties(reportsConfig);
            configurationPropertyService.updateProperties(reportsToRolesConfig);
        }

        return reports;
    }

    @Override
    public boolean saveReports(List<Report> reports)
    {
        Map<String, String> reportToUrlMapping = reportsConfig.getReportToUrlMap();
        if (reports != null && reports.size() > 0)
        {
            for (Report report : reports)
            {
                String key = report.getPropertyName();

                if (report.isInjected())
                {
                    String value = createPentahoReportUri(report);
                    reportToUrlMapping.put(key, value);
                }
                else
                {
                    reportToUrlMapping.remove(key);
                }
            }
            configurationPropertyService.updateProperties(reportToUrlMapping);
        }
        return true;
    }

    private String createPentahoReportUri(Report report)
    {
        if (report == null)
        {
            return null;
        }

        String url;
        if (report.getName() != null && report.getName().endsWith(PENTAHO_INTERACTIVE_REPORT_EXTENSION))
        {
            url = reportsConfig.getViewReportUrlPrptiTemplate() != null ? reportsConfig.getViewReportUrlPrptiTemplate()
                    : PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE_DEFAULT;
        }
        else if (report.getName() != null && report.getName().endsWith(PENTAHO_ANALYSIS_REPORT_EXTENSION))
        {
            url = reportsConfig.getViewAnalysisReportUrlTemplate() != null ? reportsConfig.getViewAnalysisReportUrlTemplate()
                    : PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE_DEFAULT;
        }
        else if (report.getName() != null && report.getName().endsWith(PENTAHO_DASHBOARD_REPORT_EXTENSION))
        {
            url = reportsConfig.getViewDashboardReportUrlTemplate() != null ? reportsConfig.getViewDashboardReportUrlTemplate()
                    : PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE_DEFAULT;
        }
        else
        {
            url = reportsConfig.getReportUrlTemplate() != null ? reportsConfig.getReportUrlTemplate() : PENTAHO_REPORT_URL_TEMPLATE_DEFAULT;
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
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
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
        return reportsToRolesMap.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, it -> StringUtils.join(it.getValue(), ",")));
    }

    private Map<String, List<String>> prepareReportToRolesMapForRetrieving(Map<String, String> reportsToRolesMap)
    {
        return reportsToRolesMap.entrySet().stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .collect(Collectors.toMap(Entry::getKey,
                        entry -> Arrays.stream(entry.getValue().split(","))
                                .collect(Collectors.toList())));
    }

    private void updateReportsConfig(Report report, String title)
    {
        reportsConfig.getReportToUrlMap().put(createReportKeyFromTitle(title), createPentahoReportUri(report));
        reportsToRolesConfig.getReportsToRolesMap().put(createReportKeyFromTitle(title), "");
    }


    @Override
    public Map<String, List<String>> getReportToRolesMap()
    {
        return prepareReportToRolesMapForRetrieving(reportsToRolesConfig.getReportsToRolesMap());
    }

    @Override
    public List<String> getReportToRoles(String sortDirection, Integer startRow, Integer maxRows, String filterName) throws IOException
    {
        List<String> result = new ArrayList<>(reportsToRolesConfig.getReportsToRolesMap().keySet());

        if (sortDirection.contains("DESC"))
        {
            result.sort(Collections.reverseOrder());
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
        configurationPropertyService.updateProperties(reportToRolesMap);
        return true;
    }

    @Override
    public List<String> saveRolesToReport(String reportName, List<String> roles, Authentication auth)
    {
        Map<String, String> reportsToRolesMapping = reportsToRolesConfig.getReportsToRolesMap();
        String[] rolesForReport = reportsToRolesMapping.get(reportName).split(",");

        Set<String> updatedRolesForReport = Arrays.stream(rolesForReport).collect(Collectors.toSet());
        updatedRolesForReport.addAll(roles);

        reportsToRolesMapping.put(reportName, String.join(",", updatedRolesForReport));
        configurationPropertyService.updateProperties(reportsToRolesConfig);
        return new ArrayList<>(updatedRolesForReport);
    }

    @Override
    public List<String> removeRolesToReport(String reportName, List<String> roles, Authentication auth)
    {
        Map<String, String> reportsToRolesMapping = reportsToRolesConfig.getReportsToRolesMap();
        String[] rolesForReport = reportsToRolesMapping.get(reportName).split(",");

        List<String> updatedRolesForReport = Arrays.stream(rolesForReport)
                .filter(role -> !roles.contains(role))
                .collect(Collectors.toList());

        reportsToRolesMapping.put(reportName, String.join(",", updatedRolesForReport));
        configurationPropertyService.updateProperties(reportsToRolesConfig);
        return updatedRolesForReport;
    }

    @Override
    public List<String> getRolesForReport(Boolean authorized, String reportId)
    {
        String reportsToRolesConfigString = reportsToRolesConfig.getReportsToRolesMap().get(reportId);

        if (reportsToRolesConfigString != null)
        {
            String[] rolesForReport = reportsToRolesConfigString.split(",");

            if (!authorized)
            {
                return rolesConfig.getApplicationRoles().stream()
                        .filter(role -> Arrays.stream(rolesForReport).noneMatch(r -> r.trim().equals(role)))
                        .collect(Collectors.toList());
            }

            return Arrays.asList(rolesForReport);
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getRolesForReport(Boolean authorized, String reportId, int startRow, int maxRows, String sortBy, String sortDirection) {
        String reportsToRolesConfigString = reportsToRolesConfig.getReportsToRolesMap().get(reportId);

        if (reportsToRolesConfigString != null) {
            String[] rolesForReport = reportsToRolesConfigString.split(",");
            List<String> result = null;

            if (!authorized) {
                result = rolesConfig.getApplicationRoles().stream()
                        .filter(role -> Arrays.stream(rolesForReport).noneMatch(r -> r.trim().equals(role)))
                        .collect(Collectors.toList());
            } else {
                result = Arrays.asList(rolesForReport);
            }


            if (sortDirection.contains("DESC")) {
                Collections.sort(result, Collections.reverseOrder());
            } else {
                Collections.sort(result);
            }

            if (startRow > result.size()) {
                return result;
            }
            maxRows = maxRows > result.size() ? result.size() : maxRows;

            return result.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }


    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public PentahoReportUrl getReportUrl()
    {
        return reportUrl;
    }

    public void setReportUrl(PentahoReportUrl reportUrl)
    {
        this.reportUrl = reportUrl;
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

    public PentahoReportsConfig getReportsConfig()
    {
        return reportsConfig;
    }

    public void setReportsConfig(PentahoReportsConfig reportsConfig)
    {
        this.reportsConfig = reportsConfig;
    }

    public ReportsToRolesConfig getReportsToRolesConfig()
    {
        return reportsToRolesConfig;
    }

    public void setReportsToRolesConfig(ReportsToRolesConfig reportsToRolesConfig)
    {
        this.reportsToRolesConfig = reportsToRolesConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public AcmUserRoleService getUserRoleService()
    {
        return userRoleService;
    }

    public void setUserRoleService(AcmUserRoleService userRoleService)
    {
        this.userRoleService = userRoleService;
    }

    public ApplicationRolesConfig getRolesConfig()
    {
        return rolesConfig;
    }

    public void setRolesConfig(ApplicationRolesConfig rolesConfig)
    {
        this.rolesConfig = rolesConfig;
    }

}
