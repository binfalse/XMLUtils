/**
 * 
 */
package de.unirostock.sems.xmlutils.exception;



/**
 * The Class BivesDocumentParseException that is thrown in case of a parsing error.
 *
 * @author Martin Scharm
 */
public class XmlDocumentParseException
	extends XmlDocumentException
{
	private static final long	serialVersionUID	= -5436539382054904120L;

	/**
	 * The Constructor.
	 *
	 * @param msg the error message
	 */
	public XmlDocumentParseException (String msg)
	{
		super (msg);
	}
}
