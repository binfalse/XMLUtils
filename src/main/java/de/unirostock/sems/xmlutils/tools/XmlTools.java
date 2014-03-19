/**
 * 
 */
package de.unirostock.sems.xmlutils.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.binfalse.bfutils.FileRetriever;



/**
 * XML toolkit.
 * 
 * @author Martin Scharm
 */
public class XmlTools
{
	/**
	 * no need to recreate the builder everytime...
	 */
	private static SAXBuilder builder;
	
	
	public static SAXBuilder getBuilder ()
	{
		return new SAXBuilder ();
	}
	
	/**
	 * Reads an XML document from File.
	 *
	 * @param file the document to read
	 * @return the document
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public static Document readDocument (File file)
		throws IOException, JDOMException
	{
		if (builder == null)
			builder = getBuilder ();//DocumentBuilderFactory.newInstance ().newDocumentBuilder ();
		
		return builder.build (new FileInputStream (file));
	}
	
	
	/**
	 * Read an XML document from web.
	 *
	 * @param url the url to the document
	 * @return the document
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws JDOMException the jDOM exception
	 */
	public static Document readDocument (URL url) throws IOException, URISyntaxException, JDOMException
	{
		if (builder == null)
			builder = getBuilder ();//DocumentBuilderFactory.newInstance ().newDocumentBuilder ();

		File tmp = File.createTempFile ("Bives", "download");
		tmp.deleteOnExit ();
		FileRetriever.getFile (url.toURI (), tmp);
		
		return builder.build (new FileInputStream (tmp));
	}
	
	/**
	 * Reads an XML document from File.
	 *
	 * @param is the stream containing the document
	 * @return the document
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public static Document readDocument (InputStream is)
		throws IOException, JDOMException
	{
		if (builder == null)
			builder = getBuilder ();//DocumentBuilderFactory.newInstance ().newDocumentBuilder ();
		
		return builder.build (is);
	}
	
	
	/**
	 * Read an XML document from String.
	 *
	 * @param doc the string containing the XML document
	 * @return the document
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public static Document readDocument (String doc)
		throws IOException, JDOMException
	{
		if (builder == null)
			builder = getBuilder ();//DocumentBuilderFactory.newInstance ().newDocumentBuilder ();
		
		return builder.build (new ByteArrayInputStream (doc.getBytes ()));
	}
	
	
	/**
	 * Prints a document.
	 * 
	 * @param doc
	 *          the document
	 * @return the string
	 */
	public static String printDocument (Document doc)
	{
		return new XMLOutputter(Format.getCompactFormat ()).outputString(doc);
	}
	
	
	/**
	 * Pretty prints a document.
	 *
	 * @param doc the document
	 * @return the output stream
	 */
	public static String prettyPrintDocument (Document doc)
	{
		return new XMLOutputter(Format.getPrettyFormat()).outputString(doc);
	}
	
}
