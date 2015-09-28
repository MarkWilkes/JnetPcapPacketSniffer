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
			connection = new Socket(host, port);
			outputStream = new ObjectOutputStream(connection.getOutputStream());
			inputStream = new ObjectInputStream(connection.getInputStream());
			outputStream.writeObject("ping"); // test message
			System.out.println(inputStream.readObject());
			outputStream.writeObject("bye");
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
