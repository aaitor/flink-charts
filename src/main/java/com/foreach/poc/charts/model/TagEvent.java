package com.foreach.poc.charts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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

    static final Logger log= LogManager.getLogger(TagEvent.class);

    @JsonProperty
    public String tagid;

    @JsonProperty
    public long timestamp;

    @JsonProperty
    public String timezone;

    @JsonProperty
    public String type;

    @JsonProperty
    public double latitude;

    @JsonProperty
    public double longitude;

    @JsonProperty
    public String client;

    public long trackId;

    public String trackTitle;

    public String artistName;

    public String geoZone;     // Tag location 2-letter state code when country is US

    public String geoRegionLocality;

    public String geoRegionCountry;

    public TagEvent()   {
        // Jackson initialization of default values
        this.trackId= -1l;
        this.trackTitle= "";
        this.artistName= "";
        this.geoZone= "";
        this.geoRegionCountry= "";
        this.geoRegionLocality= "";
    }

    // Reduced constructor
    public TagEvent(long trackId, String artistName, String trackTitle, String geoZone) {
        this();
        this.trackId= trackId;
        this.artistName= artistName;
        this.trackTitle= trackTitle;
        this.geoZone= geoZone;
    }

    // Reduced constructor
    public TagEvent(long trackId, String artistName, String trackTitle, String geoZone, String locality, String country) {
        this(trackId, artistName, trackTitle, geoZone);
        this.geoRegionLocality= locality;
        this.geoRegionCountry= country;
    }

    public static TagEvent builder(String jsonTag) throws IOException {
        return (TagEvent) convertToModel(jsonTag);
    }

    // TODO: Would be great to revisit this to implement any kind of generic nested parser
    /**
     * Methods to deal with nested TagEvent JSON structure. Jackson black magic can deal with it using
     * annotations. So some attributes have to be deserialized in an alternative way.
     * @param geolocation
     */
    @JsonSetter("geolocation")
    public void setGeolocation(LinkedHashMap geolocation) {
        try {
            if (geolocation.containsKey("zone"))
                this.geoZone= geolocation.get("zone").toString();
            if (geolocation.containsKey("latitude"))
                this.latitude= Double.parseDouble(geolocation.get("latitude").toString());
            if (geolocation.containsKey("longitude"))
                this.longitude= Double.parseDouble(geolocation.get("longitude").toString());

            if (geolocation.containsKey("region") && geolocation.get("region") instanceof LinkedHashMap)  {
                LinkedHashMap<String, String> region= (LinkedHashMap) geolocation.get("region");
                if (region.containsKey("country"))
                    this.geoRegionCountry= region.get("country").toString();
                if (region.containsKey("locality"))
                    this.geoRegionLocality= region.get("locality").toString();
            }
        }   catch (Exception ex)    {
            log.error("Unable to parse the TagEvent(geolocation): " + geolocation + ", Exception: " + ex.getMessage());
        }

    }

    @JsonSetter("match")
    public void setMatch(LinkedHashMap match) {
        try {

            if (match.containsKey("track") && match.get("track") instanceof LinkedHashMap)  {
                LinkedHashMap<String, Object> track= (LinkedHashMap) match.get("track");
                if (track.containsKey("id"))
                    this.trackId= Long.parseLong(track.get("id").toString());
                if (track.containsKey("metadata") && track.get("metadata") instanceof LinkedHashMap)  {
                    LinkedHashMap<String, String> metadata= (LinkedHashMap) track.get("metadata");
                    if (metadata.containsKey("artistname"))
                        this.artistName= metadata.get("artistname");
                    if (metadata.containsKey("tracktitle"))
                        this.trackTitle= metadata.get("tracktitle");
                }

            }
        }   catch (Exception ex)    {
            log.error("Unable to parse the TagEvent(match): " + match + ", Exception: " + ex.getMessage());
        }

    }

    @Override
    public String toString() {
        return "TagEvent{" +
                "tagid='" + tagid + '\'' +
                ", timestamp=" + timestamp +
                ", trackId=" + trackId +
                ", trackTitle='" + trackTitle + '\'' +
                ", artistName='" + artistName + '\'' +
                ", geoZone='" + geoZone + '\'' +
                ", geoRegionLocality='" + geoRegionLocality + '\'' +
                ", geoRegionCountry='" + geoRegionCountry + '\'' +
                '}';
    }

    public static TagEvent convertToModel(String json) throws IOException {
        return getReaderInstance(TagEvent.class).readValue(json);
    }
}
