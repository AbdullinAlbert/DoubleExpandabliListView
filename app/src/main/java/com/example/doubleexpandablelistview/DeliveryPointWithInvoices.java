package com.example.doubleexpandablelistview;

import java.util.List;

public class DeliveryPointWithInvoices implements ReceivableInfo {
    private final Integer debtorId;
    private final Integer routeId;
    private final Integer id;
    private final List<InvoiceInfo> invoices;

    public DeliveryPointWithInvoices(
        Integer debtorId,
        Integer routeId,
        Integer id,
        List<InvoiceInfo> invoices
    ) {
        this.debtorId = debtorId;
        this.routeId = routeId;
        this.id = id;
        this.invoices = invoices;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.DeliveryPointAndItsInvoicesLevel;
    }

    public Integer getDebtorId() {
        return debtorId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public List<InvoiceInfo> getInvoices() {
        return invoices;
    }
}
