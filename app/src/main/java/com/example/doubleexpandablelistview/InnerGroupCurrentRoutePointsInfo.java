package com.example.doubleexpandablelistview;

import java.util.List;

public class InnerGroupCurrentRoutePointsInfo implements RoutePointsInfo {
    public static final int id = 0;
    private final Integer debtorId;
    private final List<DeliveryPointWithInvoices> deliveryPointWithInvoices;
    private boolean isOpen;

    public InnerGroupCurrentRoutePointsInfo(
        Integer debtorId,
        List<DeliveryPointWithInvoices> deliveryPointWithInvoices,
        boolean isOpen
    ) {
        this.debtorId = debtorId;
        this.deliveryPointWithInvoices = deliveryPointWithInvoices;
        this.isOpen = isOpen;
    }

    @Override
    public Integer getId() {
        return InnerGroupCurrentRoutePointsInfo.id;
    }

    @Override
    public Integer getDebtorId() {
        return debtorId;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.DpRouteLevel;
    }

    @Override
    public String getDescription() {
        return "Точки данного маршрута";
    }

    @Override
    public String getOverDueReceivableTotal() {
        return "16";
    }

    @Override
    public String getReceivableTotal() {
        return "30";
    }

    @Override
    public List<DeliveryPointWithInvoices> getDeliveryPointWithInvoices() { return deliveryPointWithInvoices; }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
