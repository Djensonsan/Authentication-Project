import com.pluralsight.calcengine.PrintService;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;


public class Tests {
    PrintService service = (PrintService) Naming.lookup("rmi://localhost:8099/printer");

    public Tests() throws RemoteException, NotBoundException, MalformedURLException {
    }

    @Test
    public void testStop() throws RemoteException {
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        service.start(SID);
        String dummy = service.stop(SID);
        assertEquals(dummy,"From server: Print server turning off");
        dummy = service.stop(SID);
        assertEquals(dummy,"From server: Print server already off");
    }

    @Test
    public void testStart() throws RemoteException {
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        service.stop(SID);
        String dummy = service.start(SID);
        assertEquals(dummy,"From server: Print server turning on");
        dummy = service.start(SID);
        assertEquals(dummy,"From server: Print server already on");
    }

    @Test
    public void testServerOff() throws RemoteException {
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        service.stop(SID);
        String dummy = service.topQueue(1,SID);
        assertEquals(dummy,"Print server off");
        dummy = service.queue(SID);
        assertEquals(dummy,"Print server off");
    }

    @Test
    public void testRestart() throws RemoteException {
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        service.restart(SID);
        String dummy = service.queue(SID);
        assertEquals(dummy,"");
    }

    @Test
    public void testInitiate1() throws RemoteException {
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        assertNotNull(SID);
    }

    @Test
    public void testInitiate2() throws RemoteException {
        String username = "Alice";
        String password = "vMEr";
        UUID SID = service.initiateSession(username,password);
        assertNull(SID);
    }

    @Test
    public void testInitiate3() throws RemoteException {
        String username = null;
        String password = null;
        UUID SID = service.initiateSession(username,password);
        assertNull(SID);
    }

    @Test
    public void testRandomSID() throws IOException {
        UUID SID = UUID.randomUUID();
        String dummy = service.start(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.stop(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.status(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.restart(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.print("Docs.txt", "A1",SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        service.print("File.txt", "A1",SID);
        dummy = service.topQueue(2,SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
    }

    @Test
    public void testPrint() throws IOException, InterruptedException {
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        service.restart(SID);
        service.print("Docs.txt", "A1",SID);
        String dummy = service.queue(SID);
        assertEquals(dummy,"0 Docs.txt\n");
        TimeUnit.SECONDS.sleep(7);
        String dummy2 = service.print("File.txt", "A1",SID);
        assertEquals(dummy2,"Session expired, invalid SID or access denied to function.");
    }

    @Test
    public void testTopQueue1() throws IOException, InterruptedException {
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        service.restart(SID);
        service.print("Docs.txt", "A1",SID);
        service.print("File.txt", "A1",SID);
        service.topQueue(1,SID);
        String dummy = service.queue(SID);
        assertEquals(dummy,"0 File.txt\n1 Docs.txt\n");
    }

    @Test
    public void testTopQueue2() throws IOException, InterruptedException {
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        service.restart(SID);
        service.print("Docs.txt", "A1",SID);
        service.print("File.txt", "A1",SID);
        String dummy = service.topQueue(2,SID);
        assertEquals(dummy,"Invalid operation: Index out of bounds");
    }

    @Test
    public void testDeleteTable() {
        Exception dummy = new Exception();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", "Printer", "password");
            String sql = "TRUNCATE Users";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.execute();
            con.close();
        } catch (Exception e) {
            dummy = e;
        }
        assertEquals(dummy.getMessage(),"DROP command denied to user 'Printer'@'localhost' for table 'users'");
    }


    // Alice should be able to call all functions.
    // Alice should be able to call all functions.
    @Test
    public void testAccessControlAlice() throws IOException, InterruptedException {
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        String dummy = service.restart(SID);
        // Test Print & Queue
        service.print("Docs.txt", "A1",SID);
        dummy = service.queue(SID);
        assertEquals(dummy,"0 Docs.txt\n");
        service.print("File.txt", "A1",SID);
        // Test Start & Stop
        dummy = service.stop(SID);
        assertEquals(dummy,"From server: Print server turning off");
        dummy = service.start(SID);
        assertEquals(dummy,"From server: Print server turning on");
        // Test Topqueue, Status and Restart
        dummy = service.topQueue(1,SID);
        assertEquals(dummy,"From server: Moved job to head of queue: 1");
        dummy = service.status(SID);
        assertEquals(dummy,"From server: Print server ON\nQueue: 2 print requests");
        dummy = service.restart(SID);
        assertEquals(dummy,"From server: Print server restarting");
        // Test setConfig and readConfig
        assertEquals(dummy,"From server: Print server restarting");
        service.setConfig("colours","Black",SID);
        dummy = service.readConfig("colours",SID);
        assertEquals(dummy,"Black");
    }

    // Bob should not be able to call queue, print or topQueue.
    // All other functions Bob should be able to call.
    @Test
    public void testAccessControlBob() throws IOException, InterruptedException {
        String username = "Bob";
        String password = "zbY8MR6L";
        UUID SID = service.initiateSession(username,password);
        // Test Print & Queue
        String dummy = service.print("Docs.txt", "A1",SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.queue(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        // Test Start & Stop
        dummy = service.stop(SID);
        assertEquals(dummy,"From server: Print server turning off");
        dummy = service.start(SID);
        assertEquals(dummy,"From server: Print server turning on");
        // Test Topqueue, Status and Restart
        dummy = service.topQueue(1,SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.status(SID);
        assertEquals(dummy,"From server: Print server ON\nQueue: 0 print requests");
        dummy = service.restart(SID);
        // Test setConfig and readConfig
        assertEquals(dummy,"From server: Print server restarting");
        service.setConfig("colours","Black",SID);
        dummy = service.readConfig("colours",SID);
        assertEquals(dummy,"Black");
    }

    // Cecilia should be able to call restart, queue, print and topQueue.
    @Test
    public void testAccessControlCecilia() throws IOException, InterruptedException {
        String username = "Cecilia";
        String password = "FRgBQ5sK";
        UUID SID = service.initiateSession(username,password);
        // Test Print & Queue
        String dummy = service.restart(SID);
        service.print("Docs.txt", "A1",SID);
        dummy = service.queue(SID);
        assertEquals(dummy,"0 Docs.txt\n");
        service.print("File.txt", "A1",SID);
        // Test Start & Stop
        dummy = service.stop(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.start(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        // Test Topqueue, Status and Restart
        dummy = service.topQueue(1,SID);
        assertEquals(dummy,"From server: Moved job to head of queue: 1");
        dummy = service.status(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.restart(SID);
        assertEquals(dummy,"From server: Print server restarting");
        // Test setConfig and readConfig
        service.setConfig("colours","Black",SID);
        dummy = service.readConfig("colours",SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
    }
}
