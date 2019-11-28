package com.pluralsight.calcengine;

import java.sql.*;

// Class used for setting-up the initial state of the database.
public class SetupDatabase {
    // Username and password of the local mySQL instance admin.
    static String admin;
    static String adminPassword;

    public void setupDatabase(String admin, String password) {
        this.admin = admin;
        this.adminPassword = password;

        dropPrinterAccount();
        AddPrinterAccount();
        AddGrantsPrinterAccount();
        ClearTable("Users");
        ClearTable("Roles");

        AddUser("Alice","vMErcmgF","ServerManager");
        AddUser("Bob","zbY8MR6L","ServiceTechnician");
        AddUser("Cecilia","FRgBQ5sK","PowerUser");
        AddUser("David","FFcBr5Ej","DefaultUser");
        AddUser("Erica","RnPRs958","DefaultUser");
        AddUser("Fred","W6S9NACb","DefaultUser");
        AddUser("George","KNdQT5w7","DefaultUser");

        AddRole("ServerManager","start,stop,restart,status,readConfig,setConfig,print,queue,topQueue,AddUser,RemoveUser,UpdateUser,AddRole");
        AddRole("ServiceTechnician","start,stop,restart,status,readConfig,setConfig");
        AddRole("ServiceTechnician","restart,print,queue,topQueue");
        AddRole("DefaultUser","print,queue");
    }

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

    private static int AddUser(String username, String password,String Role) {
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
            rowsAffected = stmt.executeUpdate("INSERT INTO Users (Username, Password, Salt, Role) VALUES ('"+username+"','"+hashedPassword+"','"+salt+"','"+Role+"')");
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return rowsAffected;
    }

    private static int AddRole(String role, String access) {
        int rowsAffected = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/PWD?serverTimezone=UTC", admin, adminPassword);
            Statement stmt = con.createStatement();
            rowsAffected = stmt.executeUpdate("INSERT INTO Roles (RoleId, Access) VALUES ('"+role+"','"+access+"')");
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
