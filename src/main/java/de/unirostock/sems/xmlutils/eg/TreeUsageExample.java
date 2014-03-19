/**
 * 
 */
package de.unirostock.sems.xmlutils.eg;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * @author Martin Scharm
 * 
 */
public class TreeUsageExample
{
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	public static void main (String[] args) throws XmlDocumentParseException, IOException, JDOMException
	{
		File file = new File ("test/simple.xml");
		TreeDocument document = new TreeDocument (XmlTools.readDocument (file),
			file.toURI ());
		System.out.println ("this is the document:");
		System.out.println ();
		System.out.println (DocumentTools.printPrettySubDoc (document.getRoot ()));
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
		System.out.println ();
		
		System.out.println ("There are " + document.getNumNodes ()
			+ " nodes in this tree");
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
		System.out.println ();
		
		// get all subtrees (i.e. nodes rooting these trees) ordered by size,
		// biggest first:
		TreeNode[] subTrees = document.getSubtreesBySize ();
		System.out.println ("path to subtrees ordered by size:");
		for (TreeNode node : subTrees)
			System.out.println ("\t" + node.getXPath ());
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
		System.out.println ();
		
		// get the root of the tree
		DocumentNode root = document.getRoot ();
		System.out.println ("tag name of root is: " + root.getTagName ());
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
		System.out.println ();
		
		// get the node with id="messagetwo"
		// and print the number of nodes below this node
		DocumentNode semsNode = document.getNodeById ("messagetwo");
		System.out.println ("#nodes below messagetwo-node: "
			+ semsNode.getSizeSubtree ());
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
		System.out.println ();
		
		// get the first node having a tag name of "example"
		// and print the level of its parent
		DocumentNode node = document.getNodesByTag ("content").get (0);
		System.out.println ("level of parent of first <content> node: "
			+ node.getParent ().getLevel ());
		System.out.println ("The whole subtree below this <content> node:");
		System.out.println (DocumentTools.printPrettySubDoc (node));
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
		System.out.println ();
		
		// compare two trees
		TreeDocument document2 = new TreeDocument (XmlTools.readDocument (new File (
			"test/simple.xml")), null);
		// same document -> should be true
		System.out.println ("document2.equals (document) ? "
			+ (document2.equals (document)));
		
		TreeDocument document3 = new TreeDocument (XmlTools.readDocument (new File (
			"test/mathml.xml")), null);
		// different document -> most likely false
		System.out.println ("document3.equals (document) ? "
			+ (document3.equals (document)));
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
	}
	
}
