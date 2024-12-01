package com.rdev.bstrack.modals;

import java.util.Objects;

public class Buses {
    String routeName;
    String busId;

    public Buses(String routeName, String busId) {
        this.routeName = routeName;
        this.busId = busId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Buses buses = (Buses) o;
        return busId == buses.busId && Objects.equals(routeName, buses.routeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeName, busId);
    }

    @Override
    public String toString() {
        return "Buses{" +
                "routeName='" + routeName + '\'' +
                ", busId=" + busId +
                '}';
    }
}
