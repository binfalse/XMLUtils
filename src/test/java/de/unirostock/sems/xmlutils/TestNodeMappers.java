/**
 * 
 */
package de.unirostock.sems.xmlutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.unirostock.sems.xmlutils.ds.mappers.MultiNodeMapper;
import de.unirostock.sems.xmlutils.ds.mappers.NodeMapper;



/**
 * @author Martin Scharm
 * 
 */
@RunWith(JUnit4.class)
public class TestNodeMappers
{


	
	
	/**
	 */
	@Test
	public void testNodeMapper ()
	{
		NodeMapper<String> mm = new NodeMapper<String> ();
		mm.putNode ("myid", "mynode");
		mm.putNode ("myid", "mynode2");
		assertEquals ("expected 1 id", 1, mm.getIds ().size ());
		assertEquals ("expected 1 node for myid", "mynode2", mm.getNode ("myid"));
		mm.putNode ("myid2", "mynode2");
		assertEquals ("expected 2 ids", 2, mm.getIds ().size ());
		mm.rmNode ("myid3");
		assertEquals ("expected 2 ids", 2, mm.getIds ().size ());
		mm.rmNode ("myid2");
		assertEquals ("expected 1 id", 1, mm.getIds ().size ());
		mm.rmNode ("myid");
		assertEquals ("expected 0 ids", 0, mm.getIds ().size ());
		mm.rmNode ("myid");
		assertEquals ("expected 0 id", 0, mm.getIds ().size ());
		mm.rmNode ("myid");
		assertEquals ("expected 0 id", 0, mm.getIds ().size ());
		mm.putNode ("myid", "mynode");
		mm.putNode ("myid2", "mynode2");
		assertNotNull (mm.toString ());
	}

	
	
	/**
	 */
	@Test
	public void testMultiNodeMapper ()
	{
		MultiNodeMapper<String> mm = new MultiNodeMapper<String> ();
		mm.addNode ("myid", "mynode");
		mm.addNode ("myid", "mynode2");
		assertEquals ("expected 1 id", 1, mm.getIds ().size ());
		assertEquals ("expected 2 nodes for myid", 2, mm.getNodes ("myid").size ());
		mm.addNode ("myid2", "mynode2");
		assertEquals ("expected 2 ids", 2, mm.getIds ().size ());
		mm.rmNode ("myid3", "mynode2");
		assertEquals ("expected 2 ids", 2, mm.getIds ().size ());
		mm.rmNode ("myid2", "mynode2");
		assertEquals ("expected 2 id", 2, mm.getIds ().size ());
		mm.rmNode ("myid", "mynode3");
		assertEquals ("expected 2 id", 2, mm.getIds ().size ());
		mm.rmNode ("myid", "mynode");
		assertEquals ("expected 2 id", 2, mm.getIds ().size ());
		mm.rmNode ("myid", "mynode2");
		assertEquals ("expected 2 id", 2, mm.getIds ().size ());
		assertNotNull (mm.toString ());
	}
}
