package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import dialog.AddContainer;

public class DockerView extends ViewPart {

	public static final String ID = "viewpart.FirstViewTab"; //$NON-NLS-1$

	private Button btnSyncIS;
	private Button btnCommitIS;
	private Button btnAddContainer;
	private Button btnStart;
	private Button btnStop;
	private Button btnContainerDelete;
	private List list;
	private Text textImagename;
	private Text textAuthor;
	private Text textCommitMessage;
	private Text textTag;
	private Label lblTag;
	private Label lblAuthor;
	private Label lblCommitMessage;
	private final String USER_AGENT = "Mozilla/5.0";
	private Label lblUrl;
	private Label lblUrldisplay;
	private Link link;
	private static String sessionId;
	private static String port;
	private static String host;
	private Composite container;
	private Map<String,Button> userItems;
	private String imageName = null;
	public Button btnName;
	private AddContainer addCon;
	private String ImageNamePara;
	private String containerImage;

	public static String currentContainer;
	public static String currentImage;

	private FormData fd_btnAddContainer;
	private Button btnRefresh;
	public Composite btnComposite;
	private ScrolledComposite scrolledComposite;

	public static boolean showButtonActions = false;
	public static boolean getISResponseCode = false;
	public static String getISResponse;

	public static String getSessionId() {
		return sessionId;
	}

	public static void setSessionId(String sessionId) {
		DockerView.sessionId = sessionId;
	}

	public static String getPort() {
		return port;
	}

	public static void setPort(String port) {
		DockerView.port = port;
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		DockerView.host = host;
	}


	public DockerView() {
		setTitleImage(ResourceManager.getPluginImage("Plugin", "icons/favicon.ico"));
		setTitleToolTip("This is the  first view\r\n");
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */

	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		container.setLayout(new org.eclipse.swt.layout.FormLayout());
		initView();
		disableButtons();
		viewActionLayout();
		userItemLayout();
		hideActions();
		startNewContainerViewLayout();
		containerActionViewLayout();
		commitViewLayout();
		container.setTabList(new Control[]{btnSyncIS, list, btnCommitIS});
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 * @throws IOException 
	 */
	private void createActions() {

	}

	private void viewActionLayout(){

		btnRefresh = new Button(container, SWT.NONE);

		btnRefresh.addSelectionListener(new SelectionAdapter() 
		{
			@Override public void widgetSelected(final SelectionEvent e) 
			{

				for (Control control : btnComposite.getChildren()) {
					control.dispose();
				}
				userItemLayout();
				//new Button(btnComposite, SWT.NONE).setText("Delete");

				// DO THIS:
				btnComposite.layout(true, true);
				// .. and it will work
			}
		});

		/*btnRefresh.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {*/

		//System.out.println(btnName.getText().contains(CIName) + " *****-*-*-*-*-*-*-*-*");
		//userItemLayout();

		/*btnComposite.pack();
				btnComposite.layout(true);*/

		//				btnComposite.layout();
		//				userItemLayout();
		//				if(!btnName.getText().equals(CIName)){
		//					
		//					System.out.println("/******************/");
		//				}
		//				else{
		//					container.layout();
		//					System.out.println("//////////////////////");
		//					userItemLayout();
		//
		//				}
		//System.out.println(btnName.getText() + "************" + CIName);
		//PlatformUI.getWorkbench().restart();
		//getSite().getPage().resetPerspective();
		//getSite().getPage().findView("view.DockerView").getViewSite().getPage().resetPerspective();
		//createPartControl(container);
		//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().resetPerspective();
		/*	}
		});	*/

		btnRefresh.setImage(ResourceManager.getPluginImage("Plugin", "icons/ajax-refresh-icon.gif"));
		FormData fd_btnRefresh = new FormData();
		fd_btnRefresh.right = new FormAttachment(btnAddContainer, -6);
		fd_btnRefresh.left = new FormAttachment(0, 11);
		fd_btnRefresh.top = new FormAttachment(0, 43);
		fd_btnRefresh.height = 24;
		fd_btnRefresh.width = 24;
		btnRefresh.setLayoutData(fd_btnRefresh);

	}

	public void updateUserItems(){

		String listContainer = null; 
		String listImage = null;
		String[] splitContainer = null;
		String[] splitImage  = null;


		if(isValidSession() == true){
			
			if(!userItems.isEmpty()){
				userItems.clear();
			}
			listContainer = getContainers();
			splitContainer = listContainer.toString().split(",");
			for(String splitString : splitContainer){
				if(!"".equals(splitString)){
					userItems.put("C_"+splitString, new Button(btnComposite, SWT.PUSH));
				}
			}

			listImage = getImages();
			splitImage = listImage.toString().split(",");
			for(String splitString : splitImage){
				if(!"".equals(splitString)){
					userItems.put("I_"+splitString, new Button(btnComposite, SWT.PUSH));
				}
			}
		}
	}

	public void userItemLayout(){

		updateUserItems();

		for(String key : userItems.keySet()){

			btnName = userItems.get(key);

			btnName.setLayoutData(new RowData(100, 50));

			if(key.startsWith("I")){

				int f = key.indexOf("_");
				int l = key.lastIndexOf("_");
				imageName = key.substring(f+1, l);
				btnName.setText(imageName);
				btnName.setToolTipText("Docker Image");
				btnName.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
				btnName.addMouseListener(new MouseAdapter() {

					public void mouseUp(MouseEvent e) {
						currentImage = ((Button)e.getSource()).getText()+"_"+sessionId;
						callShell(currentImage);
					}

				});
			}else{
				int f = key.indexOf("_");
				int l = key.lastIndexOf("_");
				btnName.setText(key.substring(f+1, l));
				btnName.setToolTipText("Docker Container");
				btnName.addMouseListener(new MouseAdapter() {

					public void mouseUp(MouseEvent e) {
						currentContainer = ((Button)e.getSource()).getText()+"_"+sessionId;
						showActions();
					}
				});
			}

		}

	}

	private void startNewContainerViewLayout(){

		btnAddContainer.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		fd_btnAddContainer = new FormData();
		fd_btnAddContainer.bottom = new FormAttachment(link, -17);
		fd_btnAddContainer.top = new FormAttachment(0, 28);
		fd_btnAddContainer.right = new FormAttachment(btnComposite, 104);
		fd_btnAddContainer.left = new FormAttachment(0, 54);
		fd_btnAddContainer.height = 52;
		fd_btnAddContainer.width = 50;
		btnAddContainer.setLayoutData(fd_btnAddContainer);
		btnAddContainer.setText("+");
		btnAddContainer.setToolTipText("Adding a New Container");
		btnAddContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));

		scrolledComposite.setContent(btnComposite);
		scrolledComposite.setMinSize(4, 100);
		

		btnAddContainer.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {

				//AddContainer addCon = new AddContainer(btnName.getShell());
				callShell("");
			}
		});

	}

	private void ListViewLayout(){

		ListViewer listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL |SWT.H_SCROLL);
		list = listViewer.getList();
		list.setVisible(false);
		FormData fd_list = new FormData();
		fd_list.top = new FormAttachment(btnSyncIS, 42);
		fd_list.bottom = new FormAttachment(lblTag, -22);
		fd_list.right = new FormAttachment(100, -56);
		fd_list.left = new FormAttachment(0, 54);
		list.setLayoutData(fd_list);

		RowLayout rowLayout = new RowLayout();
		rowLayout.center = true;
		rowLayout.justify = false;
		rowLayout.wrap = true;
		btnComposite.setLayout(rowLayout);
		
		

		FormData fd_btnComposite = new FormData();
		fd_btnComposite.bottom = new FormAttachment(btnAddContainer, 0, SWT.BOTTOM);
		fd_btnComposite.top = new FormAttachment(btnAddContainer, 0, SWT.TOP);
		fd_btnComposite.right = new FormAttachment(btnSyncIS, 0, SWT.RIGHT);
		fd_btnComposite.left = new FormAttachment(0, 123);
		btnComposite.setLayoutData(fd_btnComposite);

	}

	private void containerActionViewLayout(){

		FormData fd_btnStop = new FormData();
		fd_btnStop.top = new FormAttachment(link, 6);
		fd_btnStop.right = new FormAttachment(btnStart, 103, SWT.RIGHT);
		fd_btnStop.left = new FormAttachment(btnStart, 6);
		btnStop.setLayoutData(fd_btnStop);
		btnStop.setText("Stop");

		FormData fd_btnStart = new FormData();
		fd_btnStart.right = new FormAttachment(0, 147);
		fd_btnStart.top = new FormAttachment(0, 122);
		fd_btnStart.left = new FormAttachment(0, 54);
		btnStart.setLayoutData(fd_btnStart);
		btnStart.setText("Start");

		FormData fd_btnContainerDelete = new FormData();
		fd_btnContainerDelete.top = new FormAttachment(btnStart, 0, SWT.TOP);
		fd_btnContainerDelete.left = new FormAttachment(lblUrldisplay, 7, SWT.LEFT);
		fd_btnContainerDelete.right = new FormAttachment(100, -244);
		btnContainerDelete.setLayoutData(fd_btnContainerDelete);
		btnContainerDelete.setText("Delete");

		FormData fd_btnSyncIS = new FormData();
		fd_btnSyncIS.left = new FormAttachment(lblUrldisplay, 7);
		fd_btnSyncIS.right = new FormAttachment(textCommitMessage, 0, SWT.RIGHT);
		btnSyncIS.setLayoutData(fd_btnSyncIS);
		btnSyncIS.setText("Sync IS");

		fd_btnSyncIS.top = new FormAttachment(btnStart, 0, SWT.TOP);

		FormData fd_lblUrl = new FormData();
		fd_lblUrl.top = new FormAttachment(0, 96);
		fd_lblUrl.left = new FormAttachment(0, 54);
		lblUrl.setLayoutData(fd_lblUrl);
		lblUrl.setText("URL :");

		FormData fd_lblUrldisplay = new FormData();
		fd_lblUrldisplay.top = new FormAttachment(0, 96);
		fd_lblUrldisplay.right = new FormAttachment(100, -212);
		fd_lblUrldisplay.left = new FormAttachment(link, 13);
		lblUrldisplay.setLayoutData(fd_lblUrldisplay);
		lblUrldisplay.setText("URLDisplay");

		FormData fd_link = new FormData();
		fd_link.bottom = new FormAttachment(btnStart, -6);
		fd_link.top = new FormAttachment(0, 95);
		fd_link.right = new FormAttachment(lblUrl, 153, SWT.RIGHT);
		fd_link.left = new FormAttachment(lblUrl, 6);
		link.setLayoutData(fd_link);
		link.setText("<a>New Link</a>");

		btnSyncIS.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {

				list.removeAll();
				// HTTP GET request
				if(isValidSession()){
					syncIS();
				}else{
					MessageDialog.openInformation(null, "Login", "you must login to continue");
				}
				commitButtonsEnable();
			}
		});

		ListViewLayout();
	}

	private void commitViewLayout(){

		FormData fd_btnCommitIS = new FormData();
		fd_btnCommitIS.bottom = new FormAttachment(100, -40);
		fd_btnCommitIS.top = new FormAttachment(textAuthor, 9);
		fd_btnCommitIS.right = new FormAttachment(textAuthor, 0, SWT.RIGHT);
		fd_btnCommitIS.left = new FormAttachment(0, 54);
		btnCommitIS.setLayoutData(fd_btnCommitIS);
		btnCommitIS.setText("Commit IS");


		FormData fd_lblTag = new FormData();
		fd_lblTag.right = new FormAttachment(0, 189);
		fd_lblTag.top = new FormAttachment(100, -166);
		fd_lblTag.bottom = new FormAttachment(100, -151);
		fd_lblTag.left = new FormAttachment(0, 54);
		lblTag.setLayoutData(fd_lblTag);
		lblTag.setText("Tag");

		textTag.setToolTipText("Tag");
		FormData fd_textTag = new FormData();
		fd_textTag.top = new FormAttachment(100, -145);
		fd_textTag.right = new FormAttachment(0, 201);
		fd_textTag.left = new FormAttachment(0, 54);
		fd_textTag.bottom = new FormAttachment(100, -124);
		textTag.setLayoutData(fd_textTag);


		FormData fd_lblAuthor = new FormData();
		fd_lblAuthor.top = new FormAttachment(textTag, 6);
		fd_lblAuthor.bottom = new FormAttachment(textAuthor, -6);
		fd_lblAuthor.right = new FormAttachment(0, 189);
		fd_lblAuthor.left = new FormAttachment(0, 54);
		lblAuthor.setLayoutData(fd_lblAuthor);
		lblAuthor.setText("Author");
		textAuthor.setToolTipText("Author");
		FormData fd_textAuthor = new FormData();
		fd_textAuthor.left = new FormAttachment(0, 54);
		fd_textAuthor.right = new FormAttachment(textCommitMessage, -24);
		fd_textAuthor.top = new FormAttachment(100, -95);
		fd_textAuthor.bottom = new FormAttachment(100, -74);
		textAuthor.setLayoutData(fd_textAuthor);
		textCommitMessage.setToolTipText("Commit Message");
		FormData fd_textCommitMessage = new FormData();
		fd_textCommitMessage.top = new FormAttachment(textTag, 0, SWT.TOP);
		fd_textCommitMessage.bottom = new FormAttachment(100, -40);
		fd_textCommitMessage.left = new FormAttachment(0, 225);
		fd_textCommitMessage.right = new FormAttachment(100, -58);
		textCommitMessage.setLayoutData(fd_textCommitMessage);

		FormData fd_lblCommitMessage = new FormData();
		fd_lblCommitMessage.right = new FormAttachment(100, -276);
		fd_lblCommitMessage.left = new FormAttachment(lblTag, 36);
		fd_lblCommitMessage.top = new FormAttachment(lblTag, 0, SWT.TOP);
		lblCommitMessage.setLayoutData(fd_lblCommitMessage);
		lblCommitMessage.setText("Commit Message");

		btnCommitIS.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {

				// HTTP GET request
				String CommitISTag = textTag.getText();
				String CommitISAuthor = textAuthor.getText();
				String CommitISCommitMessage = textCommitMessage.getText();

				if("".equals(CommitISTag) && "".equals(CommitISAuthor) && "".equals(CommitISCommitMessage)){
					MessageDialog.openError(null, "Error", "Port and ImageName cannot be empty");
				}else{
					if(isValidSession()){
						commitIS(CommitISTag, CommitISAuthor, CommitISCommitMessage);
					}else{
						MessageDialog.openInformation(null, "Login", "you must login to continue");
					}
				}
				textImagename.setText(CommitISAuthor+"/"+CommitISTag);
				//	getIS(textPort.getText(), CommitISAuthor+"/"+CommitISTag);
				showActions();
				textAuthor.setText("");
				textCommitMessage.setText("");
				textTag.setText("");
				list.removeAll();

			}
		});

	}

	private void initView(){

		btnAddContainer = new Button(container, SWT.NONE);
		btnSyncIS = new Button(container, SWT.NONE);
		btnCommitIS = new Button(container, SWT.NONE);
		textAuthor = new Text(container, SWT.BORDER);
		textCommitMessage = new Text(container, SWT.BORDER);
		textTag = new Text(container, SWT.BORDER);
		lblUrldisplay = new Label(container, SWT.NONE);
		lblTag = new Label(container, SWT.NONE);
		lblAuthor = new Label(container, SWT.NONE);
		lblCommitMessage = new Label(container, SWT.NONE);
		lblUrl = new Label(container, SWT.NONE);
		scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL);
		scrolledComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.right = new FormAttachment(btnSyncIS, 0, SWT.RIGHT);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		btnComposite = new Composite(scrolledComposite, SWT.NONE);
		link = new Link(container, SWT.NONE);
		fd_scrolledComposite.left = new FormAttachment(link, 33, SWT.LEFT);
		fd_scrolledComposite.bottom = new FormAttachment(link, -17);
		fd_scrolledComposite.top = new FormAttachment(link, -71, SWT.TOP);
		link.setVisible(false);
		initDeleteContainerAction();
		initStartAction();
		initStopAction();
		hideActions();
		userItems = new HashMap<String,Button>();

	}

	private void commitButtonsEnable(){

		list.setVisible(true);
		btnCommitIS.setVisible(true);
		lblTag.setVisible(true);
		lblAuthor.setVisible(true);
		lblCommitMessage.setVisible(true);
		textAuthor.setVisible(true);
		textCommitMessage.setVisible(true);
		textTag.setVisible(true);

	}

	private void showActions(){
		lblUrldisplay.setVisible(false);
		btnSyncIS.setVisible(true);
		btnStart.setVisible(true);
		btnStop.setVisible(true);
		btnContainerDelete.setVisible(true);
		containerImage = getContainerDetails().split(":")[1];
		if(currentContainer != null && currentContainer.contains("False")){
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
			btnSyncIS.setEnabled(false);
		}else{
			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
			btnSyncIS.setEnabled(true);
		}
	}

	private void hideActions(){
		lblUrldisplay.setVisible(false);
		btnStart.setVisible(false);
		btnStop.setVisible(false);
		btnSyncIS.setVisible(false);
		btnContainerDelete.setVisible(false);
	}

	private String getContainerDetails(){

		StringBuffer response = null;
		// HTTP GET request
		try {

			String url = "http://"+host+":"+port+"/containerdetails?containerName="+URLEncoder.encode(currentContainer, "UTF-8")+"&sessionId="+sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			String cookie = getCookie();
			con.setRequestProperty("Cookie", cookie);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			if(responseCode == 200){
				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String inputLine;
				response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return response.toString();
	}

	private void initDeleteContainerAction(){
		btnContainerDelete = new Button(container, SWT.NONE);
		btnContainerDelete.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {

				// HTTP GET request
				try {
					//String GetISImageName = textImagename.getText();
					if(MessageDialog.openConfirm(null, "Delete", "Are you sure you want to delete the container")){

						String url = "http://"+host+":"+port+"/deletecontainer?container="+URLEncoder.encode(currentContainer, "UTF-8")+"&sessionId="+sessionId;

						URL obj = new URL(url);
						HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

						// optional default is GET
						con.setRequestMethod("GET");

						//add request header
						con.setRequestProperty("User-Agent", USER_AGENT);

						String cookie = getCookie();
						con.setRequestProperty("Cookie", cookie);

						int responseCode = con.getResponseCode();
						System.out.println("\nSending 'GET' request to URL : " + url);
						System.out.println("Response Code : " + responseCode);
						if(responseCode == 200){
							BufferedReader in = new BufferedReader(
									new InputStreamReader(con.getInputStream()));
							String inputLine;
							StringBuffer response = new StringBuffer();

							while ((inputLine = in.readLine()) != null) {
								response.append(inputLine);
							}
							in.close();

							//print result
							if(response.toString().equals("Container Deleted")){
								MessageDialog.openInformation(null, "DELETE", "Container deleted successful");
							}else{
								MessageDialog.openError(null,"DELETE", "Error deleteing the container");
							}
						}else{
							MessageDialog.openError(null, "", "Response Code : "+ responseCode + "\n" + "Response Message : "+ con.getResponseMessage());
						}
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

	}

	private void initStartAction(){
		btnStart = new Button(container, SWT.NONE);
		btnStart.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {
				// HTTP GET request
				try {
					//String GetISImageName = textImagename.getText();

					String url = "http://"+host+":"+port+"/startcontainer?container="+URLEncoder.encode(currentContainer, "UTF-8")+"&sessionId="+sessionId;

					URL obj = new URL(url);
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

					// optional default is GET
					con.setRequestMethod("GET");

					//add request header
					con.setRequestProperty("User-Agent", USER_AGENT);

					String cookie = getCookie();
					con.setRequestProperty("Cookie", cookie);

					int responseCode = con.getResponseCode();
					System.out.println("\nSending 'GET' request to URL : " + url);
					System.out.println("Response Code : " + responseCode);
					if(responseCode == 200){
						BufferedReader in = new BufferedReader(
								new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();

						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();

						//print result

						if(response.toString().equals("Container Started")){
							MessageDialog.openInformation(null, "START", "Container started successful");
							btnStart.setEnabled(false);
							btnStop.setEnabled(true);
							btnSyncIS.setEnabled(true);
						}else{
							MessageDialog.openError(null,"START", "Error starting the container");
						}

					}else{
						MessageDialog.openError(null, "", "Response Code : "+ responseCode + "\n" + "Response Message : "+ con.getResponseMessage());
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void initStopAction(){

		btnStop = new Button(container, SWT.NONE);
		btnStop.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {

				// HTTP GET request
				try {
					//String GetISImageName = textImagename.getText();

					String url = "http://"+host+":"+port+"/stopcontainer?container="+URLEncoder.encode(currentContainer, "UTF-8")+"&sessionId="+sessionId;

					URL obj = new URL(url);
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

					// optional default is GET
					con.setRequestMethod("GET");

					//add request header
					con.setRequestProperty("User-Agent", USER_AGENT);

					String cookie = getCookie();
					con.setRequestProperty("Cookie", cookie);

					int responseCode = con.getResponseCode();
					System.out.println("\nSending 'GET' request to URL : " + url);
					System.out.println("Response Code : " + responseCode);
					if(responseCode == 200){
						BufferedReader in = new BufferedReader(
								new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();

						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
						//print result

						if(response.toString().equals("Container Stopped")){
							MessageDialog.openInformation(null, "STOP", "Container stopped successful");
							btnStart.setEnabled(true);
							btnStop.setEnabled(false);
							btnSyncIS.setEnabled(false);
						}else{
							MessageDialog.openError(null,"STOP", "Error stopping the container");
						}
					}else{
						MessageDialog.openError(null, "", "Response Code : "+ responseCode + "\n" + "Response Message : "+ con.getResponseMessage());
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});
	}

	public void disableButtons(){

		lblUrl.setVisible(false);
		btnStart.setVisible(true);
		btnContainerDelete.setVisible(true);
		btnCommitIS.setVisible(false);
		lblUrldisplay.setVisible(false);
		lblTag.setVisible(false);
		lblAuthor.setVisible(false);
		lblCommitMessage.setVisible(false);
		textAuthor.setVisible(false);
		textCommitMessage.setVisible(false);
		textTag.setVisible(false);
		link.setVisible(false);

	}

	private void callShell(String imageName){

		ImageNamePara = imageName;

		addCon = new AddContainer(container.getShell(),ImageNamePara);

		addCon.open();

		if(showButtonActions){
			hideActions();
			btnCommitIS.setVisible(false);
			//lblUrldisplay.setVisible(false);
			lblTag.setVisible(false);
			lblAuthor.setVisible(false);
			lblCommitMessage.setVisible(false);
			textAuthor.setVisible(false);
			textCommitMessage.setVisible(false);
			textTag.setVisible(false);
			link.setVisible(false);
			list.setVisible(false);

			lblUrldisplay.setVisible(true);

			lblUrldisplay.setText("Loading ....");

		}

		if(getISResponseCode){

			Browser browser = new Browser(container, SWT.NONE);
			link.setText("<a>"+getISResponse+"</a>");
			browser.setUrl(link.toString());
			link.setVisible(true);
			showActions();
		}

	}	
	/*addCon.setTxtImagename(imageName);

			addCon.getBtnGetIs().addSelectionListener(new SelectionAdapter() {

				 public void widgetSelected(SelectionEvent e) {

					if(isValidSession()){
						if(!"".equals(addCon.getTxtPort()) && !"".equals(addCon.getTxtImagename())){

							getIS(addCon.getTxtPort(), addCon.getTxtImagename());

						}else{

							MessageDialog.openError(null, "Error", "Port and ImageName cannot be empty");
						}
					}else{
						MessageDialog.openInformation(null, "Login", "you must login to continue");
					}
				}

			});  

			addCon.getBtnDelete().addMouseListener(new MouseAdapter() {

				public void mouseUp(MouseEvent e) {

					if(isValidSession()){

						if("".equals(addCon.getTxtPort()) && "".equals(addCon.getTxtImagename())){
							MessageDialog.openError(null, "Error", "Port and ImageName cannot be empty");
						}else{
							initDeleteImageAction();
						}

					}else{
						MessageDialog.openInformation(null, "Login", "you must login to continue");
					}
					//shell.close();
				}
			});

			addCon.close();  */                 

	/*display.getDefault().syncExec(new Runnable() {
			public void run() {
				shell = new Shell(display);
				shell.setMinimumSize(new Point(60, 20));
				shell.setSize(350, 200);
				shell.setText("Get IS");
				shell.setImage(ResourceManager.getPluginImage("Plugin", "icons/image.gif"));
				shell.open();
				shell.layout();
				Label lblPort = new Label(shell, SWT.NONE);
				lblPort.setBounds(210, 23, 33, 15);
				lblPort.setText("Port :");

				textPort = new Text(shell, SWT.BORDER);
				textPort.setBounds(250, 20, 61, 21);

				Label lblImageName = new Label(shell, SWT.NONE);
				lblImageName.setBounds(10, 23, 79, 15);
				lblImageName.setText("ImageName :");

				textImagename = new Text(shell, SWT.BORDER);
				textImagename.setBounds(95, 20, 100, 21);
				textImagename.setText(ImageNamePara);

				btnGetIS = new Button(shell, SWT.NONE);
				btnGetIS.setBounds(60, 57, 100, 25);
				btnGetIS.setText("getIS");

				btnImageDelete = new Button(shell, SWT.NONE);
				btnImageDelete.setBounds(180, 57, 100, 25);
				btnImageDelete.setText("Delete");

				btnGetIS.addMouseListener(new MouseAdapter() {

					public void mouseUp(MouseEvent e) {

						if(isValidSession()){

							if("".equals(textPort.getText()) && "".equals(textImagename.getText())){
								MessageDialog.openError(null, "Error", "Port and ImageName cannot be empty");
							}else{
								getIS(textPort.getText(), textImagename.getText());
								shell.close();
							}
						}else{
							MessageDialog.openInformation(null, "Login", "you must login to continue");
						}
					}

				});

				btnImageDelete.addMouseListener(new MouseAdapter() {

					public void mouseUp(MouseEvent e) {

						if(isValidSession()){

							if("".equals(textPort.getText()) && "".equals(textImagename.getText())){
								MessageDialog.openError(null, "Error", "Port and ImageName cannot be empty");
							}else{
								initDeleteImageAction();
							}
						}else{
							MessageDialog.openInformation(null, "Login", "you must login to continue");
						}
						//shell.close();
					}
				});

				 while (!shell.isDisposed()) {
				      if (!display.readAndDispatch())
				        display.sleep();
				    }
				    display.dispose();

			}

		});	*/

	private String getImages(){

		StringBuffer response = new StringBuffer();
		// HTTP GET request
		try {
			//String GetISImageName = textImagename.getText();

			String url = "http://"+host+":"+port+"/images?sessionId="+sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			String cookie = getCookie();
			con.setRequestProperty("Cookie", cookie);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result



		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(response.toString() != null && !"".equals(response.toString()) && !response.toString().equals("No images found with given sessionId")){
			return response.toString();
		}else{
			return "";
		}
	}

	private String getContainers(){

		StringBuffer response = new StringBuffer();

		// HTTP GET request
		try {
			//String GetISImageName = textImagename.getText();

			String url = "http://"+host+":"+port+"/containers?sessionId="+sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			String cookie = getCookie();
			con.setRequestProperty("Cookie", cookie);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			if(responseCode == 200){
				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();


				//print result

			}else{
				MessageDialog.openError(null, "", "Response Code : "+ responseCode + "\n" + "Response Message : "+ con.getResponseMessage());
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(response.toString() != null && !"".equals(response.toString()) && !response.toString().equals("No containers found with given sessionId")){
			return response.toString();
		}else{
			return "";
		}
	}

	public static String getCookie(){

		Properties prop = new Properties();
		InputStream input = null;
		File file = new File("DockerUserSessions" + File.separator + "CurrentSession" + File.separator + "Session.prop");

		try {
			input = new FileInputStream(file);
			prop.load(input);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// load a properties file
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String cookie = prop.getProperty("Cookie");

		return cookie;
	}

	public static boolean isValidSession(){
		Properties prop = new Properties();
		InputStream input = null;
		File file = new File("DockerUserSessions" + File.separator + "CurrentSession" + File.separator + "Session.prop");

		boolean sessionVaild = false;
		try {

			if(file.exists()){
				input = new FileInputStream(file);
				// load a properties file
				prop.load(input);

				// get the property value and print it out
				sessionId = prop.getProperty("SessionID");
				port = prop.getProperty("Port");
				host = prop.getProperty("Host");

				if(prop.getProperty("SessionID") != null && !"".equals(prop.getProperty("SessionID"))){
					sessionVaild = true;
				}
			}else{
				createFile();
			}

		}catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e11) {
					e11.printStackTrace();
				}
			}
		}
		return sessionVaild;

	}

	private static void createFile(){
		Properties prop = new Properties();
		OutputStream output = null;
		File dir = new File("DockerUserSessions" + File.separator + "CurrentSession");
		if(!dir.exists()){
			dir.mkdirs();
		}
		File file = new File("DockerUserSessions" + File.separator + "CurrentSession" + File.separator + "Session.prop");
		

		try{	
			output = new FileOutputStream(file);
			prop.setProperty("Name", "");
			prop.setProperty("User", "");
			prop.setProperty("Host", "");
			prop.setProperty("Port", "");

			// save properties to project root folder
			prop.store(output, null);
			//MessageDialog.openInformation(null, "Login", "you must login to continue");
		}catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output. close();
				} catch (IOException e11) {
					e11.printStackTrace();
				}
			}         
		}

	}

	private void syncIS(){

		// HTTP GET request
		try {
			//String GetISImageName = textImagename.getText();

			String url = "http://"+host+":"+port+"/syncIS?imageName="+URLEncoder.encode(containerImage, "UTF-8")+"&sessionId="+sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			String cookie = getCookie();
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

			if(responseCode == 200){
				for(String line : response.toString().split(",")){
					if(line.startsWith("C")){
						list.add(line);
						//list.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
					}
					if(line.startsWith("A")){
						list.add(line);
						//list.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
					}
				}
			}else{
				MessageDialog.openError(null, "", "Response Code : "+ responseCode + "\n" + "Response Message : "+ con.getResponseMessage());
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private void commitIS(String CommitISAuthor, String CommitISTag, String CommitISCommitMessage){

		// HTTP GET request
		try {

			String url = "http://"+host+":"+port+"/commit?author="+URLEncoder.encode(CommitISAuthor.toLowerCase(), "UTF-8")+"&message="+URLEncoder.encode(CommitISCommitMessage.toLowerCase(), "UTF-8")+"&tag="+URLEncoder.encode(CommitISTag.toLowerCase(), "UTF-8")+"&imageName=containerImage"+"&sessionId="+sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			String cookie = getCookie();
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


	void createProgressBar(int style) {


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

	public void setFocus() {
		// Set the focus
	}
}
