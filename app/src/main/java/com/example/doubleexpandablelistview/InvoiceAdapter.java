package com.example.doubleexpandablelistview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements InvoiceItemDecoration.InvoiceDividerItemHelper {
    private final List<InvoiceInfo> invoiceList;
    private final int INVOICE_OWNER_VIEW_TYPE = 0;
    private final int INVOICE_DESCRIPTION_VIEW_TYPE = 1;

    public InvoiceAdapter(List<InvoiceInfo> invoiceList) {
        this.invoiceList = invoiceList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return viewType == INVOICE_OWNER_VIEW_TYPE
            ?   new InvoiceOwnerViewHolder(layoutInflater.inflate(R.layout.group_child_invoice_owner_info, parent, false))
            :   new InvoiceViewHolder(layoutInflater.inflate(R.layout.group_child_invoice_description, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == INVOICE_OWNER_VIEW_TYPE)
            ((InvoiceOwnerViewHolder)holder).bind((InvoiceOwnerInfo) invoiceList.get(position));
        else
            ((InvoiceViewHolder)holder).bind(invoiceList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return invoiceList.get(position) instanceof InvoiceOwnerInfo
            ? INVOICE_OWNER_VIEW_TYPE
            : INVOICE_DESCRIPTION_VIEW_TYPE;
    }

    @Override
    public boolean isLast(Integer position) {
        return invoiceList.size() - 1 == position;
    }

    private final static class InvoiceOwnerViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView address;

        public InvoiceOwnerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.groupChildInvoiceOwnerDescription);
            address = itemView.findViewById(R.id.groupChildInvoiceOwnerAddress);
        }

        public void bind(InvoiceOwnerInfo invoiceOwnerInfo) {
            name.setText(invoiceOwnerInfo.getDescription());
            address.setText(invoiceOwnerInfo.getAddress());
        }
    }

    private final static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        private final TextView invoiceDescription;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            invoiceDescription = itemView.findViewById(R.id.invoiceDescription);
        }

        public void bind(InvoiceInfo invoice) {
            invoiceDescription.setText(invoice.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }
}
