package com.example.doubleexpandablelistview;

import java.util.List;

public interface RoutePointsInfo extends ReceivableInfo {
    Integer getId();
    Integer getDebtorId();
    String getDescription();
    String getOverDueReceivableTotal();
    String getReceivableTotal();
    List<DeliveryPointWithInvoices> getDeliveryPointWithInvoices();
    boolean isOpen();
    void setOpen(boolean isOpen);
}
