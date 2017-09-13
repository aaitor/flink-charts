package com.foreach.poc.charts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;


/**
 * This class represents a simplified TagEvent
 * The TagEvent class is flat for this test (it doesn't represent the nested model found in the JSON events).
 * Would be possible to use a nested structure to provide a better semantic and a better understanding of the model.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TagEvent extends TagsModel implements FromJsonToModel {

    @JsonProperty
    public String tagid;

    @JsonProperty
    public long timestamp;

    @JsonProperty
    public String timezone;

    @JsonProperty
    public String type;

    public String geoZone;     // Tag location 2-letter state code when country is US

    public String geoRegionLocality;

    public String geoRegionCountry;

    @JsonProperty
    public double latitude;

    @JsonProperty
    public double longitude;

    @JsonProperty
    public String client;

    public TagEvent()   {
        this.geoZone= "";
        this.geoRegionCountry= "";
        this.geoRegionLocality= "";
    }

    public static TagEvent builder(String jsonTag) throws IOException {
        return (TagEvent) convertToModel(jsonTag);
    }

    /**
     * Method to deal with nested TagEvent JSON structure. Jackson black magic can deal with it using
     * annotations. So some attributes have to be deserialized in an alternative way.
     * @param geolocation
     */
    @JsonSetter("geolocation")
    public void setGeolocation(LinkedHashMap geolocation) {
        try {
            if (geolocation.containsKey("zone"))
                this.geoZone= geolocation.get("zone").toString();
            if (geolocation.containsKey("latitude"))
                this.latitude= (double) geolocation.get("latitude");
            if (geolocation.containsKey("longitude"))
                this.longitude= (double) geolocation.get("longitude");

            if (geolocation.containsKey("region") && geolocation.get("region") instanceof LinkedHashMap)  {
                LinkedHashMap<String, String> region= (LinkedHashMap<String, String>) geolocation.get("region");
                if (region.containsKey("country"))
                    this.geoRegionCountry= region.get("country").toString();
                if (region.containsKey("locality"))
                    this.geoRegionLocality= region.get("locality").toString();
            }
        }   catch (Exception ex)    {
        }

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

    public static TagEvent convertToModel(String json) throws IOException {
        return getReaderInstance(TagEvent.class).readValue(json);
    }
}
