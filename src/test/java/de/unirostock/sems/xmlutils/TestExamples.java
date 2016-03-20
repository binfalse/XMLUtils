/**
 * 
 */
package de.unirostock.sems.xmlutils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.unirostock.sems.xmlutils.eg.NodeUsageExample;
import de.unirostock.sems.xmlutils.eg.TreeUsageExample;
import de.unirostock.sems.xmlutils.run.XMLTools;



/**
 * @author Martin Scharm
 * 
 */
@RunWith(JUnit4.class)
public class TestExamples
{


	
	
	/**
	 * Test node mapper.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testNodeMapper () throws Exception
	{
		new NodeUsageExample ();
		new TreeUsageExample ();
		NodeUsageExample.main (null);
		TreeUsageExample.main (null);
		
		new XMLTools ();
		XMLTools.main (new String [] {"test/simple.xml", "stats"});
	}
}
