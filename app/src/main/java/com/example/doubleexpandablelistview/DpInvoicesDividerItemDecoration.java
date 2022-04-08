package com.example.doubleexpandablelistview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DpInvoicesDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final ColorItemDividerHelper colorItemDividerHelper;
    private final Drawable invoiceDividerDrawable;

    public DpInvoicesDividerItemDecoration(
        ColorItemDividerHelper colorItemDividerHelper,
        Drawable invoiceDividerDrawable) {
        this.colorItemDividerHelper = colorItemDividerHelper;
        this.invoiceDividerDrawable = invoiceDividerDrawable;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        c.save();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view == null) return;
            int adapterPosition = parent.getChildAdapterPosition(view);
            if (adapterPosition == RecyclerView.NO_POSITION) return;
            if (colorItemDividerHelper.isNeedAddColorDivider(adapterPosition)) {
                final int right = parent.getWidth();
                final int left = 0;
                final int top = view.getBottom();
                final int bottom = top + invoiceDividerDrawable.getIntrinsicHeight();
                invoiceDividerDrawable.setBounds(new Rect(left, top, right, bottom));
                invoiceDividerDrawable.draw(c);
            }
        }
        c.restore();
    }

    public interface ColorItemDividerHelper {
        boolean isNeedAddColorDivider(int currentPos);
    }
}
