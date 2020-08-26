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

import com.armedia.acm.configuration.service.CollectionPropertiesConfigurationService;
import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.configuration.util.MergeFlags;
import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.pentaho.config.PentahoReportUrl;
import com.armedia.acm.pentaho.config.PentahoReportsConfig;
import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.model.Reports;
import com.armedia.acm.report.config.ReportsToRolesConfig;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.model.ApplicationRolesConfig;
import com.armedia.acm.services.users.service.AcmUserRoleService;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
    private CollectionPropertiesConfigurationService collectionPropertiesConfigurationService;
    private PentahoReportUrl reportUrl;
    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;
    private PentahoReportsConfig reportsConfig;
    private ReportsToRolesConfig reportsToRolesConfig;
    private ConfigurationPropertyService configurationPropertyService;
    private AcmUserRoleService userRoleService;
    private ApplicationRolesConfig rolesConfig;
    private RestTemplate restTemplate;
    private PdfService pdfService;

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

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(fullReportUrl);
        request.setHeader("X-ARKCASE-EXTERNAL-USER", username);

        String xml = null;

        try (CloseableHttpResponse response = httpClient.execute(request))
        {
            LOG.debug("Response status: = [{}]", response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            xml = EntityUtils.toString(entity, "utf-8");
        }
        catch (Exception e)
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
        return reportsConfig.getReports().entrySet()
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

        if (title != null && !title.isEmpty())
        {
            String[] titleArray = title.replaceAll("\\s", "_").split("_");
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
        Map<String, List<String>> reportsToRolesMapping = reportsToRolesConfig.getReportsToRolesMap();

        if (reports != null)
        {
            List<String> propertiesToDelete = new ArrayList<>();
            Map<String, String> reportToUrlMapping = reportsConfig.getReports();
            List<Report> missingReports = reports.stream()
                    .filter(item -> !reportToUrlMapping.containsKey(item.getPropertyName()))
                    .collect(Collectors.toList());

            if (!missingReports.isEmpty())
            {
                updateReportsConfig(missingReports);
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

            propertiesToDelete.forEach(item -> {
                reportToUrlMapping.remove(item);
                reportsToRolesMapping.remove(item);
            });

        }

        return reports;
    }

    @Override
    public boolean saveReports(List<Report> reports)
    {
        Map<String, String> reportToUrlMapping = reportsConfig.getReports();
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
            Map<String, Object> runtimeMapWithRootKey = new HashMap<>();
            runtimeMapWithRootKey.put(PentahoReportsConfig.REPORT_CONFIG_PROP_KEY, reportToUrlMapping);

            configurationPropertyService.updateProperties(runtimeMapWithRootKey);
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
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
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

    private void updateReportsConfig(List<Report> missingReports)
    {
        Map<String, Object> reportsRolesConfig = new HashMap<>();
        Map<String, String> reportsConfig = new HashMap<>();

        for (Report report : missingReports)
        {
            reportsConfig.put(createReportKeyFromTitle(report.getTitle()), createPentahoReportUri(report));

            collectionPropertiesConfigurationService.addEmptyListCollection(
                    ReportsToRolesConfig.REPORTS_TO_ROLES_PROP_KEY,
                    createReportKeyFromTitle(report.getTitle()),
                    MergeFlags.MERGE.toString(),
                    reportsRolesConfig);
        }

        Map<String, Object> runtimeMapWithRootKey = new HashMap<>();
        runtimeMapWithRootKey.put(ReportsToRolesConfig.REPORTS_TO_ROLES_PROP_KEY, reportsRolesConfig);
        runtimeMapWithRootKey.put(MergeFlags.MERGE.getSymbol() + PentahoReportsConfig.REPORT_CONFIG_PROP_KEY, reportsConfig);

        configurationPropertyService.updateProperties(runtimeMapWithRootKey);

    }

    @Override
    public Map<String, List<String>> getReportToRolesMap()
    {
        return reportsToRolesConfig.getReportsToRolesMap();
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
    public List<String> getReportToRolesByName(String sortDirection, Integer startRow, Integer maxRows, String filterName)
            throws IOException
    {
        return getReportToRoles(sortDirection, startRow, maxRows, filterName);
    }

    @Override
    public boolean saveReportToRolesMap(Map<String, List<String>> reportToRolesMap, Authentication auth)
    {
        return true;
    }

    @Override
    public List<String> saveRolesToReport(String reportName, List<Object> roles, Authentication auth)
    {
        List<String> rolesForReport = reportsToRolesConfig.getReportsToRolesMap().get(reportName);

        if (rolesForReport == null)
        {
            rolesForReport = new ArrayList<>();
        }

        rolesForReport.addAll((List<String>) (Object) roles);

        Map<String, Object> reportRolesConfig = collectionPropertiesConfigurationService.updateMapProperty(
                ReportsToRolesConfig.REPORTS_TO_ROLES_PROP_KEY, reportName,
                roles,
                MergeFlags.MERGE);

        configurationPropertyService.updateProperties(reportRolesConfig);

        return rolesForReport;
    }

    @Override
    public List<Object> removeRolesToReport(String reportName, List<Object> roles, Authentication auth)
    {
        Map<String, List<String>> reportsToRolesMapping = reportsToRolesConfig.getReportsToRolesMap();
        List<String> rolesForReport = reportsToRolesMapping.get(reportName);

        List<Object> updatedRolesForReport = rolesForReport.stream()
                .filter(role -> !roles.contains(role))
                .collect(Collectors.toList());

        Map<String, Object> reportRolesConfig = collectionPropertiesConfigurationService.updateMapProperty(
                ReportsToRolesConfig.REPORTS_TO_ROLES_PROP_KEY, reportName,
                roles,
                MergeFlags.REMOVE);

        configurationPropertyService.updateProperties(reportRolesConfig);

        return updatedRolesForReport;
    }

    @Override
    public List<String> getRolesForReport(Boolean authorized, String reportId)
    {
        List<String> reportsToRolesConfigString = reportsToRolesConfig.getReportsToRolesMap().get(reportId);

        if (reportsToRolesConfigString != null)
        {
            List<String> rolesForReport = reportsToRolesConfigString;

            if (!authorized)
            {
                return rolesConfig.getApplicationRoles().stream()
                        .filter(role -> rolesForReport.stream().noneMatch(r -> r.equals(role)))
                        .collect(Collectors.toList());
            }

            return rolesForReport;
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getRolesForReport(Boolean authorized, String reportId, int startRow, int maxRows, String sortBy,
            String sortDirection, String filterName)
    {
        List<String> rolesForReport = reportsToRolesConfig.getReportsToRolesMap().get(reportId);

        if (rolesForReport == null)
        {
            rolesForReport = new ArrayList<>();
        }

        List<String> result = rolesConfig.getApplicationRoles();

        if (authorized)
        {
            result.retainAll(rolesForReport);
        }
        else
        {
            result.removeAll(rolesForReport);
        }

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
            result.removeIf(role -> !(role.toLowerCase().contains(filterName.toLowerCase())));
        }

        return result.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
    }

    @Override
    public File exportReportsPDFFormat(List<String> orderedReportTitles) throws Exception
    {
        RestTemplate restTemplate = buildReportsRestTemplate();
        org.springframework.http.HttpEntity<Object> entity = buildReportsRestEntity();

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();

        Map<String, File> downloadedReports = orderedReportTitles.parallelStream()
                .map(reportTitle -> {
                    String reportToExportUrl = String.format("%s%s%s/service/export", reportsConfig.getServerUrl(),
                            reportsConfig.getReportUrl(), reportTitle);

                    ResponseEntity<Resource> response = restTemplate.exchange(reportToExportUrl, HttpMethod.GET, entity, Resource.class);

                    File file = null;
                    try
                    {
                        file = File.createTempFile("pentaho-downloaded-report", ".pdf");
                        FileUtils.copyInputStreamToFile(response.getBody().getInputStream(), file);
                    }
                    catch (IOException e)
                    {
                        LOG.warn("Failed to save temp file for report {}.", reportTitle);
                    }
                    return new AbstractMap.SimpleEntry<>(reportTitle, file);
                })
                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);

        for (String reportTitle : orderedReportTitles)
        {
            File downloadedReport = downloadedReports.get(reportTitle);

            if (downloadedReport == null)
            {
                LOG.warn("Report {} won't be included and may not exists.", reportTitle);
                continue;
            }

            try (FileInputStream downloadedReportInputStream = FileUtils.openInputStream(downloadedReport);
                    PDDocument document = PDDocument.load(downloadedReportInputStream))
            {
                // split pdf document to get only report table page
                Splitter splitter = new Splitter();
                List<PDDocument> pages = splitter.split(document);
                PdfReader pdfReader = new PdfReader(downloadedReport.getAbsolutePath());

                if (!PdfTextExtractor.getTextFromPage(pdfReader, 2).contains("About this Report"))
                {
                    PDDocument pageToMerge = pages.get(1);

                    try (ByteArrayOutputStream out = new ByteArrayOutputStream())
                    {
                        pageToMerge.save(out);
                        pageToMerge.close();
                        try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray()))
                        {
                            pdfService.addSource(pdfMergerUtility, in);
                        }
                    }
                }

                pdfReader.close();
            }

            downloadedReport.delete();
        }

        File mergedReportsTemp = File.createTempFile("reports-merged-files", ".pdf");
        File mergedPDFs = File.createTempFile("reports-merged-pdfs", ".pdf");

        try (FileOutputStream fileOutputStream = new FileOutputStream(mergedReportsTemp);
                FileOutputStream fos = new FileOutputStream(mergedPDFs))
        {
            pdfService.mergeSources(pdfMergerUtility, fileOutputStream);

            try (PDDocument document = PDDocument.load(mergedReportsTemp))
            {
                PDDocument updatedDocument = pdfService.replacePageNumbers(document);
                updatedDocument.save(fos);
            }
        }

        return mergedPDFs;
    }

    @Override
    public RestTemplate buildReportsRestTemplate()
    {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(60 * 1000);
        return new RestTemplate(requestFactory);
    }

    @Override
    public org.springframework.http.HttpEntity<Object> buildReportsRestEntity()
    {
        String auth = String.format("%s:%s", reportsConfig.getServerUser(), reportsConfig.getServerPassword());
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
        String basicAuthenticationHeaderValue = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        headers.set(HttpHeaders.AUTHORIZATION, basicAuthenticationHeaderValue);
        return new org.springframework.http.HttpEntity<>("body", headers);
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

    public void setCollectionPropertiesConfigurationService(
            CollectionPropertiesConfigurationService collectionPropertiesConfigurationService)
    {
        this.collectionPropertiesConfigurationService = collectionPropertiesConfigurationService;
    }

    public PdfService getPdfService()
    {
        return pdfService;
    }

    public void setPdfService(PdfService pdfService)
    {
        this.pdfService = pdfService;
    }
}
