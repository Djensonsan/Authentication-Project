package com.pluralsight.calcengine;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer {

    public static void main(String[] args) throws RemoteException, FileNotFoundException {
        Registry registery = LocateRegistry.createRegistry(5099);
        registery.rebind("printer", new PrintServant());
    }
}
