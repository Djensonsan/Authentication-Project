package com.pluralsight.calcengine;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client
{
    public static void main(String[] args) throws IOException, NotBoundException {
        // Service stub object
       PrintService service = (PrintService) Naming.lookup("rmi://localhost:5099/printer");
       System.out.println("---"+ service.echo("Hey Server"));
       service.print("Docs.txt","A1");
    }
}
