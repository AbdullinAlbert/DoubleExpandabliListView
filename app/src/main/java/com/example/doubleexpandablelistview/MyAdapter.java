package com.example.doubleexpandablelistview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends ListAdapter<ReceivableInfo, RecyclerView.ViewHolder>
    implements FirstLevelStickyHeaderItemDecoration.StickyHeaderInfoProvider,
        DpInvoicesDividerItemDecoration.ColorItemDividerHelper,
        SecondLevelStickyHeaderItemDecoration.SecondLevelStickyHeaderInfoProvider {

    private final int OUTER_GROUP_VIEW_TYPE = 0;
    private final int INNER_GROUP_VIEW_TYPE = 1;
    private final int INNER_GROUP_CHILD_VIEW_TYPE = 2;
    private final SelectedGroupCallback mSelectedGroupCallback;
    private int firstLevelHeaderHeight = 0;
    private int currentPositionById = -1;
    private final Map<Integer, InnerGroupChildViewHolder> listOfHolders = new HashMap<>();

    public static final String TAG = MyAdapter.class.getSimpleName();

    public MyAdapter(SelectedGroupCallback selectedGroupCallback) {
        super(new MyDiffUtil());
        mSelectedGroupCallback = selectedGroupCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == OUTER_GROUP_VIEW_TYPE) return new OuterGroupViewHolder(inflater.inflate(R.layout.outter_group, parent, false));
        else if (viewType == INNER_GROUP_VIEW_TYPE)
            return new InnerGroupViewHolder(inflater.inflate(R.layout.inner_group, parent, false));
        else return new InnerGroupChildViewHolder(inflater.inflate(R.layout.group_child_invoice_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case OUTER_GROUP_VIEW_TYPE:
                OuterGroupViewHolder outerGroupViewHolder = (OuterGroupViewHolder) holder;
                outerGroupViewHolder.bind((Debtor) getItem(position), mSelectedGroupCallback);
                break;
            case INNER_GROUP_VIEW_TYPE:
                InnerGroupViewHolder innerGroupViewHolder = (InnerGroupViewHolder) holder;
                innerGroupViewHolder.bind((RoutePointsInfo) getItem(position), mSelectedGroupCallback);
                break;
            case INNER_GROUP_CHILD_VIEW_TYPE:
                InnerGroupChildViewHolder innerGroupChildViewHolder = (InnerGroupChildViewHolder) holder;
                innerGroupChildViewHolder.bind((DeliveryPointWithInvoices) getItem(position), getItem(position).getId() == currentPositionById);
                listOfHolders.put(getItem(position).getId(), innerGroupChildViewHolder);
                break;
        }
    }

    public void updateCurrentSelectedPosition(@NonNull InnerGroupChildViewHolder holder) {
        int newCurrentPos = -1;
        for (Integer key : listOfHolders.keySet()) {
            InnerGroupChildViewHolder helperHolder = listOfHolders.get(key);
            if (helperHolder == null) return;
            if (helperHolder.equals(holder)) {
                newCurrentPos = key;
                if (currentPositionById != -1) {
                    helperHolder = listOfHolders.get(currentPositionById);
                    if (helperHolder != null) helperHolder.setPositionAsUnselected();
                    else return;
                }
                helperHolder = listOfHolders.get(newCurrentPos);
                if (helperHolder != null) {
                    helperHolder.setPositionAsSelected();
                    currentPositionById = newCurrentPos;
                    break;
                }
                else return;
            }
        }
    }

    public void resetSelectedReceivable() {
        listOfHolders.clear();
        currentPositionById = -1;
    }

    @Override
    public int getItemViewType(int position) {
       if (getItem(position).getItemType() == ReceivableInfo.ItemType.DebtorLevel) return OUTER_GROUP_VIEW_TYPE;
       else if (getItem(position).getItemType() == ReceivableInfo.ItemType.DpRouteLevel) return INNER_GROUP_VIEW_TYPE;
       else return INNER_GROUP_CHILD_VIEW_TYPE;
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPos = itemPosition;
        while (headerPos >= 0 && !getItem(headerPos).getItemType().equals(ReceivableInfo.ItemType.DebtorLevel)) {
            headerPos--;
        }
        return headerPos;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.outter_group;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        final TextView groupId = header.findViewById(R.id.groupId);
        final TextView title = header.findViewById(R.id.groupTitle);
        final TextView overDueReceivable = header.findViewById(R.id.overdueReceivable);
        final TextView commonReceivable = header.findViewById(R.id.commonReceivable);
        Debtor dataDebtor = (Debtor) getItem(headerPosition);
        groupId.setText(String.valueOf(dataDebtor.getId()));
        title.setText(dataDebtor.getTitle());
        overDueReceivable.setText(dataDebtor.getOverDueReceivable());
        commonReceivable.setText(dataDebtor.getCommonReceivable());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return getItem(itemPosition).getItemType().equals(ReceivableInfo.ItemType.DebtorLevel);
    }

    @Override
    public void headerWasClicked(Integer groupPosition) {
        if (getItem(groupPosition) instanceof Debtor) {
            Debtor debtor = (Debtor) getItem(groupPosition);
            if (mSelectedGroupCallback != null) mSelectedGroupCallback.outerGroupWasClicked(debtor.getId(), !debtor.isOpen());
        }
    }

    @Override
    public boolean isNeedAddColorDivider(int currentPos) {
        if (getCurrentList().size() == currentPos + 1) return false;
        List<ReceivableInfo> list = getCurrentList();
        ReceivableInfo currentPosItem = list.get(currentPos);
        ReceivableInfo nextPosItem = list.get(currentPos + 1);
        return (currentPosItem instanceof DeliveryPointWithInvoices)
            && (nextPosItem instanceof DeliveryPointWithInvoices);
    }

    @Override
    public int getSecondLevelHeaderPositionForItem(Integer itemPosition) {
        int pos = RecyclerView.NO_POSITION;
        int i = itemPosition;
        boolean isSecondLevelHeaderFind = false;
        while (i >= 0 && !isSecondLevelHeaderFind) {
            isSecondLevelHeaderFind = getItem(i) instanceof RoutePointsInfo;
            if (isSecondLevelHeaderFind) pos = i;
            i--;
        }
        return pos;
    }

    @Override
    public int getSecondLevelHeaderLayout(Integer headerPosition) {
        return R.layout.inner_group;
    }

    @Override
    public void bindSecondLevelHeaderWithData(View header, Integer headerPosition) {
        if (getCurrentList().size() > headerPosition && getItem(headerPosition) instanceof RoutePointsInfo)  {
            RoutePointsInfo routePointsInfo = (RoutePointsInfo) getItem(headerPosition);
            TextView description = header.findViewById(R.id.innerGroupDescription);
            TextView pdz = header.findViewById(R.id.innerGroupPdz);
            TextView odz = header.findViewById(R.id.innerGroupOdz);
            description.setText(routePointsInfo.getDescription());
            pdz.setText(routePointsInfo.getOverDueReceivableTotal());
            odz.setText(routePointsInfo.getReceivableTotal());
        }
    }

    @Override
    public int getFirstLevelHeaderHeight(ViewGroup parent) {
        if (firstLevelHeaderHeight == 0) {
            View childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.outter_group, parent, false);
            int parentWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
            int parentHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);
            int childWidthSpec = ViewGroup.getChildMeasureSpec(
                parentWidthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(),
                childView.getLayoutParams().width
            );
            int childHeightSpec =
                ViewGroup.getChildMeasureSpec(
                    parentHeightSpec,
                    parent.getPaddingTop() + parent.getPaddingBottom(),
                    childView.getLayoutParams().height
                );
            childView.measure(childWidthSpec, childHeightSpec);
            childView.layout(
                0,
                0,
                childView.getMeasuredWidth(),
                childView.getMeasuredHeight()
            );
            firstLevelHeaderHeight = childView.getBottom();
        }
        return firstLevelHeaderHeight;
    }

    @Override
    public boolean isSecondLevelHeader(Integer childAtContactPosition) {
        return getItem(childAtContactPosition) instanceof RoutePointsInfo;
    }

    @Override
    public boolean isFirstLevelHeader(Integer childAtContactPosition) {
        return getItem(childAtContactPosition) instanceof Debtor;
    }

    @Override
    public boolean isNeedHideSecondLevelGroupStickyHeader(Integer itemPosition) {
        if (itemPosition + 1 >= getCurrentList().size()) return true;
        return (getItem(itemPosition) instanceof Debtor || getItem(itemPosition) instanceof RoutePointsInfo)
            && getItem(itemPosition + 1) instanceof Debtor;
    }

    @Override
    public boolean isLastChildOfSecondGroup(Integer itemPosition) {
        return getItem(itemPosition) instanceof DeliveryPointWithInvoices &&
            getItem(itemPosition + 1) instanceof Debtor;
    }

    @Override
    public void secondLevelStickyHeaderWasClicked(Integer itemPosition) {
        if (getItem(itemPosition) instanceof RoutePointsInfo) {
            RoutePointsInfo routePointsInfo = (RoutePointsInfo) getItem(itemPosition);
            if (mSelectedGroupCallback != null) mSelectedGroupCallback.innerGroupWasClicked(
                routePointsInfo.getDebtorId(),
                routePointsInfo.getId(),
                !routePointsInfo.isOpen()
            );
        }
    }

    private static final class OuterGroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView id;
        private final TextView title;
        private final TextView overDueReceivable;
        private final TextView commonReceivable;
        final ViewGroup root;

        public OuterGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.groupRoot);
            id = itemView.findViewById(R.id.groupId);
            title = itemView.findViewById(R.id.groupTitle);
            overDueReceivable = itemView.findViewById(R.id.overdueReceivable);
            commonReceivable = itemView.findViewById(R.id.commonReceivable);
        }

        public void bind(Debtor debtor, SelectedGroupCallback callback) {
            root.setOnClickListener(v -> callback.outerGroupWasClicked(debtor.getId(), !debtor.isOpen()));
            id.setText(String.valueOf(debtor.getId()));
            title.setText(debtor.getTitle());
            overDueReceivable.setText(debtor.getOverDueReceivable());
            commonReceivable.setText(debtor.getCommonReceivable());
        }
    }

    private static final class InnerGroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView innerGroupDescription;
        private final TextView innerGroupOdz;
        private final TextView innerGroupPdz;
        private final ViewGroup root;

        public InnerGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.routesInfoRoot);
            innerGroupDescription = itemView.findViewById(R.id.innerGroupDescription);
            innerGroupOdz = itemView.findViewById(R.id.innerGroupOdz);
            innerGroupPdz = itemView.findViewById(R.id.innerGroupPdz);
        }

        public void bind(RoutePointsInfo routePointsInfo, SelectedGroupCallback callback) {
            root.setOnClickListener(v -> callback.innerGroupWasClicked(routePointsInfo.getDebtorId(), routePointsInfo.getId(), !routePointsInfo.isOpen()));
            innerGroupDescription.setText(routePointsInfo.getDescription());
            innerGroupOdz.setText(routePointsInfo.getReceivableTotal());
            innerGroupPdz.setText(routePointsInfo.getOverDueReceivableTotal());
        }
    }

    public static final class InnerGroupChildViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView invoiceList;
        private InvoiceAdapter invoiceAdapter;

        public InnerGroupChildViewHolder(@NonNull View itemView) {
            super(itemView);
            invoiceList = itemView.findViewById(R.id.invoiceList);
        }

        public void bind(DeliveryPointWithInvoices deliveryPointWithInvoices, boolean isSelected) {
            if (invoiceAdapter == null) invoiceAdapter = new InvoiceAdapter(deliveryPointWithInvoices.getInvoices());
            InvoiceItemDecoration invoiceItemDecoration = new InvoiceItemDecoration(
                AppCompatResources.getDrawable(itemView.getContext(), R.drawable.invoice_divider),
                invoiceAdapter
            );
            invoiceList.setBackgroundColor(ContextCompat.getColor(invoiceList.getContext(), isSelected ? R.color.purple_200 : R.color.white));
            invoiceList.addItemDecoration(invoiceItemDecoration);
            invoiceList.setAdapter(invoiceAdapter);
        }

        void setPositionAsUnselected() {
            invoiceList.setBackgroundColor(ContextCompat.getColor(invoiceList.getContext(), R.color.white));
        }

        void setPositionAsSelected() {
            invoiceList.setBackgroundColor(ContextCompat.getColor(invoiceList.getContext(), R.color.purple_200));
        }
    }

}

class MyDiffUtil extends DiffUtil.ItemCallback<ReceivableInfo> {

    @Override
    public boolean areItemsTheSame(@NonNull ReceivableInfo oldItem, @NonNull ReceivableInfo newItem) {
        boolean areItemsSameType =
            (oldItem instanceof Debtor && newItem instanceof Debtor)
            || (oldItem instanceof RoutePointsInfo && newItem instanceof RoutePointsInfo)
            || (oldItem instanceof DeliveryPointWithInvoices && newItem instanceof DeliveryPointWithInvoices);
        if (areItemsSameType) {
            if (oldItem instanceof Debtor) {
                return oldItem.getId().equals(newItem.getId());
            } else if (oldItem instanceof RoutePointsInfo) {
                boolean isSameOuterGroup = ((RoutePointsInfo)oldItem).getDebtorId().equals(((RoutePointsInfo)newItem).getDebtorId());
                if (!isSameOuterGroup) return false;
                return oldItem.getId().equals(newItem.getId());
            } else {
                boolean isSameOuterGroup = ((DeliveryPointWithInvoices)oldItem).getDebtorId().equals(((DeliveryPointWithInvoices)newItem).getDebtorId());
                boolean isSameInnerGroup = ((DeliveryPointWithInvoices)oldItem).getRouteId().equals(((DeliveryPointWithInvoices)newItem).getRouteId());
                boolean areItemsTheSame = oldItem.getId().equals(newItem.getId());
                return isSameOuterGroup && isSameInnerGroup && areItemsTheSame;
            }
        } else return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull ReceivableInfo oldItem, @NonNull ReceivableInfo newItem) {
        boolean areItemsSameType =
            (oldItem instanceof Debtor && newItem instanceof Debtor)
                || (oldItem instanceof RoutePointsInfo && newItem instanceof RoutePointsInfo)
                || (oldItem instanceof DeliveryPointWithInvoices && newItem instanceof DeliveryPointWithInvoices);
        if (areItemsSameType) {
            if (oldItem instanceof Debtor) {
                return oldItem.getId().equals(newItem.getId()) && (((Debtor) oldItem).isOpen() == ((Debtor) newItem).isOpen());
            } else if (oldItem instanceof RoutePointsInfo) {
                boolean isSameOuterGroup = ((RoutePointsInfo)oldItem).getDebtorId().equals(((RoutePointsInfo)newItem).getDebtorId());
                boolean isSameInnerGroup = oldItem.getId().equals((newItem).getId());
                boolean openEquality = ((RoutePointsInfo) oldItem).isOpen() == ((RoutePointsInfo) newItem).isOpen();
                return isSameOuterGroup && isSameInnerGroup && openEquality;
            } else {
                boolean isSameOuterGroup = ((DeliveryPointWithInvoices)oldItem).getDebtorId().equals(((DeliveryPointWithInvoices)newItem).getDebtorId());
                boolean isSameInnerGroup = ((DeliveryPointWithInvoices)oldItem).getRouteId().equals(((DeliveryPointWithInvoices)newItem).getRouteId());
                boolean areItemsTheSame = (oldItem).getId().equals((newItem).getId());
                return isSameOuterGroup && isSameInnerGroup && areItemsTheSame;
            }
        } else return false;
    }
}
