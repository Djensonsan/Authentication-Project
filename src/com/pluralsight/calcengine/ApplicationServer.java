package com.pluralsight.calcengine;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer {

    public static void main(String[] args) throws RemoteException {
        Registry registery = LocateRegistry.createRegistry(5099);
        try {
            registery.rebind("printer", new PrintServant());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}