package com.pluralsight.calcengine;

import java.io.*;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PrintServant extends UnicastRemoteObject implements PrintService {
    ArrayList<String> queue;
    int jobNumber = 0;

    public PrintServant() throws RemoteException {
        super();
        queue = new ArrayList<String>(); //Creating arraylist , used arraylist instead of queue to keep it simple.
    }

    @Override
    public String echo(String input) throws RemoteException {
        return "From server: " + input;
    }

    @Override
    public void print(String filename, String printer) throws IOException, RemoteException {
        // Logging the action to a file
        OutputStream log = new FileOutputStream("log.txt");
        String dummy1 = filename.concat(" ");
        String dummy2 = printer.concat("\n");
        String output = dummy1.concat(dummy2);
        byte[] outputBytes =  output.getBytes();
        log.write(outputBytes);
        log.close();

        // Adding the print request to the queue.
        queue.add(filename);
    }

    @Override
    public ArrayList <String> queue() throws RemoteException {
        return queue;
    }
//
//    @Override
//    public void topQueue(int job) {
//
//    }
//
//    @Override
//    public void start() {
//
//    }
//
//    @Override
//    public void stop() {
//
//    }
//
//    @Override
//    public void restart() {
//
//    }
//
//    @Override
//    public String status() {
//        return null;
//    }
//
//    @Override
//    public String readConfig(String parameter) {
//        return null;
//    }
//
//    @Override
//    public void setConfig(String parameter, String value) {
//
//    }


}
