package dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import view.DockerView;
import view.RepoView;

public class AddContainer extends Dialog {
	private Text textImageName;
	private Text textPort;
	private Button btnGetIs;
	private Button btnDelete;
	private String imageName;
	private final String USER_AGENT = "Mozilla/5.0";

	public Button getBtnGetIs() {
		return btnGetIs;
	}

	public void setBtnGetIs(Button btnGetIs) {
		this.btnGetIs = btnGetIs;
	}

	public Button getBtnDelete() {
		return btnDelete;
	}

	public void setBtnDelete(Button btnDelete) {
		this.btnDelete = btnDelete;
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddContainer(Shell parentShell, String imageName) {
		super(parentShell);
		this.imageName = imageName;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setToolTipText("Container");
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(5, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblImageName = new Label(container, SWT.NONE);
		lblImageName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblImageName.setText("Image Name :");

		textImageName = new Text(container, SWT.BORDER);
		GridData gd_textImageName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_textImageName.widthHint = 135;
		textImageName.setLayoutData(gd_textImageName);
		new Label(container, SWT.NONE);

		Label lblPort = new Label(container, SWT.NONE);
		GridData gd_lblPort = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblPort.widthHint = 37;
		lblPort.setLayoutData(gd_lblPort);
		lblPort.setText("Port :");

		textPort = new Text(container, SWT.BORDER);
		GridData gd_textPort = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_textPort.widthHint = 82;
		textPort.setLayoutData(gd_textPort);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		btnGetIs = new Button(container, SWT.NONE); 
		/*createButton(container, IDialogConstants.NO_ID, "btnGetIs", true);*/
		GridData gd_btnGetIs = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnGetIs.widthHint = 143;
		btnGetIs.setLayoutData(gd_btnGetIs);
		btnGetIs.setText("Get IS");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		btnDelete = new Button(container, SWT.NONE); 
		//createButton(container, IDialogConstants.NO_ID, "btnDelete", true);
		GridData gd_btnDelete = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnDelete.widthHint = 99;
		btnDelete.setLayoutData(gd_btnDelete);
		btnDelete.setText("Delete");

		if(imageName != null && !"".equals(imageName)){
			textImageName.setText(imageName);
		}
		//	dockerView = new DockerView();

		btnGetIs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {


				if(RepoView.isValidSession()){
					if(!"".equals(textPort.getText()) && !"".equals(textImageName.getText())){

						getIS(textPort.getText(), textImageName.getText());

					}else{

						MessageDialog.openError(null, "Error", "Port and ImageName cannot be empty");
					}
				}else{
					MessageDialog.openInformation(null, "Login", "you must login to continue");
				}

			}
		});


		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(RepoView.isValidSession()){
					if(!"".equals(textPort.getText()) && !"".equals(textImageName.getText())){
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


		return container;
	}

	protected void okPressed() {
		DockerView.showButtonActions = true;
		close();
	}

	public void initDeleteImageAction(){


		// HTTP GET request
		try {
			if(MessageDialog.openConfirm(null, "Delete", "Are you sure you want to delete the image")){
				String url = "http://"+DockerView.getHost()+":"+DockerView.getPort()+"/deleteimage?container="+URLEncoder.encode(textImageName.getText(), "UTF-8");

				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

				// optional default is GET
				con.setRequestMethod("GET");

				//add request header
				con.setRequestProperty("User-Agent", USER_AGENT);

				String cookie = DockerView.getCookie();
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

					if(response.toString().equals("Image Deleted")){
						MessageDialog.openInformation(null, "DELETE", "Image deleted successful");
					}else{
						MessageDialog.openError(null,"DELETE", "Error deleteing the image");
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

	public void getIS(String getISPort, String getISImageName){
		// HTTP GET request

		System.out.println(getISPort + " *** " + getISImageName + " ----------------");

		try {
			DockerView.currentImage = getISImageName;
			String url = "http://"+DockerView.getHost()+":"+DockerView.getPort()+"/getIS?port="+URLEncoder.encode(getISPort, "UTF-8")+"&imageName="+URLEncoder.encode(getISImageName, "UTF-8")+"&sessionId="+DockerView.getSessionId();

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();					

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			String cookie = DockerView.getCookie();
			con.setRequestProperty("Cookie", cookie);

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
				//lblUrldisplay.setVisible(false);

				DockerView.getISResponse = response.toString();
				DockerView.getISResponseCode = true;

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


	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add Container");
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(420, 200);
	}
}
