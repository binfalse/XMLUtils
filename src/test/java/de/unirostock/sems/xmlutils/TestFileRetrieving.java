/**
 * 
 */
package de.unirostock.sems.xmlutils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.SAXException;

import de.unirostock.sems.xmlutils.exception.XmlDocumentConsistencyException;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * @author Martin Scharm
 * 
 */
@RunWith(JUnit4.class)
public class TestFileRetrieving
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
	public void testRetrieving () throws XmlDocumentParseException, FileNotFoundException, XmlDocumentConsistencyException, ParserConfigurationException, SAXException, IOException, JDOMException
	{
		try
		{
			Document d = XmlTools.readDocument (new URL ("http://most.sems.uni-rostock.de/resources/d3d3LmViaS5hYy51ay9iaW9tb2RlbHMtbWFpbi8=/L0JJT01EMDAwMDAwMDE0MA==/MjAwNy0wOS0yNQ=="));
			assertFalse ("retrieved file is null", d == null);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			fail ("wasn't able to retrieve file: " + e.getMessage ());
		}
	}
}
