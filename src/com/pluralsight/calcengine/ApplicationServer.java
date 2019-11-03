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
        authenticateUser("Craig","whoop");
    }

    private static void authenticateUser(String username, String password) {
        int ID = GetUserId(username,password);
        if (ID > 0){
            System.out.println("User Authenticated: "+username);
        } else {
            System.out.println("Something went wrong!");
        }
    }
    // Problem what if multiple persons have same name+password?
    private static int GetUserId(String username, String password)
    {
        int userId = 0;
        SHA1Hasher hasher = new SHA1Hasher();
        String hashedPassword = hasher.HashSHA1(username,password);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD", "Printer", "password");
            String sql = "SELECT UserID from USERS WHERE Username = ? and Password = ?;";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userId = rs.getInt("UserId");
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return userId;
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
