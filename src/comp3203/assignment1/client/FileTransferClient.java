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
				}else if(s.startsWith("get ")){
					getFile(s);
				}else if(s.startsWith("put ")){
					putFile(s);
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
					System.err.println("Error, command: "+s+" not supported");
				}
				
			}
			outputStream.writeObject("bye");
		} catch (IOException e) {
			System.err.println("Error: IO Exception");
			e.printStackTrace();
		} catch(ClassNotFoundException e){
			System.err.println("Error: ClassNotFound Exception");
			e.printStackTrace();
		}
	}
	
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
