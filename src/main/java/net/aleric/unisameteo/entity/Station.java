package net.aleric.unisameteo.entity;

public enum Station {

    RETTORATO("rettorato"), STECCA8("stecca8"), BARONISSI("baronissi");

    private final String id;

    Station(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    public static Station fromId(String id) throws IllegalArgumentException {
        return Station.valueOf(id.toUpperCase());
    }

    public static Station fromString(final String s) {
        return Station.fromId(s);
    }
}
