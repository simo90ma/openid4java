/*
 * Copyright 2006-2007 Sxip Identity Corporation
 */

package net.openid.discovery;

import org.apache.log4j.Logger;

import java.net.*;
import java.util.Set;
import java.util.HashSet;
import java.io.UnsupportedEncodingException;

/**
 * @author Marius Scurtescu, Johnny Bufu
 */
public class UrlIdentifier implements Identifier
{
    private static Logger _log = Logger.getLogger(UrlIdentifier.class);
    private static final boolean DEBUG = _log.isDebugEnabled();

    private static final Set UNRESERVED_CHARACTERS = new HashSet();

    static
    {
        for (char c = 'a'; c <= 'z'; c++)
            UNRESERVED_CHARACTERS.add(new Character(c));

        for (char c = 'A'; c <= 'A'; c++)
            UNRESERVED_CHARACTERS.add(new Character(c));

        for (char c = '0'; c <= '9'; c++)
            UNRESERVED_CHARACTERS.add(new Character(c));

        UNRESERVED_CHARACTERS.add(new Character('-'));
        UNRESERVED_CHARACTERS.add(new Character('.'));
        UNRESERVED_CHARACTERS.add(new Character('_'));
        UNRESERVED_CHARACTERS.add(new Character('~'));
    }

    private URL _urlIdentifier;

    public UrlIdentifier(String identifier) throws DiscoveryException
    {
        _urlIdentifier = normalize(identifier);
    }

    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        
        if (o == null || getClass() != o.getClass())
            return false;

        final UrlIdentifier that = (UrlIdentifier) o;

        return _urlIdentifier.equals(that._urlIdentifier);
    }

    public int hashCode()
    {
        return _urlIdentifier.hashCode();
    }

    public String getIdentifier()
    {
        return _urlIdentifier.toExternalForm();
    }

    public String toString()
    {
        return _urlIdentifier.toExternalForm();
    }

    public URL getUrl()
    {
        return _urlIdentifier;
    }

    public static URL normalize(String text) throws DiscoveryException
    {
        try
        {
            URI uri = new URI(text);
            URL url = uri.normalize().toURL();

            String protocol = url.getProtocol().toLowerCase();
            String host = url.getHost().toLowerCase();
            int port = url.getPort();
            String path = normalizeUrlEncoding(url.getPath());
            String query = normalizeUrlEncoding(url.getQuery());
            String fragment = normalizeUrlEncoding(url.getRef());

            if (port == url.getDefaultPort())
                port = -1;

            // start building the 'file' part for the URL constructor...
            String file = path;

            if ("".equals(file))
                file = "/";

            if (query != null)
                file = file + "?" + query;

            if (fragment != null)
                file = file + "#" + fragment;

            URL normalized = new URL(protocol, host, port, file);

            if (DEBUG) _log.debug("Normalized: " + text + " to: " + normalized);

            return normalized;
        }
        catch (MalformedURLException e)
        {
            throw new DiscoveryException("Invalid URL identifier", e);
        }
        catch (URISyntaxException e)
        {
            throw new DiscoveryException("Invalid URL identifier", e);
        }

    }

    private static String normalizeUrlEncoding(String text)
    {
        if (text == null)
            return null;

        int len = text.length();
        StringBuffer normalized = new StringBuffer(len);

        for (int i = 0; i < len; i++)
        {
            char current = text.charAt(i);

            if (current == '%' && i < len - 2)
            {
                String percentCode = text.substring(i, i + 3).toUpperCase();

                try
                {
                    String str = URLDecoder.decode(percentCode, "ISO-8859-1");
                    char chr = str.charAt(0);

                    if (UNRESERVED_CHARACTERS.contains(new Character(chr)))
                        normalized.append(chr);
                    else
                        normalized.append(percentCode);
                }
                catch (UnsupportedEncodingException e)
                {
                    normalized.append(percentCode);
                }

                i += 2;
            }
            else
            {
                normalized.append(current);
            }
        }

        return normalized.toString();
    }
}