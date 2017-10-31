package perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import view.CommitView;
import view.DockerView;
import view.RepoView;

public class DockerPerspective implements IPerspectiveFactory {

	/**
	 * Creates the initial layout for a page.
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);
        layout.addView(RepoView.ID, IPageLayout.LEFT, 0.25f, editorArea);
        layout.getViewLayout(RepoView.ID).setCloseable(false);

        layout.addStandaloneView(DockerView.ID, false, IPageLayout.TOP, 0.75f, editorArea);
        layout.addStandaloneViewPlaceholder(DockerView.ID, IPageLayout.TOP, 0.75f, editorArea, false);
        
        layout.addStandaloneView(CommitView.ID, false, IPageLayout.BOTTOM, 0.75f, editorArea);
        layout.addStandaloneViewPlaceholder(CommitView.ID, IPageLayout.BOTTOM, 0.75f, editorArea, false);
		
		addFastViews(layout);
		addViewShortcuts(layout);
		addPerspectiveShortcuts(layout);
	}

	/**
	 * Add fast views to the perspective.
	 */
	private void addFastViews(IPageLayout layout) {
	}

	/**
	 * Add view shortcuts to the perspective.
	 */
	private void addViewShortcuts(IPageLayout layout) {
	}

	/**
	 * Add perspective shortcuts to the perspective.
	 */
	private void addPerspectiveShortcuts(IPageLayout layout) {
	}

}
