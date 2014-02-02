/**
 * 
 */
package de.unirostock.sems.xmlutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;

/**
 * @author Martin Scharm
 *
 */
@RunWith(JUnit4.class)
public class XmlTest
{
	private static final File SIMPLE_DOC = new File ("test/simple.xml");
	private static final File MATHML_DOC = new File ("test/mathml.xml");
	private static final double EPSILON = 0.0001;

	@Test
	public void testMathML ()
	{
		XmlTest.class.getResource ("/res/mmlctop2_0.xsl");
		
		if (!MATHML_DOC.canRead ())
		{
			LOGGER.error ("cannot read " + MATHML_DOC + " -> skipping test");
			return;
		}
		TreeDocument simpleDoc = null;
		try
		{
			simpleDoc = new TreeDocument (XmlTools.readDocument (MATHML_DOC), null);
		}
		catch (Exception e)
		{
			fail ("failed to parse " + MATHML_DOC);
		}
		
		
		
		
		try
		{
			String orig = DocumentTools.printSubDoc (simpleDoc.getRoot ());
			// test for typical content functions.
			assertTrue ("original mathml doesn't seem to be content mathml", orig.contains ("<apply>") && orig.contains ("<plus/>") && orig.contains ("<ci>"));
			
			String presentation = DocumentTools.transformMathML (simpleDoc.getRoot ());

			// test for non content mathml plus some typical presentation mathml stuff
			assertTrue ("converted mathml doesn't seem to be presentation mathml", !presentation.contains ("<apply>") && !presentation.contains ("<plus/>") && presentation.contains ("<mrow>") && presentation.contains ("<mi>"));
		}
		catch (TransformerException e)
		{
			fail ("wasn't able to transform mathml, got " + e);
		}
		
	}
	
	@Test
	public void testSimpleXml ()
	{
		if (!SIMPLE_DOC.canRead ())
		{
			LOGGER.error ("cannot read " + SIMPLE_DOC + " -> skipping test");
			return;
		}
		TreeDocument simpleDoc = null;
		try
		{
			simpleDoc = new TreeDocument (XmlTools.readDocument (SIMPLE_DOC), null);
		}
		catch (Exception e)
		{
			fail ("failed to parse " + SIMPLE_DOC);
		}
		
		assertTrue ("uniques are unique but doc reports they aren't", simpleDoc.uniqueIds ());
		
		DocumentNode root = simpleDoc.getRoot ();
		int numNodes = root.getSizeSubtree () + 1;
		assertEquals ("numNodes != 14", numNodes, 15);
		assertEquals ("numNodes != getNumNodes", numNodes, simpleDoc.getNumNodes ());
		assertEquals ("numNodes != num XPaths", numNodes, simpleDoc.getOccuringXPaths ().size ());
		
		double treeWeight = simpleDoc.getTreeWeight ();
		assertEquals ("treeWeight != root.getWeight ()", treeWeight, root.getWeight (), EPSILON);
		
		// let's loop through tree and count nodes
		int foundNodes = 0;
		// text nodes
		List<TextNode> textNodes = simpleDoc.getTextNodes ();
		assertEquals ("expected to find exactly 6 text nodes", 6, textNodes.size ());
		for (TextNode tn : textNodes)
		{
			foundNodes++;
			checkLvl (tn);
			//System.out.println (tn.getText () + " => " + tn.getWeight ());
		}
		
		// document nodes
		simpleDoc.getOccuringXPaths ();
		for (String tag : simpleDoc.getOccuringTags ())
		{
			List<DocumentNode> dn = simpleDoc.getNodesByTag (tag);
			assertTrue ("all tags but conversations should occure exactly two times!", tag.equals ("conversations") ? 1 == dn.size () : 2 == dn.size ());
			foundNodes += dn.size ();
			// check that weights are valid
			for (DocumentNode node : dn)
			{
				checkLvl (node);
				double weight = node.getWeight ();
				int size = node.getSizeSubtree ();
				if (node.getParent () == null)
				{
					assertEquals ("only 'conversations' is allowed to have no parent.." , "conversations", node.getTagName ());
				}
				else
				{
					// make sure our weight is less than parents weight
					assertTrue ("node's weight should be less than parent's weight", weight < node.getParent ().getWeight ());
					assertTrue ("node's subtree size should be less than parent's subtree size", size < node.getParent ().getSizeSubtree ());
				}
				
				// make sure sum children weight is less than our weight
				Vector<TreeNode> kids = node.getChildren ();
				double kWeight = 0;
				int kSize = 0;
				for (TreeNode kid : kids)
				{
					kWeight += kid.getWeight ();
					if (kid.getType () == TreeNode.DOC_NODE)
						kSize += ((DocumentNode) kid).getSizeSubtree () + 1;
					else
						kSize++;
				}
				
				assertTrue ("sum of kid's weights ("+kWeight+") should be less than our weight ("+weight+")", kWeight < weight);
				assertTrue ("sum of kid's subree sizes ("+kSize+") should be less than our subtree size ("+(size+1)+")", kSize < size + 1);
			}
		}

		assertEquals ("numNodes != foundNodes", numNodes, foundNodes);
		
		
		DocumentNode reply = simpleDoc.getNodeById ("messagetwo");
		assertNotNull ("reply shouldn't be null", reply);
		assertNotNull ("replies attribute shouldn't be null", reply.getAttribute ("replies"));
		assertNotNull ("replies target shouln't be null", simpleDoc.getNodeById (reply.getAttribute ("replies")));
		
		DocumentNode initialMessage = simpleDoc.getNodeById (reply.getAttribute ("replies"));
		assertNotNull ("content of initial message shouldn't be null", initialMessage.getChildrenWithTag ("content"));
		assertEquals ("there should be exactly one content", 1, initialMessage.getChildrenWithTag ("content").size ());
		DocumentNode content = (DocumentNode) initialMessage.getChildrenWithTag ("content").elementAt (0);
		
		assertEquals ("initial message is wrong", "keep workin'!", ((TextNode)content.getChildren ().elementAt (0)).getText ());
		
		// check lvls
		assertEquals ("content should be level 2", 2, content.getLevel ());
		assertEquals ("root should be level 0", 0, root.getLevel ());
		assertEquals ("initial message should be lvl 3", 3, ((TextNode)content.getChildren ().elementAt (0)).getLevel ());
	}
	
	public void checkLvl (TreeNode node)
	{
		int lvl = node.getLevel ();

		assertTrue ("lvl must not be negativ", lvl >= 0);
		assertTrue ("only root has lvl 0", lvl == 0 ? node.getParent () == null : node.getParent () != null);
		if (node.getParent () != null)
			checkLvl (node.getParent ());
	}
}
