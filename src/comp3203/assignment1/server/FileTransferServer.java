package comp3203.assignment1.server;
/**
 * Main class for the file transfer program, client side.
 */

import java.io.File;
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
	//client socket
	private static Socket clientS;
	//line that will be used for reading from the input stream
	private static String line;
	
	//working directory
	private static String wd;
	
	public FileTransferServer() {
		//initalize all the streams, sockets, and line to null 
		line = null;
		clientS = null;
		out = null;
		in = null;
		
		//set the working directory variable to the working directory
		wd = System.getProperty("user.dir");
		//System.out.println(wd);
		
		//initalize the server socket
		try {
			s = new ServerSocket(0);
		} catch (IOException e) {
			System.out.println("Couldn't listen on a new port");
			System.exit(-1);
		}

		//print the port we are listening on.
		//we just ask for an open port
		System.out.println("Listening on port: " + s.getLocalPort());
	}
	
	public static int ClientConnection(){
		//connect to a client
		try {
			clientS = s.accept();
			System.out.println("Handling client at " + 
					clientS.getInetAddress().getHostAddress() + " on port " +
					clientS.getPort());
		} catch (IOException e) {
			System.out.println("Accept failed " + s.getLocalPort());
			return(-1);
		}
		
		//set up the streams for the client
		try{
			in = new ObjectInputStream(clientS.getInputStream());
			out = new ObjectOutputStream(clientS.getOutputStream());
		} catch (IOException e){
			e.printStackTrace();
			return(-1);
		}
		
		while(true){
			try{
				//read from the stream
				line = in.readObject().toString();
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
						File newdir = new File(line.substring(3)).getAbsoluteFile();
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
					File newdir = new File(line.substring(6)).getAbsoluteFile();
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
				//we don't know what you said client
				else {
					out.writeObject("Error command '" + line + "' not known");
				}
			} catch (IOException e){
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e){
				e.printStackTrace();
				break;
			}
		}
		
		//close the client connection
		try{
			clientS.close();
		} catch(IOException e){
			System.out.println("Failed to close client connection");
			return(-1);
		}
		return(-1);
	}

	public static void main(String[] args) {
		//make a new server
		new FileTransferServer();
		
		boolean stop = false;
		
		//run the connection
		while(!stop){
			if(ClientConnection() == -1)
				stop = true;
		}
	}
	
	//changes the working directory to the passed file
	private static void changeDir(File dir){
		System.setProperty("user.dir", dir.getAbsolutePath());
		wd = dir.getAbsolutePath();
	}
}
