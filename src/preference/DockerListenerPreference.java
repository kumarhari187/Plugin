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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wb.swt.ResourceManager;

public class DockerListenerPreference extends PreferencePage implements IWorkbenchPreferencePage{
	private final String USER_AGENT = "Mozilla/5.0";
	private Text textName;
	private Text textHost;
	private Text textUser;
	private Text textPort;
	private Text textPassword;
	private Button btnLogin;
	private Button btnLogOut;
	private Button btnEyeButton;
	static boolean viewPassword = false;
	private Label lblLastLogged;

	/**
	 * Create the preference page.
	 */
	public DockerListenerPreference() {
		setDescription("Manage Docker Listener configuration.");
		setMessage("Docker Listener");
		setTitle("Docker Listener");

	}

	/**
	 * Create contents of the preference page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		Label lblName = new Label(container, SWT.NONE);
		lblName.setBounds(0, 10, 32, 15);
		lblName.setText("Name");

		textName = new Text(container, SWT.BORDER);
		textName.setBounds(38, 7, 127, 21);

		Label lblHost = new Label(container, SWT.NONE);
		lblHost.setBounds(0, 42, 32, 15);
		lblHost.setText("Host");

		textHost = new Text(container, SWT.BORDER);
		textHost.setBounds(38, 39, 127, 21);

		Label lblUseer = new Label(container, SWT.NONE);
		lblUseer.setBounds(0, 75, 32, 15);
		lblUseer.setText("User");

		textUser = new Text(container, SWT.BORDER);
		textUser.setBounds(38, 72, 127, 21);

		Label lblPort = new Label(container, SWT.NONE);
		lblPort.setBounds(171, 42, 32, 15);
		lblPort.setText("Port");

		textPort = new Text(container, SWT.BORDER);
		textPort.setBounds(228, 39, 127, 21);

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setBounds(171, 75, 55, 15);
		lblPassword.setText("Password");

		textPassword = new Text(container, SWT.PASSWORD | SWT.BORDER);
		textPassword.setBounds(228, 72, 127, 21);


		Button btnCommectImmediately = new Button(container, SWT.CHECK);
		btnCommectImmediately.setBounds(38, 109, 188, 16);
		btnCommectImmediately.setText("Connect immediately  ()");

		Button btnConnectAtStartup = new Button(container, SWT.CHECK);
		btnConnectAtStartup.setBounds(38, 131, 188, 16);
		btnConnectAtStartup.setText("Connect at startup");

		Button btnSecureConnection = new Button(container, SWT.CHECK);
		btnSecureConnection.setBounds(38, 153, 188, 16);
		btnSecureConnection.setText("Secure connection");

		btnLogin = new Button(container, SWT.NONE);
		btnLogin.setBounds(38, 196, 75, 25);
		btnLogin.setText("Login");

		btnLogOut = new Button(container, SWT.NONE);
		btnLogOut.setBounds(151, 196, 75, 25);
		btnLogOut.setText("Log out");

		lblLastLogged = new Label(container, SWT.NONE);
		lblLastLogged.setBounds(38, 238, 317, 15);
		lblLastLogged.setText("");


		btnEyeButton = new Button(container, SWT.NONE);
		btnEyeButton.setImage(ResourceManager.getPluginImage("DockerPlugin", "icons/eye-icon.png"));
		btnEyeButton.setBounds(361, 72, 28, 21);
		btnEyeButton.setForeground(null);

		btnEyeButton.addMouseListener(new MouseAdapter() {

			public void mouseUp(MouseEvent e) {

				if(viewPassword == false){
					textPassword.setEchoChar( '\0' );
					viewPassword = true;
				}else{

					textPassword.setEchoChar('\u25CF');
					viewPassword = false;
				}
			}
		});


		btnLogOut.setVisible(false);
		btnLogin.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {

				if(textName.getText() != null && !"".equals(textName.getText()) && textUser.getText() != null && !"".equals(textUser.getText()) && textPassword.getText() != null && !"".equals(textPassword.getText())
						&& textHost.getText() != null && !"".equals(textHost.getText()) && textPort.getText() != null && !"".equals(textPort.getText())){
					String GetUserName = textUser.getText();
					String GetPassword = textPassword.getText();
					String GetHost = textHost.getText();
					String GetPort = textPort.getText();
					login(GetUserName,GetPassword,GetHost,GetPort);
					btnLogOut.setVisible(true);
					btnLogin.setVisible(false);
					Properties prop = new Properties();
					InputStream input = null;

					try {

						input = new FileInputStream("Session.prop");

						// load a properties file
						prop.load(input);

						// get the property value and print it out
						textName.setText(prop.getProperty("Name"));
						textHost.setText(prop.getProperty("Host"));
						textPort.setText(prop.getProperty("Port"));

						BufferedReader br = null;
						FileReader fr = null;

						try {
							//br = new BufferedReader(new FileReader(FILENAME));
							fr = new FileReader("Session.prop");
							br = new BufferedReader(fr);

							if(prop.getProperty("SessionID") != null && !"".equals(prop.getProperty("SessionID"))){
								lblLastLogged.setText("Last Logged in : "+br.readLine().substring(1,20));
								btnEyeButton.setEnabled(false);
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
							if (input != null){
								input.close();
							}
						}catch (IOException e1) {
							e1.printStackTrace();
						}

					}


				}else{
					MessageDialog.openError(null, "", "Please fill all the details");
				}
			}
		});

		File file = new File("Session.prop");

		Properties prop = new Properties();
		InputStream input = null;
		try {

			input = new FileInputStream(file);

			// load a properties file
			prop.load(input);

			// get the property value and print it out

			if(prop.getProperty("SessionID") != null && !"".equals(prop.getProperty("SessionID"))){
				btnLogOut.setVisible(true);
				btnLogin.setVisible(false);
			}else {
				btnLogOut.setVisible(false);
				btnLogin.setVisible(true);
			}


		}  catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			if(input != null){
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

				if(textName.getText() != null && !"".equals(textName.getText()) && textUser.getText() != null && !"".equals(textUser.getText()) && textPassword.getText() != null && !"".equals(textPassword.getText())
						&& textHost.getText() != null && !"".equals(textHost.getText()) && textPort.getText() != null && !"".equals(textPort.getText())){
					logout();
					btnLogOut.setVisible(false);
					btnLogin.setVisible(true);
					lblLastLogged.setText("");
				}else{
					MessageDialog.openError(null, "", "Please fill all the details");
				}
			}
		});

		return container;

	}

	/**
	 * Initialize the preference page.
	 */

	private void login(String GetUserName,String GetPassword,String GetHost,String GetPort){

		BufferedReader in = null;
		// HTTP GET request
		try {

			String url = "http://"+GetHost+":"+GetPort+"/login?username="+URLEncoder.encode(GetUserName, "UTF-8")+"&password="+URLEncoder.encode(GetPassword, "UTF-8");

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}


			//print result

			if(responseCode == 200){
				for(String line : response.toString().split(",")){
					Properties prop = new Properties();
					OutputStream output = null;

					try {

						File file = new File("Session.prop");
						output = new FileOutputStream(file);

						// set the properties value
						prop.setProperty("SessionID", line);
						prop.setProperty("Name", textName.getText());
						prop.setProperty("User", GetUserName);
						prop.setProperty("Host", GetHost);
						prop.setProperty("Port", GetPort);

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
				}

			}else{
				MessageDialog.openError(null, "", "Response Code : "+ responseCode + "\n" + "Response Message : "+ con.getResponseMessage());
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			MessageDialog.openError(null, "", e1.getMessage());
		} catch (IOException e1) {
			e1.printStackTrace();
			MessageDialog.openError(null, "", e1.getMessage());
		} finally{
			try {
				if(in != null){
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void logout(){

		Properties prop = new Properties();
		InputStream input = null;
		BufferedReader in = null;
		OutputStream output = null;
		File file =  new File("Session.prop");

		try {

			input = new FileInputStream(file);

			// load a properties file
			prop.load(input);

			// get the property value and print it out

			String sessionId = prop.getProperty("SessionID");
			String host = prop.getProperty("Host");
			String port = prop.getProperty("Port");
			input.close();
			String url = "http://"+host+":"+port+"/logout?sessionId="+sessionId;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}


			if(responseCode == 200){
				try{
					output = new FileOutputStream(file);
					prop.setProperty("SessionID", "");
					prop.store(output, null);

					textName.setText("");
					textHost.setText("");
					textPort.setText("");
					textUser.setText("");
					textPassword.setText("");
					MessageDialog.openInformation(null, "", "Logout Successful");
				}catch (Exception e){
					e.printStackTrace();
					MessageDialog.openError(null, "", e.getMessage());
				} finally {
					try {
						output.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				MessageDialog.openError(null, "", "Unable To Logout");
			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			MessageDialog.openError(null, "", e1.getMessage());
		} catch (IOException e1) {
			e1.printStackTrace();
			MessageDialog.openError(null, "", e1.getMessage());
		}catch (Exception e){
			e.printStackTrace();
			MessageDialog.openError(null, "", e.getMessage());
		} finally {
			try {
				input.close();
				in.close();
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
