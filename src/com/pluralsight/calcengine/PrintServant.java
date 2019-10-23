package com.pluralsight.calcengine;

import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Queue file
// Log file

public class PrintServant extends UnicastRemoteObject implements PrintService{
    public PrintServant() throws RemoteException {
        super();
    }

    @Override
    public String echo(String input) throws RemoteException {
        return "From server: " + input;
    }

    @Override
    public void print(String filename, String printer) throws IOException {
        OutputStream log = new FileOutputStream("log.txt");
        String dummy1 = filename.concat(" ");
        String dummy2 = printer.concat("\n");
        String output = dummy1.concat(dummy2);
        byte[] outputBytes =  output.getBytes();
        log.write(outputBytes);
        log.close();
    }

//    @Override
//    public String queue() {
//        return null;
//    }
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
