package de.unirostock.sems.xmlutils.run.modules;

import java.util.HashMap;
import java.util.Set;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.run.XMLTool;


/**
 * The Class XMLStats to create stats about xml documents.
 */
public class XMLStats
	extends XMLTool
{
	
	/**
	 * Instantiates a new XML statter.
	 *
	 * @param args the arguments
	 */
	public XMLStats (String[] args)
	{
		super (args);
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmlutils.run.XMLTool#doIt(de.unirostock.sems.xmlutils.ds.TreeDocument)
	 */
	@Override
	public void doIt (TreeDocument doc) throws Exception
	{

		System.out.println ("===================");
		System.out.println ("document contains " + doc.getNumNodes () + " nodes:");
		System.out.println ("===================");
		
		HashMap<String, Integer> map = doc.getNodeStats ();
		
		for (String tag : map.keySet ())
			System.out.println (tag + " => " + map.get (tag));
		
		System.out.println ("===================");
		System.out.println ("those are the actual nodes, format:");
		System.out.println ("xpath - num args - num children - size subtree");
		System.out.println ("with size subtree being the number of nodes in the subtree or the length of the text in text nodes");
		System.out.println ("===================");
		Set<String> xpaths = doc.getOccurringXPaths ();
		for (String xpath : xpaths)
		{
			TreeNode tn = doc.getNodeByPath (xpath);
			switch (tn.getType ())
			{
				case TreeNode.DOC_NODE:
					DocumentNode dn = (DocumentNode) tn;
					System.out.println (xpath + " - " + dn.getAttributes ().size () + " - " + dn.getChildren ().size () + " - " + dn.getSizeSubtree ());
					break;
				case TreeNode.TEXT_NODE:
					TextNode txtn = (TextNode) tn;
					System.out.println (xpath + " - " + 0 + " - " + 0 + " - " + txtn.getText ().length ());
					break;
					default:
						LOGGER.error ("unexpected node type ", tn.getType (), " in ", xpath);
			}
		}
		
		//System.out.println("all in all " + numTags + " tags, " + keys.size () + " distinct ones!");
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmlutils.run.XMLTool#usage()
	 */
	@Override
	public String usage ()
	{
		return "no arguments required";
	}
	
}
