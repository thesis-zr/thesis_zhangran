package thesis_zr;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Main {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display, SWT.DIALOG_TRIM);
		GridLayout layout = new GridLayout();
		GridData gd = null;

		shell.setLayout(layout);
		layout.numColumns = 4;
		layout.makeColumnsEqualWidth = true;
		layout.marginHeight = 50;
		layout.marginWidth = 50;
		
		Label label = new Label(shell, SWT.NULL);
		label.setText("Data file:");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.horizontalSpan = 4;
		label.setLayoutData(gd);
		
		Text text = new Text(shell, SWT.NULL);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 3;
		text.setLayoutData(gd);

		Button btnBrowse = new Button(shell, SWT.PUSH);
		btnBrowse.setText("Browse...");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		btnBrowse.setLayoutData(gd);
		
		Button btnLoad = new Button(shell, SWT.PUSH);
		btnLoad.setText("Load");
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gd.widthHint = 100;
		btnLoad.setLayoutData(gd);

		Button btnProcess = new Button(shell, SWT.PUSH);
		btnProcess.setText("Process");
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gd.widthHint = 100;
		btnProcess.setLayoutData(gd);

		Button btnSave = new Button(shell, SWT.PUSH);
		btnSave.setText("Save");
		gd = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
		gd.widthHint = 100;
		btnSave.setLayoutData(gd);

		shell.setSize(700, 300);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		System.exit(0);
		
		
		
		
		
		
		
		
		
		
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
