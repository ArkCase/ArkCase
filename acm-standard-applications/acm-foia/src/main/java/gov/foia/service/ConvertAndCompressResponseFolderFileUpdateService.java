package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.convertfolder.FileConverter;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 23, 2018
 *
 */
public class ConvertAndCompressResponseFolderFileUpdateService implements ResponseFolderFileUpdateService
{
    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());

    private FileConverter converter;

    private FolderCompressor compressor;

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    /*
     * (non-Javadoc)
     * @see gov.foia.service.ResponseFolderFileUpdateService#updateFile(com.armedia.acm.plugins.ecm.model.EcmFile,
     * java.lang.Long, org.springframework.security.core.Authentication)
     */
    @Override
    @Async
    @Transactional
    public void updateFile(EcmFile file, Long folderId, String username)
    {
        try
        {
            log.debug("Converting and compresssing [{}] file in folder with [{}] ID for user [{}].", file.getFileName(), folderId,
                    username);

            auditPropertyEntityAdapter.setUserId(username);
            converter.convert(file, username);
            compressor.compressFolder(folderId);
        }
        catch (ConversionException | AcmFolderException e)
        {
            log.warn("Error while converting and compressing file [{}].", file.getFileName(), e);
        }
    }

    /**
     * @param converter
     *            the converter to set
     */
    public void setConverter(FileConverter converter)
    {
        this.converter = converter;
    }

    /**
     * @param compressor
     *            the compressor to set
     */
    public void setCompressor(FolderCompressor compressor)
    {
        this.compressor = compressor;
    }

    /**
     * @param auditPropertyEntityAdapter
     *            the auditPropertyEntityAdapter to set
     */
    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

}
