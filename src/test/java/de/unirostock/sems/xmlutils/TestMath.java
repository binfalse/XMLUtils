/**
 * 
 */
package de.unirostock.sems.xmlutils;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.SimpleOutputStream;
import de.unirostock.sems.xmlutils.tools.DocumentTools;



/**
 * @author Martin Scharm
 * 
 */
@RunWith(JUnit4.class)
public class TestMath
{
	
	@Test
	public void testMath ()
	{
		try
		{
			String math = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><apply><times/><ci>kL</ci><ci>S2</ci><ci>X0</ci><ci>c1</ci></apply></math>";
			
			TransformerFactory tFactory = TransformerFactory.newInstance ();
			
			InputStream input = DocumentTools.class
				.getResourceAsStream ("/res/mmlctop2_0.xsl");
			Transformer mathTransformer = tFactory.newTransformer (new StreamSource (
				input));
			
			SimpleOutputStream out = new SimpleOutputStream ();
			//
			// // xslt cannot namespace
			// math = math.replaceAll ("\\S+:\\S+\\s*=\\s*\"[^\"]*\"", "").replaceAll
			// (" /", "/");
			//
			
			mathTransformer.transform (new StreamSource (new ByteArrayInputStream (
				math.getBytes ())), new StreamResult (out));
			
		}
		catch (TransformerException e)
		{
			LOGGER.error (e, "failed to transform math");
			fail ("failed to transform math" + e.getMessage ());
		}
	}
}
