package com.pluralsight.calcengine;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;

// Notes: Do you not communicate with the printserver if it's off?
// Try catch statements?
public class PrintServant extends UnicastRemoteObject implements PrintService {
    ArrayList<ClientObject> activeClients; // Print job Queue
    ArrayList<String> queue; // Print job Queue
    boolean printServerStatus = false; // False = Off, True = On

    class Configuration {
        private String parameter;
        private String value;

        public Configuration(String parameter, String value) {
            this.parameter = parameter;
            this.value = value;
        }

        public String getParameter() {
            return parameter;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    } //defines what is a Configuration (couples of parameters and values)

    ArrayList<Configuration> configurations = new ArrayList<>(); //stores the Configurations

    public PrintServant() throws RemoteException {
        super();
        queue = new ArrayList<String>(); //Creating arraylist, used arraylist instead of queue/linkedlist to keep it simple.
        activeClients = new ArrayList<ClientObject>(); //Arraylist with all active clients
        configurations.add(new Configuration("colours", "black and white")); //Setting three configurations
        configurations.add(new Configuration("orientation", "portrait"));
        configurations.add(new Configuration("size", "A4"));
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
            System.out.println("File: " + filename + " added to queue");
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
        if (checkSession(SID)) {
            for (int i = 0; i < configurations.size(); i++) {
                if (configurations.get(i).getParameter().equals(parameter)) { //finds where is the parameter from the input
                    return configurations.get(i).getValue();
                }
            }
            return "No parameter with this name";
        } else {
            return "Session expired";
        }
    }

    @Override
    public void setConfig(String parameter, String value, UUID SID) {
        if (checkSession(SID)) {
            for (int i = 0; i < configurations.size(); i = i + 1) {
                if (configurations.get(i).getParameter().equals(parameter)) { //finds where is the parameter from the input
                    configurations.get(i).setValue(value);
                }
            }
        }
    }

    // When the Session time has expired, the clientObject will be deleted and the client needs to authenticate again.
    public boolean checkSession(UUID SID) {
        Iterator<ClientObject> iter = activeClients.iterator(); //the iter should handle the exceptions that can be caused by removing an item while iterating on the list
        while (iter.hasNext()) {
            ClientObject client = iter.next();
            if (client.timeElapsed()) {
                iter.remove();
                //System.out.println("client disconnected"); //Use to check if it is working
            } else if (client.getUuid().equals(SID)) {
                return true;
            }
        }
        return false;
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

    // Problem what if multiple persons have same username?
    @Override
    public int getUserId(String username, String password) throws RemoteException {
        int userId = -1;
        String salt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", "Printer", "password");
            String sql = "SELECT Salt from USERS WHERE Username = ?;";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                salt = rs.getString("Salt");
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        SHA256Hasher hasher = new SHA256Hasher();
        String hashedPassword = hasher.HashSHA256(salt, password);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", "Printer", "password");
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

    // Printer tries to delete the table.
    // Will give an SQLSyntaxErrorException: DROP command denied to user ...
    public void printerDeleteTable() throws RemoteException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", "Printer", "password");
            String sql = "TRUNCATE Users";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.execute();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
