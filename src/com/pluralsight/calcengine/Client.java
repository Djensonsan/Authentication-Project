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
        System.out.println("---" + service.echo("Hey Server"));
        service.print("Docs.txt", "A1");
        service.print("File.txt", "A1");
        service.print("Words.txt", "A1");
        service.print("Train.txt", "A1");

        // Print the Queue on the Client Side
        ArrayList<String> queue = service.queue();
        printQueue(queue);

        // Server status
        System.out.println(service.status());

        // Server start/stop
        System.out.println(service.start());
        System.out.println(service.stop());

        // Server TopQueue
        service.topQueue(2);
        queue = service.queue();
        printQueue(queue);

        // Server restart
        System.out.println(service.restart());
        // Server status
        System.out.println(service.status());
    }

    public static void printQueue(ArrayList <String> queue) {
        //Traversing list through Iterator
        Iterator itr=queue.iterator();
        int jobNumber = 0;
        while(itr.hasNext()){
            System.out.println(jobNumber+" "+itr.next());
            jobNumber++;
        }
    }
}


