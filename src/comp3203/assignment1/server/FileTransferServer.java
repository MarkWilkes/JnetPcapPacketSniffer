package comp3203.assignment1.server;
/**
 * Main class for the file transfer program, client side.
 * @author Mark
 *
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
	private static Socket clientS;
	private static String line;
	
	private static String wd;
	
	public FileTransferServer() {
		line = null;
		clientS = null;
		out = null;
		in = null;
		
		wd = System.getProperty("user.dir");
		System.out.println(wd);
		
		try {
			s = new ServerSocket(0);
		} catch (IOException e) {
			System.out.println("Couldn't listen on a new port");
			System.exit(-1);
		}

		System.out.println("Listening on port: " + s.getLocalPort());
	}
	
	public static int ClientConnection(){
		try {
			clientS = s.accept();
			System.out.println("Handling client at " + 
					clientS.getInetAddress().getHostAddress() + " on port " +
					clientS.getPort());
		} catch (IOException e) {
			System.out.println("Accept failed " + s.getLocalPort());
			return(-1);
		}
		
		try{
			in = new ObjectInputStream(clientS.getInputStream());
			out = new ObjectOutputStream(clientS.getOutputStream());
		} catch (IOException e){
			e.printStackTrace();
			return(-1);
		}
		
		while(true){
			try{
				line = in.readObject().toString();
				if(line.equals("bye")){
					System.out.println("Closed connection");
					break;
				}
				else if(line.equals("pwd")){
					out.writeObject(wd);
				}
				else if(line.equals("ls")){
					File f = new File(wd);
					String children[] = f.list();
					String message = "";
					for(String child: children){
						message += child + "\n";
					}
					out.writeObject(message);
				}
				else if (line.equals("ping")){
					out.writeObject(line);
				}
				else if (line.startsWith("cd ")){
					if(line.equals("cd ..")){
						File newdir = new File(wd).getParentFile();
						System.setProperty("user.dir", newdir.getAbsolutePath());
						wd = newdir.getAbsolutePath();
						out.writeObject(wd);
						
					}
					else{
						File newdir = new File(line.substring(3)).getAbsoluteFile();
						if(newdir.isDirectory()){
							System.setProperty("user.dir", newdir.getAbsolutePath());
							wd = newdir.getAbsolutePath();
							out.writeObject(wd);
						}
						else{
							out.writeObject("That is not a valid directory.");
						}
					}
				}
				else if (line.startsWith("mkdir ")){
					File newdir = new File(line.substring(6)).getAbsoluteFile();
					if(!newdir.exists()){
						if(newdir.mkdirs()){
							System.setProperty("user.dir", newdir.getAbsolutePath());
							wd = newdir.getAbsolutePath();
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
		
		try{
			clientS.close();
		} catch(IOException e){
			System.out.println("Failed to close client connection");
			return(-1);
		}
		return(-1);
	}

	public static void main(String[] args) {
		new FileTransferServer();
		
		boolean stop = false;
		
		while(!stop){
			if(ClientConnection() == -1)
				stop = true;
		}
	}
	
}
