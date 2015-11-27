package comp3203.assignment1.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;

/**
 * Main class for the file transfer program, client side.
 */

public class FileTransferClient {
	
	//socket to the server
	Socket connection;
	//streams
	ObjectOutputStream outputStream;
	ObjectInputStream inputStream;
	
	//creates a connection to the server
	public FileTransferClient(String host, int port) {
		try {
			System.out.println("Attempting to establish connection to host");
			BufferedReader in
			   = new BufferedReader(new InputStreamReader(System.in));
			connection = new Socket(host, port);
			outputStream = new ObjectOutputStream(connection.getOutputStream());
			inputStream = new ObjectInputStream(connection.getInputStream());
			String s;
			
			System.out.println("Connection Established\n");
			
			//loop for input from user
			while(true){
				//reads the command from the user
				System.out.println("\nPlease Enter A Command");
				s = in.readLine();
				
				//stop the client connection
				if(s.equals("exit")){
					outputStream.writeObject("bye");
					break;
				//ping the server
				}else if(s.equals("ping")){
					outputStream.writeObject("ping");
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				// list the the files on the server's current directory
				}else if(s.equals("ls")){
					outputStream.writeObject("ls");
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				// get a file from the server
				}else if(s.startsWith("get ")){
					getFile(s);
				// put a file onto the server
				}else if(s.startsWith("put ")){
					putFile(s);
				// print the servers working directory
				}else if(s.equals("pwd")){
					outputStream.writeObject("pwd");
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				// change the servers working directory
				}else if(s.startsWith("cd ")){
					outputStream.writeObject(s);
					System.out.println("Reply:");
					//prints the new working directory
					System.out.println(inputStream.readObject());
				// make a new directory and move into that directory on server sided
				}else if(s.startsWith("mkdir ")){
					outputStream.writeObject(s);
					System.out.println("Reply:");
					//print the new working directory path
					System.out.println(inputStream.readObject());
				// catch all unrecognized commands
				}else{
					System.err.println("Error, command: "+s+" not supported");
				}
				
			}
			// notify the user that we are done
			outputStream.writeObject("bye");
		} catch (IOException e) {
			System.err.println("Error: IO Exception");
			e.printStackTrace();
		} catch(ClassNotFoundException e){
			System.err.println("Error: ClassNotFound Exception");
			e.printStackTrace();
		}
	}
	
	//retrieves a file from the server
	public void getFile(String cmd) throws IOException, ClassNotFoundException {
		if(!cmd.startsWith("get ")) return; // invalid input
		String localName = cmd.substring(4);
		outputStream.writeObject(cmd);
		Object input = inputStream.readObject();
		if(input instanceof String) { //i.e. not a file
			System.out.println("Reply:");
			System.out.println(input);
		}
		else if(input instanceof byte[]) {
			System.out.print("Retrieving file " + localName + "...");
			File dest = new File(System.getProperty("user.home") + File.separator + localName);
			Files.write(dest.toPath(), (byte[]) input);
			System.out.println("done!");
			System.out.println("File written to " + dest.getAbsolutePath());
		}
	}
	
	//opens a file and sends it to the server
	public void putFile(String cmd) throws IOException, ClassNotFoundException {
		if(!cmd.startsWith("put ")) return; //invalid input
		String localName = cmd.substring(4);
		byte[] data;
		try {
			data = Files.readAllBytes(new File(System.getProperty("user.home")
					+ File.separator + localName).toPath());
		} catch (IOException e) {
			System.err.println("Error: IO exception (is the file there?)");
			return;
		}
		outputStream.writeObject(cmd);
		outputStream.writeObject(data);
		System.out.println("Reply:");
		System.out.println(inputStream.readObject());
	}
	
	//control flow
	public static void main(String[] args) {
		//reads from console
		BufferedReader in
		   = new BufferedReader(new InputStreamReader(System.in));
		
		String host ;
		int port;
		try {
			//gets the IP and the port
			System.out.println("Enter a Host IP");
			host = in.readLine();
			System.out.println("Enter a Port");
			port = Integer.parseInt(in.readLine());
			
			//runs the file transfer client control flow
			new FileTransferClient(host, port);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
	}

}
