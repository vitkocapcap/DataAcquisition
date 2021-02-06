package DataMPU6050;

/*
**********ELEK Co. Internship***********
*************VGU Students***************
****************[2019]******************
*************©Viet-Giang©***************
***************EEIT2016*****************
****************************************
*/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;


public class DataReader extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int SERVER_PORT= 80;	//Initiate port 80 on server side

	public static String SERVER_IP=null;		
	public static boolean reconnectFlag=false;
	
	//---------GUI components----------------
	private JButton readData, close,setIp;
	private JPanel mainPanel, ipPanel;
	private JTextField serverIp;
	private JLabel status;
	//---------------------------------//
	
	//----------Initiate input-output stream---------
	public static BufferedReader br;
	public static PrintWriter os;
	//------------------------------------------------
	
	//---------------Adding icon-----------------
	
	ImageIcon imgread = new ImageIcon("resources/read.png");
	ImageIcon imgclose = new ImageIcon("resources/close.png");
	ImageIcon imgicon = new ImageIcon("resources/icon.png");
	//------------------------------------------//
	
	
	public static Socket socket=null;
	
	
	//-----------------Constructor----------------------
	public DataReader() {
		super("Data Reader");
		
		this.setIconImage(imgicon.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Stop the program when exiting
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
                if(socket != null){
                    try {
                        socket.close();
                        System.exit(0);
                    } catch (IOException e1) {
                        System.out.println("Can not stop. Error: "+e1);
                    }
                }
			}	
		});
		//Start UI
		GUI(); 
	}
	//---------------------------------------------------------
	
	private void GUI() {
		setSize(300, 200); 
		setResizable(false);
		setLocation(250,80);
		setLayout(new BorderLayout());
		
		//-------------IP input in GUI-----------------
		serverIp = new JTextField(10);
		serverIp.setBackground(Color.WHITE);
		serverIp.setForeground(Color.RED);
		
		setIp = new JButton("SET");
		setIp.setBackground(Color.WHITE);
		setIp.addActionListener(this);
		
		
		ipPanel = new JPanel();
		ipPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		ipPanel.add(serverIp);
		ipPanel.add(setIp);
		
		add(ipPanel,BorderLayout.NORTH);
		//-------------------------------------------------
		
		
		//------------Button--------------------
		readData= new JButton(imgread);
		readData.setBackground(Color.WHITE);
		readData.addActionListener(this); 
		
		close= new JButton(imgclose);
		close.setBackground(Color.WHITE);
		close.addActionListener(this); 
		//-------------------------------
		
		
		//------------Adding components in order---------------------
		mainPanel=new JPanel();
		mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		mainPanel.add(readData);
		mainPanel.add(close);
		
		
		add(mainPanel,BorderLayout.CENTER);
		
		//status
		status = new JLabel("Welcome to Data Reader. \u00a9 Viet&Giang",JLabel.CENTER);
		add(status,BorderLayout.SOUTH);
		
		setVisible(true);
	}
	//-----------------------------------------------------------------
	
	//---------------Function to initiate connection to server-------  
	private void accessServer() {	
		try {
			System.out.println("Connecting to server...");
			socket = new Socket(SERVER_IP, SERVER_PORT);		//Connect to server with input SERVER_IP
			System.out.println("Connected: " + socket);
			status.setText("Connected to: "+SERVER_IP);
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));  //Read input stream from server
			os = new PrintWriter(socket.getOutputStream(), true);					  //Write output stream to server
			
			System.out.println("Ready to go...");
		}
		catch (IOException ie){
			System.out.println("Can't connect to server: "+ie );
			JOptionPane.showMessageDialog(this,"Failed to connect to Server. Check your IP again","Error",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}

	}
	//----------------------------------------------------------------------------
	
	
	
	//----------------------MAIN FUNCTION-------------------------//
	//************************************************************//
	public static void main(String[] args) throws InterruptedException {
		
		DataReader read = new DataReader(); //Create object of DataReader, constructor will automatically run
		
		while(true) {
			System.out.print("");
			if(SERVER_IP!=null) {	//check if SERVER_IP has been set or not
				
				if(socket!=null) {	//check if socket has already initiated or not
					continue;		//this means when we already has the server IP and the connection
				}					//has been established, the while loop will be nothing and it is just a check
				else {				//If the connection has not opened yet and SERVER_IP has value, new connection is created 
					System.out.println("SERVER...");	//This can be true for both the first connection and other reconnections
					read.accessServer();
				}
			}
			Thread.sleep(100);
		}
		
	}
	
	//--------------------------------------------------
	//--------------------------------------------------
	
	
	//----------Setting interupt when an event occurs, e.g: button clicked-----------
	public void actionPerformed(ActionEvent e) {
		try {
	
		//---------If the user wants to log out of the chat box-------------
		if(e.getSource()==close){
			System.out.println("Close pressed");
			if(socket != null) {
			//all the connection will be closed
			try {
				socket.close();	//close socket
				socket=null;	//set socket to be null, the same as default
				SERVER_IP=null;	//set SERVER_IP to be null, read the while loop in main function to have a deeper understand
				System.out.println("*Connection has been released*");
				status.setText("You have disconnected to Server.");
				
				setIp.setEnabled(true);			//User can input the new IP address and set it again
				serverIp.setEditable(true);		//or just reconnect to the old IP with a click
			}
			catch(IOException closeerror) {
				System.out.println("Still connected.Error: "+closeerror);
				System.exit(1);
			}
			}
		}
		//-----------------------------------------------
		
		//---------------Read the data-------------------
		//------------Please read every single line and comment to understand clearly--------------
		
		else if(e.getSource()==readData){
			
			//Make sure the connection has been initiated before we want to read, if not, a notice will be indicated
			//Check the "else" command of the if(socket!=null) below
			
			if(socket!=null) {		
				os.println("read");			//Sent the text "read" to server, the server will check this and start measuring
				System.out.println("Client: Read.");
				System.out.println("Waiting for data from server...");
				
				/*
				----------------------------------------------------------------------------------------------
				|	There will be 6 messages sent from server											 	  |
				|	They are temperature, humidity, size of first signal array its corresponding signal array,|
				|	size of second signal array and its corresponding signal array, respectively			  |
				----------------------------------------------------------------------------------------------
				*/
				
				String readTemp = br.readLine(); 	//Read temperature value
				String readHumid = br.readLine();	//Read humidity value
				//Convert temperature and humidity to float from readed string
				float Temp = Float.parseFloat(readTemp);		
				float Humid= Float.parseFloat(readHumid);	
				
				//Note that array x and y is not really axis x and y of the accelerometer(they are y and z instead)
				//The first array is axial direction, the second one is radial direction, however it's up to how the sensor is mounted
				
				//Read array x
				String readSizex = br.readLine();	//Read the size of signal array x
			
				
				int sizex = Integer.parseInt(readSizex);	//Convert size to integer
				System.out.println("Size: " + sizex);
			
				double arraydatax[] = new double[sizex];	//Create array with given size
				//Read every single datum and write to array
				for(int i=0;i<sizex;i++) {
					String readArrayx = br.readLine();	
					arraydatax[i]=Double.parseDouble(readArrayx);;
				}
				
				//The same procedure is applied to array y
				//Read array y
				String readSizey = br.readLine();
				int sizey = Integer.parseInt(readSizey);
				System.out.println("Size: " + sizey);
			
				double arraydatay[] = new double[sizey];
				for(int i=0;i<sizey;i++) {
					String readArrayy = br.readLine();	
					arraydatay[i]=Double.parseDouble(readArrayy);;
				}
				
				
				System.out.println("Data has been received.");
				status.setText("Received data");
				
				
				//Get the current Time when all data have been collected
				Calendar cal = Calendar.getInstance();
				String time= new SimpleDateFormat("dd.MMM.yy_HH.mm.ss").format(cal.getTime());
			
				//Create 2 temporary files to save values 2 directions, everytime we require new data, these two files will be recreated
				File objectx = new File("runnable/Matlab_runnable_x.txt");
				objectx.delete();
				objectx.createNewFile();
				
				File objecty = new File("runnable/Matlab_runnable_y.txt");
				objecty.delete();
				objecty.createNewFile();
			
				
				//Create 2 runnable files which is directly linked to matlab
				FileWriter runnablex = new FileWriter("runnable/Matlab_runnable_x.txt");
				FileWriter runnabley = new FileWriter("runnable/Matlab_runnable_y.txt");
				
				//Save measured value to permanent files
				FileWriter backupObjectx = new FileWriter("Data_Updated_Name/Data_"+time+ "_x.txt");
				FileWriter backupObjecty = new FileWriter("Data_Updated_Name/Data_"+time+ "_y.txt");
				
				
				//Note that temperature and humidity are written to file "x", in matlab we will take 
				//these two values first and then delete them out of the array "x" in matlab
				runnablex.write(Temp+" ");
				runnablex.write(Humid+" ");
			
				backupObjectx.write(Temp + " ");
				backupObjectx.write(Humid + " ");
				
				//Write data values to files  
				for(int i=0;i<sizex;i++) {
				
					//	System.out.println("Data "+ (i+1) +" : "+ arraydatax[i]);
					runnablex.write(arraydatax[i]+ " ");
					backupObjectx.write(arraydatax[i]+ " ");
				
				}
				runnablex.close();
				backupObjectx.close();
			
				for(int i=0;i<sizey;i++) {
				
					//	System.out.println("Data "+ (i+1) +" : "+ arraydatay[i]);
					runnabley.write(arraydatay[i]+ " ");
					backupObjecty.write(arraydatay[i]+ " ");
			
				}
				runnabley.close();
				backupObjecty.close();
			
			}
			
			//The else command is hereeee
			else
			{
				status.setText("Connect to server to receive data");
			}
		}
		//------------------------------------------------------------------------------------
		
		
		
		
		
		//Set the IP after typing to the text box
		else if(e.getSource()==setIp) {
			SERVER_IP=serverIp.getText();
			System.out.println(SERVER_IP);
			
			setIp.setText("Reconnect");
			setIp.setEnabled(false);
			serverIp.setEditable(false);
			
		}
		}
		catch(IOException e2){}
		
		
	}
	
}