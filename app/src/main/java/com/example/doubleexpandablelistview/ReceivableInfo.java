package com.example.doubleexpandablelistview;

public interface ReceivableInfo {

    Integer getId();

    ItemType getItemType();

    enum ItemType {
        DebtorLevel, DpRouteLevel, DeliveryPointAndItsInvoicesLevel
    }
}
