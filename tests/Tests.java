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
    PrintService service = (PrintService) Naming.lookup("rmi://localhost:5099/printer");

    public Tests() throws RemoteException, NotBoundException, MalformedURLException {
    }

    @Test
    public void testRestart() throws RemoteException {
        String username = "Craig";
        String password = "whoop";
        UUID SID = service.initiateSession(username,password);
        service.restart(SID);
        String dummy = service.queue(SID);
        assertEquals(dummy,"");
    }

    @Test
    public void testInitiate1() throws RemoteException {
        String username = "Craig";
        String password = "whoop";
        UUID SID = service.initiateSession(username,password);
        assertNotNull(SID);
    }

    @Test
    public void testInitiate2() throws RemoteException {
        String username = "Craig";
        String password = "who";
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
    public void testPrint1() throws IOException, InterruptedException {
        String username = "Craig";
        String password = "whoop";
        UUID SID = service.initiateSession(username,password);
        service.restart(SID);
        service.print("Docs.txt", "A1",SID);
        String dummy = service.queue(SID);
        assertEquals(dummy,"0 Docs.txt\n");
        TimeUnit.SECONDS.sleep(7);
        String dummy2 = service.print("File.txt", "A1",SID);
        assertEquals(dummy2,"Session expired");
    }

    @Test
    public void testTopQueue1() throws IOException, InterruptedException {
        String username = "Craig";
        String password = "whoop";
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
        String username = "Craig";
        String password = "whoop";
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
}
