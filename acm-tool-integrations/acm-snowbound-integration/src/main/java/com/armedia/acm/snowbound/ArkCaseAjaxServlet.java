package com.armedia.acm.snowbound;

import Snow.Snowbnd;
import com.armedia.acm.snowbound.model.ArkCaseConstants;
import com.snowbound.ajax.servlet.AjaxServlet;
import com.snowbound.common.utils.ClientServerIO;
import com.snowbound.common.utils.Logger;
import com.snowbound.common.utils.SnowURLEncoder;
import com.snowbound.common.utils.URLReturnData;
import com.snowbound.snapserv.transport.Base64Processor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by riste.tutureski on 8/13/2015.
 */
public class ArkCaseAjaxServlet extends AjaxServlet
{
    private Logger LOG = Logger.getInstance();

    private String baseURL;

    private String auditEventService;
    private String uploadNewFileService;
    private String snowboundBaseUrl;

    public ArkCaseAjaxServlet()
    {
        super();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);

        String paramBaseURL = servletConfig.getInitParameter("baseURL");
        if (paramBaseURL != null)
        {
            this.baseURL = paramBaseURL;
        }

        String codebase = servletConfig.getInitParameter("codebase");
        if (codebase != null)
        {
            this.snowboundBaseUrl = codebase;
        }

        String paramAuditEventService = servletConfig.getInitParameter("auditEventService");
        if (paramAuditEventService != null)
        {
            this.auditEventService = paramAuditEventService;
        }

        String paramUploadNewFileService = servletConfig.getInitParameter("uploadNewFileService");
        if (paramUploadNewFileService != null)
        {
            this.uploadNewFileService = paramUploadNewFileService;
        }
        LOG.log(Level.FINE, "uploadNewFileService: " + uploadNewFileService);

        // TODO: Take custom created stamps and add to the list taken from web.xml
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = getDecodedParameter(request, "action");
        LOG.log(Level.FINE, "action: " + action);
        try
        {
            super.service(request, response);
        } catch (Exception e)
        {
            LOG.log(Level.SEVERE, e.getMessage());
            throw e;
        }

        if (action != null)
        {
            if (action.equals("arkCaseCreateCustomImageStamp"))
            {
                LOG.log(Level.FINE, "Requested creation of new stamp");
                byte[] jsonBytes;
                try
                {
                    jsonBytes = arkCaseCreateCustomImageStamp();

                    response.setContentType("application/json");
                    response.setContentLength(jsonBytes.length);
                    response.getOutputStream().write(jsonBytes);
                } catch (Exception e)
                {
                    jsonBytes = createExceptionJSON(e).getBytes();
                    response.setContentType("application/json");
                    response.setContentLength(jsonBytes.length);
                    response.getOutputStream().write(jsonBytes);
                }

                LOG.log(Level.FINE, "Response: " + jsonBytes.toString());
            } else if (action.equals("arkCaseDeleteDocumentPages"))
            {
                try
                {
                    LOG.log(Level.FINE, "Requested document pages deletion");

                    // Builds audit event notification target url to ArkCase
                    String targetUrl = buildAuditEventBaseUrl(request, "delete"); // common url portion
                    targetUrl += "&" + ArkCaseConstants.ACM_AUDIT_DELETE_PAGES_PARAM + "=" + getDecodedParameter(request, "pageNumbers");
                    targetUrl += "&" + ArkCaseConstants.ACM_AUDIT_DELETE_REASON_PARAM + "=" + getDecodedParameter(request, "deleteReason");
                    LOG.log(Level.FINE, "target URL: " + targetUrl);

                    // Notifies ArkCase that a delete has occurred
                    sendAuditEventNotificationToArkCase(targetUrl);
                } catch (Exception e)
                {
                    LOG.log(Level.SEVERE, e.getMessage());
                }
            } else if (action.equals("arkCaseReorderDocumentPages"))
            {
                try
                {
                    LOG.log(Level.FINE, "Requested document page reordering");

                    // Obtains the page re-order event description
                    String pageReorderOperation = getDecodedParameter(request, "pageReorderOperation");
                    LOG.log(Level.FINE, "Requested new order: " + pageReorderOperation);

                    // Builds audit event notification target url to ArkCase
                    String targetUrl = buildAuditEventBaseUrl(request, "reorder"); // common url portion
                    targetUrl += "&" + ArkCaseConstants.ACM_AUDIT_REORDER_OPERATION_PARAM + "=" + pageReorderOperation.replaceAll(" ", ""); // reorder event specific url segment
                    LOG.log(Level.FINE, "target URL: " + targetUrl);

                    // Sends audit event notification to ArkCase that a document has been re-ordered
                    sendAuditEventNotificationToArkCase(targetUrl);

                } catch (Exception e)
                {
                    LOG.log(Level.SEVERE, e.getMessage());
                }
            } else if (action.equals("arkCaseViewDocument"))
            {
                try
                {
                    LOG.log(Level.FINE, "Document was viewed by the user");

                    // Builds audit event notification target url to ArkCase
                    String targetUrl = buildAuditEventBaseUrl(request, "viewed"); // common url portion
                    targetUrl += "&" + ArkCaseConstants.ACM_AUDIT_VIEWED_OPERATION_PARAM + "=" + "document_viewed_message";
                    LOG.log(Level.FINE, "target URL: " + targetUrl);

                    // Sends audit event notification to ArkCase that a document has been re-ordered
                    sendAuditEventNotificationToArkCase(targetUrl);

                } catch (Exception e)
                {
                    LOG.log(Level.SEVERE, e.getMessage());
                }
            }
        }
    }

    /**
     * Generates the common portion of the audit event ArkCase url
     * which can be re-used for different event types
     *
     * @param request        - standard servlet request object containing the url parameters
     * @param auditEventType - identifies the type of event being raised (delete, reorder, viewed)
     * @return base audit event url including standard url arguments for ArkCase
     */
    private String buildAuditEventBaseUrl(HttpServletRequest request, String auditEventType)
    {
        String acmTicket = getDecodedParameter(request, ArkCaseConstants.ACM_TICKET_PARAM);
        String ecmFileId = getDecodedParameter(request, ArkCaseConstants.ACM_FILE_PARAM);
        String userId = getDecodedParameter(request, ArkCaseConstants.ACM_USER_PARAM);
        return String.format("%s%s?acm_ticket=%s&file_id=%s&user_id=%s&audit_event_type=%s",
                baseURL, auditEventService, acmTicket, ecmFileId, userId, auditEventType);
    }

    /**
     * Makes an HTTP POST request to ArkCase to register an audit trail event
     *
     * @param targetUrl - the full url including all url arguments of the ArkCase audit REST call
     * @throws Exception if a connection cannot be opened or the data cannot be transmitted to ArkCase
     */
    private void sendAuditEventNotificationToArkCase(String targetUrl) throws Exception
    {
        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(targetUrl);
            LOG.log(Level.FINE, "open connection");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            int responseCode = connection.getResponseCode();
            if (HttpServletResponse.SC_OK == responseCode)
            {
                LOG.log(Level.FINE, "successfully sent audit event data");
            } else
            {
                LOG.log(Level.SEVERE, "unable to send audit event data (" + responseCode + ")");
                LOG.log(Level.SEVERE, connection.getResponseMessage());
            }
        } finally
        {
            if (connection != null)
                connection.disconnect();
        }
    }

    private byte[] arkCaseCreateCustomImageStamp() throws Exception
    {
        byte[] jsonBytes = null;

        String title = "Riste";
        String imagePath = snowboundBaseUrl + "/resources/stamps/" + title + ".png";
        int imageWidth = 500;
        int imageHeight = 500;
        String message = "";

        // Check if already exist image with that name and stamp in the list of stamps
        String data = getBase64EncodedStamp(imagePath, imageWidth, imageHeight);
        JSONObject stamp = findStamp(title);

        // If not exist on the file system, then we should create new image
        if (data == null)
        {
            LOG.log(Level.FINE, "Creating new image with text '" + title + "'");

            TextToImage textToImage = new TextToImage(getServletContext());
            textToImage.convert(title);

            data = watchImageCreation(imagePath, imageWidth, imageHeight);
        }

        if (super.customImageRubberStamps == null)
        {
            super.customImageRubberStamps = new JSONArray();
        }

        // If we created new image, add it to the list
        if (stamp == null)
        {
            JSONObject stampJSON = new JSONObject();

            stampJSON.put("stampTitle", title);
            stampJSON.put("stampWidth", 500);
            stampJSON.put("stampHeight", 500);
            stampJSON.put("stampData", data);

            // TODO: Save stamp somewhere

            super.customImageRubberStamps.put(stampJSON);

            message = "New image is created.";
        } else
        {
            message = "The image already exist.";
        }

        // Create success response
        jsonBytes = createSuccessJSON(message).getBytes();

        return jsonBytes;
    }

    private String getBase64EncodedStamp(String url, int width, int height)
    {
        String retVal = null;

        try
        {
            URLReturnData urlData = ClientServerIO.getURLBytes(url, (String) null);
            if (urlData != null)
            {
                byte[] e = urlData.getData();
                Object returnBytes = null;
                if (e == null)
                {
                    return null;
                }

                byte[] returnBytes1;
                if (e[1] == 80 && e[2] == 78 && e[3] == 71)
                {
                    returnBytes1 = e;
                } else
                {
                    Snowbnd s = new Snowbnd();
                    DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(e));
                    s.IMG_decompress_bitmap(inputStream, 0);
                    s.IMG_resize_bitmap(width, height);
                    int[] size = new int[1];
                    byte[] convertedBytes = s.IMG_save_bitmap(e.length, 16384, 43, size);
                    byte[] trimmedBytes = new byte[size[0]];
                    System.arraycopy(convertedBytes, 0, trimmedBytes, 0, size[0]);
                    returnBytes1 = trimmedBytes;
                }

                retVal = Base64Processor.encode(returnBytes1);
            }
        } catch (IOException var12)
        {
            var12.printStackTrace();
        }

        return retVal;
    }

    private String getDecodedParameter(HttpServletRequest request, String id)
    {
        String queryString = request.getQueryString();
        if (queryString != null)
        {
            Map value1;
            try
            {
                value1 = parseQueryString(queryString);
            } catch (UnsupportedEncodingException var6)
            {
                var6.printStackTrace();
                return null;
            }

            return value1.get(id) != null ? (String) ((List) value1.get(id)).get(0) : null;
        } else
        {
            String value = request.getParameter(id);
            return SnowURLEncoder.decodeURIComponent(value);
        }
    }

    private Map<String, List<String>> parseQueryString(String query) throws UnsupportedEncodingException
    {
        HashMap params = new HashMap();
        String[] arr$ = query.split("&");
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$)
        {
            String param = arr$[i$];
            String[] pair = param.split("=");
            String key = SnowURLEncoder.decodeURIComponent(pair[0]);
            String value = "";
            if (pair.length > 1)
            {
                value = SnowURLEncoder.decodeURIComponent(pair[1]);
            }

            Object values = (List) params.get(key);
            if (values == null)
            {
                values = new ArrayList();
                params.put(key, values);
            }

            ((List) values).add(value);
        }

        return params;
    }

    private String createSuccessJSON(String message)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put("status", "OK");
            if (message != null && !message.isEmpty())
            {
                json.put("message", message);
            }
        } catch (JSONException e)
        {
            LOG.log(Level.WARNING, "Error while creation success response: " + e.getMessage());
        }

        return json.toString();
    }

    private String createExceptionJSON(Exception e)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put("status", "ERROR");
            json.put("message", SnowURLEncoder.encodeURIComponent(e.getMessage()));
        } catch (JSONException e1)
        {
            LOG.log(Level.WARNING, "Error while creation exception response: " + e1.getMessage());
        }

        return json.toString();
    }

    private String watchImageCreation(String path, int width, int height)
    {
        String data = null;

        // Try 10 seconds to see if the image is created
        for (int i = 0; i < 10; i++)
        {
            data = getBase64EncodedStamp(path, width, height);
            if (data != null)
            {
                // When image is created, break execution of this code
                LOG.log(Level.FINE, "Image '" + path + "' is found.");
                break;
            }
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException ie)
            {
                LOG.log(Level.WARNING, "Image '" + path + "' is not found. Wait one second and try to take again.");
                LOG.log(Level.WARNING, "Attempt: " + (i + 1) + ". Total attempts: " + 10);
            }
        }

        return data;
    }

    private JSONObject findStamp(String title)
    {
        if (super.customImageRubberStamps != null && title != null)
        {
            for (int i = 0; i < super.customImageRubberStamps.length(); i++)
            {
                JSONObject object = super.customImageRubberStamps.getJSONObject(i);

                if (object.has("stampTitle") && title.equals(object.getString("stampTitle")))
                {
                    LOG.log(Level.FINE, "Stamp with title '" + title + "' is found.");
                    return object;
                }
            }
        }

        LOG.log(Level.WARNING, "Stamp with title '" + title + "' is not found.");
        return null;
    }
}
