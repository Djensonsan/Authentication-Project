package com.pluralsight.calcengine;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.io.IOException;

public class PrintServant extends UnicastRemoteObject implements PrintService {
    ArrayList<ClientObject> activeClients; // Print job Queue
    ArrayList<String> queue; // Print job Queue
    boolean printServerOn = false;
    private Logger logger;

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
    }

    ArrayList <Configuration> configurations = new ArrayList<>();

    public PrintServant() throws IOException{
        super();
        queue = new ArrayList<String>(); //Creating arraylist, used arraylist instead of queue/linkedlist to keep it simple.
        activeClients = new ArrayList<ClientObject>(); //Arraylist with all active clients
        configurations.add(new Configuration("colours", "black and white")); //Setting three configurations
        configurations.add(new Configuration("orientation", "portrait"));
        configurations.add(new Configuration("size", "A4"));

        FileHandler handler = new FileHandler("printServer.log",8096,1, true);
        logger = Logger.getLogger(PrintServant.class.getName());
        logger.addHandler(handler);
    }

    @Override
    public String print(String filename, String printer, UUID SID) throws IOException, RemoteException {
        if (printServerOn == true) {
            if (checkSession(SID)==true) {
                queue.add(filename);
                return "File: " + filename + " added to queue";
            } else {
                return "Session expired, invalid SID or access denied to function.";
            }
        } else {
            return "Print server off";
        }
    }

    @Override
    public String queue(UUID SID) {
        if (printServerOn == true) {
            if (checkSession(SID)==true) {
                return printQueue(queue);
            } else {
                return "Session expired, invalid SID or access denied to function.";
            }
        } else {
            return "Print server off";
        }
    }

    private String printQueue(ArrayList<String> queue) {
        Iterator itr = queue.iterator();
        int jobNumber = 0;
        String stringQueue = "";
        while (itr.hasNext()) {
            stringQueue += jobNumber + " " + itr.next() + "\n";
            jobNumber++;
        }
        return stringQueue;
    }

    @Override
    public String topQueue(int job, UUID SID) {
        if (printServerOn == true) {
            if (checkSession(SID)==true) {
                try {
                    String dummy = queue.get(job);
                    queue.remove(job);
                    queue.add(0, dummy);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    return "Invalid operation: Index out of bounds";
                }
                return "From server: Moved job to head of queue: " + job;
            } else {
                return "Session expired, invalid SID or access denied to function.";
            }
        } else {
            return "Print server off";
        }
    }

    @Override
    public String start(UUID SID) {
        if (checkSession(SID)==true) {
            if (printServerOn == true) {
                return "From server: Print server already on";
            } else {
                printServerOn = true;
                return "From server: Print server turning on";
            }
        } else {
            return "Session expired, invalid SID or access denied to function.";
        }
    }

    @Override
    public String stop(UUID SID) {
        if (checkSession(SID)==true) {
            if (printServerOn == false) {
                return "From server: Print server already off";
            } else {
                printServerOn = false;
                return "From server: Print server turning off";
            }
        } else {
            return "Session expired, invalid SID or access denied to function.";
        }
    }

    // Restart modelled as clearing the queue by turning "on/off", end-state is "on" again.
    @Override
    public String restart(UUID SID) {
        if (checkSession(SID)==true) {
            queue.clear();
            printServerOn = true;
            return "From server: Print server restarting";
        } else {
            return "Session expired, invalid SID or access denied to function.";
        }
    }

    @Override
    public String status (UUID SID) {
        if (printServerOn == true) {
            if (checkSession(SID)==true) {
                if (printServerOn == false) {
                    return "From server: Print server OFF" + "\n" + "Queue: " + queue.size() + " print requests";
                } else {
                    return "From server: Print server ON" + "\n" + "Queue: " + queue.size() + " print requests";
                }
            } else {
                return "Session expired, invalid SID or access denied to function.";
            }
        } else {
            return "Print server off";
        }
    }

    @Override
    public String readConfig (String parameter, UUID SID) {
        if (printServerOn == true) {
            if (checkSession(SID)==true) {
                for (int i = 0; i < configurations.size(); i++) {
                    if (configurations.get(i).getParameter().equals(parameter)) {
                        return configurations.get(i).getValue();
                    }
                }
                return "No parameter with this name";
            } else {
                return "Session expired, invalid SID or access denied to function.";
            }
        } else {
            return "Print server off";
        }
    }

    @Override
    public void setConfig(String parameter, String value, UUID SID) {
        if (printServerOn == true) {
            if (checkSession(SID)==true) {
                for (int i = 0; i < configurations.size(); i = i + 1) {
                    if (configurations.get(i).getParameter().equals(parameter)) {
                        configurations.get(i).setValue(value);
                    }
                }
            }
        }
    }

    // When the Session time has expired, the clientObject will be deleted and the client needs to authenticate again.
    // Will also check if the user is allowed to call the 'calling' function by checking the user's permissions.
    private boolean checkSession(UUID SID) {
        boolean sessionsValid = false;
        Iterator<ClientObject> iter = activeClients.iterator(); //the iter should handle the exceptions that can be caused by removing an item while iterating on the list
        while (iter.hasNext()) {
            ClientObject client = iter.next();
            if (client.timeElapsed()) {
                iter.remove();
            } else if (client.getUuid().equals(SID)) {
                String accessList = client.getAccessList();
                StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
                StackTraceElement e = stacktrace[2];
                String methodName = e.getMethodName();
                String [] accessListValues = accessList.split(",");
                if(Arrays.asList(accessListValues).contains(methodName)){
                    sessionsValid = true;
                    logger.info("Method invoked: "+methodName+" By: "+client.getUsername());
                }
            }
        }
        return sessionsValid;
    }


    @Override
    public UUID initiateSession(String username, String password) throws RemoteException {
        int ID = getUserId(username, password);
        UUID SID = null;
        if (ID > 0) {
            String accessList = getUserAccessControl(username);
            SID = UUID.randomUUID();
            ClientObject client = new ClientObject(SID,username,accessList);
            activeClients.add(client);
        }
        return SID;
    }

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

    @Override
    public String getUserAccessControl(String username) throws RemoteException {
        String accessList = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", "Printer", "password");
            String sql = "SELECT Access from USERS WHERE Username = ?;";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accessList = rs.getString("Access");
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return accessList;
    }

    public int AddUser (UUID SID, String username, String password,String AccessList) throws RemoteException {
        int rowsAffected = 0;
        if (checkSession(SID)==true) {
            SHA256Hasher hasher = new SHA256Hasher();
            byte[] byteSalt = hasher.getSalt();
            String salt = hasher.byteToString(byteSalt);
            String hashedPassword = hasher.HashSHA256(salt, password);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", "Printer", "password");
                String sql = "INSERT INTO Users (Username, Password, Salt, Access) VALUES (?,?,?,?)";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, salt);
                stmt.setString(4, AccessList);
                stmt.execute();
                con.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return rowsAffected;
    }

    public int RemoveUser(UUID SID, String username) throws RemoteException {
        int rowsAffected = 0;
        if (checkSession(SID)==true) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", "Printer", "password");
                String sql = "DELETE FROM Users WHERE Username =?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.executeUpdate();
                con.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return rowsAffected;
    }
}
