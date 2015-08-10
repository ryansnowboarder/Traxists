package com.xchange_place.traxists.traxists;

/**
 * Created by Ryan Fletcher on 8/10/2015.
 *
 * A simple class to hold the location information associated with the location of a user
 * account. The only data stored is the username, the latitude of the account, and
 * the longitude of the account.
 *
 */
public class UserLocation {

    private String username;
    private String latitude;
    private String longitude;

    // getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
