/*
 * (c) Copyright Ascensio System Limited 2010-2017
 * The MIT License (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.armedia.acm.plugins.onlyoffice.helpers;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.armedia.acm.plugins.onlyoffice.model.FileType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentManager
{
    private static HttpServletRequest request;

    public static void init(HttpServletRequest req, HttpServletResponse resp)
    {
        request = req;
    }

    public static long getMaxFileSize()
    {
        long size;

        try
        {
            size = Long.parseLong(ConfigManager.getProperty("filesize-max"));
        }
        catch (Exception ex)
        {
            size = 0;
        }

        return size > 0 ? size : 5 * 1024 * 1024;
    }

    public static List<String> getFileExts()
    {
        List<String> res = new ArrayList<>();

        res.addAll(getViewedExts());
        res.addAll(getEditedExts());
        res.addAll(getConvertExts());

        return res;
    }

    public static List<String> getViewedExts()
    {
        String exts = ConfigManager.getProperty("files.docservice.viewed-docs");
        return Arrays.asList(exts.split("\\|"));
    }

    public static List<String> getEditedExts()
    {
        String exts = ConfigManager.getProperty("files.docservice.edited-docs");
        return Arrays.asList(exts.split("\\|"));
    }

    public static List<String> getConvertExts()
    {
        String exts = ConfigManager.getProperty("files.docservice.convert-docs");
        return Arrays.asList(exts.split("\\|"));
    }

    public static String curUserHostAddress(String userAddress)
    {
        if (userAddress == null)
        {
            try
            {
                userAddress = InetAddress.getLocalHost().getHostAddress();
            }
            catch (Exception ex)
            {
                userAddress = "";
            }
        }

        return userAddress.replaceAll("[^0-9a-zA-Z.=]", "_");
    }

    public static String storagePath(String fileName, String userAddress)
    {
        String serverPath = request.getSession().getServletContext().getRealPath("");
        String storagePath = ConfigManager.getProperty("storage-folder");
        String hostAddress = curUserHostAddress(userAddress);

        String directory = serverPath + "\\" + storagePath + "\\";

        File file = new File(directory);

        if (!file.exists())
        {
            file.mkdir();
        }

        directory = directory + hostAddress + "\\";
        file = new File(directory);

        if (!file.exists())
        {
            file.mkdir();
        }

        return directory + fileName;
    }

    public static String getCorrectName(String fileName)
    {
        String baseName = FileUtility.getFileNameWithoutExtension(fileName);
        String ext = FileUtility.getFileExtension(fileName);
        String name = baseName + ext;

        File file = new File(storagePath(name, null));

        for (int i = 1; file.exists(); i++)
        {
            name = baseName + " (" + i + ")" + ext;
            file = new File(storagePath(name, null));
        }

        return name;
    }

    public static String createDemo(String fileExt) throws Exception
    {
        String demoName = "sample." + fileExt;
        String fileName = getCorrectName(demoName);

        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(demoName);

        File file = new File(storagePath(fileName, null));

        try (FileOutputStream out = new FileOutputStream(file))
        {
            int read;
            final byte[] bytes = new byte[1024];
            while ((read = stream.read(bytes)) != -1)
            {
                out.write(bytes, 0, read);
            }
            out.flush();
        }

        return fileName;
    }

    public static String getFileUri(String fileName) throws Exception
    {
        try
        {
            String serverPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    + request.getContextPath();
            String storagePath = ConfigManager.getProperty("storage-folder");
            String hostAddress = curUserHostAddress(null);

            String filePath = serverPath + "/" + storagePath + "/" + hostAddress + "/"
                    + URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8.toString());

            return filePath;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new AssertionError("UTF-8 is unknown");
        }
    }

    public static String getServerUrl()
    {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    public static String getCallback(String fileName)
    {
        String serverPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
        String hostAddress = curUserHostAddress(null);
        try
        {
            String query = "?type=track&fileName=" + URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8.toString())
                    + "&userAddress=" + URLEncoder.encode(hostAddress, java.nio.charset.StandardCharsets.UTF_8.toString());

            return serverPath + "/IndexServlet" + query;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new AssertionError("UTF-8 is unknown");
        }
    }

    public static String getInternalExtension(FileType fileType)
    {
        if (fileType.equals(FileType.Text))
            return ".docx";

        if (fileType.equals(FileType.Spreadsheet))
            return ".xlsx";

        if (fileType.equals(FileType.Presentation))
            return ".pptx";

        return ".docx";
    }
}
