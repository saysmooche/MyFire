package com.bb.myfire;

public class Guest {

    public String guestName;
    public String email;
    public String guestId;

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public Guest(String guestId) {
        this.guestId = guestId;
    }

    public Guest(String guestName, String email) {
        this.guestName = guestName;
        this.email = email;
    }

    public Guest(){
    }
}
