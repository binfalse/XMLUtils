/**
 * 
 */
package de.unirostock.sems.xmlutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.transform.TransformerException;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.NodeDistance;
import de.unirostock.sems.xmlutils.ds.NodeDistanceComparator;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.exception.XmlDocumentConsistencyException;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * @author Martin Scharm
 * 
 */
@RunWith(JUnit4.class)
public class TestNodes
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
	 * @throws XmlDocumentConsistencyException 
	 * 
	 */
	@Test
	public void testStructure () throws XmlDocumentConsistencyException
	{
		TreeDocument copy = new TreeDocument (simpleFile);
		assertTrue (copy.equals (simpleFile));

		DocumentNode from = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[1]/from[1]");
		DocumentNode to = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[1]/to[1]");
		DocumentNode message = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[1]");
		DocumentNode conv = (DocumentNode) copy.getNodeByPath ("/conversations[1]");
		
		assertTrue ("wrong root", conv.isRoot ());
		assertFalse ("wrong root", from.isRoot ());
		assertEquals ("wrong document", copy, from.getDocument ());
		assertTrue ("wrong document", copy == from.getDocument ());
		assertFalse ("wrong document", simpleFile == from.getDocument ());
		
		assertTrue ("expected from to be below message", from.isBelow (message));
		assertTrue ("expected to to be below message", to.isBelow (message));
		assertFalse ("did not expected to to be below from", to.isBelow (from));
		assertTrue ("expected from to be below converstation", from.isBelow (conv));

		DocumentNode from2 = (DocumentNode) simpleFile.getNodeByPath ("/conversations[1]/message[1]/from[1]");
		assertFalse ("did not expected from to be below conversation in wrong tree", from2.isBelow (conv));

		DocumentNode msg2 = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[2]");
		assertEquals ("wrong child number", 1, message.getNoOfChild (from));
		assertEquals ("wrong child number", 2, message.getNoOfChild (to));
		assertEquals ("wrong child number", -1, conv.getNoOfChild (from));
		assertEquals ("wrong child number", -1, message.getNoOfChild (from2));
		assertEquals ("wrong child number", 2, conv.getNoOfChild (msg2));
		
		assertEquals ("wrong number of children", 3, message.getNumChildren ());
		assertEquals ("wrong number of children", 3, msg2.getNumChildren ());
		msg2.rmChild ((DocumentNode) msg2.getChildrenWithTag ("from").get (0));
		assertEquals ("wrong number of children", 2, msg2.getNumChildren ());
		msg2.rmChild (from2);
		assertEquals ("wrong number of children", 2, msg2.getNumChildren ());
		message.rmChild (conv);
		assertEquals ("wrong number of children", 2, msg2.getNumChildren ());
		
		assertNotNull (from.dump ("  "));
		assertNotNull (from.toString ());
		HashMap<String, Integer> map = new HashMap<String, Integer> ();
		copy.getRoot ().getNodeStats (map);
		//System.out.println (map);
		assertEquals ("node stats map seems to be incorrect", 6, map.size ());
		int count = 0;
		for (String tag : map.keySet ())
			count += map.get (tag);
		assertEquals ("wrong number of nodes", copy.getNumNodes (), count);
		
		assertEquals ("node shouldn't be modified", 0, from.getModification ());
	}
	

	@Test
	public void testMod ()
	{
		TreeDocument copy = new TreeDocument (simpleFile);
		assertTrue (copy.equals (simpleFile));
		
		TreeNode tn1 = copy.getNodeByPath ("/conversations[1]/message[1]");
		
		assertEquals ("shouldn't be modified by default", 0, tn1.getModification ());
		tn1.addModification (TreeNode.SUB_MODIFIED);
		assertEquals ("expected different modification", TreeNode.SUB_MODIFIED, tn1.getModification ());
		tn1.addModification (TreeNode.SUB_MODIFIED);
		assertEquals ("expected different modification", TreeNode.SUB_MODIFIED, tn1.getModification ());
		tn1.addModification (TreeNode.MOVED);
		assertEquals ("expected different modification", TreeNode.SUB_MODIFIED + TreeNode.MOVED, tn1.getModification ());
		assertTrue ("expected different modification", tn1.hasModification (TreeNode.MOVED));
		assertTrue ("expected different modification", tn1.hasModification (TreeNode.SUB_MODIFIED));
		tn1.rmModification (TreeNode.KIDSSWAPPED);
		assertEquals ("expected different modification", TreeNode.SUB_MODIFIED + TreeNode.MOVED, tn1.getModification ());
		assertTrue ("expected different modification", tn1.hasModification (TreeNode.MOVED));
		assertTrue ("expected different modification", tn1.hasModification (TreeNode.SUB_MODIFIED));
		assertFalse ("expected different modification", tn1.hasModification (TreeNode.KIDSSWAPPED));
		tn1.rmModification (TreeNode.MOVED);
		assertEquals ("expected different modification", TreeNode.SUB_MODIFIED, tn1.getModification ());
		tn1.setModification (TreeNode.KIDSSWAPPED);
		assertEquals ("expected different modification", TreeNode.KIDSSWAPPED, tn1.getModification ());
		assertTrue ("expected different modification", tn1.hasModification (TreeNode.KIDSSWAPPED));
		tn1.resetModifications ();
		assertFalse ("expected different modification", tn1.hasModification (TreeNode.KIDSSWAPPED));
		assertEquals ("expected different modification", 0, tn1.getModification ());
		tn1.addModification (TreeNode.MOVED);
		tn1.addModification (TreeNode.SUB_MODIFIED);
		assertEquals ("expected different modification", TreeNode.SUB_MODIFIED + TreeNode.MOVED, tn1.getModification ());
		copy.resetAllModifications ();
		assertEquals ("expected different modification", 0, tn1.getModification ());
		

		DocumentNode n1 = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[1]/from[1]");
		DocumentNode n2 = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[1]/to[1]");
		
		TextNode text1 = (TextNode) n1.getChildren ().get (0);
		TextNode text2 = (TextNode) n2.getChildren ().get (0);
		

		assertEquals ("wrong tag name", "text()", text1.getTagName ());
		assertFalse ("same content does not differ", text2.contentDiffers (text2));
		assertTrue ("different content differs", text1.contentDiffers (text2));
		assertTrue ("expected some text distance", 0.5 < text1.getTextDistance (text2));
	}
	
	
	/**
	 * 
	 */
	@Test
	public void testChildrenTags ()
	{
		TreeDocument copy = new TreeDocument (simpleFile);
		assertTrue (copy.equals (simpleFile));
		
		DocumentNode tn1 = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[1]");
		HashMap<String, List<TreeNode>> tagMap = tn1.getChildrenTagMap ();
		assertEquals ("expected 3 different child tags", 3, tagMap.size ());
		
		for (String tag : tagMap.keySet ())
			assertEquals ("expected 1 child for tag " + tag, 1, tagMap.get (tag).size ());
		
		List<TreeNode> nodes = tn1.getChildrenWithTag ("from");
		assertEquals ("expected 1 child for tag from", 1, nodes.size ());
		nodes = tn1.getChildrenWithTag ("to");
		assertEquals ("expected 1 child for tag to", 1, nodes.size ());
		nodes = tn1.getChildrenWithTag ("nonexistent");
		assertEquals ("expected 0 children for nonexistent tag", 0, nodes.size ());
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void testAttributes ()
	{
		TreeDocument copy = new TreeDocument (simpleFile);
		assertTrue (copy.equals (simpleFile));
		
		DocumentNode tn1 = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[1]/from[1]");

		tn1.setAttribute ("attr1", "val1");
		assertEquals ("unexpected attribute value", tn1.getAttributeValue ("attr1"), "val1");
		assertNull ("unexpected attribute", tn1.getAttributeValue ("attr2"));
		
		Attribute a = new Attribute ("attr2", "val1");
		tn1.setAttribute (a);
		assertEquals (tn1.getAttributeValue ("attr2"), "val1");
		assertEquals (tn1.getAttributeValue ("attr2", ""), "val1");
		assertNotNull (tn1.dump ("  "));
		assertNotNull (tn1.toString ());
		
		a = tn1.getAttribute ("attr2");
		assertNotNull (a);
		assertEquals (a.getValue (), "val1");
		
		a = tn1.getAttribute ("attr3");
		assertNull (a);
		
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void testAttributeDist ()
	{
		TreeDocument copy = new TreeDocument (simpleFile);
		assertTrue (copy.equals (simpleFile));
		
		DocumentNode tn1 = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[1]/from[1]");
		DocumentNode tn2 = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[1]/from[1]");
		
		assertEquals ("attribute distance should be 0 for identical nodes", 0, tn1.getAttributeDistance (tn2), 0.00001);

		tn2 = (DocumentNode) copy.getNodeByPath ("/conversations[1]/message[2]/from[1]");
		assertEquals ("attribute distance should be 0 for nodes not having attributes", 0, tn1.getAttributeDistance (tn2), 0.00001);

		// add some attributes
		tn2.setAttribute ("attr1", "val1");
		assertEquals ("attribute distance should be 1 for nodes not nothing in common", 1, tn1.getAttributeDistance (tn2), 0.00001);

		// same attribute in other node
		tn1.setAttribute ("attr1", "val1");
		assertEquals ("attribute distance should be 0 for nodes having all attributes in common", 0, tn1.getAttributeDistance (tn2), 0.00001);

		// annother attribute
		tn2.setAttribute ("attr2", "val1");
		assertEquals ("attribute distance should be 0.33 for nodes having 1 common 1 diff", 0.333333, tn1.getAttributeDistance (tn2), 0.00001);

		// annother attribute
		tn1.setAttribute ("attr2", "val1");
		assertEquals ("attribute distance should be 0 for nodes having all attributes in common", 0, tn1.getAttributeDistance (tn2), 0.00001);

		// annother attribute
		tn1.setAttribute ("attr2", "val2");
		assertEquals ("attribute distance should be 0.5 for nodes having 1 common 2 diff", 0.5, tn1.getAttributeDistance (tn2), 0.00001);

		// name matters
		tn1.setAttribute ("attr2", "val1");
		tn1.setAttribute ("name", "some name");
		assertEquals ("attribute distance should be 0.2 for nodes having 2 common 1 diff = 1/5", 0.2, tn1.getAttributeDistance (tn2), 0.00001);
		tn2.setAttribute ("name", "some name");
		assertEquals ("attribute distance should be 0 for nodes having all attributes in common", 0, tn1.getAttributeDistance (tn2), 0.00001);
		
		
		
		final DocumentNode TN1 = tn1;
		final DocumentNode TN2 = tn2;
		
		
		class expected
		{
			double check (String a, String b)
			{
				TN1.setAttribute ("name", a);
				TN2.setAttribute ("name", b);
				return TN1.getAttributeDistance (TN2);
			}
		}
		expected e = new expected ();
		
		assertEquals (
			"attribute distance should be 0 for nodes having with same name",
			0,
			e.check ("some name", "some name"),
			0.00001);
		
		
		assertTrue (
			"attribute distance should be less than 2/(#attr) for levenshtein distance of 1",
			1./(double)TN1.getAttributes ().size () > 
			e.check ("some name", "s0me name"));
		
		
		assertTrue (
			"attribute distance should be less than 2/(#attr) for similar strings",
			1./(double)TN1.getAttributes ().size () > 
			e.check ("some", "s0me"));

		assertTrue (
			"attribute distance should be bigger than 2/(#attr) for unsimilar strings",
			1./(double)TN1.getAttributes ().size () < 
			e.check ("some", "s0m3"));
		
		assertTrue (
			"attribute distance should be very small for very similar strings",
			0.02 > 
			// that is a typo
			e.check ("Maltoheptaose_C42H72O56", "Maltoheptaose_C42H72O36"));
		
		assertTrue (
			"attribute distance should be over thresh for non-typos",
			1./(double)TN1.getAttributes ().size () <
			// that is no typo
			e.check ("D-Mannose_C6H12O6", "L-Rhamnose_C6H12O5"));
		
		assertTrue (
			"attribute distance should be over thresh for non-typos",
			1./(double)TN1.getAttributes ().size () <
			// that is no typo
			e.check ("D-Glucosamine_C6H14NO5", "D-Mannosamine_C6H14NO5"));

		
		tn1 = (DocumentNode) simpleFile.getNodeByPath ("/conversations[1]/message[1]/from[1]");
		tn2 = (DocumentNode) simpleFile.getNodeByPath ("/conversations[1]/message[2]/from[1]");
		
		assertEquals ("original tree shouldn't have changed!", 0, tn1.getAttributes ().size ());
		assertEquals ("original tree shouldn't have changed!", 0, tn2.getAttributes ().size ());
	}
}
