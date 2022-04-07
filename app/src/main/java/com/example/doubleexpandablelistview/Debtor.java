package com.example.doubleexpandablelistview;

import java.util.List;

public class Debtor implements ReceivableInfo {
    private final Integer id;
    private final ItemType itemType;
    private final String title;
    private final String overDueReceivable;
    private final String commonReceivable;
    private boolean open;
    private final List<RoutePointsInfo> routePointsInfo;

    public Debtor(
        Integer id,
        ItemType itemType,
        String title,
        String overDueReceivable,
        String commonReceivable,
        boolean open,
        List<RoutePointsInfo> routePointsInfo
    ) {
        this.id = id;
        this.itemType = itemType;
        this.title = title;
        this.overDueReceivable = overDueReceivable;
        this.commonReceivable = commonReceivable;
        this.open = open;
        this.routePointsInfo = routePointsInfo;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getTitle() {
        return title;
    }

    public String getOverDueReceivable() {
        return overDueReceivable;
    }

    public String getCommonReceivable() {
        return commonReceivable;
    }

    public List<RoutePointsInfo> getRoutePointsInfo() {
        return routePointsInfo;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public ItemType getItemType() {
        return itemType;
    }
}
