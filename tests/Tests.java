import com.pluralsight.calcengine.SetupDatabase;
import com.pluralsight.calcengine.PrintService;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;


public class Tests {
    PrintService service = (PrintService) Naming.lookup("rmi://localhost:8099/printer");

    // Setup the initial state for the tests.
    public Tests() throws RemoteException, NotBoundException, MalformedURLException {
//        String username = "Alice";
//        String password = "vMErcmgF";
//        UUID SID = service.initiateSession(username,password);
        // Original organisational structure:
        SetupDatabase setup = new SetupDatabase();
        // ENTER NAME AND PASSWORD OF DATABASE ADMIN
        // Needed to setup initial state of database. (Add Alice atleast.)
        setup.setupDatabase("root","10jl0298");
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

    // Will test the organisational changes i.e. remove Bob, Update George and add Henry and Ida.
    @Test
    public void testOrganisationalChanges() throws  IOException, InterruptedException {
        // Alice has permission to add/remove users:
        String username = "Alice";
        String password = "vMErcmgF";
        UUID SID = service.initiateSession(username,password);
        service.RemoveUser(SID,"Bob");
        service.UpdateUser(SID,"George","ServiceTechnician,DefaultUser");
        service.AddUser(SID,"Henry","UTdQB5w8","DefaultUser");
        service.AddUser(SID,"Ida","BZdff5w9","PowerUser");

        // Bob is fired from the company:
        username = "Bob";
        password = "zbY8MR6L";
        SID = service.initiateSession(username,password);
        String dummy = service.start(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.status(SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");

        // George takes over Bob's functions:
        username = "George";
        password = "KNdQT5w7";
        SID = service.initiateSession(username,password);
        // Test Print & Queue
        dummy = service.print("Docs.txt", "A1",SID);
        dummy = service.queue(SID);
        assertEquals(dummy,"0 Docs.txt\n");
        // Test Start & Stop
        dummy = service.stop(SID);
        assertEquals(dummy,"From server: Print server turning off");
        dummy = service.start(SID);
        assertEquals(dummy,"From server: Print server turning on");
        // Test Topqueue, Status and Restart
        dummy = service.topQueue(1,SID);
        assertEquals(dummy,"Session expired, invalid SID or access denied to function.");
        dummy = service.status(SID);
        assertEquals(dummy,"From server: Print server ON\nQueue: 1 print requests");
        dummy = service.restart(SID);
        // Test setConfig and readConfig
        assertEquals(dummy,"From server: Print server restarting");
        service.setConfig("colours","Black",SID);
        dummy = service.readConfig("colours",SID);
        assertEquals(dummy,"Black");

        // Henry joins the company:
        username = "Henry";
        password = "UTdQB5w8";
        // Test print and queue
        SID = service.initiateSession(username,password);
        service.print("Docs.txt", "A1",SID);
        dummy = service.queue(SID);
        assertEquals(dummy,"0 Docs.txt\n");

        // Ida joins the company:
        username = "Ida";
        password = "BZdff5w9";
        SID = service.initiateSession(username,password);
        // Test print and queue
        service.print("Docs.txt", "A1",SID);
        dummy = service.queue(SID);
        assertEquals(dummy,"0 Docs.txt\n1 Docs.txt\n");
        // Test topQueue
        service.print("File.txt", "A1",SID);
        dummy = service.topQueue(1,SID);
        assertEquals(dummy,"From server: Moved job to head of queue: 1");
        // Test restart
        dummy = service.restart(SID);
        assertEquals(dummy,"From server: Print server restarting");
    }
}
