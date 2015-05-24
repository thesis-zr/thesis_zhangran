package thesis_zr;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Main {

	public static Display display = new Display();

	public static Shell shell = new Shell(display, SWT.DIALOG_TRIM);
	
	public static ZrDocument docObj = new ZrDocument();
	
	public static void main(String[] args) {
		GridLayout layout = new GridLayout();
		GridData gd = null;

		shell.setText("Converter");
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
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) { 
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				String file = dialog.open(); 
				if (file != null) { 
					text.setText(file); 
				} 
			} 
		});
		
		Button btnLoad = new Button(shell, SWT.PUSH);
		btnLoad.setText("Load");
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gd.widthHint = 100;
		btnLoad.setLayoutData(gd);
		btnLoad.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (text.getText().equals("")) {
					showError("No data file selected.");
					return;
				}
				
				File f = new File(text.getText());
				if (!f.exists()) {
					showError("Data file does not exist.");
					return;
				}
				if (f.isDirectory()) {
					showError("Data file is not a text file.");
					return;
				}

				try {
					docObj.load(f.getAbsolutePath());
				} catch (Exception ex) {
					showError("Failed to load from file " + f.getAbsolutePath() + ", " + ex.getMessage());
					return;
				}

				showNotice("Successfully loaded from file " + f.getAbsolutePath() + ".");
			}
		});

		Button btnProcess = new Button(shell, SWT.PUSH);
		btnProcess.setText("Process");
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gd.widthHint = 100;
		btnProcess.setLayoutData(gd);
		btnProcess.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!docObj.isLoaded()) {
					showError("No data file loaded.");
					return;
				}

				try {
					docObj.advParse();
				} catch (Exception ex) {
					showError("Failed to process data file, " + ex.getMessage());
					return;
				}

				showNotice("Process complete.s");
			}
		});
		
		Button btnSave = new Button(shell, SWT.PUSH);
		btnSave.setText("Save");
		gd = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
		gd.widthHint = 100;
		btnSave.setLayoutData(gd);
		btnSave.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (text.getText().equals("")) {
					showError("No data file selected.");
					return;
				}
				
				File f = new File(text.getText());
				if (f.isDirectory()) {
					showError("Data file is not a text file.");
					return;
				}

				try {
					docObj.save(f.getAbsolutePath());
				} catch (Exception ex) {
					showError("Failed to save to file " + f.getAbsolutePath() + ", " + ex.getMessage());
					return;
				}

				showNotice("Successfully saved to file " + f.getAbsolutePath() + ".");
			}
		});

		shell.setSize(700, 300);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		System.exit(0);
	}
	
	public static void showError(String message) {
		MessageBox box = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
		box.setText("Error");
		box.setMessage(message);
		box.open();
	}
	
	public static void showNotice(String message) {
		MessageBox box = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
		box.setText("Notice");
		box.setMessage(message);
		box.open();
	}
}
