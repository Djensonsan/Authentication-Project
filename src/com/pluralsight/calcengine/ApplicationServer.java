package com.pluralsight.calcengine;

import javax.sound.midi.Soundbank;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.*;

public class ApplicationServer {

    public static void main(String[] args) throws RemoteException, FileNotFoundException {
        // printerDeleteTable();
        Registry registery = LocateRegistry.createRegistry(5099);
        registery.rebind("printer", new PrintServant());
    }

    // Printer tries to delete the table.
    // Will give an SQLSyntaxErrorException: DROP command denied to user ...
    private static void printerDeleteTable()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD", "Printer", "password");
            String sql = "TRUNCATE Users";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.execute();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
