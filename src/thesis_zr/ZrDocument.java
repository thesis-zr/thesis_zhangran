package thesis_zr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ZrDocument {

	private Document doc = null;
	
	private Map<String, ZrNode> nodeMap = new HashMap<String, ZrNode>();
	
	private ZrNode rootNode = null;
	
	public void load(String filename) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(new EntityResolver() {		// disable dtd validation
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if (systemId.contains("DesignRationale.dtd")) {
                    return new InputSource(new StringReader(""));
                }
                else {
                    return null;
                }
            }
        });

		doc = db.parse(new File(filename));
		nodeMap.clear();
		rootNode = null;
	}
	
	public void save(String newfilename) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, IOException {
        FileOutputStream fos = new FileOutputStream(new File(newfilename));
        try {
	        DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance();
	        DOMImplementationLS impl = (DOMImplementationLS) reg.getDOMImplementation("LS");
	        LSSerializer serializer = impl.createLSSerializer();
	        LSOutput lso = impl.createLSOutput();
	        lso.setByteStream(fos);
	        serializer.write(doc, lso);
        } finally {
	        fos.close();
	    }
    }

	public boolean isLoaded() {
		return (doc != null);
	}
	
	public ZrNode getRootNode() {
		return rootNode;
	}
	
	public Map<String, ZrNode> getNodeMap() {
		return nodeMap;
	}

	public void advParse() throws Exception {
		NodeList nodeList;
		
		nodeList = doc.getElementsByTagName("node");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element n = (Element)nodeList.item(i);

			ZrNode zn = new ZrNode();
			switch(Integer.parseInt(n.getAttribute("type"))) {
				case ZrNode.TYPE_INTENT:
					zn.nodeType = ZrNode.TYPE_INTENT;
					break;
				case ZrNode.TYPE_OPTION:
					zn.nodeType = ZrNode.TYPE_OPTION;
					break;
				case ZrNode.TYPE_OPERATION:
					zn.nodeType = ZrNode.TYPE_OPERATION;
					break;
				case ZrNode.TYPE_JUSTIFICATION:
					zn.nodeType = ZrNode.TYPE_JUSTIFICATION;
					break;
				case ZrNode.TYPE_DECISION:
					zn.nodeType = ZrNode.TYPE_DECISION;
					break;
				default:
					throw new Exception("Node " + n.getAttribute("id") + " has invalid type");
			}
			zn.domNode = n;
			
			nodeMap.put(n.getAttribute("id"), zn);
		}
		
		nodeList = doc.getElementsByTagName("link");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element n = (Element)nodeList.item(i);

			ZrLink zl = new ZrLink();
			switch(n.getAttribute("label")) {
				case "decomposed-into":
					zl.linkType = ZrLink.TYPE_DECOMPOSED_INTO;
					break;
				case "achieve-by":
					zl.linkType = ZrLink.TYPE_ACHIEVE_BY;
					break;
				case "decide-by":
					zl.linkType = ZrLink.TYPE_DECIDE_BY;
					break;
				case "refer-to":
					zl.linkType = ZrLink.TYPE_REFER_TO;
					break;
				case "realized-to":
					zl.linkType = ZrLink.TYPE_REALIZED_TO;
					break;
				case "initiate":
					zl.linkType = ZrLink.TYPE_INITIATE;
					break;
				case "return-to":
					zl.linkType = ZrLink.TYPE_RETURN_TO;
					break;
				case "evolve-to":
					zl.linkType = ZrLink.TYPE_EVOLVE_TO;
					break;
				default:
					throw new Exception("Link " + n.getAttribute("id") + " has invalid type");
			}
			zl.domNode = n;
			zl.to = nodeMap.get(n.getAttribute("to"));
			
			ZrNode zn = nodeMap.get(n.getAttribute("from"));
			zn.linkList.add(zl);
		}
		
		String[] keySet = (String[]) nodeMap.keySet().toArray();
		for (int i = 0; i < keySet.length; i++) {
			ZrNode zn = nodeMap.get(keySet[i]);
			boolean found = false;
			for (int j = 0; j < keySet.length; j++) {
				ZrNode zn2 = nodeMap.get(keySet[i]);
				for (int k = 0; k < zn2.linkList.size(); k++) {
					if (zn2.linkList.get(k).to == zn) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
			if (!found) {
				rootNode = zn;
				break;
			}
		}
		if (rootNode == null) {
			throw new Exception("can not find root node");
		}
	}
}
