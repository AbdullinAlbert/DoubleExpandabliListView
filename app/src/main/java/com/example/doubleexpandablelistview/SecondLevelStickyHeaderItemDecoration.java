package com.example.doubleexpandablelistview;

import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SecondLevelStickyHeaderItemDecoration extends RecyclerView.ItemDecoration {
    private final SecondLevelStickyHeaderInfoProvider mSecondLevelStickyHeaderInfoProvider;
    private int mCurrentSecondLevelHeaderIndex = -1;
    private View mCurrentHeader;

    public static final String TAG = SecondLevelStickyHeaderItemDecoration.class.getSimpleName();

    public SecondLevelStickyHeaderItemDecoration(SecondLevelStickyHeaderInfoProvider secondLevelStickyHeaderInfoProvider) {
        mSecondLevelStickyHeaderInfoProvider = secondLevelStickyHeaderInfoProvider;
    }


    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int firstLevelStickyHeaderHeight = mSecondLevelStickyHeaderInfoProvider.getFirstLevelHeaderHeight(parent);
        int necessaryPosition = getNecessaryPosition(parent, firstLevelStickyHeaderHeight);
        if (necessaryPosition == RecyclerView.NO_POSITION) return;
        Log.d(TAG, "onDrawOver: necessaryPosition = " + necessaryPosition);
        View necessaryChild = parent.getChildAt(necessaryPosition);
        if (necessaryChild == null) return;
        int secondChildAdapterPosition = parent.getChildAdapterPosition(necessaryChild);
        if (secondChildAdapterPosition == RecyclerView.NO_POSITION) return;
        View currentSecondLevelHeader = getHeaderViewForItem(secondChildAdapterPosition, parent);
        if (currentSecondLevelHeader == null) {
            Log.d(TAG, "onDrawOver: currentSecondLevelHeader is null");
            return;
        }
        fixLayoutSize(parent, currentSecondLevelHeader, firstLevelStickyHeaderHeight);
        int contactPosition = currentSecondLevelHeader.getBottom();
        View childInContact = getChildInContact(parent, contactPosition);
        if (childInContact == null) return;
        int childAdapterPosition = parent.getChildAdapterPosition(childInContact);
        if (childAdapterPosition == RecyclerView.NO_POSITION) return;
        if (mSecondLevelStickyHeaderInfoProvider.isSecondLevelHeader(childAdapterPosition) ||
            mSecondLevelStickyHeaderInfoProvider.isFirstLevelHeader(childAdapterPosition)) {
            moveHeader(c, currentSecondLevelHeader, childInContact, firstLevelStickyHeaderHeight);
            return;
        }
        drawHeader(c, currentSecondLevelHeader, firstLevelStickyHeaderHeight);
    }

    private int getNecessaryPosition(RecyclerView parent, int firstLevelStickyHeaderHeight) {
        View child = parent.getChildAt(0);
        int childPos = parent.getChildAdapterPosition(child);
        if (childPos == RecyclerView.NO_POSITION) return RecyclerView.NO_POSITION;
        if (mSecondLevelStickyHeaderInfoProvider.isNeedHideSecondLevelGroupStickyHeader(childPos)) return RecyclerView.NO_POSITION;
        if (mSecondLevelStickyHeaderInfoProvider.isLastChildOfSecondGroup(childPos)
            && child.getBottom() < firstLevelStickyHeaderHeight) return RecyclerView.NO_POSITION;
        if (mSecondLevelStickyHeaderInfoProvider.isFirstLevelHeader(childPos)) return  1;
        else if (mSecondLevelStickyHeaderInfoProvider.isSecondLevelHeader( childPos+ 1)) {
            View childAtFirstPosition = parent.getChildAt(1);
            return (childAtFirstPosition.getTop() <= firstLevelStickyHeaderHeight) ? 1 : 0;
        } else return 0;
    }

    private void drawHeader(Canvas c, View header, int dy) {
        c.save();
        c.translate(0, dy);
        header.draw(c);
        c.restore();
    }

    private void moveHeader(Canvas c, View currentHeader, View nextOrPreviousHeader, int topForClip) {
        c.save();
        int top = nextOrPreviousHeader.getTop();
        int height = currentHeader.getHeight();
        int dy = top - height;
        c.clipRect(0, topForClip, currentHeader.getRight(), topForClip + currentHeader.getHeight());
        c.translate(0, dy);
        currentHeader.draw(c);
        c.restore();
    }

    private View getChildInContact(ViewGroup parent, Integer contactPoint) {
        View childInContact = null;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child == null) continue;
            if (child.getBottom() > contactPoint) {
                if (child.getTop() <= contactPoint) {
                    childInContact = child;
                    break;
                }
            }
        }
        return childInContact;
    }

    private void fixLayoutSize(ViewGroup parent, View childView, int top) {
        Log.d(TAG, "fixLayoutSize: top = " + top);
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
                childView.getLayoutParams().height);
        childView.measure(childWidthSpec, childHeightSpec);
        childView.layout(
            0,
            top,
            childView.getMeasuredWidth(),
            top + childView.getMeasuredHeight()
        );
    }

    private View getHeaderViewForItem(int itemPosition, RecyclerView parent) {
        int headerPosition = mSecondLevelStickyHeaderInfoProvider.getSecondLevelHeaderPositionForItem(itemPosition);
        if (headerPosition == -1) return null;
        if (mCurrentSecondLevelHeaderIndex != headerPosition || mCurrentHeader == null) {
            Log.d(TAG, "getHeaderViewForItem: mCurrentSecondLevelHeaderIndex =" + headerPosition);
            int layoutResId = mSecondLevelStickyHeaderInfoProvider.getSecondLevelHeaderLayout(headerPosition);
            mCurrentHeader = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
            mSecondLevelStickyHeaderInfoProvider.bindSecondLevelHeaderWithData(mCurrentHeader, headerPosition);
            mCurrentSecondLevelHeaderIndex = headerPosition;
        }
        return mCurrentHeader;
    }

    public interface SecondLevelStickyHeaderInfoProvider {
        int getSecondLevelHeaderPositionForItem(Integer itemPosition);
        int getSecondLevelHeaderLayout(Integer headerPosition);
        void bindSecondLevelHeaderWithData(View header, Integer headerPosition);
        int getFirstLevelHeaderHeight(ViewGroup viewGroup);
        boolean isSecondLevelHeader(Integer childAtContactPosition);
        boolean isFirstLevelHeader(Integer childAtContactPosition);
        boolean isNeedHideSecondLevelGroupStickyHeader(Integer itemPosition);
        boolean isLastChildOfSecondGroup(Integer itemPosition);
    }
}
