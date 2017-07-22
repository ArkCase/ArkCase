package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.plugins.ecm.service.EcmTikaFileService;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.UserDataBox;
import com.googlecode.mp4parser.boxes.apple.AppleGPSCoordinatesBox;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;
import us.fatehi.pointlocation6709.format.FormatterException;
import us.fatehi.pointlocation6709.format.PointLocationFormatType;
import us.fatehi.pointlocation6709.format.PointLocationFormatter;
import us.fatehi.pointlocation6709.parse.ParserException;
import us.fatehi.pointlocation6709.parse.PointLocationParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EcmTikaFileServiceImpl implements EcmTikaFileService
{

    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, String> tikaMetadataToFilePropertiesMap;

    @Override
    public EcmTikaFile detectFileUsingTika(InputStream inputStream, String fileName) throws IOException, SAXException, TikaException
    {
        Map<String, Object> metadata = extract(inputStream, fileName);
        EcmTikaFile retval = fromMetadata(metadata);

        return retval;
    }

    protected EcmTikaFile fromMetadata(Map<String, Object> metadata)
    {
        EcmTikaFile retval = new EcmTikaFile();

        for (Map.Entry<String, String> mToP : tikaMetadataToFilePropertiesMap.entrySet())
        {
            try
            {
                BeanUtils.setProperty(retval, mToP.getValue(), metadata.get(mToP.getKey()));
            } catch (IllegalAccessException | InvocationTargetException e)
            {
                logger.error("Could not set property [{}] to value [{}]", mToP.getValue(), metadata.get(mToP.getKey()), e);
            }
        }

        return retval;
    }

    protected String extractIso6709Gps(String filename) throws IOException
    {
        File file = new File(filename);

        /*
         * mp4 files are structured in terms of boxes.  The "movie box" has information about the entire movie.
         * The movie box includes a "user data" box.  mp4 file writers can store arbitrary information here.  On
         * movies taken on Android phones, if GPS tracking is enabled, the GPS data is stored in an "Apple GPS
         * coordinates box", inside the user data box.  Hopefully other mp4 writers use the same structure.
         *
         * If present, the GPS data is stored in canonical ISO-6709 format... again, hopefully other mp4 writers also
         * store in proper canonical format.
         */
        try (IsoFile isoFile = new IsoFile(file.getAbsolutePath()))
        {
            UserDataBox udb = isoFile.getMovieBox().getBoxes(UserDataBox.class).stream().findFirst().orElse(null);
            AppleGPSCoordinatesBox gpsBox = udb == null ? null :
                    udb.getBoxes(AppleGPSCoordinatesBox.class).stream().findFirst().orElse(null);
            String iso6709 = gpsBox == null ? null : gpsBox.getValue();

            return iso6709;
        }
    }

    protected Map<String, Object> extract(InputStream inputStream, String filename) throws IOException, SAXException, TikaException
    {

        Metadata metadata = new Metadata();
        Parser parser = new AutoDetectParser();
        parser.parse(inputStream, new BodyContentHandler(), metadata, new ParseContext());

        Map<String, Object> extractedFromStream =
                Arrays.stream(metadata.names()).
                        collect(
                                HashMap::new,
                                (m, n) -> m.put(n, metadata.get(n)),
                                (m, u) -> {
                                });

        String contentType = metadata.get("Content-Type");
        MimeType mimeType = TikaConfig.getDefaultConfig().getMimeRepository().forName(contentType);
        extractedFromStream.put("File-Name-Extension", mimeType.getExtension());

        if (extractedFromStream.containsKey("Creation-Date"))
        {
            String strCreated = (String) extractedFromStream.get("Creation-Date");
            LocalDateTime created = LocalDateTime.parse(strCreated, DateTimeFormatter.ISO_DATE_TIME);
            Date createdDate = Date.from(created.toInstant(ZoneOffset.UTC));
            extractedFromStream.put("Creation-Date-Local", createdDate);
        }

        PointLocation gpsPoint = null;

        if ("video/mp4".equals(contentType))
        {
            gpsPoint = pointLocationFromVideo(filename, extractedFromStream);
        }

        // Cameras store the GPS lat and long in geo:lat and geo:long tags.
        if (extractedFromStream.containsKey("geo:lat") && extractedFromStream.containsKey("geo:long"))
        {
            gpsPoint = pointLocationFromLatLong(extractedFromStream);
        }

        if (gpsPoint != null)
        {
            enrichGpsFields(extractedFromStream, gpsPoint);
        }

        return extractedFromStream;

    }

    protected PointLocation pointLocationFromLatLong(Map<String, Object> extractedFromStream)
    {
        String geoLat = (String) extractedFromStream.get("geo:lat");
        String geoLong = (String) extractedFromStream.get("geo:long");

        Double dblLat = Double.valueOf(geoLat);
        Double dblLong = Double.valueOf(geoLong);

        Angle latAng = Angle.fromDegrees(dblLat);
        Angle longAng = Angle.fromDegrees(dblLong);

        Latitude latitude = new Latitude(latAng);
        Longitude longitude = new Longitude(longAng);

        PointLocation pointLocation = new PointLocation(latitude, longitude);
        return pointLocation;
    }

    protected void enrichGpsFields(Map<String, Object> extractedFromStream, PointLocation pointLocation)
    {
        try
        {
            extractedFromStream.put("GPS-Coordinates-ISO6709",
                    PointLocationFormatter.formatPointLocation(pointLocation, PointLocationFormatType.DECIMAL));
        } catch (FormatterException e)
        {
            logger.error("Could not create ISO 6709 representation for point location [{}]", pointLocation, e);
        }
        extractedFromStream.put("GPS-Coordinates-Latitude", pointLocation.getLatitude().getDegrees());
        extractedFromStream.put("GPS-Coordinates-Longitude", pointLocation.getLongitude().getDegrees());
        extractedFromStream.put("GPS-Coordinates-Readable", pointLocation.toString());
    }

    protected PointLocation pointLocationFromVideo(String filename, Map<String, Object> extractedFromStream) throws IOException
    {
        String iso6709GpsCoordinates = extractIso6709Gps(filename);

        if (iso6709GpsCoordinates != null)
        {
            try
            {
                PointLocation pointLocation = PointLocationParser.parsePointLocation(iso6709GpsCoordinates);
                return pointLocation;
            } catch (ParserException e)
            {
                logger.error("Got a bad GPS point location [{}] from video file [{}]", iso6709GpsCoordinates, filename, e);
                return null;
            }
        }

        return null;
    }

    public void setTikaMetadataToFilePropertiesMap(Map<String, String> tikaMetadataToFilePropertiesMap)
    {
        this.tikaMetadataToFilePropertiesMap = tikaMetadataToFilePropertiesMap;
    }
}
