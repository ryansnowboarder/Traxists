package com.xchange_place.traxists.traxists;

/**
 * Created by Ryan Fletcher on 8/3/2015.
 *
 * A class that stores data associated with a particular user. The class is implemented in one
 * object that is stored in the bundle when the activity is closed and re-created from the
 * bundle when the activity is opened.
 */
public class User {

    // the username of the client
    private String username;

    // the password associated with the username
    private String password;

    // the answer to the first recovery question associated with username
    private String recovery1;

    // the answer to the second recovery question associated with username
    private String recovery2;

    // the answer to the third recovery question associated with username
    private String recovery3;

    // stores whether or not the client is currently logged in
    // 0 -> no
    // 1 -> yes
    private boolean loggedIn = false;

    // stores the type of account that is logged into the system
    // 0 -> Creator account: creates Admin accounts and other Creator accounts
    // 1 -> Admin account: monitors and creates User accounts
    // 2 -> User account: processes data and sends to Admin accounts
    private short accType;

    /*
    *
    * GETTERS AND SETTERS FOR ABOVE ITEMS
    *
     */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecovery1() {
        return recovery1;
    }

    public void setRecovery1(String recovery1) {
        this.recovery1 = recovery1;
    }

    public String getRecovery2() {
        return recovery2;
    }

    public void setRecovery2(String recovery2) {
        this.recovery2 = recovery2;
    }

    public String getRecovery3() {
        return recovery3;
    }

    public void setRecovery3(String recovery3) {
        this.recovery3 = recovery3;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public short getAccType() {
        return accType;
    }

    public void setAccType(short accType) {
        this.accType = accType;
    }
}
