package com.pluralsight.calcengine;
import java.util.UUID;
import java.time.Duration;
import java.time.Instant;

public class ClientObject {
    int timeoutSeconds = 5;
    UUID uuid;
    Instant start;
    String username;

    public ClientObject(UUID SID, String username) {
        this.username = username;
        this.uuid = SID;
        this.start = Instant.now();
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
}
