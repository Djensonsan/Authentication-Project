import com.pluralsight.calcengine.PrintService;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;

public class Tests {
    PrintService service = (PrintService) Naming.lookup("rmi://localhost:5099/printer");

    public Tests() throws RemoteException, NotBoundException, MalformedURLException {
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


}
