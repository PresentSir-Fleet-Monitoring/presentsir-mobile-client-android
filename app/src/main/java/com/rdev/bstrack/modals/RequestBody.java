package com.rdev.bstrack.modals;

public class RequestBody {
    private User user;
    private String busId;

    // Constructor
    public RequestBody(User user, String busId) {
        this.user = user;
        this.busId = busId;
    }

    // Getters and Setters (Optional if using Gson)
}
