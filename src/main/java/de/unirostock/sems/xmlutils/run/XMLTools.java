/**
 * 
 */
package de.unirostock.sems.xmlutils.run;

import java.io.File;

import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.run.modules.XMLStats;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * @author Martin Scharm
 *
 */
public class XMLTools
{
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main (String[] args) throws Exception
	{
		//args = new String [] {"/tmp/test.xml", "stats"};
		if (args.length < 2)
			usage ();
		
		System.out.println (args[0]);
		TreeDocument doc = new TreeDocument (XmlTools.readDocument (new File (args[0])), null);
		
		XMLTool tool = null;
		
		if (args[1].equals ("stats"))
			tool = new XMLStats (args);
		/*if (args[1].equals ("validate"))
			tool = new XMLValidator (args);*/
		
		if (tool == null)
			usage ();
		
		if (args.length > 2 && args[2].equals ("help"))
		{
			System.out.println (tool.usage ());
			return;
		}
		
		tool.doIt (doc);
			
	}
	
	/**
	 * Print the Usage.
	 */
	public static void usage ()
	{
		System.out.println ("USAGE: java -jar ARCHIV XMLFILE CMD [help]");
		System.out.println ("[CMD]:");
		//System.out.println ("\tvalidate\tuse a schema to validate a file");
		System.out.println ("\tstats\t\tsome stats of the xml");
		System.exit (1);
	}
	
}
