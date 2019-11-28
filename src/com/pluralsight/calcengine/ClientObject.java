package com.pluralsight.calcengine;
import java.sql.*;
import java.util.UUID;
import java.time.Duration;
import java.time.Instant;

public class ClientObject {
    int timeoutSeconds = 5;
    UUID uuid;
    Instant start;
    String username;
    String accessList;


    public ClientObject(UUID SID, String username, String accessList) {
        this.username = username;
        this.uuid = SID;
        this.start = Instant.now();
        this.accessList = accessList;
    }

    public boolean timeElapsed() {
        boolean elapsed = false;
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        if(timeElapsed.getSeconds()>timeoutSeconds){
            elapsed = true;
        }
        return elapsed;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getAccessList() {
        return accessList;
    }

}
