package com.pluralsight.calcengine;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;



public class Client
{
    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username: ");
        String username = scanner.nextLine();
        System.out.println("Password: ");
        String password = scanner.nextLine();

        PrintService service = (PrintService) Naming.lookup("rmi://localhost:5099/printer");

        UUID SID = service.initiateSession(username,password);
        if(SID != null){
            System.out.println("Login succeeded for user: "+username);
        } else {
            System.out.println("Login failed for user: "+username);
        }
        service.print("Docs.txt", "A1",SID);
        service.print("File.txt", "A1",SID);
        service.print("Words.txt", "A1",SID);
        service.print("Train.txt", "A1",SID);
        TimeUnit.SECONDS.sleep(15);
        service.print("Shouldnotaccept.txt", "A1",SID); //to test the timeout of the session

//        // Print the Queue on the Client Side
//        ArrayList<String> queue = service.queue(SID);
//        printQueue(queue);
//
//        // Server status
//        System.out.println(service.status(SID));
//
//        // Server start/stop
//        System.out.println(service.start(SID));
//        System.out.println(service.stop(SID));
//
//        // Server TopQueue
//        service.topQueue(2,SID);
//        queue = service.queue(SID);
//        printQueue(queue);
//
//        // Server restart
//        System.out.println(service.restart(SID));
//        // Server status
//        System.out.println(service.status(SID));
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


