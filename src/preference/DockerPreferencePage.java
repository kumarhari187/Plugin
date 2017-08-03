package preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;

public class DockerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage  {

	/**
	 * Create the preference page.
	 */
	public DockerPreferencePage() {
	}

	/**
	 * Create contents of the preference page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group grpPreferencesForService = new Group(container, SWT.NONE);
		grpPreferencesForService.setText("General settings for Docker Listener");
		grpPreferencesForService.setLayout(new GridLayout(1, false));
		new Label(grpPreferencesForService, SWT.NONE);
		
		Label lblSeeSubpagesFor = new Label(grpPreferencesForService, SWT.NONE);
		lblSeeSubpagesFor.setText("See sub-pages for details");

		return container;
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}
}
