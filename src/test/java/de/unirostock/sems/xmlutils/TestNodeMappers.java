/**
 * 
 */
package de.unirostock.sems.xmlutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.transform.TransformerException;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.xmlutils.alg.SemsWeighter;
import de.unirostock.sems.xmlutils.alg.XyWeighter;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.NodeDistance;
import de.unirostock.sems.xmlutils.ds.NodeDistanceComparator;
import de.unirostock.sems.xmlutils.ds.TextNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.ds.mappers.MultiNodeMapper;
import de.unirostock.sems.xmlutils.ds.mappers.NodeMapper;
import de.unirostock.sems.xmlutils.exception.XmlDocumentConsistencyException;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;



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
