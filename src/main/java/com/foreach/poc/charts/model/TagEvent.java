package com.foreach.poc.charts.model;

import java.time.LocalDateTime;


/**
 * This class represents a simplified TagEvent
 * The TagEvent class is flat for this test (it doesn't represent the nested model found in the JSON events).
 * Would be possible to use a nested structure to provide a better semantic and a better understanding of the model.
 */
public class TagEvent {

    private String tagid;
    private LocalDateTime timestamp;
    private String timezone;
    private String type;
    private String geoZone;     // Tag location 2-letter state code when country is US
    private String geoRegionLocality;
    private String geoRegionCountry;
    private double latitude;
    private double longitude;

    private String client;

    public TagEvent()   {}

    public String getTagid() {
        return tagid;
    }

    public TagEvent setTagid(String tagid) {
        this.tagid = tagid;
        return this;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TagEvent setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getTimezone() {
        return timezone;
    }

    public TagEvent setTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public String getType() {
        return type;
    }

    public TagEvent setType(String type) {
        this.type = type;
        return this;
    }

    public String getGeoZone() {
        return geoZone;
    }

    public TagEvent setGeoZone(String geoZone) {
        this.geoZone = geoZone;
        return this;
    }

    public String getGeoRegionLocality() {
        return geoRegionLocality;
    }

    public TagEvent setGeoRegionLocality(String geoRegionLocality) {
        this.geoRegionLocality = geoRegionLocality;
        return this;
    }

    public String getGeoRegionCountry() {
        return geoRegionCountry;
    }

    public TagEvent setGeoRegionCountry(String geoRegionCountry) {
        this.geoRegionCountry = geoRegionCountry;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public TagEvent setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public TagEvent setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getClient() {
        return client;
    }

    public TagEvent setClient(String client) {
        this.client = client;
        return this;
    }

    @Override
    public String toString() {
        return "TagEvent{" +
                "tagid='" + tagid + '\'' +
                ", timestamp=" + timestamp +
                ", timezone='" + timezone + '\'' +
                ", type='" + type + '\'' +
                ", geoZone='" + geoZone + '\'' +
                ", geoRegionLocality='" + geoRegionLocality + '\'' +
                ", geoRegionCountry='" + geoRegionCountry + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", client='" + client + '\'' +
                '}';
    }
}
