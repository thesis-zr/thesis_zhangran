package thesis_zr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ZrDocument {

	private Document doc;
	
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
        
	public ZrNode getRootNode() {
		return null;
	}
}
