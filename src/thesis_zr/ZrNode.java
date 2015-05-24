package thesis_zr;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

public class ZrNode {
	
	public final static int TYPE_INTENT = 2;
	public final static int TYPE_OPTION = 3;
	public final static int TYPE_OPERATION = 4;
	public final static int TYPE_JUSTIFICATION = 5;
	public final static int TYPE_DECISION = 8;

	public int nodeType;
	
	public List<ZrLink> linkList = new ArrayList<ZrLink>();

	public Element domNode;
}
