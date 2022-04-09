package com.example.doubleexpandablelistview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SelectedGroupCallback{

    private MyAdapter myAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        myAdapter = new MyAdapter(this);
        recyclerView.addItemDecoration(new FirstLevelStickyHeaderItemDecoration(recyclerView, myAdapter));
        recyclerView.addItemDecoration(
            new DpInvoicesDividerItemDecoration(
                myAdapter,
                AppCompatResources.getDrawable(this, R.drawable.dp_invoice_divider)
            )
        );
        recyclerView.addItemDecoration(new SecondLevelStickyHeaderItemDecoration(myAdapter));
        recyclerView.setAdapter(myAdapter);
        myAdapter.submitList(getAdapterList());
    }

    private List<ReceivableInfo> getAdapterList() {
        int headerCount = 0;
        List<ReceivableInfo> adapterList = new ArrayList<>();
        for (int i = 0; i < 50; i++)
            adapterList.add(getDebtorHeaderGroup(headerCount++));
        return adapterList;
    }

    private ReceivableInfo getDebtorHeaderGroup(Integer id) {
        return new Debtor(
            id,
            ReceivableInfo.ItemType.DebtorLevel,
            "Рога и копыта (ИНН 11111111111)",
            "55",
            "67",
            false,
            getRoutesPointsInfoCopy(id)
        );
    }

    private List<DeliveryPointWithInvoices> getDPWithInvoices(Integer debtorId, Integer routeId) {
        List<DeliveryPointWithInvoices> generatedList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            generatedList.add(new DeliveryPointWithInvoices(debtorId, routeId, i, getInvoices()));
        }
        return generatedList;
    }

    private List<InvoiceInfo> getInvoices() {
        List<InvoiceInfo> invoiceInfo = new ArrayList<>();
        invoiceInfo.add(new InvoiceOwnerInfo(
            0,
            "[00989909] Гиль Валентина Никитична ИП Овощи и фрукты магазин",
            "Россия, Кабардино-Балкарской респ., Верхний Куркужин, г, Октябрьская ул. 257")
        );
        for (int i = 0; i < 5; i++) invoiceInfo.add(new Invoice(i, "[27.02.22] 6V11100022000030/800"));
        return invoiceInfo;
    }

    private List<RoutePointsInfo> getRoutesPointsInfoCopy(Integer debtorId) {
        List<RoutePointsInfo> generatedList = new ArrayList<>();
        InnerGroupCurrentRoutePointsInfo currentRoutePointsInfo =
            new InnerGroupCurrentRoutePointsInfo(
                debtorId,
                getDPWithInvoices(debtorId, InnerGroupCurrentRoutePointsInfo.id),
                false
            );
        InnerGroupOtherRoutesPointsInfo otherRoutesPointsInfo =
            new InnerGroupOtherRoutesPointsInfo(
                debtorId,
                getDPWithInvoices(debtorId, InnerGroupOtherRoutesPointsInfo.id),
                false
            );
        generatedList.add(currentRoutePointsInfo);
        generatedList.add(otherRoutesPointsInfo);
        return generatedList;
    }

    @Override
    public void outerGroupWasClicked(Integer groupId, boolean isOpen) {
        int insertPosition = 0;
        while (insertPosition < myAdapter.getCurrentList().size()) {
            ReceivableInfo receivableInfo = myAdapter.getCurrentList().get(insertPosition);
            insertPosition++;
            if (receivableInfo instanceof Debtor && receivableInfo.getId().equals(groupId)) break;
        }
        List<ReceivableInfo> newAdapterList = getCopyOfAdapterList(myAdapter.getCurrentList());
        if (newAdapterList.get(insertPosition-1) instanceof Debtor) {
            Debtor debtor = (Debtor) newAdapterList.get(insertPosition-1);
            debtor.setOpen(isOpen);
            try {
                if (isOpen) {
                    RoutePointsInfo routePointsInfo = debtor.getRoutePointsInfo().get(0);
                    routePointsInfo.setOpen(true);
                    newAdapterList.add(insertPosition++, routePointsInfo);
                    newAdapterList.addAll(insertPosition, debtor.getRoutePointsInfo().get(0).getDeliveryPointWithInvoices());
                    insertPosition += debtor.getRoutePointsInfo().get(0).getDeliveryPointWithInvoices().size();
                    newAdapterList.add(insertPosition, debtor.getRoutePointsInfo().get(1));
                }
                else
                    while (insertPosition < newAdapterList.size() && !(newAdapterList.get(insertPosition) instanceof Debtor))
                        newAdapterList.remove(insertPosition);
            } catch (Exception e) {
                Log.d(MainActivity.class.getSimpleName(), "clickedGroup: " + e.getLocalizedMessage());
            }
        }
        myAdapter.submitList(newAdapterList);
    }

    @Override
    public void innerGroupWasClicked(Integer outerGroupId, Integer innerGroupId, boolean isOpen) {
        int insertPosition = 0;
        while (insertPosition < myAdapter.getCurrentList().size()) {
            ReceivableInfo receivableInfo = myAdapter.getCurrentList().get(insertPosition);
            insertPosition++;
            if (receivableInfo instanceof Debtor && receivableInfo.getId().equals(outerGroupId)) break;
        }
        List<ReceivableInfo> newAdapterList = getCopyOfAdapterList(myAdapter.getCurrentList());
        if (newAdapterList.get(insertPosition) instanceof RoutePointsInfo) {
            insertPosition += ((RoutePointsInfo) newAdapterList.get(insertPosition)).getId().equals(innerGroupId)
                ? 1
                : ((RoutePointsInfo)newAdapterList.get(insertPosition)).isOpen()
                    ? ((RoutePointsInfo)newAdapterList.get(insertPosition)).getDeliveryPointWithInvoices().size() + 2
                    : 2;
            RoutePointsInfo routePointsInfo = (RoutePointsInfo) newAdapterList.get(insertPosition-1);
            routePointsInfo.setOpen(isOpen);
            try {
                if (isOpen) newAdapterList.addAll(insertPosition, routePointsInfo.getDeliveryPointWithInvoices());
                else for (int i = 0; i < routePointsInfo.getDeliveryPointWithInvoices().size(); i++) newAdapterList.remove(insertPosition);
            } catch (Exception e) {
                Log.d(MainActivity.class.getSimpleName(), "clickedGroup: " + e.getLocalizedMessage());
            }
        }
        myAdapter.submitList(newAdapterList);
    }

    private List<ReceivableInfo> getCopyOfAdapterList(List<ReceivableInfo> adapterList) {
        List<ReceivableInfo> copyList = new ArrayList<>();
        for (ReceivableInfo receivableInfo : adapterList) {
            if (receivableInfo instanceof Debtor) copyList.add(getDebtorHeaderGroupCopy((Debtor) receivableInfo));
            else if (receivableInfo instanceof RoutePointsInfo) copyList.add(getRoutesPointsInfoCopy((RoutePointsInfo) receivableInfo));
            else if (receivableInfo instanceof DeliveryPointWithInvoices) copyList.add(getDeliveryPointWithInvoicesCopy((DeliveryPointWithInvoices)receivableInfo));
        }
        return copyList;
    }

    private ReceivableInfo getDebtorHeaderGroupCopy(Debtor debtor) {
        return new Debtor(
            debtor.getId(),
            debtor.getItemType(),
            debtor.getTitle(),
            debtor.getOverDueReceivable(),
            debtor.getCommonReceivable(),
            debtor.isOpen(),
            debtor.getRoutePointsInfo()
        );
    }

    private ReceivableInfo getRoutesPointsInfoCopy(RoutePointsInfo routePointsInfo) {
        return routePointsInfo instanceof InnerGroupCurrentRoutePointsInfo
        ? new InnerGroupCurrentRoutePointsInfo(
            routePointsInfo.getDebtorId(),
            routePointsInfo.getDeliveryPointWithInvoices(),
            routePointsInfo.isOpen()
        )
        : new InnerGroupOtherRoutesPointsInfo(
            routePointsInfo.getDebtorId(),
            routePointsInfo.getDeliveryPointWithInvoices(),
            routePointsInfo.isOpen()
        );
    }

    private ReceivableInfo getDeliveryPointWithInvoicesCopy(DeliveryPointWithInvoices deliveryPointWithInvoices) {
        return new DeliveryPointWithInvoices(
            deliveryPointWithInvoices.getDebtorId(),
            deliveryPointWithInvoices.getRouteId(),
            deliveryPointWithInvoices.getId(),
            deliveryPointWithInvoices.getInvoices()
        );
    }
}

interface SelectedGroupCallback {
    void outerGroupWasClicked(Integer groupId, boolean isOpen);
    void innerGroupWasClicked(Integer outerGroupId, Integer innerGroupId, boolean isOpen);
}