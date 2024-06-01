import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Concurrent implements Runnable {
    String dname = null;
    String fileName = null;
    String pId = null;
    long strt_time = 0;
    long end_time = 0;
    long resp_time = 0;
    long stm = 0;
    long etm = 0;
    String portNo = null;

    Concurrent(String portNo, String dname, String fileName, String pId) {
        this.portNo = portNo;
        this.dname = dname;
        this.fileName = fileName;
        this.pId = pId;
    }

    @Override
    public void run() {
        try {
            // Replace "localhost" with the actual hostname or IP address where the RMI Registry is running.
            ServerInterface si = (ServerInterface) Naming.lookup("rmi://localhost:6025/Server1");
            System.out.println("Currently Running " + Thread.currentThread().getName());// To get know which peer or thread is running
            ArrayList<FileDetails> ar = new ArrayList<FileDetails>();// Arraylist to store the list we get after searching for the file from the server

            strt_time = System.currentTimeMillis();
            ar = si.Search(fileName);// Searching for the file
            for (int i = 0; i < ar.size(); i++) {
                System.out.println("The Peer with " + ar.get(i).peerId + " ID has the file named " + fileName);
            }
            end_time = System.currentTimeMillis() - strt_time;
            System.out.println("Response time of server for " + Thread.currentThread().getName() + " is " + end_time);// Response time from server

            stm = System.currentTimeMillis();
            dwnldFrmPeer("4500", "/Users/amitaryan/Downloads/CN/PEER4");// Downloading the file from the fixed peer
            etm = System.currentTimeMillis() - stm;
            System.out.println("Time Taken to download file from the same peer " + Thread.currentThread().getName() + " is " + etm);

            long ttkn = end_time + etm;
            System.out.println("Total Time Taken for " + Thread.currentThread().getName() + " is " + ttkn);// Total time taken i.e. adding both time taken to search from server and download it from peer
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dwnldFrmPeer(String portOfAn, String srcc) throws RemoteException, IOException, NotBoundException {
        String portOfAnotherPeer = portOfAn;
        String sDir = srcc;
        String source;
        String target;
        int len;

        int p = Integer.parseInt(portOfAnotherPeer);
        ClientInterface ci = (ClientInterface) Naming.lookup("rmi://localhost:" + p + "/FileServer");// Connecting to another Peer

        source = sDir + "/" + fileName;// File location of the Peer containing it
        target = dname;// Directory to which the file needs to be copied

        InputStream in = null;
        OutputStream out = null;

        try {
            File src = new File(source);// File object containing the location of the file
            File trgt = new File(target);// File object containing the location where the file needs to be copied

            System.out.println("The file " + src);

            if (!trgt.exists()) {
                trgt.createNewFile();// If the target didn't contain a file then create a new file
            }

            in = new FileInputStream(src);// This object is used for reading file content from the directory
            out = new FileOutputStream(trgt + "/" + src.getName());// This is used to write read file contents to a file

            byte[] buff = new byte[(int) src.length()];// Buffer of the length of the source file
            // Reading the content from the source file
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);// writing content to the destination file
            }
            System.out.println("The file " + src + " has been successfully transferred to the destination: " + trgt + "/" + src.getName());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static void main(String[] args) {
        // Manually declaring the ports for Peers
        String p1 = "2350";
        String p2 = "1589";
        String p3 = "2001";
        String p4 = "1301";
        String fil = "100.txt";// File name

        // Directories for all peers
        String dname1 = "/Users/amitaryan/Downloads/CN/PEE1";
        String dname2 = "/Users/amitaryan/Downloads/CN/PEER2";
        String dname3 = "/Users/amitaryan/Downloads/CN/PEER3";
        String dname4 = "/Users/amitaryan/Downloads/CN/PEER4";

        try {
            int s1 = Integer.parseInt(p1);
            int s2 = Integer.parseInt(p2);
            int s3 = Integer.parseInt(p3);
            int s4 = Integer.parseInt(p4);
            // Creating registry for all peers
            Registry r1 = LocateRegistry.createRegistry(s1);
            Registry r2 = LocateRegistry.createRegistry(s2);
            Registry r3 = LocateRegistry.createRegistry(s3);
            Registry r4 = LocateRegistry.createRegistry(s4);

            ClientInterface ci1 = new ClientImpl(dname1);
            ClientInterface ci2 = new ClientImpl(dname2);
            ClientInterface ci3 = new ClientImpl(dname3);
            ClientInterface ci4 = new ClientImpl(dname4);

            // Binding the Peers for specific ports
            Naming.bind("rmi://localhost:" + s1 + "/FileServer", ci1);
            Naming.bind("rmi://localhost:" + s2 + "/FileServer", ci2);
            Naming.bind("rmi://localhost:" + s3 + "/FileServer", ci3);
            Naming.bind("rmi://localhost:" + s4 + "/FileServer", ci4);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creating threads for each peer
        Thread t1 = new Thread(new Concurrent(p1, dname1, fil, "1"));
        Thread t2 = new Thread(new Concurrent(p2, dname2, fil, "2"));
        Thread t3 = new Thread(new Concurrent(p3, dname3, fil, "3"));
        Thread t4 = new Thread(new Concurrent(p4, dname4, fil, "4"));

        // Concurrent threads
        t1.start();
        t2.start();
        t3.start();
        // t4.start();
    }
}
