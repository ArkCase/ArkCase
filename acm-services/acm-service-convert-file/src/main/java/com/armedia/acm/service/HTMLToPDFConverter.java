package com.armedia.acm.service;

/*-
 * #%L
 * ACM Service: File Converting Service
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class HTMLToPDFConverter implements FileConverter {
	Logger log = LogManager.getLogger(getClass().getName());

	@Override
	public File convert(InputStream fileInputStream, String fileName) {
		String pdfFilePath = FileUtils.getTempDirectoryPath().concat(File.separator).concat(fileName).concat(".pdf");
		File pdfFile = new File(pdfFilePath);

		try (OutputStream pdfFileOutputStream = new FileOutputStream(pdfFile)) {
			// convert to correct XHTML
			ByteArrayOutputStream xhtmlOutputStream = new ByteArrayOutputStream();
			Tidy tidy = new Tidy();
			//tidy.setShowWarnings(true);
			tidy.setInputEncoding("UTF-8");
			tidy.setOutputEncoding("UTF-8");
			tidy.setXHTML(true);
			tidy.setMakeClean(true);
			tidy.setMakeBare(true);
			tidy.setDropProprietaryAttributes(true);
			tidy.setForceOutput(true);
			tidy.parse(fileInputStream, xhtmlOutputStream);

			InputStream xhtmlInputStream = new ByteArrayInputStream(xhtmlOutputStream.toByteArray());

			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, pdfFileOutputStream);

			document.open();

			XMLWorkerHelper.getInstance().parseXHtml(writer, document, xhtmlInputStream);

			document.close();
		} catch (Exception e) {
			log.error(String.format("File [%s] could not be converted to PDF", fileName), e);
			FileUtils.deleteQuietly(pdfFile);
			return null;
		}

		return pdfFile;
	}
}
