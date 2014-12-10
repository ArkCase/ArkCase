package com.armedia.acm.correspondence.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by marjan.stefanoski on 06.12.2014.
 */
public class ZipUtility {

    public void zipDirectory(File folder, File outputFile)
            throws IOException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(outputFile));
            zip(folder, folder, zos);
            zos.close();
        } catch (IOException ioException) {
            throw ioException;
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException ioException) {
            }
        }
    }

    private  void zip(File directory, File base, ZipOutputStream zos)
            throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[8192];
        int read = 0;
        for (int i = 0, n = files.length; i < n; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, zos);
            } else {
                FileInputStream in = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath().substring(
                        base.getPath().length() + 1));
                zos.putNextEntry(entry);
                while (-1 != (read = in.read(buffer))) {
                    zos.write(buffer, 0, read);
                }
                in.close();
            }
        }
    }

    public  void unzip(File inputFile, File unzipDestFolder)
            throws IOException {
        ZipFile zipFile = null;
        InputStream inputStream = null;

        try {
            zipFile = new ZipFile(inputFile);

            Enumeration<? extends ZipEntry> oEnum = zipFile.entries();
            while (oEnum.hasMoreElements()) {
                ZipEntry zipEntry = oEnum.nextElement();

                File file = new File(unzipDestFolder, zipEntry.getName());
                if (zipEntry.isDirectory() && !file.exists()) {
                    file.mkdirs();
                } else {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    inputStream = zipFile.getInputStream(zipEntry);

                    BufferedInputStream buffInputStream = new BufferedInputStream(
                            inputStream);
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    int byteData;
                    while ((byteData = buffInputStream.read()) != -1) {
                        bos.write((byte) byteData);
                    }

                    bos.close();
                    fos.close();
                    buffInputStream.close();
                }
            }
        } catch (IOException ioException) {
            throw ioException;
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ioException) {
            }
        }
    }
}
