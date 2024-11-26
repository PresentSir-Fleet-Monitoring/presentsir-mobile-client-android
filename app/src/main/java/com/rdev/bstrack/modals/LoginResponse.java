package com.rdev.bstrack.modals;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LoginResponse {
    @SerializedName("expiresIn")
    private long expiresIn;

    @SerializedName("user")
    private User user;

    @SerializedName("token")
    private String token;

    // Getters and setters


    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class User {
        @SerializedName("roles")
        private List<String> roles;

        @SerializedName("bus")
        private Bus bus;

        @SerializedName("gender")
        private String gender;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("contact")
        private String contact;

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public Bus getBus() {
            return bus;
        }

        public void setBus(Bus bus) {
            this.bus = bus;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        // Getters and setters

        public static class Bus {
            @SerializedName("busId")
            private int busId;

            @SerializedName("routeName")
            private String routeName;

            // Getters and setters

            public int getBusId() {
                return busId;
            }

            public void setBusId(int busId) {
                this.busId = busId;
            }

            public String getRouteName() {
                return routeName;
            }

            public void setRouteName(String routeName) {
                this.routeName = routeName;
            }
        }
    }
}
