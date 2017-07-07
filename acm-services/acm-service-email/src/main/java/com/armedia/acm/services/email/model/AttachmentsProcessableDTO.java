/**
 * 
 */
package com.armedia.acm.services.email.model;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 7, 2017
 *
 */
public interface AttachmentsProcessableDTO
{

    List<Long> getAttachmentIds();

    List<String> getFilePaths();

}