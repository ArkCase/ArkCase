package gov.foia.web.api;

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

import com.armedia.acm.pdf.PdfServiceException;
import com.armedia.acm.plugins.report.service.ReportService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import gov.foia.service.DOJReport;
import gov.foia.service.NiemExportService;

@Controller
@RequestMapping({ "/api/v1/plugin/report", "/api/latest/plugin/report" })
public class ExportPentahoReportsAPIController
{

    private ReportService reportService;
    private NiemExportService niemExportService;

    @RequestMapping(value = "/exportYearlyReport", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity exportPentahoReports(@RequestParam(value = "exportFormatType") String exportFormatType,
            @RequestParam(value = "fiscalYear") int fiscalYear, Authentication auth)
            throws TransformerException, ParserConfigurationException, IOException, PdfServiceException
    {
        if (exportFormatType.equals("pdf"))
        {
            List<String> reportTitles = niemExportService.getYearlyReportTitlesOrdered();
            reportService.exportReportsPDFFormat(reportTitles, fiscalYear, auth);
        }
        else if (exportFormatType.equals("xml"))
        {
            niemExportService.exportYearlyReport(fiscalYear, auth);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/exportYearlyReport/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> downloadExportPentahoReports(@RequestParam(value = "filePath") String filePath)
            throws Exception
    {
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        File exportFile = new File(filePath);
        if (exportFile.exists())
        {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(exportFile.length())
                    .body(new InputStreamResource(new FileInputStream(exportFile)));
        }
        else
        {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/exportReportToNIEMXml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> exportReportToNIEMXml(@RequestParam(value = "report") String reportName) throws Exception
    {

        DOJReport report = DOJReport.valueOf(reportName);

        File file = niemExportService.exportSingleReport(report);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(new InputStreamResource(new FileInputStream(file)));
    }

    public ReportService getReportService()
    {
        return reportService;
    }

    public void setReportService(ReportService reportService)
    {
        this.reportService = reportService;
    }

    public NiemExportService getNiemExportService()
    {
        return niemExportService;
    }

    public void setNiemExportService(NiemExportService niemExportService)
    {
        this.niemExportService = niemExportService;
    }
}
