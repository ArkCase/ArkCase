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
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final String PENTAHO_REPORT_URL_TEMPLATE = "PENTAHO_REPORT_URL_TEMPLATE";
    private final String PENTAHO_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer";
    private final String PENTAHO_SERVER_USER = "PENTAHO_SERVER_USER";
    private final String PENTAHO_SERVER_USER_DEFAULT = "admin";
    private final String PENTAHO_SERVER_PASSWORD = "PENTAHO_SERVER_PASSWORD";
    private final String PENTAHO_SERVER_PASSWORD_DEFAULT = "password";
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

    @Override
    public List<Report> getPentahoReports() throws Exception, MuleException
    {
        Reports reports = null;

        String serverFormUser = getPropertyFileManager().load(getReportServerConfigPropertiesFileLocation(), PENTAHO_SERVER_USER,
                PENTAHO_SERVER_USER_DEFAULT);
        String serverFormPassword = getPropertyFileManager().load(getReportServerConfigPropertiesFileLocation(), PENTAHO_SERVER_PASSWORD,
                PENTAHO_SERVER_PASSWORD_DEFAULT);

        String fullReportUrl = getReportUrl().getReportsUrl();
        String reportListUrl = fullReportUrl.replace("http://", "").replace("https://", "");
        reportListUrl += "?userid=" + serverFormUser + "&password=" + serverFormPassword;

        String muleEndPoint = fullReportUrl.startsWith("http://") ? "vm://getPentahoReports.in" : "vm://getPentahoReportsSecure.in";

        MuleMessage received = getMuleContextManager().send(muleEndPoint, reportListUrl);

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
        } else
        {
            throw new RuntimeException("Taking Pentaho reports failed.");
        }

        return reports.getValue();
    }

    @Override
    public List<Report> getAcmReports()
    {
        List<Report> reports = new ArrayList<Report>();
        if (getReportPluginProperties() != null)
        {
            reports.addAll(getReportPluginProperties().entrySet().stream().map(entry ->
            {
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
            if (keyArray != null)
            {
                List<String> keyList = Arrays.asList(keyArray);
                retval = keyList.stream().map(element -> StringUtils.capitalize(element.toLowerCase())).collect(Collectors.joining(" "));
            }
        }

        return retval;
    }

    @Override
    public List<Report> getAcmReports(String userId)
    {
        List<Report> userReports = new ArrayList<Report>();
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
        Map<String, String> retval = new HashMap<String, String>();

        if (reports != null)
        {
            reports.stream().forEach(report ->
            {
                retval.put(report.getTitle(), getReportUrl().getReportUrlPath(report.getPropertyName()));
            });
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
                    } catch (Exception e)
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
        List<String> retval = new ArrayList<String>();

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
        } catch (Exception e)
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
        } catch (Exception e)
        {
            LOG.error("Cannot save report to groups map", e);
            success = false;
        }
        return success;
    }

    @Override
    public Map<String, List<String>> getReportToGroupsMap()
    {
        Map<String, String> reportsToGroupsMap = getReportToGroupsMapProperties();
        return prepareReportToGroupsMapForRetrieving(reportsToGroupsMap);
    }

    @Override
    public boolean saveReports(List<Report> reports) throws AcmEncryptionException
    {

        if (reports != null && reports.size() > 0)
        {
            Map<String, String> propertiesToUpdate = new HashMap<String, String>();
            List<String> propertiesToDelete = new ArrayList<String>();
            for (Report report : reports)
            {
                String key = report.getPropertyName();

                if (report.isInjected())
                {
                    String value = createPentahoReportUri(report.getPropertyPath());
                    propertiesToUpdate.put(key, value);
                    reportPluginProperties.put(key, value);
                } else
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

    private String createPentahoReportUri(String path) throws AcmEncryptionException
    {
        String url = getPropertyFileManager().load(getReportServerConfigPropertiesFileLocation(), PENTAHO_REPORT_URL_TEMPLATE,
                PENTAHO_REPORT_URL_TEMPLATE_DEFAULT);

        if (url != null)
        {
            url = url.replace("{path}", path);
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
        } catch (Exception e)
        {
            LOG.error("Error while creating Object from XML. ", e);
        }

        return obj;
    }

    private Map<String, String> prepareReportToGroupsMapForSaving(Map<String, List<String>> reportsToGroupsMap)
    {
        Map<String, String> retval = new HashMap<String, String>();

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
            return reportsToGroupsMap.entrySet().stream().filter(entry -> !"".equals(entry.getValue()) && entry.getValue() != null)
                    .collect(Collectors.toMap(Entry::getKey, entry -> Arrays.<String> asList(entry.getValue().split(","))));
        }

        return new HashMap<String, List<String>>();
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
}
