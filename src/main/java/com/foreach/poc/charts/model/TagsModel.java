package com.foreach.poc.charts.model;

import com.fasterxml.jackson.databind.*;

public abstract class TagsModel {

    private static ObjectMapper objectMapper= null;

    public static ObjectMapper getMapperInstance()  {
        if (objectMapper == null)
            objectMapper= new ObjectMapper();

        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        return objectMapper;
    }

    public static <T> ObjectReader getReaderInstance(Class<T> clazz)    {
        return getMapperInstance().reader(clazz);
    }

}
