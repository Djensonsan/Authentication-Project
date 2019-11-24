package com.pluralsight.calcengine;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class UserDatabase {
    // Username and password of the local mySQL instance admin.
    // Can be root for example.
    // Used access control of mySQL, Printer user only has SELECT privilege.
    static String admin;
    static String adminPassword;

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username: ");
        admin = scanner.nextLine();
        System.out.println("Password: ");
        adminPassword = scanner.nextLine();

        Boolean dropSuccess = dropPrinterAccount();
        int rows = AddPrinterAccount();
        AddGrantsPrinterAccount();
        System.out.println("Affected rows: "+rows);
        ClearTable("Users");
        AddUser("Alice","vMErcmgF","start,stop,print,status,restart,topQueue,setConfig,readConfig,queue,topQueue", "ServerManager");
        AddUser("Bob","zbY8MR6L","start,stop,status,restart,setConfig,readConfig","ServiceTechnician");
        AddUser("Cecilia","FRgBQ5sK","print,topQueue,queue,topQueue","PowerUser");
        AddUser("David","FFcBr5Ej","print,queue", "DefaultUser");
        AddUser("Erica","RnPRs958","print,queue","DefaultUser");
        AddUser("Fred","W6S9NACb","print,queue","DefaultUser");
        AddUser("George","KNdQT5w7","print,queue","DefaultUser");
        AddUser("John", "JnYhd8g4", "start,stop,status,restart,setConfig,readConfig,print,queue","ServiceTechnician,DefaultUser");
        AddRole("ServerManager", "start,stop,print,status,restart,topQueue,setConfig,readConfig,queue,topQueue");
        AddRole("ServiceTechnician", "start,stop,status,restart,setConfig,readConfig");
        AddRole("PowerUser", "print,topQueue,queue,topQueue");
        AddRole("DefaultUser","print,queue");
    }

    // What about the channel between database and printer?
    // GRANT SELECT ON PWD.Users TO 'Printer1'@'localhost';
    private static Boolean dropPrinterAccount() {
        Boolean dropSuccess = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
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
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
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
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
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
    private static int AddUser(String username, String password,String AccessList, String role) {
        int rowsAffected = 0;
        SHA256Hasher hasher = new SHA256Hasher();
        byte [] byteSalt = hasher.getSalt();
        String salt = hasher.byteToString(byteSalt);
        String hashedPassword = hasher.HashSHA256(salt,password);
        String access = "start,stop,print";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
            Statement stmt = con.createStatement();
            rowsAffected = stmt.executeUpdate("INSERT INTO Users (Username, Password, Salt, Access, Role) VALUES ('"+username+"','"+hashedPassword+"','"+salt+"','"+AccessList+"','"+role+"')");
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }

    // Returns the amount of rows affected by the query
    private static int AddRole(String role, String AccessList) {
        int rowsAffected = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
            Statement stmt = con.createStatement();
            rowsAffected = stmt.executeUpdate("INSERT INTO Roles (idroles, access) VALUES ('"+role+"','"+AccessList+"')");
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
                    "jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
            Statement stmt = con.createStatement();
            rowsAffected = stmt.executeUpdate("TRUNCATE TABLE "+tablename);
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }
}
