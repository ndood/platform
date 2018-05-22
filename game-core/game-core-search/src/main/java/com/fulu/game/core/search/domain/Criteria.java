package com.fulu.game.core.search.domain;

public class Criteria {

    private String fieldName;
    private Object fieldValue;

    public Criteria(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
