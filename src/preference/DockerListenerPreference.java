package preference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import view.DockerView;

public class DockerListenerPreference extends PreferencePage implements
IWorkbenchPreferencePage {
	private final String USER_AGENT = "Mozilla/5.0";
	private Text textName;
	private Text textHost;
	private Text textUser;
	private Text textPort;
	private Text textPassword;
	private Button btnLogin;
	private Button btnLogOut;
	private Button btnEyeButton;
	private Button btnSaveSession;
	private Composite btnComposite;
	private Button btnSaveSessionButton;
	private Button btnSaveSessionDeleteButton;
	private Map<String,Button> userSessions;
	private Map<String,Button> userSessionsDelete;
	static boolean viewPassword = false;
	private Label lblLastLogged;
	private static String COOKIES_HEADER = "Set-Cookie";
	public static IViewPart viewPart;
	private String currentUserName;
	private String fileLocation = "DockerUserSessions" + File.separator + "CurrentSession" + File.separator + "Session.prop";

	static java.net.CookieManager msCookieManager = new java.net.CookieManager();
	/**
	 * Create the preference page.
	 */
	public DockerListenerPreference() {
		setSize(new Point(800, 500));
		setDescription("Manage Docker Listener configuration.");
		setMessage("Docker Listener");
		setTitle("Docker Listener");

	}

	/**
	 * Create contents of the preference page.
	 * 
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FormLayout());

		ScrolledComposite sc = new ScrolledComposite(container, SWT.BORDER | SWT.V_SCROLL);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		FormData fd_sc = new FormData();
		fd_sc.bottom = new FormAttachment(0, 312);
		sc.setLayoutData(fd_sc);
		
		btnComposite = new Composite(sc, SWT.BORDER);
		FormData fd_btnComposite = new FormData();
		fd_btnComposite.bottom = new FormAttachment(100, -12);
		btnComposite.setLayoutData(fd_btnComposite);
		btnComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		btnComposite.setLayout(gridLayout);

		sc.setContent(btnComposite);
	 	sc.setMinSize(128, 500);
	 	

		Label lblName = new Label(container, SWT.NONE);
		fd_sc.top = new FormAttachment(lblName, 0, SWT.TOP);
		FormData fd_lblName = new FormData();
		fd_lblName.top = new FormAttachment(0, 10);
		fd_lblName.left = new FormAttachment(0);
		lblName.setLayoutData(fd_lblName);
		lblName.setText("Name");

		textName = new Text(container, SWT.BORDER);
		fd_btnComposite.top = new FormAttachment(textName, 0, SWT.TOP);
		FormData fd_textName = new FormData();
		fd_textName.right = new FormAttachment(0, 165);
		fd_textName.top = new FormAttachment(0, 7);
		fd_textName.left = new FormAttachment(0, 38);
		textName.setLayoutData(fd_textName);

		Label lblHost = new Label(container, SWT.NONE);
		FormData fd_lblHost = new FormData();
		fd_lblHost.right = new FormAttachment(0, 32);
		fd_lblHost.top = new FormAttachment(0, 42);
		fd_lblHost.left = new FormAttachment(0);
		lblHost.setLayoutData(fd_lblHost);
		lblHost.setText("Host");

		textHost = new Text(container, SWT.BORDER);
		FormData fd_textHost = new FormData();
		fd_textHost.right = new FormAttachment(0, 165);
		fd_textHost.top = new FormAttachment(0, 39);
		fd_textHost.left = new FormAttachment(0, 38);
		textHost.setLayoutData(fd_textHost);

		Label lblUseer = new Label(container, SWT.NONE);
		FormData fd_lblUseer = new FormData();
		fd_lblUseer.right = new FormAttachment(0, 32);
		fd_lblUseer.top = new FormAttachment(0, 75);
		fd_lblUseer.left = new FormAttachment(0);
		lblUseer.setLayoutData(fd_lblUseer);
		lblUseer.setText("User");

		textUser = new Text(container, SWT.BORDER);
		FormData fd_textUser = new FormData();
		fd_textUser.right = new FormAttachment(0, 165);
		fd_textUser.top = new FormAttachment(0, 72);
		fd_textUser.left = new FormAttachment(0, 38);
		textUser.setLayoutData(fd_textUser);

		Label lblPort = new Label(container, SWT.NONE);
		FormData fd_lblPort = new FormData();
		fd_lblPort.right = new FormAttachment(0, 203);
		fd_lblPort.top = new FormAttachment(0, 42);
		fd_lblPort.left = new FormAttachment(0, 171);
		lblPort.setLayoutData(fd_lblPort);
		lblPort.setText("Port");

		textPort = new Text(container, SWT.BORDER);
		FormData fd_textPort = new FormData();
		fd_textPort.right = new FormAttachment(0, 355);
		fd_textPort.top = new FormAttachment(0, 39);
		fd_textPort.left = new FormAttachment(0, 228);
		textPort.setLayoutData(fd_textPort);

		Label lblPassword = new Label(container, SWT.NONE);
		FormData fd_lblPassword = new FormData();
		fd_lblPassword.right = new FormAttachment(0, 226);
		fd_lblPassword.top = new FormAttachment(0, 75);
		fd_lblPassword.left = new FormAttachment(0, 171);
		lblPassword.setLayoutData(fd_lblPassword);
		lblPassword.setText("Password");

		textPassword = new Text(container, SWT.PASSWORD | SWT.BORDER);
		FormData fd_textPassword = new FormData();
		fd_textPassword.right = new FormAttachment(0, 355);
		fd_textPassword.top = new FormAttachment(0, 72);
		fd_textPassword.left = new FormAttachment(0, 228);
		textPassword.setLayoutData(fd_textPassword);

		Button btnCommectImmediately = new Button(container, SWT.CHECK);
		FormData fd_btnCommectImmediately = new FormData();
		fd_btnCommectImmediately.right = new FormAttachment(0, 226);
		fd_btnCommectImmediately.top = new FormAttachment(0, 109);
		fd_btnCommectImmediately.left = new FormAttachment(0, 38);
		btnCommectImmediately.setLayoutData(fd_btnCommectImmediately);
		btnCommectImmediately.setText("Connect immediately  ()");

		Button btnConnectAtStartup = new Button(container, SWT.CHECK);
		FormData fd_btnConnectAtStartup = new FormData();
		fd_btnConnectAtStartup.right = new FormAttachment(0, 226);
		fd_btnConnectAtStartup.top = new FormAttachment(0, 131);
		fd_btnConnectAtStartup.left = new FormAttachment(0, 38);
		btnConnectAtStartup.setLayoutData(fd_btnConnectAtStartup);
		btnConnectAtStartup.setText("Connect at startup");

		Button btnSecureConnection = new Button(container, SWT.CHECK);
		FormData fd_btnSecureConnection = new FormData();
		fd_btnSecureConnection.right = new FormAttachment(0, 226);
		fd_btnSecureConnection.top = new FormAttachment(0, 153);
		fd_btnSecureConnection.left = new FormAttachment(0, 38);
		btnSecureConnection.setLayoutData(fd_btnSecureConnection);
		btnSecureConnection.setText("Secure connection");

		btnLogin = new Button(container, SWT.NONE);
		FormData fd_btnLogin = new FormData();
		fd_btnLogin.right = new FormAttachment(0, 113);
		fd_btnLogin.top = new FormAttachment(0, 196);
		fd_btnLogin.left = new FormAttachment(0, 38);
		btnLogin.setLayoutData(fd_btnLogin);
		btnLogin.setText("Login");

		btnLogOut = new Button(container, SWT.NONE);
		FormData fd_btnLogOut = new FormData();
		fd_btnLogOut.right = new FormAttachment(0, 226);
		fd_btnLogOut.top = new FormAttachment(0, 196);
		fd_btnLogOut.left = new FormAttachment(0, 151);
		btnLogOut.setLayoutData(fd_btnLogOut);
		btnLogOut.setText("Log out");

		lblLastLogged = new Label(container, SWT.NONE);
		FormData fd_lblLastLogged = new FormData();
		fd_lblLastLogged.right = new FormAttachment(0, 355);
		fd_lblLastLogged.top = new FormAttachment(0, 238);
		fd_lblLastLogged.left = new FormAttachment(0, 38);
		lblLastLogged.setLayoutData(fd_lblLastLogged);
		lblLastLogged.setText("");

		btnEyeButton = new Button(container, SWT.NONE);
		fd_sc.left = new FormAttachment(btnEyeButton, 6);
		fd_sc.right = new FormAttachment(btnEyeButton, 191, SWT.RIGHT);
		fd_btnComposite.right = new FormAttachment(btnEyeButton, 179, SWT.RIGHT);
		fd_btnComposite.left = new FormAttachment(btnEyeButton, 16);


		FormData fd_btnEyeButton = new FormData();
		fd_btnEyeButton.bottom = new FormAttachment(0, 93);
		fd_btnEyeButton.top = new FormAttachment(0, 72);
		fd_btnEyeButton.left = new FormAttachment(0, 361);
		btnEyeButton.setLayoutData(fd_btnEyeButton);
		btnEyeButton.setImage(ResourceManager.getPluginImage("Plugin",
				"icons/eye-icon.png"));
		btnEyeButton.setForeground(null);

		btnEyeButton.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {

				if (viewPassword == false) {
					textPassword.setEchoChar('\0');
					viewPassword = true;
				} else {
					textPassword.setEchoChar('\u25CF');
					viewPassword = false;
				}
			}
		});

		btnLogOut.setEnabled(false);

		btnSaveSession = new Button(container, SWT.NONE);
		btnSaveSession.setText("Save Session");
		btnLogin.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {

				if (textName.getText() != null
						&& !"".equals(textName.getText())
						&& textUser.getText() != null
						&& !"".equals(textUser.getText())
						&& textPassword.getText() != null
						&& !"".equals(textPassword.getText())
						&& textHost.getText() != null
						&& !"".equals(textHost.getText())
						&& textPort.getText() != null
						&& !"".equals(textPort.getText())) {
					String GetUserName = textUser.getText();
					String GetPassword = textPassword.getText();
					String GetHost = textHost.getText();
					String GetPort = textPort.getText();
					int responseCode = login(GetUserName, GetPassword, GetHost, GetPort);
					// set the properties value

					Properties prop = new Properties();
					InputStream input = null;

					try {

						input = new FileInputStream(fileLocation);

						// load a properties file
						prop.load(input);

						BufferedReader br = null;
						FileReader fr = null;

						try {
							// FileReader(FILENAME));
							fr = new FileReader(fileLocation);
							br = new BufferedReader(fr);

							if (prop.getProperty("SessionID") != null && !"".equals(prop.getProperty("SessionID")) && responseCode == 200) {

								btnLogOut.setEnabled(true);
								btnLogin.setEnabled(false);

								currentUserName = prop.getProperty("Name");
								// get the property value and print it out
								textName.setText(prop.getProperty("Name"));
								textHost.setText(prop.getProperty("Host"));
								textPort.setText(prop.getProperty("Port"));
								textUser.setText(prop.getProperty("User"));

								lblLastLogged.setText("Last Logged in : "
										+ br.readLine().substring(1, 20));
								btnEyeButton.setEnabled(false);

								for (Control control : btnComposite.getChildren()) {
									control.dispose();
								}
								getSessionsData();
								btnComposite.layout(true, true);

							}else{
								MessageDialog.openInformation(null, "Registration", "Please contact admin for registration");
							}

						} catch (IOException e1) {
							e1.printStackTrace();
						} finally {
							try {
								if (fr != null)
									fr.close();
								if (br != null)
									br.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						}

					} catch (IOException ex) {
						ex.printStackTrace();
					} finally {
						try {
							if (input != null) {
								input.close();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					MessageDialog.openError(null, "",
							"Please fill all the details");
				}
			}
		});

		File file = new File(fileLocation);
		Properties prop = new Properties();
		InputStream input = null;
		OutputStream output = null;
		try {

			if (file.exists()) {

				input = new FileInputStream(file);

				// load a properties file
				prop.load(input);

				// get the property value and print it out

				if (prop.getProperty("SessionID") != null && !"".equals(prop.getProperty("SessionID"))) {
					btnLogOut.setEnabled(true);
					btnLogin.setEnabled(false);

					// get the property value and print it out
					textName.setText(prop.getProperty("Name"));
					textHost.setText(prop.getProperty("Host"));
					textPort.setText(prop.getProperty("Port"));
					textUser.setText(prop.getProperty("User"));

					currentUserName = prop.getProperty("Name");

					BufferedReader br = null;
					FileReader fr = null;

					try {
						// br = new BufferedReader(new FileReader(FILENAME));
						fr = new FileReader(fileLocation);
						br = new BufferedReader(fr);

						lblLastLogged.setText("Last Logged in : "
								+ br.readLine().substring(1, 20));
						btnEyeButton.setEnabled(false);

					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						try {
							if (fr != null)
								fr.close();
							if (br != null)
								br.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}

				} else {
					btnLogOut.setEnabled(false);
					btnLogin.setEnabled(true);
				}
			} else {

				try {
					output = new FileOutputStream(file);

					prop.setProperty("Name", "");
					prop.setProperty("User", "");
					prop.setProperty("Host", "");
					prop.setProperty("Port", "");

					// save properties to project root folder
					prop.store(output, null);
					/*MessageDialog.openInformation(null, "Login",
							"you must login to continue");*/
				} catch (IOException ex) {
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

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		btnLogOut.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {

				if (textName.getText() != null
						&& !"".equals(textName.getText())
						&& textUser.getText() != null
						&& !"".equals(textUser.getText())
						&& textPassword.getText() != null
						&& !"".equals(textPassword.getText())
						&& textHost.getText() != null
						&& !"".equals(textHost.getText())
						&& textPort.getText() != null
						&& !"".equals(textPort.getText())) {
					logout();
					btnLogOut.setEnabled(false);
					btnLogin.setEnabled(true);
					lblLastLogged.setText("");
					/*DockerView dv = new DockerView();
					dv.btnName.setVisible(false);*/
					currentUserName = "";

					for (Control control : btnComposite.getChildren()) {
						control.dispose();
					}
					getSessionsData();
					btnComposite.layout(true, true);

				} else {
					MessageDialog.openError(null, "",
							"Please fill all the details");
				}
			}
		});

		userSessions = new HashMap<String,Button>();
		userSessionsDelete = new HashMap<String, Button>();
		FormData fd_btnSaveSession = new FormData();
		fd_btnSaveSession.top = new FormAttachment(btnLogin, 0, SWT.TOP);
		fd_btnSaveSession.right = new FormAttachment(btnEyeButton, 0, SWT.RIGHT);
		btnSaveSession.setLayoutData(fd_btnSaveSession);

		getSessionsData();
		btnSaveSession.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(userSessions.containsKey(textName.getText()) && MessageDialog.openConfirm(null, "Session already exists with Name", "Session with this name already exist do you  want to override the data")){
					writeDataToFile();
				}
				else{
					writeDataToFile();
				}
			}

		});

		return container;

	}

	/**
	 * Initialize the preference page.
	 */

	private void getSessionsData(){

		File dir = new File("DockerUserSessions");
		if(!dir.exists()){
			dir.mkdirs();
		}

		File[] directoryListing = dir.listFiles();

		if(!userSessions.isEmpty()){
			userSessions.clear();
		}
		for (File file : directoryListing) {
			if (file.isFile()) {
				userSessions.put(file.getName(), new Button(btnComposite, SWT.PUSH));
				btnSaveSessionButton = userSessions.get(file.getName());
				GridData gd_btnSaveSessionButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_btnSaveSessionButton.widthHint = 121;
				btnSaveSessionButton.setLayoutData(gd_btnSaveSessionButton);
				btnSaveSessionButton.setText(file.getName());

				userSessionsDelete.put(file.getName(), new Button(btnComposite, SWT.PUSH));
				btnSaveSessionDeleteButton = userSessionsDelete.get(file.getName());
				GridData gd_btnSaveSessionDeleteButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_btnSaveSessionDeleteButton.widthHint = 25;
				btnSaveSessionDeleteButton.setLayoutData(gd_btnSaveSessionDeleteButton);
				btnSaveSessionDeleteButton.setImage(ResourceManager.getPluginImage("Plugin",
						"icons/CloseButton.png"));

				getSessions(file.getName());

				if(btnLogOut.isEnabled() && file.getName().equals(currentUserName) && file.getName().equals(btnSaveSessionButton.getText())){
					btnSaveSessionButton.setEnabled(true);
					btnSaveSessionDeleteButton.setEnabled(true);
					textName.setText(currentUserName);
					btnSaveSessionButton.setToolTipText("Current Session");
					btnComposite.setToolTipText("You should logout of current session to enable another Session");
				}else if(btnLogin.isEnabled()){
					btnSaveSessionButton.setEnabled(true);
					btnSaveSessionDeleteButton.setEnabled(true);
				}else{
					btnSaveSessionButton.setEnabled(false);
					btnSaveSessionDeleteButton.setEnabled(false);
				}
			}
		}
	}

	private void getSessions(String btnName) {

		final String fileName = btnName;
		final File file =  new File("DockerUserSessions" + File.separator + fileName);

		userSessionsDelete.get(btnName).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {

				if(btnLogOut.isEnabled() && fileName.equals(textName.getText())){
					MessageDialog.openError(null, "Running Session", "You should logout of current session to delete the Session");
				}else{
					file.delete();
					for (Control control : btnComposite.getChildren()) {
						control.dispose();
					}
					getSessionsData();
					btnComposite.layout(true, true);
					textName.setText("");
					textHost.setText("");
					textPort.setText("");
					textUser.setText("");
					textPassword.setText("");
				}	
			}	
		});


		userSessions.get(btnName).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {

				Properties prop = new Properties();
				InputStream input = null;

				try {
					input = new FileInputStream(file);
					// load a properties file
					prop.load(input);

					BufferedReader br = null;
					FileReader fr = null;

					try {
						// FileReader(FILENAME));
						fr = new FileReader("DockerUserSessions" + File.separator + fileName);
						br = new BufferedReader(fr);

						// get the property value and print it out
						textName.setText(prop.getProperty("Name"));
						textHost.setText(prop.getProperty("Host"));
						textPort.setText(prop.getProperty("Port"));
						textUser.setText(prop.getProperty("User"));
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						try {
							if (fr != null)
								fr.close();
							if (br != null)
								br.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}

				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					try {
						if (input != null) {
							input.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}

	private void writeDataToFile(){
		
		Properties prop = new Properties();
		OutputStream output = null;

		try {

			File directory = new File("DockerUserSessions" + File.separator + "CurrentSession");
			if (! directory.exists()){
				directory.mkdir();
				// If you require it to make the entire directory path including parents,
				// use directory.mkdirs(); here instead.
			}

			File file = new File("DockerUserSessions" + File.separator + textName.getText());
			output = new FileOutputStream(file);

			// set the properties value
			prop.setProperty("Name", textName.getText());
			prop.setProperty("User", textUser.getText());
			prop.setProperty("Host", textHost.getText());
			prop.setProperty("Port", textPort.getText());
			// save properties to project root folder
			prop.store(output, null);
			for (Control control : btnComposite.getChildren()) {
				control.dispose();
			}
			getSessionsData();
			btnComposite.layout(true, true);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private int login(String GetUserName, String GetPassword, String GetHost,
			String GetPort) {

		BufferedReader in = null;
		int responseCode = 0;
		// HTTP GET request
		try {

			String url = "http://" + GetHost + ":" + GetPort
					+ "/login?username="
					+ URLEncoder.encode(GetUserName, "UTF-8") + "&password="
					+ URLEncoder.encode(GetPassword, "UTF-8");

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			Map<String, List<String>> headerFields = con.getHeaderFields();
			List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

			if (cookiesHeader != null) {
				for (String cookie : cookiesHeader) {
					msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
				}               
			}
			// print result

			if (responseCode == 200) {

				Properties prop = new Properties();
				OutputStream output = null;

				try {

					File file = new File(fileLocation);
					output = new FileOutputStream(file);

					if(!response.toString().toLowerCase().contains("please")){
						// set the properties value
						prop.setProperty("SessionID", response.toString().split(":")[1]);
						prop.setProperty("Name", textName.getText());
						prop.setProperty("User", GetUserName);
						prop.setProperty("Host", GetHost);
						prop.setProperty("Port", GetPort);
						for(HttpCookie cookie: msCookieManager.getCookieStore().getCookies()){
							prop.setProperty("Cookie", cookie.toString());
						}

					}else{
						MessageDialog.openInformation(null, "Login", response.toString());
					}
					// save properties to project root folder
					prop.store(output, null);

				} catch (IOException io) {
					io.printStackTrace();
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				MessageDialog.openError(
						null,
						"Error",
						"Response Code : " + responseCode + "\n"
								+ "Response Message : "
								+ con.getResponseMessage());
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			MessageDialog.openError(null, "Error", e1.getMessage());
		} catch (IOException e1) {
			e1.printStackTrace();
			MessageDialog.openError(null, "Error", e1.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return responseCode;
	}

	private void logout() {

		Properties prop = new Properties();
		InputStream input = null;
		BufferedReader in = null;
		OutputStream output = null;
		File file = new File(fileLocation);

		try {

			input = new FileInputStream(file);

			// load a properties file
			prop.load(input);

			// get the property value and print it out

			String sessionId = prop.getProperty("SessionID");
			String host = prop.getProperty("Host");
			String port = prop.getProperty("Port");
			input.close();
			String url = "http://" + host + ":" + port + "/logout?sessionId="
					+ sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			/*String cookie = dv.getCookie();
			con.setRequestProperty("Cookie", cookie);*/

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			if (responseCode == 200) {
				try {
					output = new FileOutputStream(file);
					prop.setProperty("SessionID", "");
					prop.store(output, null);

					textName.setText(prop.getProperty("Name"));
					textHost.setText(prop.getProperty("Host"));
					textPort.setText(prop.getProperty("Port"));
					textUser.setText("");
					textPassword.setText("");
					MessageDialog
					.openInformation(null, "Logout", "Logout Successful");

				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(null, "Error", e.getMessage());
				} finally {
					try {
						output.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				MessageDialog.openError(null, "Error", "Unable To Logout");
			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			MessageDialog.openError(null, "Error", e1.getMessage());
		} catch (IOException e1) {
			e1.printStackTrace();
			MessageDialog.openError(null, "Error", e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(null, "Error", e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}
}
