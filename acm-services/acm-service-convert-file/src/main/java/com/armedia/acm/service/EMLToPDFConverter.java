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
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.tidy.Tidy;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EMLToPDFConverter implements FileConverter {
	Logger log = LogManager.getLogger(getClass().getName());

	private static final Pattern IMG_CID_REGEX = Pattern.compile("cid:(.*?)\"", Pattern.DOTALL);
	private static final Pattern IMG_CID_PLAIN_REGEX = Pattern.compile("\\[cid:(.*?)\\]", Pattern.DOTALL);
	private static final String HTML_WRAPPER_TEMPLATE = "<!DOCTYPE html><html><head><style>body{font-size: 0.5cm;}</style><meta charset=\"%s\"><title>title</title></head><body>%s</body></html>";

	@Override
	public File convert(InputStream fileInputStream, String fileName) {
		String timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());
		String tmpPdfFilePath = FileUtils.getTempDirectoryPath().concat(File.separator).concat(fileName).concat("_")
				.concat(timestamp).concat(".pdf");

		File pdfFile = new File(tmpPdfFilePath);

		try (OutputStream fos = new FileOutputStream(tmpPdfFilePath)) {
			MimeMessage message = new MimeMessage(null, fileInputStream);
			createPdf(message, fos);
		} catch (Exception e) {
			log.error(String.format("File [%s] could not be converted to PDF", fileName), e);
			return null;
		}

		return pdfFile;
	}

	private void createPdf(MimeMessage msg, OutputStream fos) throws Exception {
		String fromEmail = msg.getFrom()[0].toString();
		fromEmail = fromEmail.contains("Content_Types") ? "" : fromEmail;
		String toEmail = getEmails(msg.getRecipients(RecipientType.TO));
		String toCC = getEmails(msg.getRecipients(RecipientType.CC));
		String subject = msg.getSubject();
		String bodyHTML = getHtmlBody(msg);
		String bodyText = "";

		Document document = new Document();
		try {
			PdfWriter.getInstance(document, fos);
			document.open();
			document.add(new Paragraph(MessageFormat.format("From: {0}", fromEmail)));
			document.add(new Paragraph(MessageFormat.format("To: {0}", toEmail)));
			document.add(new Paragraph(MessageFormat.format("Cc: {0}", toCC)));
			document.add(new Paragraph("Subject: " + subject));
			if (!StringUtils.isEmpty(bodyHTML)) {
				if (!parseHtml(bodyHTML, document)) {
					document.add(new Paragraph("Body text: "));
					document.add(new Paragraph(bodyText));
				}
			} else {
				document.add(new Paragraph("Body text: "));
				document.add(new Paragraph(bodyText));
			}
		} finally {
			document.close();
		}
	}

	private String getHtmlBody(MimeMessage message) throws Exception {
		MimeObjectEntry<String> bodyEntry = MimeMessageParser.findBodyPart(message);
		String charsetName = bodyEntry.getContentType().getParameter("charset");

		log.info("Extract the inline images");
		final HashMap<String, MimeObjectEntry<String>> inlineImageMap = MimeMessageParser.getInlineImageMap(message);

		/* ######### Embed images in the html ######### */
		String htmlBody = bodyEntry.getEntry();
		if (bodyEntry.getContentType().match("text/html")) {
			if (inlineImageMap.size() > 0) {
				log.debug("Embed the referenced images (cid) using <img src=\"data:image ...> syntax");

				// find embedded images and embed them in html using <img src="data:image ...>
				// syntax
				htmlBody = replace(htmlBody, IMG_CID_REGEX, new StringReplacerCallback() {
					@Override
					public String replace(Matcher m) throws Exception {
						MimeObjectEntry<String> base64Entry = inlineImageMap.get("<" + m.group(1) + ">");

						// found no image for this cid, just return the matches string as it is
						if (base64Entry == null) {
							return m.group();
						}

						return "data:" + base64Entry.getContentType().getBaseType() + ";base64,"
								+ base64Entry.getEntry() + "\"";
					}
				});
			}
		} else {
			log.debug("No html message body could be found, fall back to text/plain and embed it into a html document");

			// replace \n line breaks with <br>
			htmlBody = htmlBody.replace("\n", "<br>").replace("\r", "");

			// replace whitespace with &nbsp;
			htmlBody = htmlBody.replace(" ", "&nbsp;");

			htmlBody = String.format(HTML_WRAPPER_TEMPLATE, charsetName, htmlBody);
			if (inlineImageMap.size() > 0) {
				log.debug("Embed the referenced images (cid) using <img src=\"data:image ...> syntax");

				// find embedded images and embed them in html using <img src="data:image ...>
				// syntax
				htmlBody = replace(htmlBody, IMG_CID_PLAIN_REGEX, new StringReplacerCallback() {
					@Override
					public String replace(Matcher m) throws Exception {
						MimeObjectEntry<String> base64Entry = inlineImageMap.get("<" + m.group(1) + ">");

						// found no image for this cid, just return the matches string
						if (base64Entry == null) {
							return m.group();
						}

						return "<img src=\"data:" + base64Entry.getContentType().getBaseType() + ";base64,"
								+ base64Entry.getEntry() + "\" />";
					}
				});
			}
		}

		log.debug("Successfully parsed the .eml and converted it into html:");

		return htmlBody;
	}

	private String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			String s = (String) p.getContent();
			return s;
		}

		if (p.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}

		return "";
	}

	private String getEmails(Address[] recipients) {
		if (recipients == null) {
			return "";
		}
		StringBuilder toEmail = new StringBuilder();
		String separator = "";
		for (Address address : recipients) {
			toEmail.append(separator);
			toEmail.append(address.toString());
			separator = ", ";
		}
		return toEmail.toString();
	}

	private boolean parseHtml(String bodyHTML, Document document) {
		// convert to correct XHTML
		ByteArrayOutputStream xhtmlOutputStream = new ByteArrayOutputStream();
		Tidy tidy = new Tidy();
		// tidy.setShowWarnings(true);
		tidy.setInputEncoding("UTF-8");
		tidy.setOutputEncoding("UTF-8");
		tidy.setXHTML(true);
		tidy.setMakeClean(true);
		tidy.setMakeBare(true);
		tidy.setDropProprietaryAttributes(true);
		tidy.setForceOutput(true);
		tidy.parse(new ByteArrayInputStream(bodyHTML.getBytes()), xhtmlOutputStream);

		try {
			ElementList list = XMLWorkerHelper
					.parseToElementList(new String(xhtmlOutputStream.toByteArray(), StandardCharsets.UTF_8), null);
			document.add(new Paragraph("Body HTML: "));
			for (Element element : list) {
				document.add(element);
			}
		} catch (Exception e) {
			log.error("Failed to parse HTML email body!", e);
			return false;
		}

		return true;
	}

	public String replace(String input, Pattern regex, StringReplacerCallback callback) throws Exception {
		StringBuffer resultString = new StringBuffer();
		Matcher regexMatcher = regex.matcher(input);
		while (regexMatcher.find()) {
			regexMatcher.appendReplacement(resultString, Matcher.quoteReplacement(callback.replace(regexMatcher)));
		}
		regexMatcher.appendTail(resultString);

		return resultString.toString();
	}
}
