/**
 * 
 */
package de.unirostock.sems.xmlutils.exception;

/**
 * The Class XmlDocumentException that is thrown in case of an error in an XML
 * document.
 * 
 * @author Martin Scharm
 * 
 */
public class XmlDocumentException
	extends Exception
{
	
	private static final long	serialVersionUID	= 7247663104322110820L;
	
	
	/**
	 * The Constructor.
	 * 
	 * @param msg
	 *          the error message
	 */
	public XmlDocumentException (String msg)
	{
		super (msg);
	}
	
}
