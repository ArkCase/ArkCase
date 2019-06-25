package com.armedia.acm.plugins.ecm.pipeline.postsave;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.service.FileConverter;
import com.armedia.acm.service.FileConverterFactory;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

public class FileUploadConversionHandler implements PipelineHandler<EcmFile, EcmFileTransactionPipelineContext> {

    private final Logger log = LogManager.getLogger(getClass());

	private FileConverterFactory fileConverterFactory;
	private EcmFileService ecmFileService;
	private EcmFileDao ecmFileDao;

	@Value("${document.upload.policy.convertHtmlToPdf:false}")
    private Boolean convertHtmlToPdf;

    @Value("${document.upload.policy.convertMsgToPdf:false}")
    private Boolean convertMsgToPdf;

    @Value("${document.upload.policy.convertEmlToPdf:false}")
    private Boolean convertEmlToPdf;

	@Override
	public void execute(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext)
			throws PipelineProcessException {
		entity = pipelineContext.getEcmFile();
		String fileName = entity.getFileName();
		String fileType = "." + entity.getFileExtension();

		FileConverter fileConverter = getFileConverterFactory().getConverterOfType(fileType);
		if (isConverterEnabledForFileType(fileType) && Objects.nonNull(fileConverter)) {
			try {
				log.debug("Converting file [{}.{}] to PDF started!", entity.getFileName(), entity.getFileExtension());

				File originalFile = pipelineContext.getFileContents();
				File pdfConvertedFile = fileConverter.convert(new FileInputStream(originalFile), fileName);
				if (pdfConvertedFile != null && pdfConvertedFile.exists() && pdfConvertedFile.length() > 0) {
					try (InputStream pdfConvertedFileIs = new FileInputStream(pdfConvertedFile)) {
						entity.setFileActiveVersionNameExtension(".pdf");
						entity.setFileActiveVersionMimeType("application/pdf");

						ecmFileService.update(entity, pdfConvertedFileIs, pipelineContext.getAuthentication());

						entity.getVersions().get(1).setFile(entity);

						pipelineContext.setEcmFile(entity);
					}
				}

				log.debug("Converting file [{}.{}] to PDF finished successfully!", entity.getFileName(),
						entity.getFileExtension());
			} catch (Exception e) {
				log.debug("Converting file [{}.{}] to PDF failed!", entity.getFileName(), entity.getFileExtension(), e);
				// Conversion failed and we keep the original file only
				return;
			}
		}
	}

	@Override
	public void rollback(EcmFile entity, EcmFileTransactionPipelineContext pipelineContext)
			throws PipelineProcessException {
		// nothing to rollback
	}

	private Boolean isConverterEnabledForFileType(String fileType)
	{
		if(".html".equals(fileType))
		{
			return Objects.nonNull(getConvertHtmlToPdf()) ? getConvertHtmlToPdf() : false;
		}
		else if(".msg".equals(fileType))
		{
			return Objects.nonNull(getConvertMsgToPdf()) ? getConvertMsgToPdf() : false;
		}
		else if(".eml".equals(fileType))
		{
			return Objects.nonNull(getConvertEmlToPdf()) ? getConvertEmlToPdf() : false;
		}
		return false;
	}

	public FileConverterFactory getFileConverterFactory() {
		return fileConverterFactory;
	}

	public void setFileConverterFactory(FileConverterFactory fileConverterFactory) {
		this.fileConverterFactory = fileConverterFactory;
	}

	public EcmFileService getEcmFileService() {
		return ecmFileService;
	}

	public void setEcmFileService(EcmFileService ecmFileService) {
		this.ecmFileService = ecmFileService;
	}

	public EcmFileDao getEcmFileDao() {
		return ecmFileDao;
	}

	public void setEcmFileDao(EcmFileDao ecmFileDao) {
		this.ecmFileDao = ecmFileDao;
	}

    public Boolean getConvertHtmlToPdf() {
        return convertHtmlToPdf;
    }

    public void setConvertHtmlToPdf(Boolean convertHtmlToPdf) {
        this.convertHtmlToPdf = convertHtmlToPdf;
    }

    public Boolean getConvertMsgToPdf() {
        return convertMsgToPdf;
    }

    public void setConvertMsgToPdf(Boolean convertMsgToPdf) {
        this.convertMsgToPdf = convertMsgToPdf;
    }

    public Boolean getConvertEmlToPdf() {
        return convertEmlToPdf;
    }

    public void setConvertEmlToPdf(Boolean convertEmlToPdf) {
        this.convertEmlToPdf = convertEmlToPdf;
    }
}
