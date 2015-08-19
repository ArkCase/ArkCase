package com.armedia.acm.snowbound;

import Snow.Format;
import Snow.FormatHash;
import Snow.Snowbnd;
import com.snowbound.common.utils.ClientServerIO;
import com.snowbound.common.utils.Logger;
import com.snowbound.common.utils.URLReturnData;
import com.snowbound.snapserv.servlet.AnnotationLayer;
import com.snowbound.snapserv.servlet.ContentHandlerInput;
import com.snowbound.snapserv.servlet.ContentHandlerResult;
import com.snowbound.snapserv.servlet.DocumentNotesInterface;
import com.snowbound.snapserv.servlet.FlexSnapSIAPIException;
import com.snowbound.snapserv.servlet.FlexSnapSIContentHandlerInterface;
import com.snowbound.snapserv.servlet.FlexSnapSISaverInterface;
import com.snowbound.snapserv.servlet.NotesTemplate;
import com.snowbound.snapserv.servlet.RedactionInterface;
import com.snowbound.snapserv.servlet.UsersAndGroupsInterface;
import com.snowbound.snapserv.transport.UsersAndGroups;
import com.snowbound.snapserv.transport.pagedata.DocumentModel;
import com.snowbound.snapserv.transport.pagedata.FlexSnapSISnowAnn;
import com.snowbound.snapserv.transport.pagedata.PermissionsEntities;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Created by armdev on 5/29/15.
 */
public class ArkCaseContentHandler implements FlexSnapSIContentHandlerInterface, FlexSnapSISaverInterface, UsersAndGroupsInterface, DocumentNotesInterface, RedactionInterface
{
    private String baseURL;

    protected static final String PARAM_PARSE_PATHS_IN_DOCUMENT_ID = "parsePathsInDocumentId";
    protected static final String PARAM_TIFF_TAG_ANNOTATIONS = "tiffTagAnnotations";
    protected static String gFilePath = System.getProperty("user.home") + File.separator;
    private final static String FORWARD_SLASH = "/";
    private final static String BACK_SLASH = "\\";
    private boolean parsePathsInDocumentKey = false;
    private static boolean gSupportTiffTagAnnotations = false;

    private Logger log = Logger.getInstance();
    private String retrieveFileService;
    private String sendFileService;


    @Override
    public ContentHandlerResult getNotesContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in getNotesContent");
        return null;
    }

    @Override
    public ContentHandlerResult getNotesPermissions(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in getNotesPermissions");
        ContentHandlerResult result = new ContentHandlerResult();
        result.put("KEY_NOTES_PERMISSIONS", "PERM_DELETE");
        return result;
    }

    @Override
    public ContentHandlerResult getNotesTemplates(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in getNotesTemplates");
        ContentHandlerResult result = new ContentHandlerResult();
        Vector vTemplates = new Vector();
        NotesTemplate template1 = new NotesTemplate("Sample", "This is a sample");
        NotesTemplate template2 = new NotesTemplate("Approve", "I approve this document!");
        vTemplates.add(template1);
        vTemplates.add(template2);
        result.put("KEY_NOTES_TEMPLATES", vTemplates);
        return result;
    }

    @Override
    public void init(ServletConfig servletConfig) throws FlexSnapSIAPIException
    {
        String paramBaseURL = servletConfig.getInitParameter("baseURL");
        if (paramBaseURL != null)
        {
            this.baseURL = paramBaseURL;
        }

        String retrieveFileService = servletConfig.getInitParameter("getFileService");
        if (retrieveFileService != null)
        {
            this.retrieveFileService = retrieveFileService;
        }

        String sendFileService = servletConfig.getInitParameter("sendFileService");
        if (sendFileService != null)
        {
            this.sendFileService = sendFileService;
        }


        String parseBooleanString = servletConfig.getInitParameter(PARAM_PARSE_PATHS_IN_DOCUMENT_ID);
        if ("true".equalsIgnoreCase(parseBooleanString))
        {
            parsePathsInDocumentKey = true;
        }
        String tiffTagParam = servletConfig.getInitParameter(PARAM_TIFF_TAG_ANNOTATIONS);
        if ("true".equalsIgnoreCase(tiffTagParam))
        {
            gSupportTiffTagAnnotations = true;
        }

    }

    private boolean hasTiffTagAnnotations(byte[] documentContent)
    {
        log.log(Level.FINE, "in hasTiffTagAnnotations");
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(documentContent));
        Snowbnd snow = new Snowbnd();
        int filetype = snow.IMGLOW_get_filetype(dis);
        Format format = FormatHash.getInstance().getFormat(filetype);
        if (!format.isTiff())
        {
            return false;
        }
        int pageCount = snow.IMGLOW_get_pages(dis);
        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++)
        {
            int[] value = new int[1];
            byte[] buff = new byte[40000];
            int WANG_ANNOTATION_TAG_ID = 32932;
            int stat = snow.IMGLOW_get_tiff_tag(WANG_ANNOTATION_TAG_ID,
                    buff.length,
                    value,
                    dis,
                    buff,
                    pageIndex);
            if (stat < 0)
            {
                continue;
            } else
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public ContentHandlerResult getDocumentContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in getDocumentContent");
        String urlAppend = contentHandlerInput.getDocumentId();

        String getDocUrl = baseURL + retrieveFileService;
        if (getDocUrl.endsWith("/") && urlAppend.startsWith("/"))
        {
            urlAppend = urlAppend.substring(1);
        }

        String completeURL = getDocUrl + urlAppend;

        try
        {
            URLReturnData t = ClientServerIO.getURLBytes(completeURL);
            ContentHandlerResult result = new ContentHandlerResult();
            result.put("KEY_DOCUMENT_CONTENT", t.getData());
            String displayName = null;
            String filenameFromResponse = null;

            try
            {
                Map urlEnd = t.getHeaderFields();
                List contentDispositionValue = (List) urlEnd.get("Content-Disposition");
                if (contentDispositionValue != null)
                {
                    String contentDisposition = (String) contentDispositionValue.get(0);
                    String[] dispositionTokens = contentDisposition.split(";");

                    for (int i = 0; i < dispositionTokens.length; ++i)
                    {
                        String dispositionToken = dispositionTokens[i];
                        if (dispositionToken.startsWith("filename="))
                        {
                            filenameFromResponse = dispositionToken.split("=")[1];
                        }
                    }
                }
            } catch (Exception var14)
            {
                var14.printStackTrace();
            }

            if (filenameFromResponse != null)
            {
                displayName = filenameFromResponse;
            }

            if (displayName == null)
            {
                String var17 = completeURL.substring(completeURL.lastIndexOf(47) + 1);
                if (var17.indexOf(63) != -1)
                {
                    var17 = var17.substring(0, var17.indexOf(63));
                }

                displayName = var17;
            }

            result.put("KEY_DOCUMENT_DISPLAY_NAME", displayName);
            return result;
        } catch (MalformedURLException var15)
        {
            throw new FlexSnapSIAPIException(var15.getMessage());
        } catch (Throwable var16)
        {
            throw new FlexSnapSIAPIException(var16.getMessage());
        }
    }

    @Override
    public ContentHandlerResult eventNotification(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in eventNotification");
        return ContentHandlerResult.VOID;
    }

    @Override
    public ContentHandlerResult getAnnotationNames(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {

        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        String documentId = contentHandlerInput.getDocumentId();
        String documentKey = docIdFromDocumentKey(documentId);
        int userIdIndex = documentId.indexOf("userid");
        String userid = userIdIndex > 0 ? documentId.substring(userIdIndex + 7) : null;
        log.log(Logger.FINEST, "Inside getAnnotationNames  userid: " + userid);
        log.log(Logger.FINEST, "Inside getAnnotationNames  clientInstanceId: " + clientInstanceId);
        log.log(Logger.FINEST, "Inside getAnnotationNames  documentKey: " + documentKey);
        Vector vAnnotationIds = new Vector();
        String documentPath = "";
        String documentFile = documentKey;
        int tiffTagLayers = 0;
        if (gSupportTiffTagAnnotations)
        {
            log.log(Logger.FINEST, "Inside getAnnotationNames  gSupportTiffTagAnnotations: true ");
            try
            {
                byte[] tiffTagBuffer = null;
                ContentHandlerResult result = getDocumentContent(contentHandlerInput);
                byte[] documentContent = result.getDocumentContent();
                if (documentContent != null)
                {
                    if (hasTiffTagAnnotations(documentContent))
                    {
                        vAnnotationIds.add(FlexSnapSISnowAnn.TIFF_TAG_LAYER);
                    }
                }
            } catch (FlexSnapSIAPIException fsapie)
            {
            }
        }
        if (parsePathsInDocumentKey)
        {
            boolean pathFound = false;
            String pathSeparator = "";
            if (documentKey.indexOf(FORWARD_SLASH) != -1)
            {
                pathFound = true;
                pathSeparator = FORWARD_SLASH;
            } else if (documentKey.indexOf(BACK_SLASH) != -1)
            {
                pathFound = true;
                pathSeparator = BACK_SLASH;
            }
            if (pathFound)
            {

                int pathPoint = documentKey.lastIndexOf(pathSeparator) + 1;
                documentPath = documentKey.substring(0, pathPoint);
                documentFile = documentKey.substring(pathPoint);

            }
        }
        String annPath = gFilePath + documentPath;
        File imgDirectory = new File(annPath);
        String[] files = imgDirectory.list();
        for (int i = 0; i < files.length; i++)
        {
            String fileName = files[i];
//            if (!fileName.equals(documentFile)
//                    && fileName.indexOf(documentFile) == 0
//                    && fileName.endsWith(".ann")
            if (fileName.startsWith(documentKey) && fileName.endsWith(".ann")
            /*
             * && fileName.indexOf("-redactionBurn") == -1 ||
             * clientInstanceId.toUpperCase().indexOf("SUPER") != -1)
             */)
            {
                int nameBegin = fileName.indexOf("."); //documentFile.length() + 1;
                int nameEnd = fileName.lastIndexOf(".ann");
                if (fileName.indexOf("-redactionBurn") != -1)
                {
                    nameEnd = fileName.lastIndexOf("-redactionBurn");
                } else if (fileName.indexOf("-redactionEdit") != -1)
                {
                    nameEnd = fileName.lastIndexOf("-redactionEdit");
                }
                String annotationId = fileName.substring(nameBegin + 1, nameEnd);


                if ("Default".equals(annotationId) ||
                        userid == null ||
                        annotationId.contains(userid))
                {
                    vAnnotationIds.addElement(annotationId);
                }
            }
            if (fileName.equals(documentFile + ".redactions.xml"))
            {
                vAnnotationIds.addElement(DocumentModel.REDACTION_LAYER_NAME);
            }
        }
        String[] arrayAnnotationIds = new String[vAnnotationIds.size()];
        for (int i = 0; i < arrayAnnotationIds.length; i++)
        {
            arrayAnnotationIds[i] = (String) vAnnotationIds.elementAt(i);
        }
        ContentHandlerResult result = new ContentHandlerResult();
        result.put(ContentHandlerResult.KEY_ANNOTATION_NAMES,
                arrayAnnotationIds);
        return result;
    }

    @Override
    public ContentHandlerResult getAnnotationContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        // AJH - Note,  we should never be called with getAnnotationContent for the tiff tag layer.
        // the Content server will handle this without dependency on the content handler.
        log.log(Level.FINE, "in getAnnotationContent");
        return getAnnotationContentFromFile(contentHandlerInput);
    }


    public ContentHandlerResult getAnnotationContentFromFile(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        String documentKey = contentHandlerInput.getDocumentId();
        documentKey = docIdFromDocumentKey(documentKey);
        String annotationKey = contentHandlerInput.getAnnotationId();
        log.log(Logger.FINEST, "getAnnotationContentFromFile()");
        String annotationFilename = documentKey + "." + annotationKey + ".ann";
        String fullFilePath = gFilePath + annotationFilename;
        Hashtable props = null;
        ContentHandlerResult propsResult = getAnnotationProperties(contentHandlerInput);
        if (propsResult != null)
        {
            props = propsResult.getAnnotationProperties();
        }
        Boolean tmpRedactionFlag = (Boolean) props
                .get(AnnotationLayer.PROPERTIES_KEY_REDACTION_FLAG);
        Integer tmpPermissionLevel = (Integer) props
                .get(AnnotationLayer.PROPERTIES_KEY_PERMISSION_LEVEL);
        boolean redactionFlag = false;
        int permissionLevel = PERM_DELETE.intValue();
        if (tmpRedactionFlag != null)
        {
            redactionFlag = tmpRedactionFlag.booleanValue();
        }
        if (tmpPermissionLevel != null)
        {
            permissionLevel = tmpPermissionLevel.intValue();
        }
        // this is a burned in redaction
        if (annotationKey.equals(DocumentModel.REDACTION_LAYER_NAME))
        {
            annotationFilename = documentKey + ".redactions.xml";
            fullFilePath = gFilePath + annotationFilename;
        } else if ((redactionFlag == true)
                && (permissionLevel < PERM_VIEW.intValue()))
        {
            annotationFilename = documentKey + "." + annotationKey
                    + "-redactionBurn.ann";
            fullFilePath = gFilePath + annotationFilename;
        }
        // this is an editable redaction
        else if ((redactionFlag == true)
                && (permissionLevel >= PERM_VIEW.intValue()))
        {
            annotationFilename = documentKey + "." + annotationKey
                    + "-redactionEdit.ann";
            fullFilePath = gFilePath + annotationFilename;
        }
        try
        {
            File file = new File(fullFilePath);
            byte[] bytes = ClientServerIO.getFileBytes(file);
            ContentHandlerResult result = new ContentHandlerResult();
            result.put(ContentHandlerResult.KEY_ANNOTATION_CONTENT, bytes);
            result.put(ContentHandlerResult.KEY_ANNOTATION_DISPLAY_NAME,
                    contentHandlerInput.getAnnotationId());
            if (props != null)
            {
                result.put(ContentHandlerResult.KEY_ANNOTATION_PROPERTIES,
                        props);
            }
            return result;
        } catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public ContentHandlerResult getAnnotationProperties(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
//        Hashtable properties = new Hashtable();
//        ContentHandlerResult result = new ContentHandlerResult();
//        result.put("KEY_ANNOTATION_PROPERTIES", properties);
//        return result;
        HttpServletRequest request = contentHandlerInput.getHttpServletRequest();
        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        String documentKey = contentHandlerInput.getDocumentId();
        String annotationKey = contentHandlerInput.getAnnotationId();
        log.log(Logger.FINEST, "getAnnotationProperties()");
        Hashtable properties = new Hashtable();
        String baseAnnFilename = documentKey + "." + annotationKey;
        String annFilename = gFilePath + baseAnnFilename + ".ann";
        String redactionEditFilename = gFilePath + baseAnnFilename
                + "-redactionEdit.ann";
        String redactionBurnFilename = gFilePath + baseAnnFilename
                + "-redactionBurn.ann";
        // Is it a regular annotation layer ?
        File file = new File(annFilename);
        if (file.exists())
        {
            properties.put(AnnotationLayer.PROPERTIES_KEY_PERMISSION_LEVEL,
                    PERM_DELETE);
            properties.put(AnnotationLayer.PROPERTIES_KEY_REDACTION_FLAG,
                    new Boolean(false));
        }
        // Is it a redaction layer the user can edit?
        file = new File(redactionEditFilename);
        if (file.exists())
        {
            properties.put(AnnotationLayer.PROPERTIES_KEY_PERMISSION_LEVEL,
                    PERM_DELETE);
            properties.put(AnnotationLayer.PROPERTIES_KEY_REDACTION_FLAG,
                    new Boolean(true));
        }
        // Is it a redaction layer the user can NOT edit?
        file = new File(redactionBurnFilename);
        if (file.exists() == true)
        {
            properties.put(AnnotationLayer.PROPERTIES_KEY_PERMISSION_LEVEL,
                    PERM_REDACTION);
            properties.put(AnnotationLayer.PROPERTIES_KEY_REDACTION_FLAG,
                    new Boolean(true));
        }
        /* The permissions entities is simply to simulate the P8 Permissions handling */
        /* AJH commenting out because the file checking can hurt performance */
        //        String entitiesFilename = gFilePath + baseAnnFilename
        //            + ".ann.permissions-entities.xml";
        //        File entitiesFile = new File(entitiesFilename);
        //        try
        //        {
        //            if (entitiesFile.exists())
        //            {
        //                byte[] entitiesData = ClientServerIO.getFileBytes(entitiesFile);
        //                String entitiesXML = new String(entitiesData);
        //                PermissionsEntities permissionsEntities = PermissionsEntities
        //                    .createFromXML(entitiesXML);
        //                properties
        //                    .put(AnnotationLayer.PROPERTIES_KEY_PERMISSIONS_ENTITIES,
        //                         permissionsEntities);
        //            }
        //        }
        //        catch (Throwable t)
        //        {
        //            Logger.getInstance().printStackTrace(t);
        //            throw new FlexSnapSIAPIException(t.getMessage());
        //        }
        ContentHandlerResult result = new ContentHandlerResult();
        result.put(ContentHandlerResult.KEY_ANNOTATION_PROPERTIES, properties);
        return result;
    }

    @Override
    public ContentHandlerResult deleteAnnotation(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        String documentKey = contentHandlerInput.getDocumentId();
        String annotationKey = contentHandlerInput.getAnnotationId();
        String annotationFilename = documentKey + "." + annotationKey + ".ann";
        String fullFilePath = gFilePath + annotationFilename;
        log.log(Logger.FINEST, "Deleting annotation file: " + fullFilePath);
        try
        {
            File file = new File(fullFilePath);
            file.delete();
        } catch (Throwable e)
        {
            Logger.getInstance().log(Logger.INFO,
                    "Attempt to delete layer " + annotationKey
                            + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public ContentHandlerResult getBookmarkContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in getBookmarkContent");
        return null;
    }

    @Override
    public ContentHandlerResult sendDocumentContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in ArkCaseContentHandler.sendDocumentContent");

        try
        {
            log.log(Level.FINE, "new CHR");
            ContentHandlerResult retVal = new ContentHandlerResult();

            log.log(Level.FINE, "get doc id");
            String documentKey = contentHandlerInput.getDocumentId();

            log.log(Level.FINE, "document key: " + documentKey);

            int ticketStart = documentKey.indexOf("&acm_ticket=");

            log.log(Level.FINE, "ticket start: " + ticketStart);

            String docIdString = docIdFromDocumentKey(documentKey);

            log.log(Level.FINE, "Doc ID: " + docIdString);

            int userIdStart = documentKey.indexOf("&userid=");
            String user = documentKey.substring(userIdStart + 8);

            log.log(Level.FINE, "User: " + user);

            String ticket = documentKey.substring(ticketStart + 12, userIdStart);
            log.log(Level.FINE, "ticket: " + ticket);


            log.log(Level.FINE, "get merge annotations");
            boolean mergeAnnotations = contentHandlerInput.mergeAnnotations();

            log.log(Level.FINE, "get doc content");
            byte[] data = contentHandlerInput.getDocumentContent();

            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            Snowbnd snow = new Snowbnd();
            int filetype = snow.IMGLOW_get_filetype(dis);
            Format format = FormatHash.getInstance().getFormat(filetype);

            String extension = format.getExtension();
            log.log(Level.FINE, "extension: " + extension);
            log.log(Level.FINE, "MIME type: " + format.getMimeType());


            String fileName = "sendDocument-" + docIdString + "." + extension;

            File saveFile = new File(gFilePath + fileName);

            log.log(Level.FINE, "save file");
            ClientServerIO.saveFileBytes(data, saveFile);


            String targetUrl = baseURL + sendFileService + docIdString + "?acm_ticket=" + ticket;
            log.log(Level.FINE, "target URL: " + targetUrl);
            URL url = new URL(targetUrl);

            log.log(Level.FINE, "open connection");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");

            String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
            String CRLF = "\r\n"; // Line separator required by multipart/form-data.

            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

            log.log(Level.FINE, "Writing file!!!");
            // Send normal param.
            writer.append("--" + boundary).append(CRLF);

            //Content-Disposition: form-data; name="files[]"; filename="Report_of_Investigation_26032015174543228.pdf"

            writer.append("Content-Disposition: form-data; name=\"files[]\"; filename=\"" + saveFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + format.getMimeType()); //URLConnection.guessContentTypeFromName(saveFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(saveFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            log.log(Level.FINE, "Response: " + responseCode + " " + responseMessage);

            InputStream is = connection.getInputStream();

            String fullResponse = ClientServerIO.readStringFromInputStream(is);
            log.log("Response body: " + fullResponse);


            log.log(Level.FINE, "Done writing!!!");

            log.log(Level.FINE, "returning");

            return retVal;
        } catch (Throwable t)
        {
            log.log(Level.SEVERE, "Error in sendDocumentContent: " + t.getMessage());
            t.printStackTrace();

            throw new FlexSnapSIAPIException(t.getMessage());

        }

//
//        ContentHandlerResult retVal = new ContentHandlerResult();
//        return retVal;
    }

    @Override
    public boolean hasAnnotations(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in hasAnnotations");
        return false;
    }

    @Override
    public ContentHandlerResult getClientPreferencesXML(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in getClientPreferencesXML");
        Logger logger = Logger.getInstance();
        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        logger.log(Logger.FINEST, "getClientPreferencesXML: clientInstanceId " + clientInstanceId);
        String preferencesFilename = clientInstanceId + ".preferences.xml";
        String fullFilePath = "/tmp/" + preferencesFilename;
        logger.log(Logger.FINEST, "Retrieving preferences file: " + fullFilePath);

        try
        {
            File ioe = new File(fullFilePath);
            String xmlString = new String(ClientServerIO.getFileBytes(ioe), "UTF-8");
            ContentHandlerResult result = new ContentHandlerResult();
            result.put("KEY_CLIENT_PREFERENCES_XML", xmlString);
            return result;
        } catch (UnsupportedEncodingException var9)
        {
            logger.printStackTrace(var9);
            return null;
        } catch (IOException var10)
        {
            logger.printStackTrace(var10);
            return null;
        }
    }

    @Override
    public ContentHandlerResult saveClientPreferencesXML(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in saveClientPreferencesXML");
        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        String preferencesFilename = clientInstanceId + ".preferences.xml";
        String fullFilePath = "/tmp/" + preferencesFilename;
        Logger.getInstance().log(Logger.FINEST, "Saving preferences file: " + fullFilePath);

        try
        {
            File e = new File(fullFilePath);
            String xmlString = contentHandlerInput.getClientPreferencesXML();
            ClientServerIO.saveFileBytes(xmlString.getBytes("UTF-8"), e);
            return ContentHandlerResult.VOID;
        } catch (Exception var7)
        {
            return null;
        }
    }

    @Override
    public ContentHandlerResult getAvailableDocumentIds(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in getAvailableDocumenIds");
        String[] ids = new String[0];

        ContentHandlerResult result = new ContentHandlerResult();
        result.put("KEY_AVAILABLE_DOCUMENT_IDS", ids);
        return result;
    }

    @Override
    public ContentHandlerResult saveDocumentContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in saveDocumentContent");
        HttpServletRequest request = contentHandlerInput.getHttpServletRequest();
        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        String documentKey = contentHandlerInput.getDocumentId();
        byte[] data = contentHandlerInput.getDocumentContent();
        return saveDocumentContent(request, clientInstanceId, documentKey, data);
    }

    public ContentHandlerResult saveDocumentContent(HttpServletRequest request,
                                                    String clientInstanceId,
                                                    String documentId,
                                                    byte[] data)
            throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in saveDocumentContent/req/cid/docid/data)");
        if (data == null)
        {
            return null;
        }
        File saveFile = new File(gFilePath + documentId);
        ClientServerIO.saveFileBytes(data, saveFile);
        ContentHandlerResult result = new ContentHandlerResult();
        result.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentId);
        return result;
    }

    /**
     */
    public ContentHandlerResult saveDocumentContent(HttpServletRequest request,
                                                    String clientInstanceId,
                                                    String documentId,
                                                    File inputfile)
    {
        log.log(Level.FINE, "in saveDocumentContent(req,cid,docid,file)");
        ContentHandlerResult result = new ContentHandlerResult();
        if (inputfile == null)
        {
            return null;
        }
        File saveFile = new File(gFilePath + documentId);
        try
        {
            ClientServerIO.copyFile(inputfile, saveFile);
        } catch (Exception e)
        {
            result.put(ContentHandlerResult.ERROR_MESSAGE, e.getMessage());
            return result;
        }
        result.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentId);
        return result;
    }

    @Override
    public ContentHandlerResult saveBookmarkContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in saveBookmarkContent");
        HttpServletRequest request = contentHandlerInput.getHttpServletRequest();
        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        String documentKey = contentHandlerInput.getDocumentId();
        byte[] data = contentHandlerInput.getBookmarkContent();
        return saveBookmarkContent(request, clientInstanceId, documentKey, data);
    }

    /**
     * @param request
     * @param clientInstanceId
     * @param documentKey
     * @param data
     * @return
     */
    private ContentHandlerResult saveBookmarkContent(HttpServletRequest request,
                                                     String clientInstanceId,
                                                     String documentId,
                                                     byte[] data)
    {
        log.log(Level.FINE, "in saveBookmarkContent/req/cid/docid/data");
        Logger.getInstance().log(Logger.FINEST,
                "saveBookmarkContent..." + documentId
                        + "clientInstanceId: " + clientInstanceId);
        String fullFilePath = gFilePath + documentId + ".bookmarks.xml";
        File file = new File(fullFilePath);
        if (data == null)
        {
            if (file.exists())
            {
                file.delete();
            } else
            {
                return ContentHandlerResult.VOID;
            }
        }
        try
        {
            ClientServerIO.saveFileBytes(data, file);
        } catch (Exception e)
        {
            Logger.getInstance().printStackTrace(e);
        }
        return ContentHandlerResult.VOID;
    }

    @Override
    public ContentHandlerResult saveDocumentComponents(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in saveDocumentComponents");
        HttpServletRequest request = contentHandlerInput.getHttpServletRequest();
        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        String documentId = contentHandlerInput.getDocumentId();
        byte[] data = contentHandlerInput.getDocumentContent();
        File file = contentHandlerInput.getDocumentFile();
        AnnotationLayer[] annotations = contentHandlerInput.getAnnotationLayers();
        byte[] bookmarkBytes = contentHandlerInput.getBookmarkContent();
        byte[] noteBytes = contentHandlerInput.getNotesContent();
        /* The following line shows how to get the page count if needed. */
        // int pageCount = input.getDocumentPageCount();
        Logger.getInstance().log("saveDocumentContents");
        if (data != null)
        {
            saveDocumentContent(request, clientInstanceId, documentId, data);
        } else if (file != null)
        {
            saveDocumentContent(request, clientInstanceId, documentId, file);
        }
        if (annotations != null)
        {
            Hashtable existingAnnHash = getExistingAnnotationsHash(documentId,
                    clientInstanceId);
            for (int annIndex = 0; annIndex < annotations.length; annIndex++)
            {
                AnnotationLayer annLayer = annotations[annIndex];
                /*
                 * Remove the annLayer from the existingHashToindicate that it
                 * should still exist and not be deleted.
                 */
                existingAnnHash.remove(annLayer.getLayerName());
                if (annLayer.isNew() || annLayer.isModified())
                {
                    saveAnnotationContent(request,
                            clientInstanceId,
                            documentId,
                            annLayer.getLayerName(),
                            annLayer.getPageSpecificIndex(),
                            annLayer.getData(),
                            annLayer.getProperties());
                } else
                {
                    Logger.getInstance().log(Logger.FINEST,
                            "Skipping unmodified Layer: "
                                    + annLayer.getLayerName());
                }
            }
            /* Any annotation that is still in the existing hash should be deleted */
            deleteUnsavedExistingLayers(documentId, existingAnnHash);
        }
        saveNotesContent(request, clientInstanceId, documentId, noteBytes);
        saveBookmarkContent(request,
                clientInstanceId,
                documentId,
                bookmarkBytes);
        ContentHandlerResult result = new ContentHandlerResult();
//        result.put(ContentHandlerResult.DOCUMENT_ID_TO_RELOAD, documentId);
        return result;
    }

    private void deleteAnnotationLayer(String documentId, String layerId) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in deleteAnnotationLayer");
        /* Note - there is some code in here that implies that
         * we simply rename the file, but currentlty we just delete the file
         */
        String annotationFilename = documentId + "." + layerId + ".ann";
        //        String renamedFilename = documentId + "." + layerId + ".deleted-ann";
        String fullFilePath = gFilePath + annotationFilename;
        //        String fullRenamedFilePath = gFilePath + renamedFilename;
        //        File renamedFile = new File (fullRenamedFilePath);
        File annFile = new File(fullFilePath);
        if (annFile.exists())
        {
            /* AH. The initial implementaion will be to actually delete the file.
             * But as you can see from the code, we could simply rename it
             */
            //            annFile.renameTo(renamedFile);
            annFile.delete();
        }
    }

    private void deleteUnsavedExistingLayers(String documentId, Hashtable existingAnnHash) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in deleteUnsavedExistingLayers");
        Enumeration hashEnum = existingAnnHash.keys();
        while (hashEnum.hasMoreElements())
        {
            String deleteLayerId = (String) hashEnum.nextElement();
            Logger.getInstance().log(Logger.INFO,
                    "About to delete layer: " + deleteLayerId);
            deleteAnnotationLayer(documentId, deleteLayerId);
        }
    }

    /**
     * @param request
     * @param clientInstanceId
     * @param documentKey
     * @param data
     * @return
     */
    private ContentHandlerResult saveNotesContent(HttpServletRequest request, String clientInstanceId, String documentId, byte[] data)
    {
        log.log(Level.FINE, "in saveNotesContent/req/cid/docid/data");
        if (data == null)
        {
            return null;
        }
        Logger.getInstance().log(Logger.FINEST,
                "saveNotesContent..." + documentId
                        + "clientInstanceId: " + clientInstanceId);
        String fullFilePath = gFilePath + documentId + ".notes.xml";
        File file = new File(fullFilePath);
        try
        {
            ClientServerIO.saveFileBytes(data, file);
        } catch (Exception e)
        {
            Logger.getInstance().printStackTrace(e);
        }
        return new ContentHandlerResult();
    }


    @Override
    public ContentHandlerResult saveDocumentComponentsAs(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in saveDocumentComponentsAs");
        return saveDocumentComponents(contentHandlerInput);
    }

    @Override
    public ContentHandlerResult saveAnnotationContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in saveAnnotationContent");
        HttpServletRequest request = contentHandlerInput.getHttpServletRequest();
        String clientInstanceId = contentHandlerInput.getClientInstanceId();
        String documentKey = contentHandlerInput.getDocumentId();
        String annotationKey = contentHandlerInput.getAnnotationId();
        int pageSpecificIndex = -1;
        byte[] data = contentHandlerInput.getAnnotationContent();
        Hashtable annProperties = contentHandlerInput.getAnnotationProperties();
        return saveAnnotationContent(request,
                clientInstanceId,
                documentKey,
                annotationKey,
                pageSpecificIndex,
                data,
                annProperties);
    }

    @Override
    public ContentHandlerResult publishDocument(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {

        log.log(Level.FINE, "in publishDocument");
        return ContentHandlerResult.VOID;
    }

    @Override
    public ContentHandlerResult saveRedactionContent(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        Logger logger = Logger.getInstance();
        logger.log(Level.FINE, "in saveRedactionContent");
        return this.publishDocument(contentHandlerInput);
    }

    @Override
    public ContentHandlerResult getUsersAndGroups(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in saveRedactionContent");
        String[] users = new String[]{"Bob", "Javier", "Martin"};
        String[] groups = new String[]{"Celtics", "Bruins", "Red Sox"};
        ContentHandlerResult result = new ContentHandlerResult();
        UsersAndGroups uag = new UsersAndGroups(users, groups);
        result.put("KEY_USERS_AND_GROUPS", uag);
        return result;
    }

    @Override
    public ContentHandlerResult getCurrentUserName(ContentHandlerInput contentHandlerInput) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in getCurrentUserName");
        ContentHandlerResult result = new ContentHandlerResult();
        result.put("KEY_CURRENT_USER_NAME", "File User");
        return result;
    }

    /**
     * @param request
     * @param clientInstanceId
     * @param documentKey
     * @param annotationKey
     * @param pageSpecificIndex Default is -1 and is ignored. 0 or positive value means annotations are for that page.
     * @param data
     * @param annProperties
     * @return
     * @throws FlexSnapSIAPIException
     */
    public ContentHandlerResult saveAnnotationContent(HttpServletRequest request,
                                                      String clientInstanceId,
                                                      String documentKey,
                                                      String annotationKey,
                                                      int pageSpecificIndex,
                                                      byte[] data,
                                                      Hashtable annProperties)
            throws FlexSnapSIAPIException
    {
        Logger logger = Logger.getInstance();
        logger.log(Logger.FINEST, "Inside SaveAnnotationContent method! ");
        logger.log(Logger.FINEST, "Inside SaveAnnotationContent method, clientInstanceId: " + clientInstanceId);
        logger.log(Logger.FINEST, "Inside SaveAnnotationContent method, documentKey: " + documentKey);
        logger.log(Logger.FINEST, "Inside SaveAnnotationContent method, annotationKey: " + annotationKey);
        logger.log(Logger.FINEST, "Inside SaveAnnotationContent method, pageSpecificIndex: " + pageSpecificIndex);

        if (data == null)
        {
            logger.log(Logger.FINEST, "NO DATA data==null, return null! ");
            return null;
        }
        String pageIndexPortion = ""; /* By default we don't use pageIndex in the filename */
        if (pageSpecificIndex != -1)
        {
            pageIndexPortion = "-page" + pageSpecificIndex;
        }
        logger.log(Logger.FINEST, "Inside SaveAnnotationContent method, pageIndexPortion: " + pageIndexPortion);

        String docId = docIdFromDocumentKey(documentKey);
        logger.log(Logger.FINEST, "Inside SaveAnnotationContent method, docId: " + docId);
        String baseFilePath = gFilePath + docId + "." + annotationKey
                + pageIndexPortion;

        logger.log(Logger.FINEST, "Inside SaveAnnotationContent baseFilePath: " + baseFilePath);

        String annFilePath = baseFilePath + ".ann";

        logger.log(Logger.FINEST, "Inside SaveAnnotationContent annFilePath: " + annFilePath);
        if (annotationKey.equals(DocumentModel.REDACTION_LAYER_NAME))
        {
            annFilePath = gFilePath + documentKey + ".redactions.xml";
            logger.log(Logger.FINEST, "Inside SaveAnnotationContent Redaction ... annFilePath: " + annFilePath);
        }
        String editFilePath = baseFilePath + "-redactionEdit.ann";
        String burnFilePath = baseFilePath + "-redactionBurn.ann";
        String fullFilePath = annFilePath;
        if (annProperties != null)
        {
            Boolean tmpRedactionFlag = (Boolean) annProperties
                    .get(AnnotationLayer.PROPERTIES_KEY_REDACTION_FLAG);
            Integer tmpPermissionLevel = (Integer) annProperties
                    .get(AnnotationLayer.PROPERTIES_KEY_PERMISSION_LEVEL);
            PermissionsEntities permissionsEntities = (PermissionsEntities) annProperties
                    .get(AnnotationLayer.PROPERTIES_KEY_PERMISSIONS_ENTITIES);
            boolean redactionFlag = false;
            int permissionLevel = PERM_DELETE.intValue();
            if (tmpRedactionFlag != null)
            {
                redactionFlag = tmpRedactionFlag.booleanValue();
            }
            if (tmpPermissionLevel != null)
            {
                permissionLevel = tmpPermissionLevel.intValue();
            }
            if (permissionLevel <= PERM_REDACTION.intValue())
            {
                fullFilePath = burnFilePath;
            } else if (redactionFlag == true)
            {
//                fullFilePath = editFilePath;
                fullFilePath = burnFilePath; // Changed as per PM/Sales request, FB#7002
            }
            if (permissionsEntities != null)
            {
                String permissionsEntitiesFilePath = annFilePath
                        + ".permissions-entities.xml";
                File entitiesFile = new File(permissionsEntitiesFilePath);
                try
                {
                    byte[] entitiesData = permissionsEntities.toXML()
                            .getBytes(ClientServerIO.UTF_8);
                    ClientServerIO.saveFileBytes(entitiesData, entitiesFile);
                } catch (Exception e)
                {
                    Logger.getInstance().printStackTrace(e);
                }
            }
        }
        // Make sure any existing ann files are deleted
        File file = new File(annFilePath);
        if (file.exists())
        {
            file.delete();
        }
        file = new File(burnFilePath);
        if (file.exists())
        {
            file.delete();
        }
        file = new File(editFilePath);
        if (file.exists())
        {
            file.delete();
        }
        Logger.getInstance().log(Logger.FINEST,
                "saveAnnotationContent..." + annotationKey);
        file = new File(fullFilePath);
        try
        {
            if (data.length > 0)
            {
                ClientServerIO.saveFileBytes(data, file);
            }
        } catch (Exception e)
        {
            Logger.getInstance().printStackTrace(e);
        }
        return new ContentHandlerResult();
    }

    public String docIdFromDocumentKey(String documentKey)
    {
        int ecmFileIdPosition = documentKey.indexOf("ecmFileId=") + "ecmFileId".length() + 1;
        int endOfDocIdPosition = documentKey.indexOf("&");
        return documentKey.substring(ecmFileIdPosition, endOfDocIdPosition);
    }

    private Hashtable getExistingAnnotationsHash(String documentId, String clientInstanceId) throws FlexSnapSIAPIException
    {
        log.log(Level.FINE, "in getExistingAnnotationsHash");

        ContentHandlerInput contentHandlerInput = new ContentHandlerInput();
        contentHandlerInput.setDocumentId(documentId);
        contentHandlerInput.setClientInstanceId(clientInstanceId);
        ContentHandlerResult annResult = this.getAnnotationNames(contentHandlerInput);
        String[] annNames = annResult.getAnnotationNames();
        Hashtable existingAnnHash = new Hashtable();
        if (annNames != null)
        {
            for (int annIndex = 0; annIndex < annNames.length; annIndex++)
            {
                String existingAnnotationId = annNames[annIndex];
                if (existingAnnotationId.indexOf("-page") != -1)
                {
                    existingAnnotationId = annNames[annIndex]
                            .substring(0, existingAnnotationId.indexOf("-page"));
                }
                existingAnnHash.put(existingAnnotationId, existingAnnotationId);
            }
        }
        return existingAnnHash;
    }
}

