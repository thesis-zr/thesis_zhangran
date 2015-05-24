package thesis_zr;

import java.util.ArrayList;
import java.util.List;

public class ZrProcessor {

	ZrDocument docObj = null;
	
	public ZrProcessor(ZrDocument docObj) {
		this.docObj = docObj;
	}

	public void process() {
		processImpl(this.docObj.getRootNode());
	}	
	
	private void processImpl(ZrNode node) {
		ZrNode evolveTo = null;
		for (int i = 0; i < node.linkList.size(); i++) {
			ZrLink zl = node.linkList.get(i);
			if (zl.linkType == ZrLink.TYPE_EVOLVE_TO) {
				evolveTo = zl.to;
				break;
			}
		}

		if (evolveTo != null) {
			// this node has an evolve-to, do evolve-to infering
			for (int i = 0; i < node.linkList.size(); i++) {
				ZrLink zl = node.linkList.get(i);
				if (zl.linkType == ZrLink.TYPE_ACHIEVE_BY) {
					docObj.removeSubtreeOfNode(zl.to);
					docObj.changeParent(zl.to, node, evolveTo);
				}
			}
			docObj.replaceNode(node, evolveTo);
			processImpl(evolveTo);
			return;
		} else {
			// this node does not have an evolve-to, do recursing
			List<ZrNode> toList = new ArrayList<ZrNode>();
			for (int i = 0; i < node.linkList.size(); i++) {
				ZrLink zl = node.linkList.get(i);
				if (zl.linkType == ZrLink.TYPE_REFER_TO) {
					continue;
				}
				if (zl.linkType == ZrLink.TYPE_RETURN_TO) {
					continue;
				}
				toList.add(zl.to);
			}
			for (int i = 0; i < toList.size(); i++) {
				processImpl(toList.get(i));
			}
		}
	}	
}
