package thesis_zr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

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

	/* Quirks:
	 * 1. the _from_ and _to_ in xml seems to be reversely stored 
	 */

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

		// process all the <node>
		nodeList = doc.getElementsByTagName("node");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element n = (Element)nodeList.item(i);

			ZrNode zn = new ZrNode();
			switch(Integer.parseInt(n.getAttribute("type"))) {
				case ZrNode.TYPE_INBOX: 
					zn.nodeType = ZrNode.TYPE_INBOX;
					break;
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
		
		// process all the <link>
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
				case "realized-by":
					zl.linkType = ZrLink.TYPE_REALIZED_BY;
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
			zl.to = nodeMap.get(n.getAttribute("from"));
			
			ZrNode zn = nodeMap.get(n.getAttribute("to"));
			zn.linkList.add(zl);
		}
		
		// get root node
		Object[] keys = nodeMap.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			ZrNode zn = nodeMap.get((String)keys[i]);

			if (zn.domNode.getAttribute("id").equals("1270011432466020442")) {
				int x = 1;
				x++;
			}
			
			// root node should have no link points to it
			boolean found = false;
			for (int j = 0; j < keys.length; j++) {
				ZrNode zn2 = nodeMap.get((String)keys[j]);
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
			if (found) {
				continue;
			}
			// only INTENT can be root node
			if (zn.nodeType != ZrNode.TYPE_INTENT) {
				continue;
			}
			// there's an special node with label "Home Window", which should be excluded
			if (zn.domNode.getAttribute("label").equals("Home Window")) {
				continue;
			}
			
			rootNode = zn;
			break;
		}
		if (rootNode == null) {
			throw new Exception("can not find root node");
		}
	}
	
	public void removeNodeWithSubTree(ZrNode node) {
		Object[] keys;
		
		// remove links _to_ this node
		keys = nodeMap.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			ZrNode zn = nodeMap.get((String)keys[i]);
			for (int j = 0; j < zn.linkList.size(); j++) {
				ZrLink zl = zn.linkList.get(i);
				if (zl.to == node) {
					zl.domNode.getParentNode().removeChild(zl.domNode);
					zn.linkList.remove(j);
					j--;
				}
			}
		}

		// remove links _from_ this node and recurse into the subtree
		for (int i = 0; i < node.linkList.size(); i++) {
			ZrLink zl = node.linkList.get(i);
			if (zl.linkType != ZrLink.TYPE_RETURN_TO && zl.linkType != ZrLink.TYPE_EVOLVE_TO) {
				removeNodeWithSubTree(zl.to);
			}
			zl.domNode.getParentNode().removeChild(zl.domNode);
		}
		
		// remove this node
		node.domNode.getParentNode().removeChild(node.domNode);
		keys = nodeMap.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			ZrNode zn = nodeMap.get((String)keys[i]);
			if (zn == node) {
				nodeMap.remove((String)keys[i]);
				break;
			}
		}
	}

	public void removeSubtreeOfNode(ZrNode node) {
		for (int i = 0; i < node.linkList.size(); i++) {
			ZrLink zl = node.linkList.get(i);
			if (zl.linkType == ZrLink.TYPE_REFER_TO) {
				continue;
			}
			
			removeNodeWithSubTree(zl.to);

			zl.domNode.getParentNode().removeChild(zl.domNode);
			node.linkList.remove(i);
			i--;
		}
	}
	
	public void replaceNode(ZrNode oldNode, ZrNode newNode) {
		Object[] keys;

		// change all the links _to_ this node
		keys = nodeMap.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			ZrNode zn = nodeMap.get((String)keys[i]);
			for (int j = 0; j < zn.linkList.size(); j++) {
				ZrLink zl = zn.linkList.get(i);
				if (zl.to == oldNode) {
					zl.domNode.setAttribute("from", newNode.domNode.getAttribute("to"));
					zl.to = newNode;
				}
			}
		}

		// remove oldNode
		removeNodeWithSubTree(oldNode);
	}
	
	public void changeParent(ZrNode node, ZrNode oldParent, ZrNode newParent) {
		for (int i = 0; i < oldParent.linkList.size(); i++) {
			ZrLink zl = oldParent.linkList.get(i);
			if (zl.to == node) {
				zl.domNode.setAttribute("to", newParent.domNode.getAttribute("id"));
				oldParent.linkList.remove(i);
				newParent.linkList.add(zl);
			}
		}
	}
}
