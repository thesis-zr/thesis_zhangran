package thesis_zr;

public class Main {

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: thesis_zr <src-document> <dst-document>");
			System.exit(1);
		}

		String srcDoc = args[1];
		String dstDoc = args[2];
		ZrDocument docObj = new ZrDocument();

		System.out.println("Loading source document " + srcDoc + ".");
		docObj.load(srcDoc);

		System.out.println("Processing...");

		
		System.out.println("Saving to " + dstDoc + ".");
		docObj.save(dstDoc);

		System.out.println("Complete.");
		System.exit(0);
	}

}
