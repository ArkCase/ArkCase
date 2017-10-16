package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.plugins.ecm.service.EcmTikaFileService;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.UserDataBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.boxes.apple.AppleGPSCoordinatesBox;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;
import us.fatehi.pointlocation6709.format.FormatterException;
import us.fatehi.pointlocation6709.format.PointLocationFormatType;
import us.fatehi.pointlocation6709.format.PointLocationFormatter;
import us.fatehi.pointlocation6709.parse.ParserException;
import us.fatehi.pointlocation6709.parse.PointLocationParser;

import java.io.ByteArrayInputStream;
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
    public EcmTikaFile detectFileUsingTika(byte[] fileBytes, String fileName) throws IOException, SAXException, TikaException
    {
        Map<String, Object> metadata = extract(fileBytes, fileName);
        metadata.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> logger.debug("{}: {}", e.getKey(), e.getValue()));
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
            } catch (IllegalAccessException | InvocationTargetException | ConversionException e)
            {
                logger.error("Could not set property [{}] to value [{}]", mToP.getValue(), metadata.get(mToP.getKey()), e);
            }
        }

        return retval;
    }

    protected String extractIso6709Gps(byte[] fileBytes) throws IOException
    {
        /*
         * mp4 files are structured in terms of boxes.  The "movie box" has information about the entire movie.
         * The movie box includes a "user data" box.  mp4 file writers can store arbitrary information here.  On
         * movies taken on Android phones, if GPS tracking is enabled, the GPS data is stored in an "Apple GPS
         * coordinates box", inside the user data box.  Hopefully other mp4 writers use the same structure.
         *
         * If present, the GPS data is stored in canonical ISO-6709 format... again, hopefully other mp4 writers also
         * store in proper canonical format.
         */
        try (DataSource dataSource = new MemoryDataSourceImpl(fileBytes);
             IsoFile isoFile = new IsoFile(dataSource))
        {
            UserDataBox udb = isoFile.getMovieBox().getBoxes(UserDataBox.class).stream().findFirst().orElse(null);
            AppleGPSCoordinatesBox gpsBox = udb == null ? null :
                    udb.getBoxes(AppleGPSCoordinatesBox.class).stream().findFirst().orElse(null);
            String iso6709 = gpsBox == null ? null : gpsBox.getValue();

            return iso6709;
        }
    }

    protected Map<String, Object> extract(byte[] fileBytes, String fileName) throws IOException, SAXException, TikaException
    {
        String contentType = null;
        String extension = null;

        Metadata metadata = new Metadata();
        metadata.add(Metadata.RESOURCE_NAME_KEY, fileName);

        TikaConfig defaultConfig = TikaConfig.getDefaultConfig();
        Detector detector = defaultConfig.getDetector();
        TikaInputStream stream = TikaInputStream.get(fileBytes);
        MediaType mediaType = detector.detect(stream, metadata);
        MimeType mimeType = defaultConfig.getMimeRepository().forName(mediaType.toString());
        contentType = mediaType.toString();
        extension = mimeType.getExtension();

        Map<String, Object> fileMetadata = null;

        try (InputStream inputStream = new ByteArrayInputStream(fileBytes))
        {

            Parser parser = new AutoDetectParser();
            ParseContext parseContext = new ParseContext();

            parser.parse(inputStream, new DefaultHandler(), metadata, parseContext);

            fileMetadata =
                    Arrays.stream(metadata.names()).
                            collect(
                                    HashMap::new,
                                    (m, n) -> m.put(n, metadata.get(n)),
                                    (m, u) -> {
                                    });
        } catch (TikaException tikaException)
        {
            // we have to at least return the mime type and extension, so we just log the parser error, and continue
            // with the already-detected mime type.
            logger.warn("Could not extract metadata from file: [{}]", tikaException.getMessage());
            fileMetadata = new HashMap<>();
        }

        fileMetadata.put("Content-Type", contentType);
        fileMetadata.put("File-Name-Extension", extension);

        // Creation-Date is not always trustworthy... some webcams produce values that make no sense, like the year 1903.
        // Prefer a set of fields, whichever is set first -
        String strCreated = null;
        String[] preferredDateFieldsInOrder = {
                "date",
                "Last-Save-Date",
                "Last-Modified",
                "dcterms:modified",
                "Creation-Date"
        };
        for (String preferredField : preferredDateFieldsInOrder)
        {
            if (fileMetadata.containsKey(preferredField))
            {
                strCreated = (String) fileMetadata.get(preferredField);
                break;
            }
        }

        if (strCreated != null)
        {
            LocalDateTime created = LocalDateTime.parse(strCreated, DateTimeFormatter.ISO_DATE_TIME);

            // some movies don't store any dates at all, and then Tika presents the dates from the year 1904 for
            // some reason, usually as "1904-01-01T00:00:00Z".  So if the date is before 1950 we will ignore it.
            if (created.getYear() > 1950)
            {
                Date createdDate = Date.from(created.toInstant(ZoneOffset.UTC));
                fileMetadata.put("Creation-Date-Local", createdDate);
            }
            else
            {
                fileMetadata.put("Creation-Date-Local", null);
            }
        }

        PointLocation gpsPoint = null;

        if ("video/mp4".equals(contentType))
        {
            gpsPoint = pointLocationFromVideo(fileBytes, fileMetadata);
        }

        // Cameras store the GPS lat and long in geo:lat and geo:long tags.
        if (fileMetadata.containsKey("geo:lat") && fileMetadata.containsKey("geo:long"))
        {
            gpsPoint = pointLocationFromLatLong(fileMetadata);
        }

        if (gpsPoint != null)
        {
            enrichGpsFields(fileMetadata, gpsPoint);
        }

        return fileMetadata;

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

    protected PointLocation pointLocationFromVideo(byte[] fileBytes, Map<String, Object> extractedFromStream) throws IOException
    {
        String iso6709GpsCoordinates = extractIso6709Gps(fileBytes);

        if (iso6709GpsCoordinates != null)
        {
            try
            {
                PointLocation pointLocation = PointLocationParser.parsePointLocation(iso6709GpsCoordinates);
                return pointLocation;
            } catch (ParserException e)
            {
                logger.error("Got a bad GPS point location [{}]", iso6709GpsCoordinates, e);
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
