/**
 * 
 */
package de.unirostock.sems.xmlutils.exception;


/**
 * The Class BivesDocumentParseException that is thrown in case of an inconsistent document.
 * 
 * @author Martin Scharm
 *
 */
public class XmlDocumentConsistencyException
	extends XmlDocumentException
{
	private static final long	serialVersionUID	= 3956623453651464648L;

	/**
	 * The Constructor.
	 *
	 * @param msg the error message
	 */
	public XmlDocumentConsistencyException (String msg)
	{
		super (msg);
	}
	
}
