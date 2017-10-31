package utils;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;


public class CommitView2 extends ViewPart {

	public static final String ID = "view.DockerCommit"; //$NON-NLS-1$

	private FormToolkit toolkit;
	private Form form;
	private Section unStagedChangesSection;
	private Section stagedChangesSection;
	private Section commitMessageSection;
	private Text text;
	private Text text_1;


	public CommitView2() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {

		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.getHead().setToolTipText("");

		unStagedChanges();
		stagedChanges();
		commitMessage();

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	private void unStagedChanges(){

		unStagedChangesSection = toolkit.createSection(form.getBody(), 
				Section.DESCRIPTION|Section.TITLE_BAR);
		unStagedChangesSection.setBounds(10, 10, 325, 233);
		TableWrapData unStagedChangesTableWrapData = new TableWrapData(TableWrapData.FILL);
		unStagedChangesTableWrapData.colspan = 2;
		unStagedChangesSection.setLayoutData(unStagedChangesTableWrapData);

		unStagedChangesSection.setText("Unstaged Changes");
		/*unStagedChangesSection.setDescription("This is the description that goes "+
				"below the title");*/
		Composite unStagedChangesSectionClient = toolkit.createComposite(unStagedChangesSection);
		unStagedChangesSection.setClient(unStagedChangesSectionClient);
		unStagedChangesSectionClient.setLayout(null);

		TreeViewer unStagedChangesTreeViewer = new TreeViewer(unStagedChangesSectionClient, SWT.BORDER);
		Tree unStagedChangesTree = unStagedChangesTreeViewer.getTree();
		unStagedChangesTree.setBounds(0, 0, 313, 186);
		toolkit.paintBordersFor(unStagedChangesTree);

	}


	private void  stagedChanges(){

		stagedChangesSection = toolkit.createSection(form.getBody(), 
				Section.DESCRIPTION|Section.TITLE_BAR);
		stagedChangesSection.setBounds(10, 265, 325, 233);
		TableWrapData stagedChangesTableWrapData = new TableWrapData(TableWrapData.FILL);
		stagedChangesTableWrapData.colspan = 2;
		stagedChangesSection.setLayoutData(stagedChangesTableWrapData);

		stagedChangesSection.setText("Staged Changes");
		/*stagedChangesSection.setDescription("This is the description that goes "+
				"below the title");*/
		Composite stagedChangesSectionClient = toolkit.createComposite(stagedChangesSection);
		stagedChangesSection.setClient(stagedChangesSectionClient);
		stagedChangesSectionClient.setLayout(null);

		TreeViewer stagedChangesTreeViewer = new TreeViewer(stagedChangesSectionClient, SWT.BORDER);
		Tree stagedChangesTree = stagedChangesTreeViewer.getTree();
		stagedChangesTree.setSize(313, 186);
		toolkit.paintBordersFor(stagedChangesTree);

	}

	private void commitMessage(){

		commitMessageSection = toolkit.createSection(form.getBody(), 
				Section.DESCRIPTION|Section.TITLE_BAR);
		commitMessageSection.setBounds(344, 10, 325, 479);
		TableWrapData commitMessageTableWrapData = new TableWrapData(TableWrapData.FILL);
		commitMessageTableWrapData.colspan = 2;
		commitMessageSection.setLayoutData(commitMessageTableWrapData);

		commitMessageSection.setText("Commit Message");
		/*unStagedChangesSection.setDescription("This is the description that goes "+
				"below the title");*/
		Composite commitMessageSectionClient = toolkit.createComposite(commitMessageSection);
		commitMessageSection.setClient(commitMessageSectionClient);
		commitMessageSectionClient.setLayout(null);

		TreeViewer commitMessageTreeViewer = new TreeViewer(commitMessageSectionClient, SWT.BORDER);
		Tree commitMessageTree = commitMessageTreeViewer.getTree();
		commitMessageTree.setBounds(0, 0, 313, 322);
		toolkit.paintBordersFor(commitMessageTree);
		
		Label lblAuthor = new Label(commitMessageSectionClient, SWT.NONE);
		lblAuthor.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		lblAuthor.setBounds(0, 344, 55, 15);
		toolkit.adapt(lblAuthor, true, true);
		lblAuthor.setText("Author :");
		
		text = new Text(commitMessageSectionClient, SWT.BORDER);
		text.setBounds(61, 341, 252, 21);
		toolkit.adapt(text, true, true);
		
		Label lblTag = new Label(commitMessageSectionClient, SWT.NONE);
		lblTag.setBounds(0, 379, 55, 15);
		toolkit.adapt(lblTag, true, true);
		lblTag.setText("Tag :");
		
		text_1 = new Text(commitMessageSectionClient, SWT.BORDER);
		text_1.setBounds(61, 376, 252, 21);
		toolkit.adapt(text_1, true, true);
		
		Button btnCommit = new Button(commitMessageSectionClient, SWT.NONE);
		btnCommit.setBounds(238, 415, 75, 25);
		toolkit.adapt(btnCommit, true, true);
		btnCommit.setText("Commit");

	}

	
	
	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
