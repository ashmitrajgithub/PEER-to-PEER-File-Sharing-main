import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote{
	
	public void Register(String peer_id,String File_Name,String Port_No,String Src_Dir) throws RemoteException;
	public ArrayList<FileDetails> Search(String File_Name) throws RemoteException;
	public void DeRegister(String peer_id,String File_Name,String Port_No,String Src_Dir) throws RemoteException;

}
