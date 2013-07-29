package de.ichmann.java.schaltwerk;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import de.ichmann.java.schaltwerk.blocks.CompoundBlock;
import de.ichmann.java.schaltwerk.io.XMLImport;

public class XMLImportTest {

	private static String xmlFile = "/home/erebos/projects/workspaces/workspace_schaltwerk/Schaltwerk/test/de/ichmann/java/schaltwerk/example.xml";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// TODO implement real test case!
		XMLImport xmlimport = new XMLImport();
		Document d = xmlimport.buildDOM(new File(xmlFile));
		CompoundBlock block = xmlimport.loadDocument(d);
		System.out.println(block);
	}

	@Test
	public void testBuildDOM() {
		fail("not yet implemented!");
	}

	@Test
	public void testLoadDocument() {
		fail("not yet implemented!");
	}

}
