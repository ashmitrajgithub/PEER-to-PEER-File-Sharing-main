import java.io.Serializable;

@SuppressWarnings("serial")
public class FileDetails implements Serializable{
	
	String peerId;
	String fileName;
	String portNo;
	String sourceDir;
	
	FileDetails(){
		
	}
	
	
	FileDetails(String p, String f, String port,String sd ){
		
		peerId=p;
		fileName=f;
		portNo= port;
		sourceDir=sd;
		
		
	}

}
