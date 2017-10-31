package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/*import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;*/
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class CommitView extends ViewPart {

	public static final String ID = "view.CommitView"; //$NON-NLS-1$
	private Text textAuthor;
	private Text textTag;
	private Text textCommit;

	public CommitView() {
		setTitleImage(ResourceManager.getPluginImage("Plugin", "icons/favicon.ico"));
		setTitleToolTip("Commit View\r\n");
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */ 
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.right = new FormAttachment(0, 65);
		fd_lblNewLabel.top = new FormAttachment(0, 334);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setForeground(SWTResourceManager.getColor(0, 0, 0));
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setText("Author :");
		
		textAuthor = new Text(container, SWT.BORDER);
		FormData fd_textAuthor = new FormData();
		fd_textAuthor.right = new FormAttachment(0, 584);
		fd_textAuthor.top = new FormAttachment(0, 331);
		fd_textAuthor.left = new FormAttachment(0, 71);
		textAuthor.setLayoutData(fd_textAuthor);
		
		Label lblTag = new Label(container, SWT.NONE);
		FormData fd_lblTag = new FormData();
		fd_lblTag.right = new FormAttachment(0, 65);
		fd_lblTag.top = new FormAttachment(0, 365);
		fd_lblTag.left = new FormAttachment(0, 10);
		lblTag.setLayoutData(fd_lblTag);
		lblTag.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTag.setText("Tag :");
		
		textTag = new Text(container, SWT.BORDER);
		FormData fd_textTag = new FormData();
		fd_textTag.right = new FormAttachment(0, 583);
		fd_textTag.top = new FormAttachment(0, 362);
		fd_textTag.left = new FormAttachment(0, 71);
		textTag.setLayoutData(fd_textTag);
		
		Button btnCommit = new Button(container, SWT.NONE);
		FormData fd_btnCommit = new FormData();
		fd_btnCommit.right = new FormAttachment(0, 584);
		fd_btnCommit.top = new FormAttachment(0, 397);
		fd_btnCommit.left = new FormAttachment(0, 509);
		btnCommit.setLayoutData(fd_btnCommit);
		btnCommit.setForeground(SWTResourceManager.getColor(0, 0, 0));
		btnCommit.setText("Commit");
		
		textCommit =  new Text(container, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_textCommit = new FormData();
		fd_textCommit.top = new FormAttachment(0, 10);
		fd_textCommit.bottom = new FormAttachment(0, 316);
		fd_textCommit.right = new FormAttachment(0, 584);
		fd_textCommit.left = new FormAttachment(0, 10);
		textCommit.setLayoutData(fd_textCommit);
	    
		btnCommit.addSelectionListener(new SelectionAdapter() 
		{
			@Override public void widgetSelected(final SelectionEvent e){
				if(RepoView.isValidSession() == true){
			    	commitIS(textAuthor.getText(), textTag.getText(), textCommit.getText());
			    }
			}
		});
		
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	private void commitIS(String CommitISAuthor, String CommitISTag, String CommitISCommitMessage){

		// HTTP GET request
		try {

			String url = "http://"+RepoView.host+":"+RepoView.port+"/commit?author="+URLEncoder.encode(CommitISAuthor.toLowerCase(), "UTF-8")
					+"&message="+URLEncoder.encode(CommitISCommitMessage.toLowerCase(), "UTF-8")
					+"&tag="+URLEncoder.encode(CommitISTag.toLowerCase(), "UTF-8")
					+"&imageName=containerImage"+"&sessionId="+RepoView.sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", RepoView.USER_AGENT);
			String cookie = RepoView.getCookie();
			con.setRequestProperty("Cookie", cookie);
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result

			if(response.toString().startsWith("Commit Successful")){
				MessageDialog.openInformation(null, "", "Commit Successful : "+ response.toString().split(":")[1]);
			}else{
				MessageDialog.openError(null, "", "Unable To Commit");
			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	
	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the tool bar.
	 */
	private void initializeToolBar() {
		/*IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();*/
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
	/*	IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();*/
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
