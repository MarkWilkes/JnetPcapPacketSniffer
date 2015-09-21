package comp3203.assignment1.server;
/**
 * Main class for the file transfer program, client side.
 * @author Richard
 *
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class FileTransferServer {

	//ServerSocket
	private static ServerSocket s;
	
	//Streams
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	
	//working directory
	//private static String wd;
	
	public FileTransferServer() {
		try {
			s = new ServerSocket(0);
		} catch (IOException e) {
			System.out.println("Couldn't listen on a new port");
			System.exit(-1);
		}
		
		System.out.println("Listening on port: " + s.getLocalPort());
	}

	public static void main(String[] args) {
		new FileTransferServer();
		
		boolean stop = false;
		String line = null;
		Socket clientS = null;
		out = null;
		in = null;
		
		while(!stop){
			try {
				clientS = s.accept();
				System.out.println("Handling client at " + 
						clientS.getInetAddress().getHostAddress() + " on port " +
						clientS.getPort());
			} catch (IOException e) {
				System.out.println("Accept failed " + s.getLocalPort());
				System.exit(-1);
			}
			
			try{
				in = new ObjectInputStream(clientS.getInputStream());
				out = new ObjectOutputStream(clientS.getOutputStream());
			} catch (IOException e){
				e.printStackTrace();
				System.exit(-1);
			}
			
			while(true){
				try{
					line = in.readObject().toString();
					if(line.equals("bye")){
						break;
					}
					out.writeObject(line);
				} catch (IOException e){
					e.printStackTrace();
					break;
				} catch (ClassNotFoundException e){
					e.printStackTrace();
					break;
				}
			}
			
			try{
				clientS.close();
			} catch(IOException e){
				System.out.println("Failed to close client connection");
				System.exit(-1);
			}
		}
	}

}
