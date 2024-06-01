import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements Runnable{
	
	String portNo=null;
	String dirNam=null;
	String fileToSearch=null;
	
	BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
	
	Client(String port,String dname){
		
		this.portNo=port;
		this.dirNam=dname;
	}
	
	@Override
	public void run() {
		String pId=null;//Peer ID
		String filenam=null;// File Name
		int p=0,a=0;
		
		int pf= Integer.parseInt(portNo);
		try {
			
			ServerInterface si = (ServerInterface) Naming.lookup("rmi://localhost:6025/Server1");//Client or peer establishing connection with Server
			Scanner sc= new Scanner(System.in);
			
			
			
			System.out.println("Please Enter 1 for Registering the File and 2 for searching and downloading the file from the peer");
			
			p=sc.nextInt();
			sc.nextLine();//Writing this because the Scanner will read from where it left. 
			
			System.out.println("Enter your Peer ID");
			 
			pId=sc.nextLine();
			
			if (p==1) {
				
				System.out.println("Please Enter the number of files you want to register");
				int b=sc.nextInt();
				sc.nextLine();//Since the next input is String I need to put this else Scanner will try to read it from the same line and cause issues
				
			//For Registering a file on to Server
			for (int i=1;i<=b;i++)
			{	
				
			System.out.println("Enter the File no. " +i+ " File Name which you would like to register");
			filenam= sc.nextLine();
			si.Register(pId, filenam, portNo, dirNam);//Calling Server to register a file
			}
			
			System.out.println("1. Enter 1 if you want to search and download a particular file else 0");
			a=sc.nextInt();
			sc.nextLine();
			
			}if (p==2 || a==1) {
				//Searching for a file and Downloading it from one of the available peer
				ArrayList<FileDetails> ar= new ArrayList<FileDetails>();
				System.out.println("Please Enter the File name to be searched ");
				
				fileToSearch=sc.nextLine();//Getting the file name to searched from the user
				
				if(fileToSearch!=null) {
					ar=si.Search(fileToSearch);//Searching the file from the indexing server
					
					for(int i=0;i<ar.size();i++) {
						
						System.out.println("The Peer with "+ar.get(i).peerId+" ID has the file named "+ fileToSearch);
					}
					
					System.out.println("From the above list, Please select the Peer from whom you want to download the file and give the PeerID");
					String pp=sc.nextLine();
					
					if(pp!=null) {
						
						dwnldFrmPeer(pp,ar);//Downloading the file from the peer selected by the user
						si.Register(pId, fileToSearch, portNo, dirNam);//Registering the peer as soon as the file gets download(Updating the server list )
						
						
						
						
					}
					
					else {
						System.out.println("Please enter the peerId and dont leave it blank");
					}
					
				}else {
					System.out.println("Please enter the file name and dont leave it blank");
				}
				
				
				
			}
			
			WatchService watchservice= FileSystems.getDefault().newWatchService();//Creating a object of Watch Service
			Path path= Paths.get(dirNam);//The directory which is to be monitored
			path.register(watchservice, StandardWatchEventKinds.ENTRY_DELETE);//We are registering to monitor the directory and raise an event when a file is deleted from the directory
			WatchKey key;
			
			while((key=watchservice.take()) !=null) {
				for(WatchEvent<?> event : key.pollEvents()) {
					System.out.println("Event kind:" +event.kind() + " . File Affected " + event.context());
					String fname1= String.valueOf(event.context());//getting the filename of the file which was deleted by the user
					si.DeRegister(pId, fname1, portNo, dirNam);//De-registering the the file which was deleted by the user
				}
				key.reset();//Reseting the Key value so that it will again change whenever the another file is deleted.
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
    //Method to Download a file after selecting a particular peer
	private void dwnldFrmPeer(String pp, ArrayList<FileDetails> ar) throws RemoteException,IOException, NotBoundException{
		
		String portOfAnotherPeer=null;
		String sDir=null;
		String source;
		String target;
		int len;
		
		//Getting the port no and source directory of the other peer from whom we want to transfer the file
		for(int i=0;i<ar.size();i++) {
			
			if(pp.equals(ar.get(i).peerId)) {        //Comparing the peer iD user entered with the peer id present in the Array List
				portOfAnotherPeer=ar.get(i).portNo;  //Getting the port number of the matched peer id
				sDir=ar.get(i).sourceDir;            //Getting the Source Directory of the matched Peer
			}
		}
		int p= Integer.parseInt(portOfAnotherPeer);
		ClientInterface ci = (ClientInterface) Naming.lookup("rmi://localhost:"+p+"/FileServer");//Connecting to the another Peer
		
		source=sDir+"/"+fileToSearch;//File location of the Peer containing it
		target=dirNam;//Directory to which the file needs to be copied
		
		InputStream in=null;
		OutputStream out=null;
		
		try {
			
			File src= new File(source);//File object containing the location of the file
			File trgt= new File(target);//File object containing the location where the file needs to be copied
			
			System.out.println("The file "+src);
			
			if(!trgt.exists()) {
				trgt.createNewFile();//if the target didn't contain a file then create a new file
			}
			
			in= new FileInputStream(src);//This object is used for reading file content from the directory
			out = new FileOutputStream(trgt+"/"+src.getName());//this is used to write read file contents to a file
			
			byte [] buff= new byte[(int) src.length()];// Buffer of length of source file
			//REading the content from the source file
			while((len=in.read(buff))>0) {
				out.write(buff, 0, len);// writing content to the destination file
			}
			System.out.println("The file"+src+"has been successfully transferred to the destination: "+trgt+"/"+src.getName());
			
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		finally {
			in.close();//CLosing both Input and Output Stream
			out.close();
		}
	}
	
	
	
	public static void main(String [] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Enter the Port number on which the Peer needs to be registered");
		String pn=br.readLine();//Port number on which the Peer should run
		
		System.out.println("Enter the directory for the peer with port number mentioned above");
		
		String dirNam=br.readLine();//Directory of the Peer
		
		try {
			
			int p= Integer.parseInt(pn);
			Registry r=LocateRegistry.createRegistry(p);//Registering the Peer on the port mentioned by the user
			ClientInterface cii= new ClientImpl(dirNam);
			System.out.println("The directory of this Peer is "+dirNam);
			 //System.setProperty("java.rmi.server.hostname", "192.168.50.73");
			Naming.bind("rmi://localhost:"+p+"/FileServer", cii);//Client running on a different port given by the user
			
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		new Client(pn,dirNam).run();//Creating new Thread for every time we run this program
	}
	

}
