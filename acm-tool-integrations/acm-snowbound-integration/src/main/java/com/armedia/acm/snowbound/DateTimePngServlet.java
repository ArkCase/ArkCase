package com.armedia.acm.snowbound;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Create PNG image of current date/time, allows overriding the default format.
 *  * (PNG generation code used from TextToImage class)
 *
 * Override default format example: http://hostname/path-to-servlet/DateTime.png?format=yyyy/MM/dd
 *
 * TODO: allow overriding other properties, such as font face, font size and others...
 *
 * Created by Petar Ilin <petar.ilin@armedia.com> on 17.08.2015.
 */
public class DateTimePngServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // default date format pattern, with a possibility to override
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if (req.getParameter("format") != null)
        {
            pattern = req.getParameter("format");
        }
        SimpleDateFormat sdf = null;
        try
        {
            sdf = new SimpleDateFormat(pattern);
        } catch (IllegalArgumentException e)
        {
            // invalid pattern, fall back to default
            sdf = new SimpleDateFormat("HH:mm:ss yyyy MM dd");
        }
        Date now = new Date();
        String text = sdf.format(now);

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        // TODO: allow overriding the font face and its properties
        Font font = new Font("SansSerif", Font.PLAIN, 20);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();

        resp.setContentType("image/png");
        ImageIO.write(img, "png", resp.getOutputStream());
    }
}
