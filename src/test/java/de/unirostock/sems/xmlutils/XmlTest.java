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

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
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
public class XmlTest
{
	
	private static final File		SIMPLE_DOC	= new File ("test/simple.xml");
	private static final File		MATHML_DOC	= new File ("test/mathml.xml");
	private static final double	EPSILON			= 0.0001;

	private static TreeDocument mathmlFile;
	private static TreeDocument simpleFile;

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
				LOGGER.error ("cannot read " + SIMPLE_DOC + " -> skipping tests", e);
			}
		}
		else
		{
			LOGGER.error ("cannot read " + SIMPLE_DOC + " -> skipping tests");
		}
		if (MATHML_DOC.canRead ())
		{
			try
			{
				mathmlFile = new TreeDocument (XmlTools.readDocument (MATHML_DOC), MATHML_DOC.toURI ());
			}
			catch (Exception e)
			{
				LOGGER.error ("cannot read " + MATHML_DOC + " -> skipping tests", e);
			}
		}
		else
		{
			LOGGER.error ("cannot read " + MATHML_DOC + " -> skipping tests");
		}
	}
	
	@Test
	public void testDocReadWrite ()
	{
		String prettyDocument = DocumentTools.printPrettySubDoc (simpleFile.getRoot ());
		try
		{
			TreeDocument test = new TreeDocument (XmlTools.readDocument (prettyDocument), null);
			assertTrue (test.equals (simpleFile));
			String uglyDocument = DocumentTools.printSubDoc (test.getRoot ());
			assertFalse ("ubly and pretty export should result in different output", prettyDocument.equals (uglyDocument));
			test = new TreeDocument (XmlTools.readDocument (uglyDocument), null);
			assertTrue (test.equals (simpleFile));
			
		}
		catch (Exception e)
		{
			fail ("unexpected error reading exported document" + e);
		}
	}

	@Test
	public void testDocEquals ()
	{
		// lets test whether equals is correct
		String document = DocumentTools.printSubDoc (simpleFile.getRoot ());
		try
		{
			TreeDocument test = new TreeDocument (XmlTools.readDocument (document), null);
			assertTrue (test.equals (simpleFile));
			
			TreeNode tn = test.getNodeByPath ("/conversations[1]/message[1]/from[1]/text()[1]");
			assertNotNull ("node with path '/conversations[1]/message[1]/from[1]/text()[1]' is suppossed to be non-null", tn);
			TextNode text = (TextNode) tn;
			text.setText ("Ron");
			
			assertFalse (test.equals (simpleFile));
		}
		catch (Exception e)
		{
			fail ("unexpected error reading exported document" + e);
		}
	}
	
	
	@Test
	public void testNodeDistances ()
	{
		Random rand = new SecureRandom ();
		
		List<NodeDistance> distances = new ArrayList<NodeDistance> ();
		for (int i = 0; i < 10; i++)
			distances.add (new NodeDistance (null, null, rand.nextDouble ()));
		
		// ascending?
		Collections.sort (distances, new NodeDistanceComparator ());
		for (int i = 1; i < distances.size (); i++)
		{
			double prev = distances.get (i - 1).distance;
			double cur = distances.get (i).distance;
			assertTrue ("getSubtreesBySize isn't sorted ascending: " + prev + ">"
				+ cur, cur >= prev);
		}
		
		// descending
		Collections.sort (distances, new NodeDistanceComparator (true));
		for (int i = 1; i < distances.size (); i++)
		{
			double prev = distances.get (i - 1).distance;
			double cur = distances.get (i).distance;
			assertTrue ("getSubtreesBySize isn't sorted ascending: " + prev + ">"
				+ cur, cur <= prev);
		}
	}
	
	
	public int getSize (TreeNode n)
	{
		if (n.getType () == TreeNode.DOC_NODE)
			return ((DocumentNode) n).getSizeSubtree () + 1;
		return 1;
	}
	
	
	@Test
	public void testTreeNodeComparatorBySubtreeSize ()
	{
		if (simpleFile == null)
		{
			LOGGER.error ("cannot read simpleFile -> skipping test");
			return;
		}
		
		TreeNode[] subTrees = simpleFile.getSubtreesBySize ();
		DocumentNode root = simpleFile.getRoot ();
		assertEquals ("num subtrees doesn't equal number of nodes",
			root.getSizeSubtree () + 1, subTrees.length);
		
		for (int i = 1; i < subTrees.length; i++)
		{
			int prevSize = getSize (subTrees[i - 1]);
			int curSize = getSize (subTrees[i]);
			assertTrue (
				"getSubtreesBySize isn't sorted: " + prevSize + "<" + curSize,
				curSize <= prevSize);
		}
	}
	
	
	@Test
	public void testMathML ()
	{
		if (mathmlFile == null)
		{
			LOGGER.error ("cannot read mathmlFile -> skipping test");
			return;
		}
		
		assertNotNull ("cannot find mathml converter xsl", XmlTest.class.getResource ("/res/mmlctop2_0.xsl"));
		
		try
		{
			String orig = DocumentTools.printSubDoc (mathmlFile.getRoot ());
			// test for typical content functions.
			assertTrue (
				"original mathml doesn't seem to be content mathml",
				orig.contains ("<apply>") && orig.contains ("<plus/>")
					&& orig.contains ("<ci>"));
			
			String presentation = DocumentTools
				.transformMathML (mathmlFile.getRoot ());
			
			// test for non content mathml plus some typical presentation mathml stuff
			assertTrue (
				"converted mathml doesn't seem to be presentation mathml",
				!presentation.contains ("<apply>")
					&& !presentation.contains ("<plus/>")
					&& presentation.contains ("<mrow>") && presentation.contains ("<mi>"));
		}
		catch (TransformerException e)
		{
			fail ("wasn't able to transform mathml, got " + e);
		}
		
	}
	
	
	@Test
	public void testSimpleXml ()
	{
		if (simpleFile == null)
		{
			LOGGER.error ("cannot read simpleFile -> skipping test");
			return;
		}
		
		assertTrue ("uniques are unique but doc reports they aren't",
			simpleFile.uniqueIds ());
		
		DocumentNode root = simpleFile.getRoot ();
		int numNodes = root.getSizeSubtree () + 1;
		assertEquals ("numNodes != 15", numNodes, 15);
		assertEquals ("numNodes != getNumNodes", numNodes, simpleFile.getNumNodes ());
		assertEquals ("numNodes != num XPaths", numNodes, simpleFile
			.getOccurringXPaths ().size ());
		
		double treeWeight = simpleFile.getTreeWeight ();
		assertEquals ("treeWeight != root.getWeight ()", treeWeight,
			root.getWeight (), EPSILON);
		
		// let's loop through tree and count nodes
		int foundNodes = 0;
		// text nodes
		List<TextNode> textNodes = simpleFile.getTextNodes ();
		assertEquals ("expected to find exactly 6 text nodes", 6, textNodes.size ());
		for (TextNode tn : textNodes)
		{
			foundNodes++;
			testNodeStuff (tn);
			// System.out.println (tn.getText () + " => " + tn.getWeight ());
		}
		
		// document nodes
		simpleFile.getOccurringXPaths ();
		for (String tag : simpleFile.getOccurringTags ())
		{
			List<DocumentNode> dn = simpleFile.getNodesByTag (tag);
			assertTrue (
				"all tags but conversations should occure exactly two times!",
				tag.equals ("conversations") ? 1 == dn.size () : 2 == dn.size ());
			foundNodes += dn.size ();
			// check that weights are valid
			for (DocumentNode node : dn)
			{
				testNodeStuff (node);
				double weight = node.getWeight ();
				int size = node.getSizeSubtree ();
				if (node.getParent () == null)
				{
					assertEquals ("only 'conversations' is allowed to have no parent..",
						"conversations", node.getTagName ());
				}
				else
				{
					// make sure our weight is less than parents weight
					assertTrue ("node's weight should be less than parent's weight",
						weight < node.getParent ().getWeight ());
					assertTrue (
						"node's subtree size should be less than parent's subtree size",
						size < node.getParent ().getSizeSubtree ());
				}
				
				// make sure sum children weight is less than our weight
				List<TreeNode> kids = node.getChildren ();
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
				
				assertTrue ("sum of kid's weights (" + kWeight
					+ ") should be less than our weight (" + weight + ")",
					kWeight < weight);
				assertTrue ("sum of kid's subree sizes (" + kSize
					+ ") should be less than our subtree size (" + (size + 1) + ")",
					kSize < size + 1);
			}
		}
		
		assertEquals ("numNodes != foundNodes", numNodes, foundNodes);
	}
	
	@Test
	public void testIdAndMapper ()
	{
		if (simpleFile == null)
		{
			LOGGER.error ("cannot read simpleFile -> skipping test");
			return;
		}
		
		DocumentNode root = simpleFile.getRoot ();
		
		DocumentNode reply = simpleFile.getNodeById ("messagetwo");
		testNodeStuff (reply);
		assertNotNull ("reply shouldn't be null", reply);
		assertNotNull ("replies attribute shouldn't be null",
			reply.getAttribute ("replies"));
		assertNotNull ("replies target shouln't be null",
			simpleFile.getNodeById (reply.getAttribute ("replies")));
		
		DocumentNode initialMessage = simpleFile.getNodeById (reply
			.getAttribute ("replies"));
		testNodeStuff (initialMessage);
		assertNotNull ("content of initial message shouldn't be null",
			initialMessage.getChildrenWithTag ("content"));
		assertEquals ("there should be exactly one content", 1, initialMessage
			.getChildrenWithTag ("content").size ());
		DocumentNode content = (DocumentNode) initialMessage.getChildrenWithTag (
			"content").get (0);
		
		assertEquals ("initial message is wrong", "keep workin'!",
			((TextNode) content.getChildren ().get (0)).getText ());
		
		// check lvls
		assertEquals ("content should be level 2", 2, content.getLevel ());
		assertEquals ("root should be level 0", 0, root.getLevel ());
		assertEquals ("initial message should be lvl 3", 3, ((TextNode) content
			.getChildren ().get (0)).getLevel ());
	}
	
	@Test
	public void testDiffersAndHashesAndNodeStats ()
	{
		if (simpleFile == null)
		{
			LOGGER.error ("cannot read simpleFile -> skipping test");
			return;
		}
		
		DocumentNode reply = simpleFile.getNodeById ("messagetwo");
		DocumentNode initialMessage = simpleFile.getNodeById (reply
			.getAttribute ("replies"));
		// test content differs and hashes and weights
		List<DocumentNode> nodes = simpleFile.getNodesByTag ("message");
		assertEquals ("expected to find exactly two message nodes", 2, nodes.size ());
		assertTrue ("content between two different nodes should differ", nodes.get (0).contentDiffers (nodes.get (1)));
		assertFalse ("content of the same node shouldn't differ", nodes.get (0).contentDiffers (nodes.get (0)));
		
		nodes = simpleFile.getNodesByTag ("to");
		assertEquals ("expected to find exactly two <to> nodes", 2, nodes.size ());
		assertFalse ("content between the two <to> nodes shouldn't differ", nodes.get (0).contentDiffers (nodes.get (1)));

		List<TreeNode> initTo = initialMessage.getChildrenWithTag ("to");
		List<TreeNode> initFrom = initialMessage.getChildrenWithTag ("from");
		List<TreeNode> replyFrom = reply.getChildrenWithTag ("from");
		assertTrue ("initial and reply are supposed to have exactly one <to>/<two>", initTo.size () == replyFrom.size () && initTo.size () == 1);
		TreeNode ito = initTo.get (0);
		TreeNode ifrom = initFrom.get (0);
		TreeNode rfrom = replyFrom.get (0);
		assertTrue ("content of the nodes w/ different tag names should differ", ito.contentDiffers (rfrom));
		assertFalse ("content of the nodes w/ same tag names should differ", ifrom.contentDiffers (rfrom));
		DocumentNode dito = (DocumentNode) ito;
		DocumentNode difrom = (DocumentNode) ifrom;
		DocumentNode drfrom = (DocumentNode) rfrom;
		assertEquals ("nodes should have exactly one child", 1, dito.getNumChildren ());
		assertEquals ("nodes should have exactly one child", 1, difrom.getNumChildren ());
		assertEquals ("nodes should have exactly one child", 1, drfrom.getNumChildren ());
		// check text nodes
		assertFalse ("content of the text nodes w/ same text shouldn't differ", dito.getChildren ().get (0).contentDiffers (drfrom.getChildren ().get (0)));
		assertTrue ("content of the text nodes w/ different text should differ", difrom.getChildren ().get (0).contentDiffers (drfrom.getChildren ().get (0)));
		// check different node types
		assertTrue ("content of the different node types should differ", dito.getChildren ().get (0).contentDiffers (drfrom));
		// check weights
		assertEquals ("weights of the similar subtrees (only different tag names) should be equal", ito.getWeight (), rfrom.getWeight (), EPSILON);
		assertEquals ("weights of the similar subtrees (only different texts, but same length) should be equal", ifrom.getWeight (), rfrom.getWeight (), EPSILON);
		nodes = simpleFile.getNodesByTag ("message");
		assertTrue ("weights of the different subtrees should differ", Math.abs (nodes.get (0).getWeight () - nodes.get (1).getWeight ()) > EPSILON);
		
		// check node stats
		HashMap<String, Integer> stats0 = new HashMap<String, Integer> ();
		HashMap<String, Integer> stats1 = new HashMap<String, Integer> ();
		nodes.get (0).getNodeStats (stats0);
		nodes.get (1).getNodeStats (stats1);
		// stats should equal
		for (String s : stats0.keySet ())
		{
			assertNotNull ("stats key " + s + " in 1 shouldn't be null", stats1.get (s));
			assertEquals ("stats key " + s + " in 1 should equal 0", stats1.get (s), stats0.get (s));
		}
		// and the other way around
		for (String s : stats1.keySet ())
		{
			assertNotNull ("stats key " + s + " in 0 shouldn't be null", stats0.get (s));
			assertEquals ("stats key " + s + " in 1 should equal 0", stats1.get (s), stats0.get (s));
		}
		
		// test node hashes
		DocumentNode dummy1 = dito.extract ();
		DocumentNode dummy2 = dito.extract ();
		// make sure both are the same so far
		assertTrue ("hashes of original and copy differs", dummy1.getSubTreeHash ().equals (dito.getSubTreeHash ()));
		assertTrue ("hashes of identical copies differs", dummy1.getSubTreeHash ().equals (dummy2.getSubTreeHash ()));
		// modify node1
		dummy1.setAttribute ("smells", "abc");
		dummy1.setAttribute ("like", "abc");
		dummy1.setAttribute ("garlic", "abc");
		dummy1.setAttribute ("attribute", "abc");
		dummy1.setAttribute ("node", "abc");
		dummy1.setAttribute ("z", "abc");
		dummy1.setAttribute ("whatever", "");
		// make sure they now differ
		assertFalse ("hashes of original and modified copy should differ", dummy1.getSubTreeHash ().equals (dito.getSubTreeHash ()));
		assertFalse ("hashes of copies should differs", dummy1.getSubTreeHash ().equals (dummy2.getSubTreeHash ()));
		// modify node2
		dummy2.setAttribute ("z", "abc");
		dummy2.setAttribute ("node", "abc");
		dummy2.setAttribute ("smells", "abc");
		dummy2.setAttribute ("like", "abc");
		dummy2.setAttribute ("garlic", "abc");
		dummy2.setAttribute ("whatever", "");
		dummy2.setAttribute ("attribute", "abc");
		assertFalse ("hashes of original and modified copy should differ", dummy1.getSubTreeHash ().equals (dito.getSubTreeHash ()));
		assertTrue ("hashes of identical copies differs", dummy1.getSubTreeHash ().equals (dummy2.getSubTreeHash ()));
		
	}
	
	@Test
	public void testExtract ()
	{
		// need another file just to destroy it
		TreeDocument simpleFile2 = null;
		if (SIMPLE_DOC.canRead ())
		{
			try
			{
				simpleFile2 = new TreeDocument (XmlTools.readDocument (SIMPLE_DOC), SIMPLE_DOC.toURI ());
			}
			catch (Exception e)
			{
				fail ("cannot read " + SIMPLE_DOC + " -> skipping tests" + e);
			}
		}
		else
		{
			fail ("cannot read " + SIMPLE_DOC + " -> skipping tests");
		}

		String xPath = "/conversations[1]/to[1]";
		
		int prevXPaths = simpleFile2.getOccurringXPaths ().size ();
		int prevNumNodes = simpleFile2.getNumNodes ();
		double prevWeight = simpleFile2.getTreeWeight ();
		assertNull ("whoops? where does this node come from? " + xPath, simpleFile2.getNodeByPath (xPath));
		
		// first <to> node
		DocumentNode to = simpleFile2.getNodesByTag ("to").get (0);
		DocumentNode toCopy = to.extract ();
		assertEquals ("expected to extract a subtree of size 2", 2, 1 + toCopy.getSizeSubtree ());
		
		// append it to document
		simpleFile2.getRoot ().addChild (toCopy);
		
		//System.out.println (DocumentTools.printPrettySubDoc (simpleFile2.getRoot ()));
		assertTrue ("expected more xpaths after adding a subtree", prevXPaths < simpleFile2.getOccurringXPaths ().size ());
		assertTrue ("expected more nodes after adding a subtree", prevNumNodes < simpleFile2.getNumNodes ());
		assertTrue ("expected a larger weight after adding a subtree", prevWeight < simpleFile2.getTreeWeight ());
		assertNotNull ("expected to find a node in " + xPath, simpleFile2.getNodeByPath (xPath));
		
	}
	
	@Test
	public void testRemoveAndInsert ()
	{
		if (simpleFile == null)
		{
			LOGGER.error ("cannot read simpleFile -> skipping test");
			return;
		}

		DocumentNode root = simpleFile.getRoot ();
		List<DocumentNode> nodes = simpleFile.getNodesByTag ("message");
		
		// check the remove and insert things to compute new hashes
		DocumentNode extract = nodes.get (0);
		// store node stuff
		String extractPrevXpath = extract.getXPath ();
		double extractPrevWeight = extract.getWeight ();
		String extractPrevHash = extract.getOwnHash ();
		String extractPrevTreeHash = extract.getSubTreeHash ();
		// store tree stuff
		String treePrevXpath = root.getXPath ();
		double treePrevWeight = root.getWeight ();
		String treePrevHash = root.getOwnHash ();
		String treePrevTreeHash = root.getSubTreeHash ();
		int treePrevNumXpaths = simpleFile.getOccurringXPaths ().size ();
		boolean uniqueIds = simpleFile.uniqueIds ();
		// remove node
		try
		{
			root.rmChild (extract);
		}
		catch (XmlDocumentConsistencyException e)
		{
			fail ("we shouldn't get an exception here..");
		}
		// reinsert it
		root.addChild (extract);
		// check that everything was updated correctly
		assertFalse ("xpath wasn't updated after remove + insert", extractPrevXpath.equals (extract.getXPath ()));
		assertEquals ("weight was updated after remove + insert", extractPrevWeight, extract.getWeight (), EPSILON);
		assertTrue ("own hash was updated after remove + insert", extractPrevHash.equals (extract.getOwnHash ()));
		assertTrue ("subtree hash was updated after remove + insert", extractPrevTreeHash.equals (extract.getSubTreeHash ()));

		assertEquals ("xpath was updated after remove + insert", treePrevXpath, root.getXPath ());
		assertEquals ("weight was updated after remove + insert", treePrevWeight, root.getWeight (), EPSILON);
		assertTrue ("own hash was updated after remove + insert", treePrevHash.equals (root.getOwnHash ()));
		assertFalse ("subtree hash wasn't updated after remove + insert", treePrevTreeHash.equals (root.getSubTreeHash ()));

		assertTrue ("num xpaths has changed after remove + insert", treePrevNumXpaths == simpleFile.getOccurringXPaths ().size ());
		
		assertTrue ("uniqueIds has changed after remove + insert", uniqueIds == simpleFile.uniqueIds ());
		
		// check children map in root
		assertTrue ("children tag map in root is apparently broken", root.getChildrenWithTag (extract.getTagName ()).get (1) == extract);
	}
	
	public void testNodeStuff (TextNode node)
	{
		// test lvl
		int lvl = node.getLevel ();
		
		assertTrue ("lvl must not be negativ", lvl >= 0);
		assertTrue ("only root has lvl 0", lvl == 0 ? node.getParent () == null
			: node.getParent () != null);
		if (node.getParent () != null)
			testNodeStuff (node.getParent ());
	}
	
	public void testNodeStuff (DocumentNode node)
	{
		// make sure attributes are in correct order
		String prev = null;
		for (String attr : node.getAttributes ())
		{
			if (prev != null)
				assertTrue ("attributes aren't sorted", prev.compareTo (attr) < 0);
			prev = attr;
		}

		// test lvl
		int lvl = node.getLevel ();
		
		assertTrue ("lvl must not be negativ", lvl >= 0);
		assertTrue ("only root has lvl 0", lvl == 0 ? node.getParent () == null
			: node.getParent () != null);
		if (node.getParent () != null)
			testNodeStuff (node.getParent ());
	}
}
