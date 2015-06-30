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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.SAXException;

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
import de.unirostock.sems.xmlutils.eg.NodeUsageExample;
import de.unirostock.sems.xmlutils.eg.TreeUsageExample;
import de.unirostock.sems.xmlutils.exception.XmlDocumentConsistencyException;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.run.XMLTools;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;



/**
 * @author Martin Scharm
 * 
 */
@RunWith(JUnit4.class)
public class TestExamples
{


	
	
	/**
	 * @throws Exception 
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
