package comp3203.assignment1.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Main class for the file transfer program, client side.
 * @author Richard
 *
 */

public class FileTransferClient {
	
	Socket connection;
	ObjectOutputStream outputStream;
	ObjectInputStream inputStream;
	
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
			
			while(true){
				System.out.println("\nPlease Enter A Command");
				s = in.readLine();
				
				if(s.equals("exit")){
					break;
				}else if(s.equals("ping")){
					outputStream.writeObject("ping");
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				}else if(s.equals("ls")){
					outputStream.writeObject("ls");
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				}else if(s.equals("get")){
					outputStream.writeObject("get");
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				}else if(s.equals("put")){
					outputStream.writeObject("put");
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				}else if(s.equals("pwd")){
					outputStream.writeObject("pwd");
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				}else if(s.startsWith("cd ")){
					outputStream.writeObject(s);
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				}else if(s.startsWith("mkdir ")){
					outputStream.writeObject(s);
					System.out.println("Reply:");
					System.out.println(inputStream.readObject());
				}else{
					System.out.println("Error, command: "+s+" not supported");
				}
				
			}
			outputStream.writeObject("bye");
		} catch (IOException e) {
			System.out.println("Error: IO Exception");
			e.printStackTrace();
		} catch(ClassNotFoundException e){
			System.out.println("Error: ClassNotFound Exception");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BufferedReader in
		   = new BufferedReader(new InputStreamReader(System.in));
		
		String host ;
		int port;
		try {
			System.out.println("Enter a Host IP");
			host = in.readLine();
			System.out.println("Enter a Port");
			port = Integer.parseInt(in.readLine());
			
			new FileTransferClient(host, port);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
	}

}
