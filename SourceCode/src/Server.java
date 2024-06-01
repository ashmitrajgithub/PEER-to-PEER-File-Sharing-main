import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server implements Runnable{
	
	
	@Override
	public void run() {
		try {
			int p=6025;
			Registry reg=LocateRegistry.createRegistry(p);//Creating a registry for Server on a port
			 ServerInterface si = new ServerImpl();
			 //System.setProperty("java.rmi.server.hostname", "192.168.50.73");
			 Naming.bind("rmi://localhost:"+p+"/Server1", si);// Binding to specific name  and this is used by client to connect to this server
			 
			 
			 
			System.out.println("Server is setup successfully");
			
			
			
			
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		
	}

	
	
	public static void main(String [] args) {
		
		new Server().run();// New Thread is created
	}

	
}
