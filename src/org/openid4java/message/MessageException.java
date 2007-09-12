/*
 * Copyright 2006-2007 Sxip Identity Corporation
 */

package org.openid4java.message;

import org.openid4java.OpenIDException;

/**
 * @author Marius Scurtescu, Johnny Bufu
 */
public class MessageException extends OpenIDException
{
    public MessageException(String message)
    {
        super(message);
    }

    public MessageException(Throwable cause)
    {
        super(cause);
    }

    public MessageException(String message, Throwable cause)
    {
        super(message, cause);
    }
}