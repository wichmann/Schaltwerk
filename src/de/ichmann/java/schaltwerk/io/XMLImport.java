package de.ichmann.java.schaltwerk.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.ichmann.java.schaltwerk.blocks.AND;
import de.ichmann.java.schaltwerk.blocks.BaseBlock;
import de.ichmann.java.schaltwerk.blocks.Block;
import de.ichmann.java.schaltwerk.blocks.CompoundBlock;
import de.ichmann.java.schaltwerk.blocks.Input;
import de.ichmann.java.schaltwerk.blocks.NAND;
import de.ichmann.java.schaltwerk.blocks.NOR;
import de.ichmann.java.schaltwerk.blocks.NOT;
import de.ichmann.java.schaltwerk.blocks.OR;
import de.ichmann.java.schaltwerk.blocks.Output;
import de.ichmann.java.schaltwerk.blocks.Signal;

public class XMLImport {

	// TODO make this path relative and/or dynamic
	private static final String XSDFILE = "/home/erebos/projects/workspaces/workspace_schaltwerk/Schaltwerk/src/resources/xsd/SchaltWerk.xsd";

	// All Blocks are hashed, to allow for signal mapping.
	private HashMap<String, Block> blockHash = new HashMap<>();

	private class SimpleErrorHandler implements ErrorHandler {
		public void warning(SAXParseException e) throws SAXException {
			System.out.println(e.getMessage());
		}

		public void error(SAXParseException e) throws SAXException {
			System.out.println(e.getMessage());
		}

		public void fatalError(SAXParseException e) throws SAXException {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Inner class that encapsulates list of input and output signalnames.
	 * 
	 * @author Martin Wichmann
	 */
	private class IOListContainer {
		private ArrayList<String> inList;
		private ArrayList<String> outList;

		public IOListContainer(ArrayList<String> inList,
				ArrayList<String> outList) {
			this.inList = inList;
			this.outList = outList;
		}

		public ArrayList<String> getInList() {
			return this.inList;
		}

		public ArrayList<String> getOutList() {
			return this.outList;
		}
	}

	/**
	 * Inner class that encapsulates a pair of Output-Input for signal mapping.
	 * 
	 * @author Martin Wichmann
	 */
	private class SignalMappingContainer {
		private Output output;
		private Input input;

		public SignalMappingContainer(Output output, Input input) {
			this.output = output;
			this.input = input;
		}

		public Output getOutput() {
			return this.output;
		}

		public Input getInput() {
			return this.input;
		}
	}

	/**
	 * Default constructor.
	 */
	public XMLImport() {
	}

	public final Document buildDOM(File f) {
		// Create DOM Document builder
		DocumentBuilderFactory buildFac = DocumentBuilderFactory.newInstance();

		// TODO enable xsd validation
		buildFac.setValidating(false);
		buildFac.setNamespaceAware(true);
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			buildFac.setSchema(schemaFactory.newSchema(new File(XSDFILE)));
		} catch (SAXException e) {
			e.printStackTrace();
		}

		DocumentBuilder builder = null;
		try {
			builder = buildFac.newDocumentBuilder();
			builder.setErrorHandler(new SimpleErrorHandler());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		// get XML element of top level block and signal mapping
		try {
			Document document = builder.parse(f);
			return document;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Load an xml file and return the content wrapped in a CompoundBlock. Also
	 * takes care of the signal mapping.
	 * 
	 * @param f
	 *            XML file to load
	 * @return Top level CompoundBlock
	 */
	public final CompoundBlock loadDocument(Document d) {

		Element topLevelBlockElement = null;
		String topLevelBlockUUID = "";
		Element signalMappingElement = null;

		Element rootElement = d.getDocumentElement();
		NodeList nodes = rootElement.getChildNodes();

		// parse top level of xml, for top CompoundBlock and SignalMapping
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				Element child = (Element) node;
				if (child.getNodeName() == "CompoundBlock") {
					topLevelBlockElement = child;
					topLevelBlockUUID = child.getAttribute("uuid");
				}
				if (child.getNodeName() == "SignalMapping") {
					signalMappingElement = child;
				}
			}
		}

		// TODO: replace with Exception
		CompoundBlock parsedBlock = null;

		if (topLevelBlockElement != null && signalMappingElement != null) {
			// parse top level CompoundBlock
			IOListContainer temp = loadCompoundBlock(topLevelBlockElement);

			// create top level CompoundBlock
			parsedBlock = new CompoundBlock(topLevelBlockUUID, temp.inList,
					temp.outList);
			blockHash.put(topLevelBlockUUID, parsedBlock);

			// load signal mapping from xml...
			ArrayList<SignalMappingContainer> mapping = loadSignalMapping(signalMappingElement);
			// ...and map the signals
			mapSignals(mapping);
		}

		// return the top level CompoundBlock
		return parsedBlock;
	}

	/**
	 * Load a CompoundBlock element. Can be used recursively to traverse the
	 * tree.
	 * 
	 * @return List of IO names, for the parent CompoundBlock.
	 */
	private final IOListContainer loadCompoundBlock(Element element) {

		ArrayList<String> inList = new ArrayList<>();
		ArrayList<String> outList = new ArrayList<>();

		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node instanceof Element) {

				Element child = (Element) node;

				if (child.getNodeName() == "CompoundBlock") {
					// load child CompoundBlocks recursively
					IOListContainer temp = loadCompoundBlock(child);
					CompoundBlock currentBlock = new CompoundBlock(
							child.getAttribute("uuid"), temp.inList,
							temp.outList);

					blockHash.put(currentBlock.getBlockID(), currentBlock);

				} else if (child.getNodeName() == "BaseBlock") {

					BaseBlock baseBlock = loadBaseBlock(child);
					blockHash.put(baseBlock.getBlockID(), baseBlock);

				} else if (child.getNodeName() == "in") {
					inList.add(child.getAttribute("name"));
				} else if (child.getNodeName() == "out") {
					outList.add(child.getAttribute("name"));
				}

			}
		}

		return new IOListContainer(inList, outList);
	}

	/**
	 * Parse the top level xml tag 'SignalMapping' and return an ArrayList of
	 * signals to map,
	 * 
	 * @param element
	 *            Top level xml element
	 * @return List containing the signal mapping
	 */
	private final ArrayList<SignalMappingContainer> loadSignalMapping(
			Element element) {

		ArrayList<SignalMappingContainer> signalMapping = new ArrayList<>();

		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node instanceof Element) {
				Element child = (Element) node;

				// retrieve attributes
				String fromUUID = child.getAttribute("from");
				String toUUID = child.getAttribute("to");

				// parse the attributes to strings
				String fromBlockUUID = fromUUID.substring(0,
						fromUUID.indexOf("_"));
				String fromPortUUID = fromUUID.substring(
						fromUUID.indexOf("_") + 1, fromUUID.length());
				String toBlockUUID = toUUID.substring(0, toUUID.indexOf("_"));
				String toPortUUID = toUUID.substring(toUUID.indexOf("_") + 1,
						toUUID.length());

				Signal fromSignal = null;
				Signal toSignal = null;

				// get Signals for the UUIDs from XML
				Block currentBlock = blockHash.get(fromBlockUUID);
				// TODO The differentiation between CompoundBlock and other
				// Blocks (BaseBlocks), is necessary, because the method
				// signature is different ('.output' vs. '.internalInput')
				if (currentBlock instanceof CompoundBlock) {
					CompoundBlock temp = (CompoundBlock) currentBlock;
					fromSignal = temp.internalInput(fromPortUUID);
				} else {
					fromSignal = currentBlock.output(fromPortUUID);
				}

				currentBlock = blockHash.get(toBlockUUID);
				if (currentBlock instanceof CompoundBlock) {
					CompoundBlock temp = (CompoundBlock) currentBlock;
					toSignal = temp.internalOutput(toPortUUID);
				} else {
					toSignal = currentBlock.input(toPortUUID);
				}

				signalMapping.add(new SignalMappingContainer(
						(Output) fromSignal, (Input) toSignal));
			}
		}

		return signalMapping;
	}

	/**
	 * Connect the signals in list 'mapping'.
	 * 
	 * @param mapping
	 *            List containg signals to map
	 */
	private final void mapSignals(ArrayList<SignalMappingContainer> mapping) {
		for (SignalMappingContainer entry : mapping) {
			entry.getOutput().connectTo(entry.getInput());
		}
	}

	/**
	 * Load a BaseBlock from XML element.
	 * 
	 * @param element
	 *            BaseBlock XML element.
	 * @return Parsed BaseBlock
	 */
	private final BaseBlock loadBaseBlock(Element element) {
		// List of IO names.
		// TODO: currently not used!
		ArrayList<String> inList = new ArrayList<>();
		ArrayList<String> outList = new ArrayList<>();

		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node instanceof Element) {
				Element child = (Element) node;

				String IOName = child.getAttribute("name");
				String IOType = child.getNodeName();

				if (IOType.equals("in")) {
					inList.add(IOName);
				} else if (IOType.equals("out")) {
					outList.add(IOName);
				}
			}
		}

		String baseBlockType = element.getAttribute("type");
		String baseBlockUUID = element.getAttribute("uuid");

		BaseBlock ret = null;

		// Create BaseBlock according to 'type' in XML
		switch (baseBlockType) {
		case "and":
			ret = new AND(baseBlockUUID, inList.size());
			break;
		case "or":
			ret = new OR(baseBlockUUID, inList.size());
			break;
		case "nand":
			ret = new NAND(baseBlockUUID, inList.size());
			break;
		case "nor":
			ret = new NOR(baseBlockUUID, inList.size());
			break;
		case "not":
			ret = new NOT(baseBlockUUID);
			break;
		default:
			assert false;
			break;
		}

		return ret;
	}

}
