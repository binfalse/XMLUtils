package de.unirostock.sems.xmlutils.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.SimpleOutputStream;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;



/**
 * Toolkit for Documents.
 * 
 * @author Martin Scharm
 */
public class DocumentTools
{
	
	/**
	 * no need to recreate the builder everytime...
	 */
	private static SAXBuilder		builder;
	
	/** The transformer to convert content mathml to presentation mathml. */
	private static Transformer	mathTransformer;
	
	
	/**
	 * Extracts the document from a given TreeDocument.
	 * 
	 * @param treeDoc
	 *          the tree document
	 * @return the document
	 */
	public static Document getDoc (TreeDocument treeDoc)
	{
		return getSubDoc (treeDoc.getRoot ());
	}
	
	
	/**
	 * Computes the document oft a subtree.
	 * 
	 * @param node
	 *          the node rooting the subtree
	 * @return the document representing the subtree
	 */
	public static Document getSubDoc (DocumentNode node)
	{
		try
		{
			if (builder == null)
				builder = XmlTools.getBuilder ();
			
			// Document d = builder.newDocument ();
			Document d = new Document (node.getSubDoc (null));
			return d;
		}
		catch (Exception e)
		{
			LOGGER.error (e, "error creating subdoc");
			return null;
		}
	}
	
	
	/**
	 * Prints the sub doc.
	 * 
	 * @param node
	 *          the node
	 * @return the string
	 */
	public static String printSubDoc (DocumentNode node)
	{
		try
		{
			if (builder == null)
				builder = XmlTools.getBuilder ();
			
			/*
			 * Document d = builder.newDocument ();
			 * node.getSubDoc (d, null);
			 */
			return XmlTools.printDocument (new Document (node.getSubDoc (null)));
		}
		catch (Exception e)
		{
			LOGGER.error (e, "error creating subdoc");
			return "error creating doc: " + e.getMessage ();
		}
	}
	
	
	/**
	 * Prints the pretty sub doc.
	 * 
	 * @param node
	 *          the node
	 * @return the string
	 */
	public static String printPrettySubDoc (DocumentNode node)
	{
		try
		{
			if (builder == null)
				builder = XmlTools.getBuilder ();
			
			/*
			 * Document d = builder.newDocument ();
			 * node.getSubDoc (d, null);
			 */
			return XmlTools
				.prettyPrintDocument (new Document (node.getSubDoc (null))).toString ();
		}
		catch (Exception e)
		{
			LOGGER.error (e, "error creating subdoc");
			return "error creating doc: " + e.getMessage ();
		}
	}
	
	
	/**
	 * Transform content MathML to display MathML, e.g. to display the MathML in a
	 * browser. This operation can be very expensive.
	 * 
	 * @param doc
	 *          the document node rooting the MathML subtree
	 * @return the string
	 * @throws TransformerException
	 *           the transformer exception
	 */
	public static String transformMathML (DocumentNode doc)
		throws TransformerException
	{
		/************************************************
		 * this is a workaround due to a bug in xerces
		 * which stupidly prints warnings to std::err...
		 * 
		 * no way to stop it, except:
		 * disabling std::err.
		 * 
		 * very annoying... and expensive..
		 * 
		 * see also
		 * http://bugs.java.com/view_bug.do?bug_id=8015487
		 * http://bugs.java.com/view_bug.do?bug_id=8016153
		 * http://stackoverflow.com/questions/25453042/how-to-disable-accessexternaldtd-and-entityexpansionlimit-warnings-with-logback
		 ************************************************/
		PrintStream err = System.err;
		System.setErr (new DummyPrintStream ());
		
		if (mathTransformer == null)
		{
			TransformerFactory tFactory = TransformerFactory.newInstance ();
			
			InputStream input = DocumentTools.class
				.getResourceAsStream ("/res/mmlctop2_0.xsl");
			mathTransformer = tFactory.newTransformer (new StreamSource (input));
		}
		
		SimpleOutputStream out = new SimpleOutputStream ();
		String math = printSubDoc (doc);
		// xslt cannot namespace
		math = math.replaceAll ("\\S+:\\S+\\s*=\\s*\"[^\"]*\"", "").replaceAll (
			" /", "/");
		
		mathTransformer.transform (
			new StreamSource (new ByteArrayInputStream (math.getBytes ())),
			new StreamResult (out));
		
		/************************************************
		 * here we just restore our good old std::err...
		 ************************************************/
		System.setErr (err);
		
		return out.toString ();
	}
	
	/************************************************
	 * this is the print stream that we'll use to
	 * work around the xerces bug..
	 * 
	 * this class is just to reduce the number of calls
	 ************************************************/
	private static class DummyPrintStream
		extends PrintStream
	{
		
		public DummyPrintStream ()
		{
			super (new ByteArrayOutputStream ());
		}
		
		
		public PrintStream append (char c)
		{
			return this;
		}
		
		
		public PrintStream append (CharSequence c)
		{
			return this;
		}
		
		
		public PrintStream append (CharSequence c, int i, int j)
		{
			return this;
		}
		
		
		public boolean checkError ()
		{
			return false;
		}
		
		
		public void flush ()
		{
		}
		
		
		public void close ()
		{
		}
		
		
		public void write (int b)
		{
		}
		
		
		public void print (long l)
		{
		}
		
		
		public void print (float f)
		{
		}
		
		
		public void print (char s[])
		{
		}
		
		
		public void println ()
		{
		}
		
		
		public void print (String s)
		{
		}
		
		
		public void print (Object obj)
		{
		}
		
		
		public void print (double d)
		{
		}
		
		
		public void print (int i)
		{
		}
		
		
		public void print (char c)
		{
		}
		
		
		public void print (boolean b)
		{
		}
		
		
		public void write (byte buf[], int off, int len)
		{
		}
		
		
		public void println (boolean x)
		{
		}
		
		
		public void println (char x)
		{
		}
		
		
		public void println (int x)
		{
		}
		
		
		public void println (long x)
		{
		}
		
		
		public void println (float x)
		{
		}
		
		
		public void println (double x)
		{
		}
		
		
		public void println (char x[])
		{
		}
		
		
		public void println (String x)
		{
		}
		
		
		public void println (Object x)
		{
		}
		
		
		public PrintStream printf (String format, Object... args)
		{
			return this;
		}
		
		
		public PrintStream printf (Locale l, String format, Object... args)
		{
			return this;
		}
		
		
		public PrintStream format (String format, Object... args)
		{
			return this;
		}
		
		
		public PrintStream format (Locale l, String format, Object... args)
		{
			return this;
		}
	}
}
