package com.foreach.poc.charts.model;

import java.time.LocalDateTime;


/**
 * This class represents a simplified TagEvent
 * The TagEvent class is flat for this test (it doesn't represent the nested model found in the JSON events).
 * Would be possible to use a nested structure to provide a better semantic and a better understanding of the model.
 */
public class TagEvent {

    /**
     * Tag info
     */
    // Tag event identifier
    private String tagid;
    // Tag timestamp
    private LocalDateTime timestamp;
    private String timezone;
    private String type;

    /**
     * Geo-location information
     */
    // Tag location 2-letter state code when country is US
    private String geoZone;
    private String geoRegionLocality;
    private String geoRegionCountry;
    private double latitude;
    private double longitude;

    private String client;

    public TagEvent()   {}

}
