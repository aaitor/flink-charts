package com.foreach.poc.charts.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TagEventUtils {

    /**
     * Utility test class useful to generate mock data. Can be improved to generate more advanced testing scenarios.
     */

    private static Random rand= new Random();
    public static List<TagEvent> sampleTags= new ArrayList();
    static {
        sampleTags.add(0, new TagEvent(111l, "Artist 1", "Title 1", "Z1", "L1", "United States"));
        sampleTags.add(1, new TagEvent(222l, "Artist 2", "Title 2", "Z2", "L2", "United States"));
        sampleTags.add(2, new TagEvent(333l, "Artist 3", "Title 3", "Z1", "L1", "United States"));
        sampleTags.add(3, new TagEvent(444l, "Artist 4", "Title 4", "Z4", "L4", "United States"));
        sampleTags.add(4, new TagEvent(555l, "Artist 5", "Title 5", "Z2", "L5", "United States"));
    }

    public static List<TagEvent> getMockData(int numItems)    {
        int counter;
        List<TagEvent> tags= new ArrayList<>();
        for (counter= 0; counter< numItems; counter++) {
            // Getting a random tag from the list
            tags.add(counter, sampleTags.get( rand.nextInt( sampleTags.size() -1)));
        }
        return tags;
    }
}
