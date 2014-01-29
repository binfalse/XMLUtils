/**
 * 
 */
package de.unirostock.sems.xmltools.ds;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unirostock.sems.xmltools.comparison.Connection;
import de.unirostock.sems.xmltools.comparison.ConnectionManager;



/**
 * @author Martin Scharm
 *
 */
public abstract class TreeNode
{
	protected static final String TEXT_TAG = "text()";
	
	public static final int UNCHANGED = 0;
	public static final int UNMAPPED = 1;
	public static final int MOVED = 2;
	public static final int MODIFIED = 4;
	public static final int SUB_MODIFIED = 8;
	public static final int COPIED = 16;
	public static final int GLUED = 32;
	public static final int KIDSSWAPPED = 64;
	public static final int SWAPPEDKID = 128;
	public static final int SUBTREEUNMAPPED = 256;
	
	public static final int DOC_NODE = 1;
	public static final int TEXT_NODE = 2;
	
	
	protected int modified;
	
	protected int type;
	/** The x path. */
	protected String xPath;
	
	/** The parent. */
	protected DocumentNode parent;
	
	protected TreeDocument doc;
	
	protected int level;
	
	public TreeNode (int type, DocumentNode parent, TreeDocument doc, int level)
	{
		this.doc = doc;
		this.type = type;
		this.parent = parent;
		this.modified = UNCHANGED;
		this.level = level;
	}
	
	public int getLevel ()
	{
		return level;
	}
	
	public int getModification ()
	{
		return modified;
	}
	
	public void rmModification (int mod)
	{
		this.modified &= ~mod;
	}
	
	public void addModification (int mod)
	{
		this.modified |= mod;
	}
	
	public void setModification (int mod)
	{
		this.modified = mod;
	}
	
	public boolean hasModification (int mod)
	{
		return (this.modified & mod) > 0;
	}
	
	public int getType ()
	{
		return type;
	}

	
	public DocumentNode getParent ()
	{
		return parent;
	}
	
	/**
	 * Gets the x path.
	 *
	 * @return the x path
	 */
	public String getXPath ()
	{
		return xPath;
	}
	
	public boolean isRoot ()
	{
		return parent == null;
	}
	
	public void resetModifications ()
	{
		this.modified = UNCHANGED;
		if (type == DOC_NODE)
		{
			Vector<TreeNode> kids = ((DocumentNode) this).getChildren ();
			for (TreeNode kid : kids)
				kid.resetModifications ();
		}
	}
	
	public TreeDocument getDocument ()
	{
		return doc;
	}
	
	
	public abstract double getWeight ();
	public abstract String getOwnHash ();
	/**
	 * Gets the calculated hash of this subtree, in TextNodes it equals the own hash.
	 *
	 * @return the hash
	 */
	public abstract String getSubTreeHash ();

	public abstract boolean evaluate (ConnectionManager conMgmr);
	public boolean networkDiffers (TreeNode tn, ConnectionManager conMgmr, Connection c)
	{
		//System.out.println ("checking : " + getXPath () + " -> " + tn.getXPath ());
		DocumentNode p = getParent ();
		DocumentNode tnp = tn.getParent ();
		
		// both root?
		if (p == null && tnp == null)
			return false;
		
		// one root?
		if (p == null || tnp == null)
			return true;
		
		//System.out.println ("netw diff : " + getXPath () + " -> " + tn.getXPath ());
		//System.out.println ("netw diff parents : " + p.getXPath () + " -> " + tnp.getXPath ());

		
		// parents connected and same child no.?
		if (!conMgmr.parentsConnected (c))
		//if ( (c))
		{
			/*System.out.println ("nodes: " + ((DocumentNode) this).getAttribute ("species") + "->" + getXPath () + " --- " + ((DocumentNode) tn).getAttribute ("species") + "->" + tn.getXPath ());
			System.out.println ("parents: " + p.getXPath () + " --- " + tnp.getXPath ());
		System.out.println ("p1: " + conMgmr.getConnectionOfNodes (p, tnp));
		System.out.println ("p2: " + conMgmr.parentsConnected (c));*/
			//System.out.println ("parents not connected: ");
			if (p != null)
				p.addModification (SUB_MODIFIED);
			if (tnp != null)
				tnp.addModification (SUB_MODIFIED);
			return true;
		}
		
		//System.out.println ("par connected");
		
		if (p.getNoOfChild (this) != tnp.getNoOfChild (tn))
		{
			p.addModification (KIDSSWAPPED);
			tnp.addModification (KIDSSWAPPED);
			addModification (SWAPPEDKID);
			tn.addModification (SWAPPEDKID);
			//return true;
		}
		
		//System.out.println ("same child no");
		
		return false;
	}
	protected abstract boolean contentDiffers (TreeNode tn);
	public abstract String dump (String prefix);
	
	public abstract void getSubDoc (Document doc, Element parent);
	
	protected abstract void reSetupStructureDown (TreeDocument doc, int numChild);
	
	protected abstract void reSetupStructureUp ();
	
	public abstract void getNodeStats (HashMap<String, Integer> map);
}
