package com.pluralsight.calcengine;

import java.io.*;
import java.util.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Notes: Do you not communicate with the printserver if it's off?

public class PrintServant extends UnicastRemoteObject implements PrintService {

    ArrayList<String> queue; // Print job Queue
    boolean printServerStatus = false; // False = Off, True = On
    class Configuration{
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
    ArrayList<Configuration> configurations =  new ArrayList<>(); //stores the Configurations



    public PrintServant() throws RemoteException {
        super();
        queue = new ArrayList<String>(); //Creating arraylist , used arraylist instead of queue/linkedlist to keep it simple.
        configurations.add(new Configuration("colours", "black and white")); //Setting three configurations
        configurations.add(new Configuration("orientation", "portrait"));
        configurations.add(new Configuration("size", "A4"));
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

    @Override
    public String topQueue(int job) {
        String dummy = queue.get(job);
        queue.remove(job);
        queue.add(0,dummy);
        return "From server: Moved job to head of queue: "+job;
    }

    @Override
    public String start() {
        if(printServerStatus == true){
            return "From server: Print server already on";
        } else {
            printServerStatus = true;
            return "From server: Print server turning on";
        }
    }

    @Override
    public String stop() {
        if(printServerStatus == false){
            return "From server: Print server already off";
        } else {
            printServerStatus = false;
            return "From server: Print server turning off";
        }
    }

    @Override
    public String restart() {
        queue.clear();
        printServerStatus = false;
        return "From server: Print server restarting";
    }

    @Override
    public String status() {
        if(printServerStatus == false){
            return "From server: Print server OFF"+"\n"+"Queue: "+queue.size()+" print requests";
        } else {
            return "From server: Print server ON"+"\n"+"Queue: "+queue.size()+" print requests";
        }
    }

    @Override
    public String readConfig(String parameter) {
        for (int i=0; i<configurations.size(); i++) {
            if (configurations.get(i).getParameter().equals(parameter)){ //finds where is the parameter from the input
                return configurations.get(i).getValue();
            }
        }
        return "No parameter with this name";
    }

    @Override
    public void setConfig(String parameter, String value) {
        for (int i=0; i<configurations.size(); i=i+1) {
            if (configurations.get(i).getParameter().equals(parameter)){ //finds where is the parameter from the input
                configurations.get(i).setValue(value);
            }
        }
    }


}
