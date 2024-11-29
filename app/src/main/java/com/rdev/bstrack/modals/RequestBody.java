package com.rdev.bstrack.modals;

public class RequestBody {
    private User user;
    private int busId;

    // Constructor
    public RequestBody(User user, int busId) {
        this.user = user;
        this.busId = busId;
    }

    // Getters and Setters (Optional if using Gson)
}
