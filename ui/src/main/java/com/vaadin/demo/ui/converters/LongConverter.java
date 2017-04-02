package com.vaadin.demo.ui.converters;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

public class LongConverter implements Converter<String, Long> {
    @Override
    public Result<Long> convertToModel(String value, ValueContext context) {
        return Result.ok((value == null ||value.isEmpty())? null : Long.valueOf(value));
    }

    @Override
    public String convertToPresentation(Long value, ValueContext context) {
        return value == null ? "" : value.toString();
    }
}
