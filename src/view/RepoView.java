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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.action.Action;
/*import org.eclipse.jface.action.IMenuManager;*/
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import style.RoundedLabel;
import dialog.AddContainer;

public class RepoView extends ViewPart {
	public static final String USER_AGENT = "Mozilla/5.0";
	public static String sessionId;
	public static String port;
	public static String host;
	private Composite container;
	private Map<String,Label> userItems;
	private Map<String,RoundedLabel> userItemsImage;
	private String imageName = null;
	public Label lblCurrent;
	private AddContainer addCon;
	private String ImageNamePara;
	/*private String containerImage;*/
	private Composite lblComposite;
	private Menu popupMenu;

	public static String currentContainer;
	public static String currentImage;
	private String currentImageName;
	private RoundedLabel lblCurrentImg;

	public static boolean showButtonActions = false;
	public static boolean getISResponseCode = false;
	public static String getISResponse;

	public static final String ID = "view.RepoView"; //$NON-NLS-1$
	private Action refreshAction;
	private Action addContainerAction;
	
	public RepoView() {
		setTitleImage(ResourceManager.getPluginImage("Plugin", "icons/favicon.ico"));
		setTitleToolTip("This is the  first view\r\n");
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		initView();
		startNewContainerViewLayout();
		viewActionLayout();
		userItemLayout();
		createActions();
		initializeToolBar();
		initializeMenu();

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	private void initView(){
		userItems = new HashMap<String, Label>();
		userItemsImage = new HashMap<String, RoundedLabel>();
		lblComposite = new Composite(container, SWT.NONE);
	}

	private void startNewContainerViewLayout(){
		container.setLayout(new FormLayout());

	}

	private void viewActionLayout(){

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 2;
		lblComposite.setLayout(gridLayout);
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(0, 468);
		fd_composite.right = new FormAttachment(0, 594);
		fd_composite.top = new FormAttachment(0);
		fd_composite.left = new FormAttachment(0);
		lblComposite.setLayoutData(fd_composite);
		lblComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
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
					userItemsImage.put("C_"+splitString , new RoundedLabel(lblComposite, SWT.NONE));
					userItems.put("C_"+splitString, new Label(lblComposite, SWT.NONE));

				}
			}

			listImage = getImages();
			splitImage = listImage.toString().split(",");
			for(String splitString : splitImage){
				if(!"".equals(splitString)){
					userItemsImage.put("I_"+splitString , new RoundedLabel(lblComposite, SWT.NONE));
					userItems.put("I_"+splitString, new Label(lblComposite, SWT.NONE));
				}
			}
		}
	}

	public void userItemLayout(){

		updateUserItems();

		for(String key : userItems.keySet()){

			lblCurrent = userItems.get(key);

			lblCurrentImg = userItemsImage.get(key);

			lblCurrentImg.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			GridData gd_lblCurrentImg = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
			gd_lblCurrentImg.widthHint = 25;
			gd_lblCurrentImg.heightHint = 18;
			lblCurrentImg.setLayoutData(gd_lblCurrentImg);
			Font btnBoldFont = new Font( lblCurrent.getDisplay(), new FontData( "Rockwell Bold", 8, SWT.NONE ));
			lblCurrentImg.setFont(btnBoldFont);

			//lblCurrent.setLayoutData(new RowData(100, 50));
			lblCurrent.setImage(ResourceManager.getPluginImage("Plugin", "icons/image.gif"));
			lblCurrent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblCurrent.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			Font lblBoldFont = new Font( lblCurrent.getDisplay(), new FontData( "Arial", 10, SWT.NONE ));
			lblCurrent.setFont(lblBoldFont);

			popupMenuOnClick();

			if(key.startsWith("I")){

				lblCurrentImg.setText(" I ");
				lblCurrentImg.setToolTipText("Docker Image");

				int f = key.indexOf("_");
				int l = key.lastIndexOf("_");
				imageName = key.substring(f+1, l);
				//lblCurrent.setText(imageName.substring(0, 1).toUpperCase() + imageName.substring(1));
				lblCurrent.setText(imageName);
				lblCurrent.setToolTipText("Docker Image");
				lblCurrent.addMouseListener(new MouseAdapter() {

					public void mouseUp(MouseEvent e) {
						currentImage = ((Label)e.getSource()).getText()+"_"+sessionId;
						currentImageName = ((Label)e.getSource()).getText();
					}
				});
				MenuItem newItem = new MenuItem(popupMenu, SWT.CASCADE);
				newItem.setText("Create Container");
				newItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						callShell(currentImageName);
					}

				});
			}else{
				lblCurrentImg.setText(" C ");
				lblCurrentImg.setToolTipText("Docker Container");

				int f = key.indexOf("_");
				int l = key.lastIndexOf("_");
				String containerName = key.substring(f+1, l);
				//lblCurrent.setText(containerName.substring(0, 1).toUpperCase() + containerName.substring(1));
				lblCurrent.setText(containerName);
				lblCurrent.setToolTipText("Docker Container");
				lblCurrent.addMouseListener(new MouseAdapter() {

					public void mouseUp(MouseEvent e) {
						currentContainer = ((Label)e.getSource()).getText()+"_"+sessionId;
					}
				});
			}
		}
	}

	private void callShell(String imageName){

		ImageNamePara = imageName;

		addCon = new AddContainer(container.getShell(),ImageNamePara);

		addCon.open();

	}	

	private void popupMenuOnClick(){

		popupMenu = new Menu(lblCurrent);

		MenuItem deleteItem = new MenuItem(popupMenu, SWT.NONE);
		deleteItem.setText("Delete");
		/*MenuItem addContainerItem = new MenuItem(popupMenu, SWT.NONE);
		addContainerItem.setText("Add Container");
		addContainerItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				callShell("");
			}

		});*/
		lblCurrent.setMenu(popupMenu);

	}

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
		if(response.toString() != null && !"".equals(response.toString()) && 
				!response.toString().equals("No images found with given sessionId")){
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
				MessageDialog.openError(null, "Error", "Response Code : "+ responseCode + "\n" + "Response Message : "+ con.getResponseMessage());
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(response.toString() != null && !"".equals(response.toString()) && 
				!response.toString().equals("No containers found with given sessionId")){
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

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			refreshAction = new Action("Refresh") {				@Override 
				public void run(){
					for (Control control : lblComposite.getChildren()) {
						control.dispose();
					}
					userItemLayout();
					// DO THIS:
					lblComposite.layout(true, true);
					// .. and it will work
				}
			};
			refreshAction.setToolTipText("Refresh");
			refreshAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor("Plugin", "icons/ajax-refresh-icon.gif"));
			
			addContainerAction = new Action("Add Container") {
				@Override 
				public void run(){
					callShell("");
				}
			};
			addContainerAction.setToolTipText("Add Container");
			addContainerAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor("Plugin", "icons/16-em-plus.png"));
		}
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		if(toolbarManager.isEmpty()){
			toolbarManager.add(addContainerAction);
			toolbarManager.add(refreshAction);
		}	
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		/*IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();*/
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
