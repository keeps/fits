package edu.harvard.hul.ois.fits.tools;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ToolErrorHandler implements ErrorHandler
{

    public void warning (SAXParseException exception) throws SAXException
    {
        System.out.println("warn" + exception.getMessage());
        
    }

    public void error (SAXParseException exception) throws SAXException
    {
        System.out.println("error" + exception.getMessage());
    }

    public void fatalError (SAXParseException exception) throws SAXException
    {
        System.out.println("fatal" + exception.getMessage());
        
    }
}
