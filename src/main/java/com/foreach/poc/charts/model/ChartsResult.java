package com.foreach.poc.charts.model;

public class ChartsResult {

    private long trackId;

    private int counter;

    private TagEvent tagEvent;

    public ChartsResult(long trackId, int counter, TagEvent tagEvent) {
        this.trackId = trackId;
        this.counter = counter;
        this.tagEvent = tagEvent;
    }

    public long getTrackId() {
        return trackId;
    }

    public int getCounter() {
        return counter;
    }

    public TagEvent getTagEvent() {
        return tagEvent;
    }

    @Override
    public String toString() {
        // Returns TRACK TITLE , ARTIST NAME , COUNT
        return tagEvent.trackTitle + ", " + tagEvent.artistName + ", " +counter + " times";
    }
}
