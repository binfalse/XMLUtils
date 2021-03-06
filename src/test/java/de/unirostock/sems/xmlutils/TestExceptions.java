/**
 * 
 */
package de.unirostock.sems.xmlutils;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.JDOMException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.SAXException;

import de.unirostock.sems.xmlutils.exception.XmlDocumentConsistencyException;
import de.unirostock.sems.xmlutils.exception.XmlDocumentException;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;



/**
 * @author Martin Scharm
 * 
 */
@RunWith(JUnit4.class)
public class TestExceptions
{


	
	
	/**
	 * Test node mapper.
	 *
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws XmlDocumentConsistencyException the xml document consistency exception
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the JDOM exception
	 */
	@Test
	public void testNodeMapper () throws XmlDocumentParseException, FileNotFoundException, XmlDocumentConsistencyException, ParserConfigurationException, SAXException, IOException, JDOMException
	{
		assertEquals ("my ex msg", new XmlDocumentConsistencyException ("my ex msg").getMessage ());
		assertEquals ("my ex msg", new XmlDocumentException ("my ex msg").getMessage ());
		assertEquals ("my ex msg", new XmlDocumentParseException ("my ex msg").getMessage ());
	}
}
