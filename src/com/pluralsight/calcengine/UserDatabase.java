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
        AddUser("Alice","vMErcmgF","ServerManager");
        AddUser("Bob","zbY8MR6L","ServiceTechnician");
        AddUser("Cecilia","FRgBQ5sK","PowerUser");
        AddUser("David","FFcBr5Ej","DefaultUser");
        AddUser("Erica","RnPRs958","DefaultUser");
        AddUser("Fred","W6S9NACb","DefaultUser");
        AddUser("George","KNdQT5w7","DefaultUser");
        AddRole("ServerManager", "start,stop,print,status,restart,topQueue,setConfig,readConfig,queue,topQueue,AddRole,AddUser,UpdateUser,RemoveUser");
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
            rowsAffected += stmt.executeUpdate("GRANT ALL PRIVILEGES ON *.* TO 'Printer'@'localhost';");
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }

    private static int AddUser(String username, String password, String role) {
        int rowsAffected = 0;
        SHA256Hasher hasher = new SHA256Hasher();
        byte [] byteSalt = hasher.getSalt();
        String salt = hasher.byteToString(byteSalt);
        String hashedPassword = hasher.HashSHA256(salt,password);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
            String sql = "INSERT INTO Users (Username, Password, Salt, Role) VALUES (?,?,?,?);";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, salt);
            stmt.setString(4, role);
            rowsAffected = stmt.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }

    private static int AddRole(String role, String AccessList) {
        int rowsAffected = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
            String sql = "INSERT INTO Roles (idroles, access) VALUES (?,?);";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, role);
            stmt.setString(2, AccessList);
            rowsAffected = stmt.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }

    private static int UpdateUser (String username, String role) {
        int rowsAffected = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
            String sql = "UPDATE Users SET role=? WHERE username=?;";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, role);
            stmt.setString(2, username);
            rowsAffected = stmt.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }

    private static int RemoveUser (String username) {
        int rowsAffected = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
            String sql = "DELETE FROM Users WHERE username=?;";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            rowsAffected= stmt.executeUpdate();
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
