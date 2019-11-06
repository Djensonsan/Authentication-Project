package com.pluralsight.calcengine;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

public interface PrintService extends Remote {

    // To test
    public String echo (String input) throws RemoteException;
    // Real Functionality
    public void print (String filename, String printer, UUID SID) throws IOException, RemoteException;   // prints file filename on the specified printer
    public ArrayList<String> queue(UUID SID) throws RemoteException;   // lists the print queue on the user's display in lines of the form <job number>   <file name>
    public String topQueue(int job,UUID SID) throws RemoteException;   // moves job to the top of the queue
    public String start(UUID SID) throws RemoteException;   // starts the print server
    public String stop(UUID SID) throws RemoteException;   // stops the print server
    public String restart(UUID SID) throws RemoteException;   // stops the print server, clears the print queue and starts the print server again
    public String status(UUID SID) throws RemoteException;  // prints status of printer on the user's display
    public String readConfig(String parameter,UUID SID) throws RemoteException;   // prints the value of the parameter on the user's display
    public void setConfig(String parameter, String value,UUID SID) throws RemoteException;   // sets the parameter to value
    public int getUserId(String username, String password) throws RemoteException;
    public UUID initiateSession(String username, String password) throws RemoteException;
    public void printerDeleteTable() throws RemoteException;
}
