package com.example.doubleexpandablelistview;

import java.util.List;

public class InnerGroupOtherRoutesPointsInfo implements RoutePointsInfo {
    public static final int id = 1;
    private final Integer debtorId;
    private final List<DeliveryPointWithInvoices> deliveryPointWithInvoices;
    private boolean isOpen;

    public InnerGroupOtherRoutesPointsInfo(
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
        return InnerGroupOtherRoutesPointsInfo.id;
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
        return "Точки других маршрутов";
    }

    @Override
    public String getOverDueReceivableTotal() {
        return "11";
    }

    @Override
    public String getReceivableTotal() {
        return "22";
    }

    @Override
    public List<DeliveryPointWithInvoices> getDeliveryPointWithInvoices() {
        return deliveryPointWithInvoices;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
