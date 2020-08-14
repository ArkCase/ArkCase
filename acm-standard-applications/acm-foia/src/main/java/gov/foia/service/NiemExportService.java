package gov.foia.service;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface NiemExportService
{

    /**
     *
     * Converts a single Pentaho exported CSV report to an XML file in accordance to the FOIA Yearly Report NIEM
     * Standard
     * 
     * @param report
     *            Single section of the Yearly report
     * @return XML File in accordance to the FOIA Yearly Report NIEM Standard
     * @throws Exception
     */
    File exportSingleReport(DOJReport report) throws Exception;

    /**
     * 
     * Converts all Pentaho exported CSV reports to an XML file in accordance to the FOIA Yearly Report NIEM Standard
     * 
     * @return XML File in accordance to the FOIA Yearly Report NIEM Standard
     * @throws Exception
     */
    File exportYearlyReport() throws Exception;

    /**
     * 
     * Returns an ordered list of the report titles that should be included in the FOIA Yearly Report
     * 
     * @return List of report titles
     * @throws Exception
     */
    List<String> getYearlyReportTitlesOrdered() throws Exception;

    /**
     * 
     * Returns an XML document that holds basic NIEM XML info and a single iepd:FoiaAnnualReport wrapper element
     * 
     * @param agencyIdentifiers
     *            The agency name (as expected in the report) and their identification throughout the report
     * @return XML Document
     * @throws ParserConfigurationException
     */
    Document createYearlyReportDocumentWithBaseData(Map<String, String> agencyIdentifiers) throws ParserConfigurationException;

    /**
     * 
     * Maps and executes the methods needed to generate and append a single section of the DOJ FOIA Yearly Report. The
     * report sections are generated as described by the DOJ provided IEPD NIEM model version 1.03.
     *
     * @param data
     *            The data from the exported CSV report from Pentaho
     * @param parent
     *            The element that will hold the section, usually a wrapper iepd:FoiaAnnualReport element
     * @param agencyIdentifiers
     *            The agency name (as expected in the report) and their identification throughout the report
     * @param report
     *            The report that we would like to append to the parent
     */
    void appendReportSection(List<Map<String, String>> data, Element parent,
            Map<String, String> agencyIdentifiers, DOJReport report);
}
