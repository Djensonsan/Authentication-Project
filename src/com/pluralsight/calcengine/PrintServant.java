package com.pluralsight.calcengine;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Notes: Do you not communicate with the printserver if it's off?
// Try catch statements?
public class PrintServant extends UnicastRemoteObject implements PrintService {
    ArrayList<ClientObject> activeClients; // Print job Queue
    ArrayList<String> queue; // Print job Queue
    boolean printServerStatus = false; // False = Off, True = On


    public PrintServant() throws RemoteException {
        super();
        queue = new ArrayList<String>(); //Creating arraylist, used arraylist instead of queue/linkedlist to keep it simple.
        activeClients = new ArrayList<ClientObject>(); //Arraylist with all active clients
    }

    @Override
    public String echo(String input) throws RemoteException {
        return "From server: " + input;
    }

    @Override
    public void print(String filename, String printer, UUID SID) throws IOException, RemoteException {
        if (checkSession(SID)) {
            // Logging the action to a file?
            queue.add(filename);
            System.out.println("File: "+filename+" added to queue");
        }
    }

    @Override
    public ArrayList<String> queue(UUID SID) throws RemoteException {
        if (checkSession(SID)) {
            return queue;
        } else {
            return null;
        }
    }

    @Override
    public String topQueue(int job, UUID SID) {
        if (checkSession(SID)) {
            String dummy = queue.get(job);
            queue.remove(job);
            queue.add(0, dummy);
            return "From server: Moved job to head of queue: " + job;
        } else {
            return "Session expired";
        }
    }

    @Override
    public String start(UUID SID) {
        if (checkSession(SID)) {
            if (printServerStatus == true) {
                return "From server: Print server already on";
            } else {
                printServerStatus = true;
                return "From server: Print server turning on";
            }
        } else {
            return "Session expired";
        }
    }

    @Override
    public String stop(UUID SID) {
        if (checkSession(SID)) {
            if (printServerStatus == false) {
                return "From server: Print server already off";
            } else {
                printServerStatus = false;
                return "From server: Print server turning off";
            }
        } else {
            return "Session expired";
        }
    }

    @Override
    public String restart(UUID SID) {
        if (checkSession(SID)) {
            queue.clear();
            printServerStatus = false;
            return "From server: Print server restarting";
        } else {
            return "Session expired";
        }
    }

    @Override
    public String status(UUID SID) {
        if (checkSession(SID)) {
            if (printServerStatus == false) {
                return "From server: Print server OFF" + "\n" + "Queue: " + queue.size() + " print requests";
            } else {
                return "From server: Print server ON" + "\n" + "Queue: " + queue.size() + " print requests";
            }
        } else {
            return "Session expired";
        }
    }

    @Override
    public String readConfig(String parameter, UUID SID) {
        return null;
    }

    @Override
    public void setConfig(String parameter, String value, UUID SID) {
    }

    // When the Session time has expired, the clientObject will be deleted and the client needs to authenticate again.
    // Something broken here!!!!
    public boolean checkSession(UUID SID) {
        boolean authenticated = false;
        for (ClientObject client : activeClients) {
            if (client.timeElapsed()) {
                activeClients.remove(client);
            } else if (client.getUuid().equals(SID)) {
                authenticated = true;
            }
        }
        return authenticated;
    }

    @Override
    public UUID initiateSession(String username, String password) throws RemoteException {
        int ID = getUserId(username, password);
        UUID SID = null;
        if (ID > 0) {
            SID = UUID.randomUUID();
            ClientObject client = new ClientObject(SID);
            activeClients.add(client);
        }
        return SID;
    }

    // Problem what if multiple persons have same name+password?
    @Override
    public int getUserId(String username, String password) throws RemoteException {
        int userId = -1;
        SHA1Hasher hasher = new SHA1Hasher();
        String hashedPassword = hasher.HashSHA1(username, password);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD", "Printer", "password");
            String sql = "SELECT UserID from USERS WHERE Username = ? and Password = ?;";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userId = rs.getInt("UserId");
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return userId;
    }
}
