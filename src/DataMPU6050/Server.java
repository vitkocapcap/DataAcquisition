package DataMPU6050;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.omg.CORBA.INTERNAL;

public class Server {
	public static final int SERVER_PORT= 2019;
	private static double arraydata[]=new double[10];
	
	public static void main(String[] args) throws IOException {
		String data = "Server is ready";
		System.out.println("Binding to port "+ SERVER_PORT);
		ServerSocket serverSocket= new ServerSocket(SERVER_PORT);
		System.out.println("Server starts at " + serverSocket);
		System.out.println("Waiting for a client");
		Socket socket=null;
		try {
			socket=serverSocket.accept();
		//	DataInputStream is = new DataInputStream(socket.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//	DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			String response;  
			
			System.out.println(data);
		//	os.writeUTF(data);
			while(true) {
				
				response = br.readLine();
				
				if(response.equals("CLOSE")) {
					System.out.println("Client has leave by saying: "+response);
					break;
				}
				else if(response.equals("data2")) {
					System.out.println("Waiting for data from client...");
					
					String readSize = br.readLine();
					int size = Integer.parseInt(readSize);
					System.out.println(size);
					
					double arraydata2[] = new double[size];
					for(int i=0;i<size;i++) {
						String readArray = br.readLine();	
						arraydata2[i]=Double.parseDouble(readArray);;
					}
					System.out.println("Data has been received.");
					
					//Time
					Calendar cal = Calendar.getInstance();
					String time= new SimpleDateFormat("dd.MMM.yy_HH.mm.ss").format(cal.getTime());
					
					//File
					File object = new File("runable/Matlab_runable.txt");
					object.delete();
					object.createNewFile();
						
					
					FileWriter runable = new FileWriter("runable/Matlab_runable.txt");
					FileWriter backupObject = new FileWriter("Data/Data_"+time+ ".txt");	
					
					
					for(int i=0;i<size;i++) {
						
							System.out.println("Data "+ (i+1) +" : "+ arraydata2[i]);
							runable.write(arraydata2[i]+ " ");
							backupObject.write(arraydata2[i]+ " ");
						
					}
					runable.close();
					backupObject.close();
				} 
				else {
					System.out.println("Client: "+response);
				}
			}
		}
		catch(IOException e) {
			System.out.println("Server did not work properly.\n");
			System.err.println("Connection Error: "+e);
		}
		finally {
			if(serverSocket !=null) {
				serverSocket.close();
			}
		}
	}		
}