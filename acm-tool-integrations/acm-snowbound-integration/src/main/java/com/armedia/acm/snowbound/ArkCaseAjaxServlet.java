package com.armedia.acm.snowbound;

import Snow.Snowbnd;
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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        String paramAuditEventService = servletConfig.getInitParameter("auditEventService");
        if (paramAuditEventService != null)
        {
            this.auditEventService = paramAuditEventService;
        }

        // TODO: Take custom created stamps and add to the list taken from web.xml
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        super.service(request, response);

        String action = getDecodedParameter(request, "action");

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
            }
            if (action.equals("arkCaseDeleteDocumentPages"))
            {
                LOG.log(Level.FINE, "Requested document pages deletion");
                String ecmFileId = getDecodedParameter(request, "ecmFileId");
                String userId = getDecodedParameter(request, "userid");
                String acmTicket = getDecodedParameter(request, "acm_ticket");
                String pageNumbers = getDecodedParameter(request, "pageNumbers");
                String deleteReason = getDecodedParameter(request, "deleteReason");
                String targetUrl = String.format("%s%s?acm_ticket=%s&file_id=%s&user_id=%s&page_numbers=%s&delete_reason=%s",
                        baseURL, auditEventService, acmTicket, ecmFileId, userId, pageNumbers, deleteReason);
                LOG.log(Level.FINE, "target URL: " + targetUrl);
                URL url = new URL(targetUrl);

                LOG.log(Level.FINE, "open connection");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                if (HttpServletResponse.SC_OK == connection.getResponseCode())
                {
                    LOG.log(Level.FINE, "successfully sent audit event data");
                } else
                {
                    LOG.log(Level.SEVERE, "unable to send audit event data");
                }
                connection.disconnect();
            }
            if (action.equals("arkCaseReorderDocumentPages")) {
                try {
                    LOG.log(Level.FINE, "Requested document page reordering");

                    // Obtains url parameters
                    String acmTicket = getDecodedParameter(request, "acm_ticket");
                    String ecmFileId = getDecodedParameter(request, "ecmFileId");
                    String userId = getDecodedParameter(request, "userid");
                    String pageReorderOperation = getDecodedParameter(request, "pageReorderOperation");
                    LOG.log(Level.FINE, "Requested new order: " + pageReorderOperation);

                    // Builds audit event notification target url to ArkCase
                    String targetUrl = String.format("%s%s?acm_ticket=%s&file_id=%s&user_id=%s&reorder_operation=%s",
                            baseURL, auditEventService, acmTicket, ecmFileId, userId, pageReorderOperation);
                    LOG.log(Level.FINE, "target URL: " + targetUrl);

                    // Sends audit event notification to ArkCase that a document has been re-ordered
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(targetUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        if (HttpServletResponse.SC_OK == connection.getResponseCode()) {
                            LOG.log(Level.FINE, "successfully sent audit event data");
                        } else {
                            LOG.log(Level.SEVERE, "unable to send audit event data");
                        }
                    } finally {
                        if (connection != null)
                            connection.disconnect();
                    }

                } catch (Exception e) {
                    LOG.log(Level.SEVERE, e.getMessage());
                }
            }
        }
    }

    private byte[] arkCaseCreateCustomImageStamp() throws Exception
    {
        byte[] jsonBytes = null;

        String title = "Riste";
        String imagePath = "http://localhost:8083/VirtualViewerJavaHTML5/resources/stamps/" + title + ".png";
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
