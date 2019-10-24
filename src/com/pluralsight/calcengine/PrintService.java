package com.pluralsight.calcengine;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PrintService extends Remote {

    // To test
    public String echo (String input) throws RemoteException;
    // Real Functionality
    public void print (String filename, String printer) throws IOException, RemoteException;   // prints file filename on the specified printer
    public ArrayList<String> queue() throws RemoteException;   // lists the print queue on the user's display in lines of the form <job number>   <file name>
    public String topQueue(int job) throws RemoteException;   // moves job to the top of the queue
    public String start() throws RemoteException;   // starts the print server
    public String stop() throws RemoteException;   // stops the print server
    public String restart() throws RemoteException;   // stops the print server, clears the print queue and starts the print server again
    public String status() throws RemoteException;  // prints status of printer on the user's display
    public String readConfig(String parameter) throws RemoteException;   // prints the value of the parameter on the user's display
    public void setConfig(String parameter, String value) throws RemoteException;   // sets the parameter to value
}
