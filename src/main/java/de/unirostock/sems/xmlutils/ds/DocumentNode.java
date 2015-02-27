/**
 * 
 */
package de.unirostock.sems.xmlutils.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.xmlutils.alg.Weighter;
import de.unirostock.sems.xmlutils.comparison.Connection;
import de.unirostock.sems.xmlutils.comparison.ConnectionManager;
import de.unirostock.sems.xmlutils.exception.XmlDocumentConsistencyException;



/**
 * The class DocumentNode, representing a node in an XML tree.
 * 
 * @author Martin Scharm
 * 
 */
public class DocumentNode
	extends TreeNode
{
	
	/** The id attr. */
	public static String											ID_ATTR	= "id";
	
	/** The tag name. */
	private String														tagName;
	
	private String nsUri;
	private String nsPrefix;
	
	/** The id. */
	private String														id;
	
	/** The attributes. */
	private Map<String, Attribute>							attributes;
	
	/** The children of this node. */
	private List<TreeNode>									children;
	
	/** The children mapped by tag names. */
	private HashMap<String, ArrayList<TreeNode>>	childrenByTag;
	
	/** The hash of the subtree rooted in this node. */
	private String														subTreeHash;
	
	/**
	 * the number of nodes in the subtree rooted in this node. (current node
	 * excluded)
	 */
	private int																sizeSubtree;
	
	/** The number of leaves below this node. */
	private int																numLeaves;
	
	/** The weight. */
	private double														weight;
	
	/** The weighter. */
	private Weighter													weighter;
	
	
	/**
	 * Instantiates a new document node as a copy of another node.
	 * 
	 * @param toCopy
	 *          the node to copy
	 * @param parent
	 *          the new parent or null if this is going to be root
	 * @param numChild
	 *          the number of that child among its siblings (first child = 1)
	 */
	private DocumentNode (DocumentNode toCopy, DocumentNode parent, int numChild)
	{
		// init the tree node
		super (TreeNode.DOC_NODE, parent, null, parent == null ? 0 : parent.level + 1);
		
		tagName = toCopy.tagName;
		nsPrefix = toCopy.nsPrefix;
		nsUri = toCopy.nsUri;
		id = toCopy.id;
		sizeSubtree = toCopy.sizeSubtree;
		numLeaves = toCopy.numLeaves;
		weight = toCopy.weight;
		weighter = toCopy.weighter;
		
		// compute xpath
		if (parent == null)
			xPath = "";
		else
			xPath = parent.getXPath ();
		xPath += "/" + tagName + "[" + numChild + "]";
		
		attributes = new TreeMap<String, Attribute> ();
		for (String attr : toCopy.attributes.keySet ())
			attributes.put (attr, toCopy.attributes.get (attr).clone ());
		
		children = new ArrayList<TreeNode> ();
		childrenByTag = new HashMap<String, ArrayList<TreeNode>> ();
		
		for (TreeNode tn : toCopy.getChildren ())
		{
			if (tn.getType () == TreeNode.DOC_NODE)
			{
				DocumentNode c = (DocumentNode) tn;

				if (childrenByTag.get (c.tagName) == null)
					childrenByTag.put (c.tagName, new ArrayList<TreeNode> ());
				
				DocumentNode cc = new DocumentNode (c, this, childrenByTag
					.get (c.tagName).size () + 1);
				
				children.add (cc);
				cc.parent = this;
				childrenByTag.get (cc.tagName).add (cc);
			}
			else
			{
				if (childrenByTag.get (TEXT_TAG) == null)
					childrenByTag.put (TEXT_TAG, new ArrayList<TreeNode> ());
				
				TextNode c = (TextNode) tn;
				TextNode cc = new TextNode (c, this, childrenByTag
					.get (TEXT_TAG).size () + 1);
				children.add (cc);
				cc.parent = this;
				
				childrenByTag.get (TEXT_TAG).add (cc);
			}
		}
		
		doc = null;
		
		ownHash = toCopy.ownHash;
		subTreeHash = toCopy.subTreeHash;
	}
	
	
	/**
	 * Instantiates a new document node.
	 * 
	 * @param element
	 *          the corresponding element
	 * @param parent
	 *          the parent node
	 * @param doc
	 *          the corresponding document
	 * @param w
	 *          the weighter
	 * @param numChild
	 *          the number among its siblings
	 * @param level
	 *          the level in the tree
	 */
	public DocumentNode (Element element, DocumentNode parent, TreeDocument doc,
		Weighter w, int numChild, int level)
	{
		// init the tree node
		super (TreeNode.DOC_NODE, parent, doc, level);
		// init objects
		attributes = new TreeMap<String, Attribute> ();
		children = new ArrayList<TreeNode> ();
		tagName = element.getName ();//.getTagName ();
		nsPrefix = element.getNamespacePrefix ();
		nsUri = element.getNamespaceURI ();
		sizeSubtree = numLeaves = 0;
		weighter = w;
		
		// compute xpath
		if (parent == null)
			xPath = "";
		else
			xPath = parent.getXPath ();
		xPath += "/" + tagName + "[" + numChild + "]";
		
		// find attributes
		List<Attribute> attrs = element.getAttributes ();
		for (Attribute a : attrs)
			attributes.put (a.getName (), a);
			
		/*NamedNodeMap a = element.getAttributes ();
		int numAttrs = a.getLength ();
		for (int i = 0; i < numAttrs; i++)
		{
			Attr attr = (Attr) a.item (i);
			attributes.put (attr.getNodeName (), attr.getNodeValue ());
		}*/
		
		// get id
		id = attributes.get (ID_ATTR) == null ? null : attributes.get (ID_ATTR).getValue ();
		
		// add kids
		List<Content> kids = element.getContent ();//.getChildren ();
		childrenByTag = new HashMap<String, ArrayList<TreeNode>> ();
		for (Content current : kids)
		{
			//Node current = kids.item (i);
			// == DOC_NODE
			if (current.getCType () == CType.Element)
			{
				Element cur = (Element) current;
				if (childrenByTag.get (cur.getName ()) == null)
					childrenByTag.put (cur.getName (), new ArrayList<TreeNode> ());
				DocumentNode kid = new DocumentNode (cur, this, doc, w, childrenByTag
					.get (cur.getName ()).size () + 1, level + 1);
				
				children.add (kid);
				childrenByTag.get (cur.getName ()).add (kid);
				sizeSubtree += kid.getSizeSubtree () + 1;
				numLeaves += kid.getNumLeaves ();
			}
			// == TEXT_NODE
			if (current.getCType () == CType.Text)
			{
				String text = ((Text) current).getText ().trim ();
				
				// lets discard whitespace-only nodes
				if (text.length () < 1)
					continue;
				
				if (childrenByTag.get (TEXT_TAG) == null)
					childrenByTag.put (TEXT_TAG, new ArrayList<TreeNode> ());
				
				TextNode kid = new TextNode (text, this, doc, childrenByTag.get (
					TEXT_TAG).size () + 1, w, level + 1);
				children.add (kid);
				childrenByTag.get (TEXT_TAG).add (kid);
				sizeSubtree++;
				numLeaves++;
			}
		}
		
		
		calcHash ();
		if (numLeaves == 0)
			numLeaves = 1;
		
		weight = w.getWeight (this);
		doc.integrate (this, false);
	}
	
	
	/**
	 * Extracts this subtree. Creates a copy of the subtree rooted in this node
	 * and returns a DocumentNode that has no parent, e.g. to
	 * transfer it to another document.
	 * 
	 * @return the copy of this DocumentNode
	 */
	public DocumentNode extract ()
	{
		return new DocumentNode (this, null, 1);
	}
	
	
	/**
	 * Gets the calculated hash of the subtree rooted in this node.
	 * 
	 * @return the hash
	 */
	public String getSubTreeHash ()
	{
		return subTreeHash;
	}
	
	
	/**
	 * Gets the calculated hash of this single element (ignoring subtree).
	 * 
	 * @return the hash
	 */
	public String getOwnHash ()
	{
		return ownHash;
	}
	
	
	/**
	 * Gets the size of this subtree (number of nodes under the current node,
	 * <strong>current node excluded</strong>).
	 * 
	 * @return the size of the subtree
	 */
	public int getSizeSubtree ()
	{
		return sizeSubtree;
	}
	
	
	/**
	 * Gets the number of leaves in the subtree rooted by this node. If this is a
	 * leave it will return 1.
	 * 
	 * @return the num leaves
	 */
	public int getNumLeaves ()
	{
		return numLeaves;
	}
	
	
	/**
	 * Sets the id attribute. (you may want to use something like the
	 * <code>metaid</code> instead of the <code>id<code> as identifier)
	 * 
	 * @param id
	 *          the new id attribute
	 */
	public static final void setIdAttr (String id)
	{
		ID_ATTR = id;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#getTagName()
	 */
	public String getTagName ()
	{
		return tagName;
	}
	
	
	/**
	 * Gets the value of the id attribute.
	 * 
	 * @return the id
	 */
	public String getId ()
	{
		return id;
	}
	
	
	/**
	 * Adds a child to this node.
	 * 
	 * @param toAdd
	 *          the new child
	 */
	public void addChild (DocumentNode toAdd)
	{
		// integrate subtree
		toAdd.parent = this;
		if (childrenByTag.get (toAdd.getTagName ()) == null)
			childrenByTag.put (toAdd.getTagName (), new ArrayList<TreeNode> ());
		children.add (toAdd);
		// propagate downwards
		doc.integrate (toAdd, true);
		toAdd.reSetupStructureDown (doc, childrenByTag.get (toAdd.getTagName ())
			.size () + 1);
		childrenByTag.get (toAdd.getTagName ()).add (toAdd);
		
		// update parents
		reSetupStructureUp ();
		
		// resort subtreesizes
		doc.resortSubtrees ();
	}
	
	
	/**
	 * Remove a child.
	 * 
	 * @param toRemove
	 *          the child to remove
	 * @throws XmlDocumentConsistencyException
	 *           thrown if there is no such child
	 */
	public void rmChild (DocumentNode toRemove)
		throws XmlDocumentConsistencyException
	{
		List<TreeNode> nodes = childrenByTag.get (toRemove.getTagName ());
		if (nodes == null)
			return;
		
		// remove it
		if (!nodes.remove (toRemove))
			return;
		else if (!children.remove (toRemove))
		{
			LOGGER.error ("we produced an inconsistent state. we removed a node",
				" from tag-mapped children, but weren't able to find it in",
				" children!?");
			throw new XmlDocumentConsistencyException ("inconsistens state."
				+ " there was a node in tag-mapped children, but not in children!?");
		}
		
		doc.separate (toRemove, true);
		toRemove.parent = null;
		toRemove.reSetupStructureDown (null, 1);
		
		// update parents
		reSetupStructureUp ();
		if (this.parent != null)
			this.parent.reSetupStructureDown (doc, -1);
		else
			reSetupStructureDown (doc, -1);
		
		// resort subtreesizes
		if (doc != null)
			doc.resortSubtrees ();
	}
	
	
	/**
	 * (Re-)Compute the hash of this node and its subtree.
	 */
	private void calcHash ()
	{
		// current hash = tagname(;attr=value)*
		StringBuilder h = new StringBuilder ();
		h.append (tagName);
		for (String a : attributes.keySet ())
			h.append (";" + a + "=" + attributes.get (a));
		ownHash = GeneralTools.hash (h.toString ());
		
		// subtree hash = tagname(;attr=value)*(child hash)*
		for (TreeNode kid : children)
			h.append (kid.getSubTreeHash ());
		subTreeHash = GeneralTools.hash (h.toString ());
	}
	
	
	/**
	 * Gets the value of an attribute. <strong>Don't use it to get the id, use
	 * getId () instead!</strong>
	 * 
	 * @param attr
	 *          the name of the attribute
	 * @return the value of the attribute
	 */
	public String getAttributeValue (String attr)
	{
		if (attributes.get (attr) == null)
			return null;
		return attributes.get (attr).getValue ();
	}
	
	
	/**
	 * Gets the an attribute.
	 * 
	 * @param attr
	 *          the name of the attribute
	 * @return the the attribute
	 */
	public Attribute getAttribute (String attr)
	{
		return attributes.get (attr);
	}
	
	
	/**
	 * Gets the value of an attribute with matching name space.
	 * <strong>Don't use it to get the id, use getId () instead!</strong>
	 *
	 * @param attr the name of the attribute
	 * @param nsContains the name space must contain <code>nsContains</code>
	 * @return the value of the attribute
	 */
	public String getAttributeValue (String attr, String nsContains)
	{
		if (attributes.get (attr) == null)
			return null;
		Attribute a = attributes.get (attr);
		if (a.getNamespaceURI ().contains (nsContains))
			return a.getValue ();
		return null;
	}
	
	
	/**
	 * Overrides an attribute.
	 * 
	 * @param attr
	 *          the attribute
	 */
	public void setAttribute (Attribute attr)
	{
		attributes.put (attr.getName (), attr);
		reSetupStructureUp ();
	}
	
	
	/**
	 * Overrides an attribute.
	 * 
	 * @param attr
	 *          the name of the attribute
	 * @param value
	 *          the new value
	 */
	public void setAttribute (String attr, String value)
	{
		setAttribute (new Attribute (attr, value));
		/*attributes.put (attr, new Attribute (attr, value));
		reSetupStructureUp ();*/
	}
	
	
	/**
	 * Gets set attributes.
	 * 
	 * @return the attribute names
	 */
	public Set<String> getAttributes ()
	{
		return attributes.keySet ();
	}
	
	
	/**
	 * Checks if this node is a child of some other node (multilevel). Both nodes
	 * have to be from the same origin document and the XPath of the current node
	 * has to start with the parent's XPath.
	 * 
	 * @param parent
	 *          the parent in question
	 * @return true, if is this is a child of parent
	 */
	public boolean isBelow (DocumentNode parent)
	{
		return doc == parent.doc && xPath.startsWith (parent.xPath);
	}
	
	
	/**
	 * Gets the number of children in this node.
	 * 
	 * @return the number of children
	 */
	public int getNumChildren ()
	{
		return children.size ();
	}
	
	
	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	public List<TreeNode> getChildren ()
	{
		return children;
	}
	
	
	/**
	 * Gets the children sharing a certain tag.
	 * 
	 * @param tag
	 *          the tag
	 * @return the children having tag as tag name or an empty list if there are
	 *         no such children
	 */
	@SuppressWarnings("unchecked")
	public List<TreeNode> getChildrenWithTag (String tag)
	{
		ArrayList<TreeNode> ret = childrenByTag.get (tag);
		if (ret == null)
			return new ArrayList<TreeNode> ();
		return (ArrayList<TreeNode>) ret.clone ();
	}
	
	
	/**
	 * Gets the children tag map.
	 * 
	 * @return the children tag map
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, List<TreeNode>> getChildrenTagMap ()
	{
		HashMap<String, List<TreeNode>> ret = new HashMap<String, List<TreeNode>> (childrenByTag.size ());
		for (String tag : childrenByTag.keySet ())
			ret.put (tag, (ArrayList<TreeNode>) childrenByTag.get (tag).clone ());
		return ret;
	}
	
	
	/**
	 * Gets the child number of a child. Will return 1 if it's the first child and
	 * <strong>getNumChildren ()</strong> for the last child. If there is no such
	 * child it returns -1.
	 * 
	 * @param kid
	 *          the kid
	 * @return the no of child
	 */
	public int getNoOfChild (TreeNode kid)
	{
		int ret = children.indexOf (kid);
		if (ret < 0)
			return ret;
		return ret + 1;
	}
	
	
	/**
	 * Calculates the distance of attributes. Returns a double in [0,1].
	 * If all attributes match the distance will be 0, if none of the attributes
	 * match the distance will be 1.
	 * 
	 * 
	 * @param cmp
	 *          the node to compare
	 * @return the attribute distance in [0,1]
	 */
	public double getAttributeDistance (DocumentNode cmp)
	{
		// both have no attributes => equality in this context
		if (attributes.size () == 0 && cmp.attributes.size () == 0)
			return 0;
		
		// let's see how many attributes differ
		double unmatch = 0.;
		for (String name : attributes.keySet ())
		{
			if (cmp.attributes.get (name) == null)
				unmatch += 1;
			else if (!cmp.attributes.get (name).getValue ().equals (attributes.get (name).getValue ()))
				unmatch += 2;
		}
		for (String name : cmp.attributes.keySet ())
		{
			if (attributes.get (name) == null)
				unmatch += 1;
		}
		
		// final recipe:
		return unmatch / (double) (attributes.size () + cmp.attributes.size ());
	}
	
	
	/**
	 * Gets the weighter used to compute the weight of this document.
	 *
	 * @return the weighter
	 */
	public Weighter getWeighter ()
	{
		return weighter;
	}
	
	/**
	 * Gets the name space uri.
	 *
	 * @return the name space uri
	 */
	public String getNameSpaceUri ()
	{
		return nsUri;
	}
	
	/**
	 * Gets the name space prefix.
	 *
	 * @return the name space prefix
	 */
	public String getNameSpacePrefix ()
	{
		return nsPrefix;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmlutils.ds.TreeNode#getSubDoc(org.w3c.dom.Document,
	 * org.w3c.dom.Element)
	 */
	@Override
	public Element getSubDoc (Element parent)
	{
		// TODO: NAMESPACE
		
		// create a new element
		Element node = new Element (tagName, nsPrefix, nsUri);
		for (String att : attributes.keySet ())
		{
			Attribute a = attributes.get (att);
			node.setAttribute (a.getName (), a.getValue (), a.getNamespace ());
		}
		
		// are we root?
		if (parent != null)
			parent.addContent (node);
		
		// attach children
		for (TreeNode kid : children)
			kid.getSubDoc (node);
		
		return node;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmlutils.ds.TreeNode#getNodeStats(java.util.HashMap)
	 */
	@Override
	public void getNodeStats (HashMap<String, Integer> map)
	{
		Integer i = map.get (tagName);
		if (i == null)
			map.put (tagName, 1);
		else
			map.put (tagName, i + 1);
		
		for (TreeNode child : children)
			child.getNodeStats (map);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmlutils.ds.TreeNode#evaluate(de.unirostock.sems.xmlutils
	 * .comparison.ConnectionManager)
	 */
	@Override
	public boolean evaluate (ConnectionManager conMgmr)
	{
		// needed to take this out: cause modifications might be set somewhere else,
		// e.g. in TreeNode.networkDiffers...
		// therefore it's essential to call TreeDocument.resetAllModifications
		// setModification (UNCHANGED);
		
		// evaluate kids
		boolean kidChanged = false;
		boolean kidsUnmapped = true;
		for (TreeNode child : children)
		{
			kidChanged |= child.evaluate (conMgmr);
			if (!child.hasModification (SUBTREEUNMAPPED))
				kidsUnmapped = false;
		}
		if (kidChanged)
			addModification (SUB_MODIFIED);
		LOGGER.debug ("evaluate kids changed: ", kidChanged, " -- for ", xPath);
		
		// do we have a connection?
		Connection con = conMgmr.getConnectionForNode (this);
		if (con == null)
		{
			LOGGER.debug (xPath, " is unmapped");
			addModification (UNMAPPED);
			// kids also unconnected? -> SUBTREEUNMAPPED
			if (kidsUnmapped)
				addModification (SUBTREEUNMAPPED);
			return true;
		}
		
		// ok, let's compare
		TreeNode partner = con.getPartnerOf (this);
		LOGGER.debug ("evaluate ", xPath, " is mapped to ", partner.getXPath ());
		
		// different content?
		if (contentDiffers (partner))
			addModification (MODIFIED);
		
		// moved?
		if (networkDiffers (partner, conMgmr, con))
			addModification (MOVED);
		
		LOGGER.debug ("mod of ", xPath, " = ", modified);
		
		return modified != 0;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#getWeight()
	 */
	public double getWeight ()
	{
		return weight;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unirostock.sems.xmlutils.ds.TreeNode#reSetupStructureDown(de.unirostock
	 * .sems.xmlutils.ds.TreeDocument, int)
	 */
	protected void reSetupStructureDown (TreeDocument doc, int numChild)
	{
		// extract
		if (this.doc != null)
			this.doc.separate (this, false);
		this.doc = doc;
		
		// recalculate the properties
		if (parent != null)
		{
			if (numChild > 0) // might be null if a children's child was removed
				this.xPath = parent.xPath + "/" + tagName + "[" + numChild + "]";
			this.level = parent.level + 1;
		}
		else if (numChild > 0)
			this.xPath = "/" + tagName + "[" + numChild + "]";
		
		// propagate downwards
		for (String tag : childrenByTag.keySet ())
		{
			List<TreeNode> kids = childrenByTag.get (tag);
			for (int i = 0; i < kids.size (); i++)
				kids.get (i).reSetupStructureDown (doc, i + 1);
		}
		
		// integrate into (new) doc
		if (this.doc != null)
			this.doc.integrate (this, false);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#reSetupStructureUp()
	 */
	protected void reSetupStructureUp ()
	{
		TreeDocument treeDoc = this.doc;
		if (this.doc != null)
			this.doc.separate (this, false);
		
		calcHash ();
		numLeaves = 0;
		sizeSubtree = 0;
		for (TreeNode kid : children)
		{
			if (kid.type == TreeNode.DOC_NODE)
			{
				DocumentNode k = (DocumentNode) kid;
				numLeaves += k.numLeaves;
				sizeSubtree += k.getSizeSubtree () + 1;
			}
			else
			{
				numLeaves++;
				sizeSubtree++;
			}
		}
		
		if (numLeaves == 0)
			numLeaves = 1;
		
		if (treeDoc != null)
			treeDoc.integrate (this, false);
		weight = weighter.getWeight (this);
		if (parent != null)
			parent.reSetupStructureUp ();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		StringBuilder attr = new StringBuilder (" ");
		for (String a : attributes.keySet ())
			attr.append (a + "=\"" + attributes.get (a) + "\" ");
		attr = new StringBuilder ("<" + tagName + attr.toString () + ">\t" + weight
			+ "\t(" + xPath + ")\t" + subTreeHash + "\n");
		for (int i = 0; i < children.size (); i++)
			attr.append (children.get (i));
		return attr.toString () + "</" + tagName + ">\n";
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.xmlutils.ds.TreeNode#dump(java.lang.String)
	 */
	@Override
	public String dump (String prefix)
	{
		String s = prefix + xPath + " -> " + modified + "\n";
		for (TreeNode child : children)
		{
			s += child.dump (prefix + "\t");
		}
		return s;
	}


	/**
	 * Gets the name space associated with this node.
	 *
	 * @return the name space
	 */
	public Namespace getNameSpace ()
	{
		return Namespace.getNamespace (nsPrefix, nsUri);
	}
}
