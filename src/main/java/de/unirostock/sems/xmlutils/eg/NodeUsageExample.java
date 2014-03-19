/**
 * 
 */
package de.unirostock.sems.xmlutils.eg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.exception.XmlDocumentConsistencyException;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * @author Martin Scharm
 * 
 */
public class NodeUsageExample
{
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws FileNotFoundException
	 * @throws XmlDocumentParseException
	 * @throws XmlDocumentConsistencyException
	 * @throws JDOMException 
	 */
	public static void main (String[] args)
		throws XmlDocumentParseException,
			FileNotFoundException,
			ParserConfigurationException,
			SAXException,
			IOException,
			XmlDocumentConsistencyException, JDOMException
	{
		File file = new File ("test/simple.xml");
		TreeDocument document = new TreeDocument (XmlTools.readDocument (file),
			file.toURI ());
		
		// get root node
		DocumentNode root = document.getRoot ();
		List<TreeNode> firstLevel = root.getChildren ();
		System.out.println ("There are " + firstLevel.size () + " children in "
			+ root.getXPath () + " :");
		for (TreeNode kid : firstLevel)
			System.out.println ("\t" + kid.getXPath () + " having "
				+ ((DocumentNode) kid).getNumLeaves () + " leaves and a weight of "
				+ kid.getWeight ());
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
		System.out.println ();
		
		// get first message node
		DocumentNode message = document.getNodeById ("messageone");
		// you can also get access to the same node using it's path:
		TreeNode sameNode = document.getNodeByPath (message.getXPath ());
		// let's test if it's really the same:
		System.out.println ("found same node by id and by XPath? "
			+ (sameNode == message));
		// you can also get this node by it's signature (here i know it's the first
		// node having this hash value)
		sameNode = document.getNodesByHash (message.getSubTreeHash ()).get (0);
		// test:
		System.out.println ("found same node by id and by hash? "
			+ (sameNode == message));
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
		System.out.println ();
		
		// let's print some information about this node
		System.out.println ("Path to the message node: " + message.getXPath ());
		System.out.println ("Path to its parent: "
			+ message.getParent ().getXPath ());
		System.out.println ("Weight of the message node: " + message.getWeight ());
		System.out.println ("Signature of the message node: "
			+ message.getOwnHash ());
		System.out.println ("Signature of the subtree rooted in the message node: "
			+ message.getSubTreeHash ());
		System.out.println ("#number nodes in its subtree: "
			+ (message.getSizeSubtree () + 1));
		System.out.println ("number of direct children: "
			+ message.getNumChildren ());
		System.out.println ("id of the node: " + message.getId ());
		System.out.println ("tag name: " + message.getTagName ());
		System.out.println ("attributes in this node:");
		for (String attr : message.getAttributes ())
			System.out.println ("\t" + attr + " => " + message.getAttributeValue (attr));
		System.out.println ();
		System.out.println ("--- 8< --- 8< --- 8< --- 8< --- 8< --- 8< ---");
		System.out.println ();
		
		// remove the node from the tree
		System.out.println ("# nodes in document before remove: "
			+ document.getNumNodes ());
		DocumentNode parent = message.getParent ();
		parent.rmChild (message);
		System.out.println ("# nodes in document after remove: "
			+ document.getNumNodes ());
		
		// and reinsert it
		parent.addChild (message);
		System.out.println ("# nodes in document after reinsert: "
			+ document.getNumNodes ());
		
		// note how the path ot the node has changed
		System.out.println ("New path to the message node: " + message.getXPath ());
		// but everything else is still the same
		System.out.println ("Weight of the message node: " + message.getWeight ());
		System.out.println ("Signature of the subtree rooted in the message node: "
			+ message.getSubTreeHash ());
	}
	
}
