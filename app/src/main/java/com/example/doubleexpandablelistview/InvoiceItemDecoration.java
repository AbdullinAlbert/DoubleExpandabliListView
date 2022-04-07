package com.example.doubleexpandablelistview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InvoiceItemDecoration extends RecyclerView.ItemDecoration {
    private final Drawable invoiceDivider;
    private final InvoiceDividerItemHelper invoiceDividerItemHelper;

    public InvoiceItemDecoration(Drawable invoiceDivider, InvoiceDividerItemHelper invoiceDividerItemHelper) {
        this.invoiceDivider = invoiceDivider;
        this.invoiceDividerItemHelper = invoiceDividerItemHelper;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        c.save();
        int left;
        int right = parent.getRight();
        int top;
        int bottom;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view == null) return;
            int adapterPosition = parent.getChildAdapterPosition(view);
            if (adapterPosition == RecyclerView.NO_POSITION) return;
            if (adapterPosition == 0) {
                left = parent.getLeft();
                top = view.getBottom();
                bottom = top + invoiceDivider.getIntrinsicHeight();
                invoiceDivider.setBounds(new Rect(left, top, right, bottom));
                invoiceDivider.draw(c);
            } else if (!invoiceDividerItemHelper.isLast(adapterPosition)) {
                left = view.getPaddingLeft();
                top = view.getBottom();
                bottom = top + invoiceDivider.getIntrinsicHeight();
                invoiceDivider.setBounds(new Rect(left, top, right, bottom));
                invoiceDivider.draw(c);
            }
        }
        c.restore();
    }

    public interface InvoiceDividerItemHelper {
        boolean isLast(Integer position);
    }
}
