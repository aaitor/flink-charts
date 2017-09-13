package com.foreach.poc.charts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TagEventTest {

    private static final String TAG_JSON_PATH= "src/test/resources/data/one-tag.json";
    private static String TAG_JSON_CONTENT;

    private static ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        TAG_JSON_CONTENT= new String(Files.readAllBytes(Paths.get(TAG_JSON_PATH)));
        objectMapper= new ObjectMapper();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void convertToModel() throws Exception {

        TagEvent tagEvent= TagEvent.convertToModel(TAG_JSON_CONTENT);

        assertEquals("D57A56E7-9827-43", tagEvent.tagid);
        assertEquals(1495195191721l, tagEvent.timestamp);
        assertEquals("America/New_York", tagEvent.timezone);
        assertEquals("AUDIO", tagEvent.type);
        assertEquals("MD", tagEvent.geoZone);
        assertEquals("Maryland", tagEvent.geoRegionLocality);
        assertEquals("United States", tagEvent.geoRegionCountry);
        assertEquals(38.96, tagEvent.latitude, 0.001);
        assertEquals(-76.21, tagEvent.longitude, 0.001);
        assertEquals("XXX", tagEvent.client);
        assertEquals(329032579, tagEvent.trackId);
        assertEquals("Loveless", tagEvent.trackTitle);
        assertEquals("Lo Moon", tagEvent.artistName);

    }

}