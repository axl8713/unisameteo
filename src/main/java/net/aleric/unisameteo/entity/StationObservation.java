package net.aleric.unisameteo.entity;

import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.Map;

public class StationObservation {

    private Station station;
    private Map<WeatherParameter, String> data = new EnumMap<>(WeatherParameter.class);
    private ZonedDateTime time;
    private String webcamUrl;

    public StationObservation() {
    }

    public StationObservation(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Map<WeatherParameter, String> getData() {
        return data;
    }

    public void setData(Map<WeatherParameter, String> data) {
        this.data = data;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public String getWebcamUrl() {
        return webcamUrl;
    }

    public void setWebcamUrl(String webcamUrl) {
        this.webcamUrl = webcamUrl;
    }
}