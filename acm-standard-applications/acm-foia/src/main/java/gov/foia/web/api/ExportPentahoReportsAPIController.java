package gov.foia.web.api;

import com.armedia.acm.plugins.report.service.ReportService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
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
    public ResponseEntity<?> exportPentahoReportsToPDF(@RequestParam(value = "exportFormatType") String exportFormatType) throws Exception
    {
        File mergedFile = null;
        if (exportFormatType.equals("pdf"))
        {
            List<String> reportTitles = niemExportService.getYearlyReportTitlesOrdered();

            mergedFile = reportService.exportReportsPDFFormat(reportTitles);
        }
        else if (exportFormatType.equals("xml"))
        {
            mergedFile = niemExportService.exportYearlyReport();
        }
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mergedFile.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(mergedFile.length())
                .body(new InputStreamResource(new FileInputStream(mergedFile)));
    }

    @RequestMapping(value = "/exportReportToNIEMXml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> exportReportToNIEMXml(@RequestParam(value = "report") String reportName) throws Exception
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
