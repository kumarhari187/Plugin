package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

public class DockerView extends ViewPart {

	public static final String ID = "viewpart.FirstViewTab"; //$NON-NLS-1$
	private Button btnGetIS;
	private Button btnSyncIS;
	private Button btnCommitIS;
	private List list;
	private FormData fd_btnGetIS;
	private ProgressBar progressBar;
	private Text textAuthor;
	private Text textCommitMessage;
	private Text textTag;
	private Label lblPort;
	private Label lblTag;
	private Label lblAuthor;
	private Label lblCommitMessage;
	private final String USER_AGENT = "Mozilla/5.0";
	private Text textPort;
	private Label lblImagename;
	private Text textImagename;
	private Label lblUrl;
	private Label lblUrldisplay;
	private Link link;
	private String sessionId;
	private String port;
	private String host;



	public DockerView() {
		setTitleImage(ResourceManager.getPluginImage("DockerPlugin", "icons/image.gif"));
		setTitleToolTip("This is the  first view\r\n");
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new org.eclipse.swt.layout.FormLayout());
		{

			btnGetIS = new Button(container, SWT.NONE);
			btnSyncIS = new Button(container, SWT.NONE);
			btnCommitIS = new Button(container, SWT.NONE);
			textAuthor = new Text(container, SWT.BORDER);
			textCommitMessage = new Text(container, SWT.BORDER);
			textTag = new Text(container, SWT.BORDER);
			lblPort = new Label(container, SWT.NONE);
			lblUrldisplay = new Label(container, SWT.NONE);
			lblTag = new Label(container, SWT.NONE);
			lblAuthor = new Label(container, SWT.NONE);
			lblCommitMessage = new Label(container, SWT.NONE);
			lblImagename = new Label(container, SWT.NONE);
			lblUrl = new Label(container, SWT.NONE);
			textPort = new Text(container, SWT.BORDER);
			link = new Link(container, SWT.NONE);


			btnSyncIS.setVisible(false);
			btnCommitIS.setVisible(false);
			lblUrldisplay.setVisible(false);
			lblTag.setVisible(false);
			lblAuthor.setVisible(false);
			lblCommitMessage.setVisible(false);
			textAuthor.setVisible(false);
			textCommitMessage.setVisible(false);
			textTag.setVisible(false);
			link.setVisible(false);

			btnGetIS.addMouseListener(new MouseAdapter() {

				public void mouseUp(MouseEvent e) {

					// HTTP GET request

					String GetISPort = textPort.getText();
					String GetISImageName = textImagename.getText();

					if(GetISPort == "" && GetISImageName == ""){
						MessageDialog.openError(null, "Error", "Port and ImageName cannot be empty");
					}else{


						Properties prop = new Properties();
						InputStream input = null;
						OutputStream output = null;
						File file = new File("Session.prop");

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
									getIS(GetISPort, GetISImageName);
									btnSyncIS.setVisible(true);	
								}else{
									MessageDialog.openInformation(null, "Login", "you must login to continue");
								}
							}else{
								try{
									output = new FileOutputStream(file);

									prop.setProperty("Name", "");
									prop.setProperty("User", "");
									prop.setProperty("Host", "");
									prop.setProperty("Port", "");

									// save properties to project root folder
									prop.store(output, null);
									MessageDialog.openInformation(null, "Login", "you must login to continue");
								}catch (IOException ex) {
									ex.printStackTrace();
								} finally {
									if (input != null) {
										try {
											output.close();
										} catch (IOException e11) {
											e11.printStackTrace();
										}
									}
								}


							}
						} catch (IOException ex) {
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

					}
				}

			});

			fd_btnGetIS = new FormData();
			fd_btnGetIS.right = new FormAttachment(100, -58);
			btnGetIS.setLayoutData(fd_btnGetIS);
			btnGetIS.setText("Get IS");
		}

		ListViewer listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL |SWT.H_SCROLL);
		list = listViewer.getList();
		list.setVisible(false);
		FormData fd_list = new FormData();
		fd_list.right = new FormAttachment(100, -56);
		fd_list.left = new FormAttachment(0, 54);
		fd_list.bottom = new FormAttachment(100, -188);
		fd_list.top = new FormAttachment(0, 136);
		list.setLayoutData(fd_list);
		{
			progressBar = new ProgressBar(container, SWT.NONE);
			fd_btnGetIS.bottom = new FormAttachment(progressBar, 40, SWT.BOTTOM);
			fd_btnGetIS.top = new FormAttachment(progressBar, 15);

			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
			FormData fd_progressBar = new FormData();
			fd_progressBar.right = new FormAttachment(100, -56);
			fd_progressBar.left = new FormAttachment(0, 54);
			fd_progressBar.top = new FormAttachment(0, 22);
			fd_progressBar.bottom = new FormAttachment(0, 39);
			progressBar.setLayoutData(fd_progressBar);
		}

		btnSyncIS.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {


				list.removeAll();
				// HTTP GET request
				syncIS();

				list.setVisible(true);
				btnCommitIS.setVisible(true);
				lblTag.setVisible(true);
				lblAuthor.setVisible(true);
				lblCommitMessage.setVisible(true);
				textAuthor.setVisible(true);
				textCommitMessage.setVisible(true);
				textTag.setVisible(true);
			}
		});


		FormData fd_btnSyncIS = new FormData();
		fd_btnSyncIS.right = new FormAttachment(btnGetIS, 0, SWT.RIGHT);
		fd_btnSyncIS.left = new FormAttachment(100, -193);
		fd_btnSyncIS.top = new FormAttachment(btnGetIS, 12);
		btnSyncIS.setLayoutData(fd_btnSyncIS);
		btnSyncIS.setText("Sync IS");

		btnCommitIS.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {

				// HTTP GET request

				/*String CommitISImageName = textImagename.getText();*/
				String CommitISTag = textTag.getText();
				String CommitISAuthor = textAuthor.getText();
				String CommitISCommitMessage = textCommitMessage.getText();

				if(CommitISTag == "" && CommitISAuthor == "" && CommitISCommitMessage == ""){
					MessageDialog.openError(null, "Error", "Port and ImageName cannot be empty");
				}else{
					commitIS(CommitISTag, CommitISAuthor, CommitISCommitMessage);
				}

				textImagename.setText(CommitISAuthor+"/"+CommitISTag);
				getIS(textPort.getText(), CommitISAuthor+"/"+CommitISTag);
				btnSyncIS.setVisible(true);

				textAuthor.setText("");
				textCommitMessage.setText("");
				textTag.setText("");
				list.removeAll();

			}
		});


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


		FormData fd_lblIp = new FormData();
		fd_lblIp.top = new FormAttachment(progressBar, 20);
		fd_lblIp.bottom = new FormAttachment(lblUrl, -20);
		fd_lblIp.right = new FormAttachment(lblUrl, 0, SWT.RIGHT);
		fd_lblIp.left = new FormAttachment(0, 54);
		lblPort.setLayoutData(fd_lblIp);
		lblPort.setText("Port :");

		FormData fd_lblCommitMessage = new FormData();
		fd_lblCommitMessage.right = new FormAttachment(100, -276);
		fd_lblCommitMessage.left = new FormAttachment(lblTag, 36);
		fd_lblCommitMessage.top = new FormAttachment(lblTag, 0, SWT.TOP);
		lblCommitMessage.setLayoutData(fd_lblCommitMessage);
		lblCommitMessage.setText("Commit Message");

		FormData fd_textPort = new FormData();
		fd_textPort.top = new FormAttachment(progressBar, 17);
		fd_textPort.right = new FormAttachment(lblImagename, -14);
		fd_textPort.left = new FormAttachment(lblPort, 6);
		textPort.setLayoutData(fd_textPort);


		FormData fd_lblImagename = new FormData();
		fd_lblImagename.top = new FormAttachment(progressBar, 20);
		fd_lblImagename.left = new FormAttachment(0, 168);
		lblImagename.setLayoutData(fd_lblImagename);
		lblImagename.setText("ImageName :");

		textImagename = new Text(container, SWT.BORDER);
		fd_btnGetIS.left = new FormAttachment(textImagename, 19);
		fd_lblImagename.right = new FormAttachment(textImagename, -6);
		FormData fd_textImagename = new FormData();
		fd_textImagename.top = new FormAttachment(progressBar, 17);
		fd_textImagename.right = new FormAttachment(100, -212);
		fd_textImagename.left = new FormAttachment(0, 247);
		textImagename.setLayoutData(fd_textImagename);

		FormData fd_lblUrl = new FormData();
		fd_lblUrl.top = new FormAttachment(0, 96);
		fd_lblUrl.left = new FormAttachment(0, 54);
		lblUrl.setLayoutData(fd_lblUrl);
		lblUrl.setText("URL :");

		FormData fd_lblUrldisplay = new FormData();
		fd_lblUrldisplay.left = new FormAttachment(textImagename, 0, SWT.LEFT);
		fd_lblUrldisplay.right = new FormAttachment(textImagename, 0, SWT.RIGHT);
		fd_lblUrldisplay.top = new FormAttachment(textPort, 19);
		lblUrldisplay.setLayoutData(fd_lblUrldisplay);
		lblUrldisplay.setText("URLDisplay");

		FormData fd_link = new FormData();
		fd_link.top = new FormAttachment(btnSyncIS, 4, SWT.TOP);
		fd_link.bottom = new FormAttachment(btnSyncIS, 0, SWT.BOTTOM);
		fd_link.right = new FormAttachment(lblUrl, 153, SWT.RIGHT);
		fd_link.left = new FormAttachment(lblUrl, 6);
		link.setLayoutData(fd_link);
		link.setText("<a>New Link</a>");
		container.setTabList(new Control[]{progressBar, btnGetIS, btnSyncIS, list, btnCommitIS});

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


	private void getIS(String GetISPort, String GetISImageName){
		/*try {

			// get URL content
				url = new URL("http://www.itcuties.com");
	            InputStream is = url.openStream();
	            BufferedReader br = new BufferedReader(new InputStreamReader(is));

	            String line;
	            while ( (line = br.readLine()) != null)
	                list.add(line);

	            br.close();
	            is.close();

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/

		// HTTP GET request

		try {
			btnSyncIS.setVisible(false);
			btnCommitIS.setVisible(false);
			lblUrldisplay.setVisible(false);
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

			String url = "http://"+host+":"+port+"/getIS?port="+URLEncoder.encode(GetISPort, "UTF-8")+"&imageName="+URLEncoder.encode(GetISImageName, "UTF-8")+"&sessionId="+sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			System.out.println("Response Message :" + con.getResponseMessage());

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			if (responseCode == 200) {
				lblUrldisplay.setVisible(false);
				link.setText("<a>"+response.toString()+"</a>");
				link.setVisible(true);

				/*	lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 300){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 301){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 302){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 304){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 307){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 400){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 401){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 403){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 404){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 410){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 500){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 501){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 503){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());
			}else if(responseCode == 550){
				lblUrldisplay.setText("Response Code : "+ responseCode +" - " + con.getResponseMessage());*/
			}else{
				/*lblUrldisplay.setVisible(false);*/
				MessageDialog.openError(null, "", "Response Code : "+ responseCode + "\n" + "Response Message : "+ con.getResponseMessage());
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}


	}

	private void syncIS(){

		// HTTP GET request
		try {
			String GetISImageName = textImagename.getText();

			String url = "http://"+host+":"+port+"/syncIS?imageName="+URLEncoder.encode(GetISImageName, "UTF-8")+"&sessionId="+sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

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
						list.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
					}
					if(line.startsWith("A")){
						list.add(line);
						list.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
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

			String url = "http://"+host+":"+port+"/commit?author="+URLEncoder.encode(CommitISAuthor.toLowerCase(), "UTF-8")+"&message="+URLEncoder.encode(CommitISCommitMessage.toLowerCase(), "UTF-8")+"&tag="+URLEncoder.encode(CommitISTag.toLowerCase(), "UTF-8")+"&imageName=demotempIS"+"&sessionId="+sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

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
				MessageDialog.openInformation(null, "", "Commit Successful : "+ response.toString().split("=")[1]);
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
