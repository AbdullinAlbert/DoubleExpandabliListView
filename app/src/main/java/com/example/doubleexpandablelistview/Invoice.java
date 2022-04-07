package com.example.doubleexpandablelistview;

public class Invoice implements InvoiceInfo {
    private final Integer id;
    private final String description;

    public Invoice(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
