/**
 * 
 */
package de.unirostock.sems.xmlutils.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import de.binfalse.bfutils.SimpleOutputStream;



/**
 * XML toolkit.
 * 
 * @author Martin Scharm
 */
public class XmlTools
{
	
	/**
	 * Reads an XML document.
	 * 
	 * @param file
	 *          the document to read
	 * @return the document
	 * @throws ParserConfigurationException
	 *           the parser configuration exception
	 * @throws FileNotFoundException
	 *           the file not found exception
	 * @throws SAXException
	 *           the sAX exception
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public static Document readDocument (File file)
		throws ParserConfigurationException,
			FileNotFoundException,
			SAXException,
			IOException
	{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		return builder.parse (new FileInputStream (file));
	}
	
	
	/**
	 * Pretty print a document.
	 * 
	 * @param doc
	 *          the doccument
	 * @return the pretty string
	 * @throws IOException
	 *           the IO exception
	 * @throws TransformerException
	 *           the transformer exception
	 */
	public static String prettyPrintDocument (Document doc)
		throws IOException,
			TransformerException
	{
		return XmlTools.prettyPrintDocument (doc, new SimpleOutputStream ())
			.toString ();
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
		DOMImplementationLS domImplLS = (DOMImplementationLS) doc
			.getImplementation ();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream ();
		
		LSOutput lso = domImplLS.createLSOutput ();
		lso.setByteStream (baos);
		LSSerializer lss = domImplLS.createLSSerializer ();
		lss.write (doc, lso);
		return baos.toString ();
		
	}
	
	
	/**
	 * Pretty print a document.
	 * 
	 * @param doc
	 *          the document
	 * @param out
	 *          the output stream
	 * @return the output stream
	 * @throws IOException
	 *           the IO exception
	 * @throws TransformerException
	 *           the transformer exception
	 */
	public static OutputStream prettyPrintDocument (Document doc, OutputStream out)
		throws IOException,
			TransformerException
	{
		TransformerFactory tf = TransformerFactory.newInstance ();
		Transformer transformer = tf.newTransformer ();
		transformer.setOutputProperty (OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty (OutputKeys.METHOD, "xml");
		transformer.setOutputProperty (OutputKeys.INDENT, "yes");
		transformer.setOutputProperty (OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty ("{http://xml.apache.org/xslt}indent-amount",
			"4");
		
		transformer.transform (new DOMSource (doc), new StreamResult (
			new OutputStreamWriter (out, "UTF-8")));
		return out;
	}
	
}
