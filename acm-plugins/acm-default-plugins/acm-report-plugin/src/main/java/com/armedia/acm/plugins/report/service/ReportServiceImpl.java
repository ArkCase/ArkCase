package com.armedia.acm.plugins.report.service;

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
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;
import com.armedia.acm.services.users.model.group.AcmGroup;

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
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String PENTAHO_DASHBOARD_REPORT_EXTENSION = ".xdash";
    private static final String PENTAHO_ANALYSIS_REPORT_EXTENSION = ".xanalyzer";
    private static final String PENTAHO_INTERACTIVE_REPORT_EXTENSION = ".prpti";
    private final String PENTAHO_REPORT_URL_TEMPLATE = "PENTAHO_REPORT_URL_TEMPLATE";
    private final String PENTAHO_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer";
    private final String PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE = "PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE";
    private final String PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/prpti.view";
    private final String PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE = "PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE";
    private final String PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer?ts={timestamp}";
    private final String PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE = "PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE";
    private final String PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer";
    private String reportsPropertiesFileLocation;
    private String reportToGroupsMapPropertiesFileLocation;
    private String reportServerConfigPropertiesFileLocation;
    private Map<String, String> reportToGroupsMapProperties;
    private Map<String, String> reportPluginProperties;
    private MuleContextManager muleContextManager;
    private PropertyFileManager propertyFileManager;
    private PentahoReportUrl reportUrl;
    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;
    private AcmGroupDao groupDao;

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

        Map<String, List<String>> reportsToGroupsMap = getReportToGroupsMap();

        if (report != null && reportsToGroupsMap != null && !reportsToGroupsMap.isEmpty())
        {
            if (reportsToGroupsMap.containsKey(report.getPropertyName()))
            {
                List<String> reportToGroups = reportsToGroupsMap.get(report.getPropertyName());
                List<String> userGroups = getUserGroups(userId);
                if (reportToGroups != null && userGroups != null)
                {
                    try
                    {
                        Optional<String> optional = reportToGroups.stream().filter(reportGroup -> userGroups.contains(reportGroup))
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

    private List<String> getUserGroups(String userId)
    {
        List<String> retval = new ArrayList<>();

        try
        {
            String query = "object_id_s:" + userId
                    + " AND object_type_s:USER AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

            Authentication auth = new UsernamePasswordAuthenticationToken(userId, userId);
            String response = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 1, "");

            LOG.debug("Response: {}", response);

            if (response != null && getSearchResults().getNumFound(response) > 0)
            {
                JSONArray docs = getSearchResults().getDocuments(response);
                if (docs != null && docs.length() > 0)
                {
                    JSONObject doc = docs.getJSONObject(0);
                    retval = getSearchResults().extractStringList(doc, SearchConstants.PROPERTY_GROUPS_ID_SS);
                }
            }
        }
        catch (Exception e)
        {
            LOG.error("Cannot retrieve User information from Solr for userId={}", userId, e);
        }

        return retval;
    }

    @Override
    public boolean saveReportToGroupsMap(Map<String, List<String>> reportsToGroupsMap, Authentication auth)
    {
        boolean success;
        try
        {
            Map<String, String> prepared = prepareReportToGroupsMapForSaving(reportsToGroupsMap);
            getPropertyFileManager().storeMultiple(prepared,
                    getReportToGroupsMapPropertiesFileLocation(), true);
            setReportToGroupsMapProperties(prepared);
            success = true;
        }
        catch (Exception e)
        {
            LOG.error("Cannot save report to groups map", e);
            success = false;
        }
        return success;
    }

    @Override
    public List<String> saveAdhocGroupsToReport(String reportName, List<String> adhocGroups, Authentication auth)
    {
        String reportUpdated = "";
        try
        {
            reportUpdated += getPropertyFileManager().load(getReportToGroupsMapPropertiesFileLocation(), reportName, null);
            reportUpdated += (reportUpdated.isEmpty() ? "" : ",") + adhocGroups.stream().collect(Collectors.joining(","));
            getPropertyFileManager().store(reportName, reportUpdated, getReportToGroupsMapPropertiesFileLocation(), false);
        }
        catch (AcmEncryptionException e)
        {
            LOG.error("Cannot save groups to application role", e);
        }

        return Arrays.asList(reportUpdated.split(","));
    }

    @Override
    public List<String> removeAdhocGroupsToReport(String reportName, List<String> adhocGroups, Authentication auth)
    {
        List<String> reportGroups;
        String reportUpdated = "";
        try
        {
            reportGroups = new ArrayList<>(
                    Arrays.asList(
                            getPropertyFileManager().load(getReportToGroupsMapPropertiesFileLocation(), reportName, null).split(",")));

            for (String group : adhocGroups)
            {
                reportGroups.remove(group);
            }

            reportUpdated += reportGroups.stream().collect(Collectors.joining(","));

            getPropertyFileManager().store(reportName, reportUpdated, getReportToGroupsMapPropertiesFileLocation(), false);

        }
        catch (AcmEncryptionException e)
        {
            LOG.error("Cannot save groups to application role", e);
        }

        return Arrays.asList(reportUpdated.split(","));
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
            getPropertyFileManager().removeMultiple(propertiesToDelete, getReportToGroupsMapPropertiesFileLocation());
        }

        return reports;
    }

    @Override
    public Map<String, List<String>> getReportToGroupsMap()
    {
        Map<String, String> reportsToGroupsMap = getReportToGroupsMapProperties();
        return prepareReportToGroupsMapForRetrieving(reportsToGroupsMap);
    }

    @Override
    public List<String> getReportToGroupsPaged(String sortDirection, Integer startRow, Integer maxRows) throws IOException
    {
        return getReportToGroups(sortDirection, startRow, maxRows, "");
    }

    @Override
    public List<String> getReportToGroupsByName(String sortDirection, Integer startRow, Integer maxRows, String filterQuery)
            throws IOException
    {
        return getReportToGroups(sortDirection, startRow, maxRows, filterQuery);
    }

    @Override
    public List<String> getReportToGroups(String sortDirection, Integer startRow, Integer maxRows, String filterQuery) throws IOException
    {
        Properties reportsToGroups = propertyFileManager.readFromFile(new File(getReportToGroupsMapPropertiesFileLocation()));
        List<String> result = new ArrayList<>(reportsToGroups.stringPropertyNames());

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

        if (!filterQuery.isEmpty())
        {
            result.removeIf(report -> !(report.toLowerCase().contains(filterQuery.toLowerCase())));
        }

        return result.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
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

    private Map<String, String> prepareReportToGroupsMapForSaving(Map<String, List<String>> reportsToGroupsMap)
    {
        Map<String, String> retval = new HashMap<>();

        if (reportsToGroupsMap != null && reportsToGroupsMap.size() > 0)
        {
            reportsToGroupsMap.forEach((key, value) -> retval.put(key, StringUtils.join(value, ",")));
        }

        return retval;
    }

    private Map<String, List<String>> prepareReportToGroupsMapForRetrieving(Map<String, String> reportsToGroupsMap)
    {
        if (reportsToGroupsMap != null && reportsToGroupsMap.size() > 0)
        {
            Map<String, List<AcmGroup>> groupsCache = new HashMap<>();

            return reportsToGroupsMap.entrySet().stream()
                    .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                    .collect(Collectors.toMap(Entry::getKey,
                            entry -> Arrays.stream(entry.getValue().split(","))
                                    .flatMap(AcmRoleToGroupMapping.mapGroupsString(
                                            name -> groupsCache.computeIfAbsent(name, it -> groupDao.findByMatchingName(it))))
                                    .collect(Collectors.toList())));
        }

        return new HashMap<>();
    }

    @Override
    public String buildGroupsForReportSolrQuery(Boolean authorized, String reportId, String filterQuery) throws AcmEncryptionException
    {
        StringBuilder solrQuery = new StringBuilder();
        String groupsForReportttt = propertyFileManager.load(getReportToGroupsMapPropertiesFileLocation(), reportId, null);
        List<String> groupsForReport = new ArrayList<>(Arrays.asList(groupsForReportttt.split(",")));

        solrQuery.append(
                "object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED");

        if (groupsForReport != null)
        {
            solrQuery.append(authorized ? " AND name_lcs:" : " AND -name_lcs:");
            solrQuery.append(groupsForReport.stream().collect(Collectors.joining("\" OR \"", "(\"", "\")")));
        }
        else if (authorized)
        {
            return "";
        }

        if (!filterQuery.isEmpty())
        {
            solrQuery.append(" AND name_partial:" + filterQuery);
        }
        return solrQuery.toString();
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

    public String getReportToGroupsMapPropertiesFileLocation()
    {
        return reportToGroupsMapPropertiesFileLocation;
    }

    public void setReportToGroupsMapPropertiesFileLocation(String reportToGroupsMapPropertiesFileLocation)
    {
        this.reportToGroupsMapPropertiesFileLocation = reportToGroupsMapPropertiesFileLocation;
    }

    public String getReportServerConfigPropertiesFileLocation()
    {
        return reportServerConfigPropertiesFileLocation;
    }

    public void setReportServerConfigPropertiesFileLocation(String reportServerConfigPropertiesFileLocation)
    {
        this.reportServerConfigPropertiesFileLocation = reportServerConfigPropertiesFileLocation;
    }

    public Map<String, String> getReportToGroupsMapProperties()
    {
        return reportToGroupsMapProperties;
    }

    public void setReportToGroupsMapProperties(Map<String, String> reportToGroupsMapProperties)
    {
        this.reportToGroupsMapProperties = reportToGroupsMapProperties;
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

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }
}
