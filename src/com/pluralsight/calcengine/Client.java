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
        System.out.println(service.start(SID));
        System.out.println(service.print("Docs.txt", "A1",SID));
        System.out.println(service.print("File.txt", "A1",SID));
        System.out.println(service.print("Words.txt", "A1",SID));
        System.out.println(service.print("Train.txt", "A1",SID));

        // Print the Queue
        System.out.println(service.queue(SID));

        // Server status
        System.out.println(service.status(SID));

        // Server TopQueue
        System.out.println(service.topQueue(2,SID));
        System.out.println(service.queue(SID));

        // Server restart
        System.out.println(service.restart(SID));
        // Server status
        System.out.println(service.status(SID));

        // Server start/stop
        System.out.println(service.start(SID));
        System.out.println(service.stop(SID));

        TimeUnit.SECONDS.sleep(15);
        service.print("Shouldnotaccept.txt", "A1",SID); //to test the timeout of the session
    }
}