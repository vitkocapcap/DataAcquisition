package DataMPU6050;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;


public class test {
	public static final int SERVER_PORT= 80;
	public final static String SERVER_IP = "192.168.10.194";
	
	
	public static void main(String[] args) {
		Socket socket=null;
		System.out.println("Connecting");
		try {
			System.out.println("Connecting?");
			socket = new Socket(SERVER_IP, SERVER_PORT);
			System.out.println("Connected?");
			System.out.println("Connected: " + socket);
			Scanner sc= new Scanner(System.in);
			
			String message;
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter os = new PrintWriter(socket.getOutputStream(), true);	
			System.out.println("Ready to go...");

			do{
				System.out.println("Information want to send to Server: ");
				message=sc.nextLine();
		
				if(message.equals("read")) {
					System.out.println("Client: "+message);
					os.println(message);
					System.out.println("Waiting for data from server...");
					
					String readSize = br.readLine();
					
					int size = Integer.parseInt(readSize);
					System.out.println("Size: " + size);
					
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
					File object = new File("runnable/Matlab_runnable.txt");
					object.delete();
					object.createNewFile();
						
					
					FileWriter runnable = new FileWriter("runnable/Matlab_runnable.txt");
					FileWriter backupObject = new FileWriter("Data/Data_"+time+ ".txt");	
					
					
					for(int i=0;i<size;i++) {
						
							System.out.println("Data "+ (i+1) +" : "+ arraydata2[i]);
							runnable.write(arraydata2[i]+ " ");
							backupObject.write(arraydata2[i]+ " ");
						
					}
					runnable.close();
					backupObject.close();
				} 
				else {
					System.out.println("Client: "+message);
					os.println(message);
				}
			}
			while(!message.equals("CLOSE"));
		}
		catch (IOException ie){
			System.out.println("Can't connect to server: "+ie );
			System.exit(0);
		}
		finally {
			try {
				System.out.println("*Connection has been released*");
				socket.close();
			}
			catch(IOException e) {
				System.out.println("Still connected.Error: "+e);
				System.exit(1);
			}
		}
	}	
}