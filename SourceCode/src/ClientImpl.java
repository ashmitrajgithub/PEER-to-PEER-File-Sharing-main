import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("serial")
public class ClientImpl extends UnicastRemoteObject implements ClientInterface {

	String dname;
	
	protected ClientImpl(String str) throws RemoteException {
		super();
		dname=str;
		
	}

	/*@Override
	public byte[] Download(String filenam) throws RemoteException {
		
		try {
			
			File f = new File(dname+"/"+filenam);
			
			byte b[]= new byte[(int) f.length()];
			
			FileInputStream fin = new FileInputStream(f);
			
			BufferedInputStream bin = new BufferedInputStream(fin);
			
			bin.read(b, 0, b.length);
			bin.close();
			
			return(b); 
			
			
			
		}
		catch(Exception e) {
			
			e.printStackTrace();
			return null;
			
		}
		
		
		
		
		
		
	}*/

}
