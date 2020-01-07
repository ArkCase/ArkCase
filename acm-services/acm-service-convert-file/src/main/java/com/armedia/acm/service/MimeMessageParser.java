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

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import com.sun.mail.util.BASE64DecoderStream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentDisposition;
import javax.mail.internet.ContentType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class to parse a MimeMessage.
 * @author Nick Russler
 */
public class MimeMessageParser {
	/***
	 * Walk the Mime Structure recursivly and execute the callback on every part.
	 * @param p mime object
	 * @param initial level of current depth of the part
	 * @param callback Object holding the callback function
	 * @throws Exception
	 */
	
	private static Logger log = LogManager.getLogger(MimeMessageParser.class);
	
	private static void walkMimeStructure(Part p, int level, WalkMimeCallback callback) throws Exception {
		callback.walkMimeCallback(p, level);
		
		if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				walkMimeStructure(mp.getBodyPart(i), level + 1, callback);
			}
		}
	}
	
	/***
	 * Print the structure of the Mime object.
	 * @param p Mime object
	 * @throws Exception
	 */
	public static String printStructure(Part p) throws Exception {
		final StringBuilder result = new StringBuilder();
		
		result.append("-----------Mime Message-----------\n");
		walkMimeStructure(p, 0, new WalkMimeCallback() {
			@Override
			public void walkMimeCallback(Part p, int level) throws Exception {
				String s = "> " + Strings.repeat("|  ", level) + new ContentType(p.getContentType()).getBaseType();
				
				String[] contentDispositionArr = p.getHeader("Content-Disposition");
				if (contentDispositionArr != null) {
					s += "; " + new ContentDisposition(contentDispositionArr[0]).getDisposition();
				}
				
				result.append(s);
				result.append("\n");
			}
		});
		result.append("----------------------------------");
		
		return result.toString();
	}
	
	/**
	 * Get the String Content of a MimePart.
	 * @param p MimePart
	 * @return Content as String
	 * @throws IOException
	 * @throws MessagingException
	 */
	private static String getStringContent(Part p) throws IOException, MessagingException {
		Object content = null;
		
		try {
			content = p.getContent();
		} catch (Exception e) {
			log.debug("Email body could not be read automatically (%s), we try to read it anyway.", e.toString());
			
			// most likely the specified charset could not be found
			content = p.getInputStream();
		}
		
		String stringContent = null;
		
		if (content instanceof String) {
			stringContent = (String) content;
		} else if (content instanceof InputStream) {
			stringContent = new String(ByteStreams.toByteArray((InputStream) content), "utf-8");
		}
		
		return stringContent;
	}
	
	/**
	 * Find the main message body, prefering html over plain.
	 * @param p mime object
	 * @return the main message body and the corresponding contentType or an empty text/plain
	 * @throws Exception
	 */
	public static MimeObjectEntry<String> findBodyPart(Part p) throws Exception {
		final MimeObjectEntry<String> result = new MimeObjectEntry<String>("", new ContentType("text/plain; charset=\"utf-8\""));
		
		walkMimeStructure(p, 0, new WalkMimeCallback() {
			@Override
			public void walkMimeCallback(Part p, int level) throws Exception {
				// only process text/plain and text/html
				if (!p.isMimeType("text/plain") && !p.isMimeType("text/html")) {
					return;
				}
				
				String stringContent = getStringContent(p);
				boolean isAttachment = Part.ATTACHMENT.equalsIgnoreCase(p.getDisposition());
				
				if (Strings.isNullOrEmpty(stringContent) || isAttachment) {
					return;
				}
				
				// use text/plain entries only when we found nothing before
				if (result.getEntry().isEmpty() || p.isMimeType("text/html")) {
					result.setEntry(stringContent);
					result.setContentType(new ContentType(p.getContentType()));
				}
			}
		});
		
		return result;
	}
	
	/**
	 * Get all inline images (images with an Content-Id) as a Hashmap.
	 * The key is the Content-Id and all images in all multipart containers are included in the map.
	 * @param p mime object
	 * @return Hashmap&lt;Content-Id, &lt;Base64Image, ContentType&gt;&gt;
	 * @throws Exception
	 */
	public static HashMap<String, MimeObjectEntry<String>> getInlineImageMap(Part p) throws Exception {
		final HashMap<String, MimeObjectEntry<String>> result = new HashMap<String, MimeObjectEntry<String>>();
		
		walkMimeStructure(p, 0, new WalkMimeCallback() {
			@Override
			public void walkMimeCallback(Part p, int level) throws Exception {
				if (p.isMimeType("image/*") && (p.getHeader("Content-Id") != null)) {
					String id = p.getHeader("Content-Id")[0];
		
					BASE64DecoderStream b64ds = (BASE64DecoderStream) p.getContent();
					String imageBase64 = BaseEncoding.base64().encode(ByteStreams.toByteArray(b64ds));
					result.put(id, new MimeObjectEntry<String>(imageBase64, new ContentType(p.getContentType())));
				}
			}
		});

		return result;
	}
	
	public static List<Part> getAttachments(Part p) throws Exception {
		final List<Part> result = new ArrayList<Part>();
		
		walkMimeStructure(p, 0, new WalkMimeCallback() {
			@Override
			public void walkMimeCallback(Part p, int level) throws Exception {
				if (Part.ATTACHMENT.equalsIgnoreCase(p.getDisposition()) || ((p.getDisposition() == null) && !Strings.isNullOrEmpty(p.getFileName()))) {
					result.add(p);
				}
			}
		});
		
		return result;
	}
}
