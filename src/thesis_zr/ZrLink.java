package thesis_zr;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ZrLink {

	public final static int TYPE_DECOMPOSED_INTO = 1;
	public final static int TYPE_ACHIEVE_BY = 2;
	public final static int TYPE_DECIDE_BY = 3;
	public final static int TYPE_REFER_TO = 4;
	public final static int TYPE_REALIZED_BY = 5;
	public final static int TYPE_INITIATE = 6;
	public final static int TYPE_RETURN_TO = 7;
	public final static int TYPE_EVOLVE_TO = 8;

	public Element domNode;
	
	public int linkType;
	
	public ZrNode to;
}
