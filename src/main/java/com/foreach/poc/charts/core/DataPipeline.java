package com.foreach.poc.charts.core;

import com.foreach.poc.charts.model.TagEvent;
import org.apache.flink.api.java.DataSet;

public interface DataPipeline {

    DataSet<TagEvent> ingestion();

    DataSet<?> cleansing(DataSet<?> input);

    DataSet<?> normalization(DataSet<?> input);

    DataSet<?> transformation(DataSet<?> input);

    boolean persistence(DataSet<?> input);
}
