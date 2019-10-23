package com.pluralsight.calcengine;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

public class Client
{
    public static void main(String[] args) throws IOException, NotBoundException {
        // Service stub object
       PrintService service = (PrintService) Naming.lookup("rmi://localhost:5099/printer");
       System.out.println("---"+ service.echo("Hey Server"));
       service.print("Docs.txt","A1");
       service.print("File.txt","A1");

       // Print the Queue on the Client Side
       ArrayList<String> queue = service.queue();
       printQueue(queue);

    }

    public static void printQueue(ArrayList <String> queue) {
        //Traversing list through Iterator
        Iterator itr=queue.iterator();
        int jobNumber = 0;
        while(itr.hasNext()){
            jobNumber++;
            System.out.println(jobNumber+" "+itr.next());
        }
    }
}


