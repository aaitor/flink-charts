package com.foreach.poc.charts.core;

import com.foreach.poc.charts.model.TagEvent;
import org.apache.flink.api.java.DataSet;

/**
 * Interface defining the data pipeline behaviour
 * @param <T>
 */
public interface DataPipeline<T> {

    /**
     * ETL phase implementing the ingestion of data
     * @return
     */
    DataSet<?> ingestion();

    DataSet<?> cleansing(DataSet<?> input);

    DataSet<?> normalization(DataSet<?> input);

    DataSet<?> transformation(DataSet<?> input) throws Exception;

    boolean persistence(DataSet<?> input);
}
