package com.example.doubleexpandablelistview;

public class InvoiceOwnerInfo implements InvoiceInfo {
    private final Integer id;
    private final String description;
    private final String address;

    public InvoiceOwnerInfo(
        Integer id,
        String description,
        String address
    ) {
        this.id = id;
        this.description = description;
        this.address = address;
    }

    public String getAddress() {
        return address;
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
