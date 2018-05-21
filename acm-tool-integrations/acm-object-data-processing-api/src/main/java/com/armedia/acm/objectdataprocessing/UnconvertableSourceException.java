package com.armedia.acm.objectdataprocessing;

/*-
 * #%L
 * ACM Object Data Processing API
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

/**
 * Thrown in case the source input stream cannot be converted in input stream with binary data containing a PDF
 * document.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
 *
 */
public class UnconvertableSourceException extends Exception
{

    private static final long serialVersionUID = 5639921122954148227L;

    public UnconvertableSourceException(String message)
    {
        super(message);
    }

    public UnconvertableSourceException(String message, Throwable e)
    {
        super(message, e);
    }

}
