/**
 * 
 */
package de.unirostock.sems.xmlutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.jdom2.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.xmlutils.alg.SemsWeighter;
import de.unirostock.sems.xmlutils.alg.XyWeighter;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.exception.XmlDocumentConsistencyException;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * @author Martin Scharm
 * 
 */
@RunWith(JUnit4.class)
public class TestTreeDocument
{
	
	private static final File		SIMPLE_DOC	= new File ("test/simple.xml");
	private static final File		MATHML_DOC	= new File ("test/mathml.xml");
	private static final double	EPSILON			= 0.0001;

	private static TreeDocument mathmlFile;
	private static TreeDocument simpleFile;

	/**
	 * 
	 */
	@BeforeClass
	public static void readFiles ()
	{
		if (SIMPLE_DOC.canRead ())
		{
			try
			{
				simpleFile = new TreeDocument (XmlTools.readDocument (SIMPLE_DOC), SIMPLE_DOC.toURI ());
			}
			catch (Exception e)
			{
				LOGGER.error (e, "cannot read ", SIMPLE_DOC, " -> skipping tests");
			}
		}
		else
		{
			LOGGER.error ("cannot read ", SIMPLE_DOC, " -> skipping tests");
		}
		if (MATHML_DOC.canRead ())
		{
			try
			{
				mathmlFile = new TreeDocument (XmlTools.readDocument (MATHML_DOC), MATHML_DOC.toURI ());
			}
			catch (Exception e)
			{
				LOGGER.error (e, "cannot read ", MATHML_DOC, " -> skipping tests");
			}
		}
		else
		{
			LOGGER.error ("cannot read ", MATHML_DOC, " -> skipping tests");
		}
	}
	

	
	
	/**
	 * Test structure.
	 *
	 * @throws XmlDocumentConsistencyException the xml document consistency exception
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the JDOM exception
	 */
	@Test
	public void testStructure () throws XmlDocumentConsistencyException, XmlDocumentParseException, IOException, JDOMException
	{
		TreeDocument test = new TreeDocument (XmlTools.readDocument (SIMPLE_DOC), null, SIMPLE_DOC.toURI ());
		assertNotNull (test);
		test = new TreeDocument (XmlTools.readDocument (SIMPLE_DOC), new SemsWeighter (), SIMPLE_DOC.toURI ());
		assertNotNull (test);
		test = new TreeDocument (XmlTools.readDocument (SIMPLE_DOC), new XyWeighter (), SIMPLE_DOC.toURI (), true);
		assertNotNull (test);
		test = new TreeDocument (XmlTools.readDocument (SIMPLE_DOC), null, SIMPLE_DOC.toURI (), true);
		assertNotNull (test);
		test = new TreeDocument (XmlTools.readDocument (SIMPLE_DOC), SIMPLE_DOC.toURI (), true);
		assertNotNull (test);
		assertTrue (test.getBaseUri ().toString ().endsWith ("test/simple.xml"));
		
		assertEquals (2, test.getNodesByTag ("from").size ());
		assertEquals (0, test.getNodesByTag ("fr0m").size ());
		assertEquals (2, test.getOccurringIds ().size ());
		TreeNode[] subtrees = test.getSubtreesBySize ();
		assertNotNull (subtrees);
		for (int i = 1; i < subtrees.length; i++)
			assertTrue (subtrees[i-1].getWeight () >= subtrees[i].getWeight ());
		assertEquals (test.getNumNodes (), subtrees.length);
		assertNotNull (test.getNodesByHash (subtrees[0].getSubTreeHash ()));
		assertNull (test.getNodesByHash (subtrees[0].getOwnHash ()));
		// two are the same
		assertEquals (test.getNumNodes () - 2, test.getOccurringHashes ().size ());
		HashMap<String, Integer> map = new HashMap<String, Integer> ();
		test.getRoot ().getNodeStats (map);
		assertEquals (test.getNodeStats (), map);
		assertNotNull (test.dump ());
		assertTrue (test.dump ().length () > 20);
		assertNotNull (test.toString ());
		assertTrue (test.toString ().length () > 20);
		
		
	}
}
