package comp3203.assignment1.client;

import java.io.IOException;
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
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		new FileTransferClient(host, port);
	}

}
