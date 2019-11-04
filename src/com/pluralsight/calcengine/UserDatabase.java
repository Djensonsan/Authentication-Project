package com.pluralsight.calcengine;

import java.sql.*;

public class UserDatabase {
    // Username and password of the local mySQL instance admin.
    // Can be root for example.
    // Used access control of mySQL, Printer user only has SELECT privilege.
    static String admin = "root";
    static String adminPassword = "";

    public static void main(String args[]) {
        Boolean dropSuccess = dropPrinterAccount();
        int rows = AddPrinterAccount();
        AddGrantsPrinterAccount();
        System.out.println("Affected rows: "+rows);
        ClearTable("Users");
        AddUser("Craig","whoop");
        AddUser("Frank","whoopie");
        AddUser("Jens","whoop");
        AddUser("Frank","whoopie");
    }

    // What about the channel between database and printer?
    // GRANT SELECT ON PWD.Users TO 'Printer1'@'localhost';
    private static Boolean dropPrinterAccount() {
        Boolean dropSuccess = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD", admin, adminPassword);
            Statement stmt = con.createStatement();
            dropSuccess = stmt.execute("DROP USER 'Printer'@'localhost';");
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return dropSuccess;
    }

    private static int AddPrinterAccount() {
        int rowsAffected = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD", admin, adminPassword);
            Statement stmt = con.createStatement();
            rowsAffected += stmt.executeUpdate("CREATE USER 'Printer'@'localhost' IDENTIFIED BY 'password';");
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }

    private static int AddGrantsPrinterAccount() {
        int rowsAffected = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD", admin, adminPassword);
            Statement stmt = con.createStatement();
            stmt = con.createStatement();
            rowsAffected += stmt.executeUpdate("GRANT SELECT ON *.* TO 'Printer'@'localhost';");
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }

    // Returns the amount of rows affected by the query
    private static int AddUser(String username, String password) {
        int rowsAffected = 0;
        SHA1Hasher hasher = new SHA1Hasher();
        String hashedPassword = hasher.HashSHA1(username,password);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/PWD", admin, adminPassword);
            Statement stmt = con.createStatement();
            rowsAffected = stmt.executeUpdate("INSERT INTO Users (Username, Password) VALUES ('"+username+"','"+hashedPassword+"')");
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }

    private static int ClearTable(String tablename) {
        int rowsAffected = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/PWD", admin, adminPassword);
            Statement stmt = con.createStatement();
            rowsAffected = stmt.executeUpdate("TRUNCATE TABLE "+tablename);
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }
}
