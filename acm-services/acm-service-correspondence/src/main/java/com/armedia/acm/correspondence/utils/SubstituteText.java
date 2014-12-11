package com.armedia.acm.correspondence.utils;

import java.io.*;
import java.util.Date;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 06.12.2014.
 */
public class SubstituteText {

    public void substitute(String[] args) throws Exception {
        Map<String, String> env = System.getenv();
        String dir = null;
        dir = env.get("HOME");
        if ( dir == null ) {
            dir = env.get("HOMEPATH");
        }

        final String docxDir = "C:\\$HOME\\Downloads\\"; // directory with
        // the original
        // .docx file
        final String docxName = "ClearanceDenied.docx"; // file name of the original .docx
        // file
        final String docxSubName = "CD_TEST.docx"; // file name of the .docx
        // file created with
        // substituted texts
        ZipUtility zipUtility = new ZipUtility();
        try {
            // 1. unzip docx file
            zipUtility.unzip(new File(docxDir, docxName), new File(docxDir,
                    stripFileExt(docxName)));

            // 2a. get list of XML files in /word in unzipped folder
            FilenameFilter ff = new ExtensionFilter("xml");
            File XMLdir = new File(new File(docxDir, stripFileExt(docxName)),
                    "word");
            String[] XMLfiles = XMLdir.list(ff);

            // 2b. read xml files and do text substitution
            if (XMLfiles != null) {
                for (int i = 0; i < XMLfiles.length; i++) {
                    substituteText(new File(XMLdir, XMLfiles[i]), new File(
                            XMLdir, "_" + XMLfiles[i]));
                }
            }

            // 3. zip contents back to docx file
            zipUtility.zipDirectory(new File(docxDir, stripFileExt(docxName)),
                    new File(docxDir, docxSubName));

            // 4. delete unzipped folder
            cleanDirectory(new File(docxDir, stripFileExt(docxName)));

            // System.out.println("END");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Function to substitute placeholders with other text IMPORTANT:
     * Placeholders must start and end with %
     */
    private  boolean substituteText(File origFile, File tmpFile)
            throws Exception {
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(origFile), "UTF-8"));
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(tmpFile), "UTF-8"));

        // Names of placeholders, starting and ending with % (to be updated
        // accordingly)
        String placeholder1 = "%Date%";
        String placeholder2 = "%EmployeeName%";
        String placeholder3 = "%EmployeeID%";
        String placeholder4 = "%CurrentAddress%";

        // Values to replace placeholders (to be updated accordingly)
        String var1 = escapeHTML(new Date().toString()); // %name%
        String var2 = escapeHTML("Marjan Stefanoski"); // %text%
        String var3 = escapeHTML("2702980433007");
        String var4 = escapeHTML("Vera Jocik 14/40 1000 Skopje");

        String line;
        for (int i = 1; ((line = reader.readLine()) != null); i++) {
            int cursor = 0;
            // Print to file and flush for every 1000 lines
            // To prevent memory error
            if (i % 1000 == 0) {
                writer.write(sb.toString());
                writer.flush();
                sb = new StringBuffer();
            }

            int startIdx = 0;
            int endIdx = 0;
            String result = "";
            while ((startIdx = line.indexOf("%", cursor)) > -1
                    && (endIdx = line.indexOf("%", startIdx + 1)) > -1) {
                result += line.substring(cursor, startIdx);
                cursor = endIdx + 1;

                String substring = line.substring(startIdx, cursor);
                String stripXML = stripXMLHTMLTags(substring);

                if (stripXML != null && !stripXML.equals("")) {
                    // if is placeholder, replace with text accordingly
                    if (stripXML.equals(placeholder1))
                        result += var1;
                    else if (stripXML.equals(placeholder2))
                        result += var2;
                    else if (stripXML.equals(placeholder3))
                        result += var3;
                    else if (stripXML.equals(placeholder4   ))
                        result += var4;
                    else {
                        result += (substring.substring(0,
                                substring.length() - 1));
                        cursor = endIdx;
                    }
                }
            }

            result += line.substring(cursor);
            line = result;

            sb.append(line);
            sb.append("\r\n");
        }
        writer.write(sb.toString());
        writer.flush();
        writer.close();

        reader.close();

        origFile.delete();
        // Rename file (or directory)
        return tmpFile.renameTo(origFile);
    }

    /**
     * Clean directory - delete all files and folders
     */
    private void cleanDirectory(File d) {
        if (d.exists()) {
            File[] files = d.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory() && files[i].listFiles().length > 0) {
                    cleanDirectory(files[i]);
                }
                files[i].delete();
            }
            d.delete();
        }
    }

    /**
     * Strip string of XML and HTML tags
     */
    private  String stripXMLHTMLTags(String s) {
        return s.replaceAll("\\<.*?>", "");
    }

    /**
     * Strip file name of extension
     */
    private String stripFileExt(String f) {
        return f.substring(0, f.lastIndexOf("."));
    }

    /**
     * Escape valid html tags
     */
    private  String escapeHTML(String s) {
        if (s == null)
            return s;
        return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;").replaceAll("\"", "&quot;")
                .replaceAll("'", "'").replaceAll("/", "/")
                .replaceAll("'", "'");
    }
}
