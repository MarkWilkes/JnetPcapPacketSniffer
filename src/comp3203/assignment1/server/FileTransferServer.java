package comp3203.assignment1.server;
/**
 * Main class for the file transfer program, server side.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;


public class FileTransferServer {

	//ServerSocket
	private static ServerSocket s;

	//Streams
	private ObjectInputStream in;
	private ObjectOutputStream out;
	//client socket
	private Socket clientSocket;

	//working directory
	private String wd;

	public FileTransferServer() {		
		//set the working directory variable to the working directory
		wd = System.getProperty("user.dir");
		//System.out.println(wd);

		//initalize the server socket
		try {
			s = new ServerSocket(0);
		} catch (IOException e) {
			System.err.println("Couldn't listen on a new port");
			System.exit(-1);
		}
	}

	public void listen() {
		//print the port we are listening on.
		//we just ask for an open port
		System.out.println("Listening on port: " + s.getLocalPort());

		try {
			acceptClientConnection();
		} catch (IOException e) {
			System.err.println("Accept failed " + s.getLocalPort());
		}

		try {
			readFromClient();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to close client connection");
		}
	}

	private int acceptClientConnection() throws IOException {
		//await connection from client
		clientSocket = s.accept();
		System.out.println("Handling client at " + 
				clientSocket.getInetAddress().getHostAddress() + " on port " +
				clientSocket.getPort());

		//set up the streams for the client
		try{
			in = new ObjectInputStream(clientSocket.getInputStream());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e){
			e.printStackTrace();
			return(-1);
		}

		return 0;
	}

	private void readFromClient() throws IOException, ClassNotFoundException {
		while(true) {
			//read from the stream
			String line = in.readObject().toString();
			//close the connection
			if(line.equals("bye")){
				System.out.println("Closed connection");
				break;
			}
			//print working directory
			else if(line.equals("pwd")){
				out.writeObject(wd);
			}
			//list files in the working directory
			else if(line.equals("ls")){
				File f = new File(wd);
				//gets the files for this directory
				String children[] = f.list();
				//makes a message with all the files separated by new lines
				String message = "";
				for(String child: children){
					message += child + "\n";
				}
				//returns the list
				out.writeObject(message);
			}
			//ping echo
			else if (line.equals("ping")){
				out.writeObject(line);
			}
			//change directory
			else if (line.startsWith("cd ")){
				//checks if they want to go up a directory
				if(line.equals("cd ..")){
					//gets the parents name
					File newdir = new File(wd).getParentFile();
					//goes into the parent
					changeDir(newdir);
					//returns the new working directory
					out.writeObject(wd);

				}
				else{
					//get the file path for the new directory
					File newdir = new File(wd + File.separator + line.substring(3))
						.getAbsoluteFile();
					//checks if the file is a directory
					if(newdir.isDirectory()){
						//goes into the dir
						changeDir(newdir);
						//returns the new working directory
						out.writeObject(wd);
					}
					//this isn't a directory
					else{
						out.writeObject("That is not a valid directory.");
					}
				}
			}
			//make a new directory
			else if (line.startsWith("mkdir ")){
				//gets the new directory name from the command the client made
				File newdir = new File(wd + File.separator + line.substring(6))
					.getAbsoluteFile();
				//checks if the directory exists
				if(!newdir.exists()){
					//makes the directory
					if(newdir.mkdirs()){
						//goes into the new directory
						changeDir(newdir);
						//returns the new working directory
						out.writeObject(wd);
					}
					else{
						out.writeObject("Failed to make new directory");
					}
				}
				else{
					out.writeObject("A directory of that name already exists");
				}
			}
			// transfer file to client
			else if(line.startsWith("get ")) {
				// full path and name of file
				String fileName = wd + File.separator + line.substring(4);
				try {
					//NOTE: This does not work if the file size exceeds available memory
					out.writeObject(Files.readAllBytes(new File(fileName).toPath()));
				} catch(FileNotFoundException e) {
					out.writeObject("Error: File not found");
				} catch(SecurityException e) {
					out.writeObject("Error: Access denied");
				}
			}
			//transfer file from client
			else if(line.startsWith("put ")) {
				// full path and name of file
				String fileName = wd + File.separator + line.substring(4);
				try {
					Files.write(new File(fileName).toPath(), (byte[]) in.readObject());
					out.writeObject("File Transfer Success");
				} catch(ClassCastException e) {
					out.writeObject("Error: Invalid file data");
				}
			}
			//we don't know what you said client
			else {
				out.writeObject("Error command '" + line + "' not known");
			}
		}
	}

	public static void main(String[] args) {
		//make a new server
		new FileTransferServer().listen();
	}

	//changes the working directory to the passed file
	private void changeDir(File dir){
		wd = dir.getAbsolutePath();
	}
}
