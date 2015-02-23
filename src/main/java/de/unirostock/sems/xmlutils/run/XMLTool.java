package de.unirostock.sems.xmlutils.run;

import de.unirostock.sems.xmlutils.ds.TreeDocument;


/**
 * The Class XMLTool.
 */
public abstract class XMLTool
{
	
	/** The args. */
	protected String [] args;
	
	/**
	 * Instantiates a new xML tool.
	 *
	 * @param args the args
	 */
	public XMLTool (String [] args)
	{
		this.args = args;
	}
	
	/**
	 * Do it.
	 *
	 * @param doc the doc
	 * @throws Exception the exception
	 */
	public abstract void doIt (TreeDocument doc) throws Exception;
	
	/**
	 * Usage.
	 *
	 * @return the string
	 */
	public abstract String usage ();
}
