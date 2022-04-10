package com.example.doubleexpandablelistview;

import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SecondLevelStickyHeaderItemDecoration extends RecyclerView.ItemDecoration {
    private final SecondLevelStickyHeaderInfoProvider mSecondLevelStickyHeaderInfoProvider;
    private int mCurrentSecondLevelHeaderIndex = -1;
    private View mCurrentHeader;
    private int mSecondLevelStickyHeaderTop = 0;
    private int mSecondLevelStickyHeaderBottom = 0;

    public static final String TAG = SecondLevelStickyHeaderItemDecoration.class.getSimpleName();

    public SecondLevelStickyHeaderItemDecoration(
        RecyclerView recyclerView,
        SecondLevelStickyHeaderInfoProvider secondLevelStickyHeaderInfoProvider
    ) {
        mSecondLevelStickyHeaderInfoProvider = secondLevelStickyHeaderInfoProvider;
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (mSecondLevelStickyHeaderTop <= e.getY() && e.getY() <= mSecondLevelStickyHeaderBottom) {
                    View clickedView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (clickedView != null) {
                        int viewPosition = rv.getChildAdapterPosition(clickedView);
                        if (viewPosition == RecyclerView.NO_POSITION) return false;
                        int headerPosition = mSecondLevelStickyHeaderInfoProvider.getSecondLevelHeaderPositionForItem(viewPosition);
                        if (headerPosition == RecyclerView.NO_POSITION) return false;
                        mSecondLevelStickyHeaderInfoProvider.secondLevelStickyHeaderWasClicked(headerPosition);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }


    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int firstLevelStickyHeaderHeight = mSecondLevelStickyHeaderInfoProvider.getFirstLevelHeaderHeight(parent);
        int necessaryPosition = getNecessaryPosition(parent, firstLevelStickyHeaderHeight);
        if (necessaryPosition == RecyclerView.NO_POSITION) return;
        View necessaryChild = parent.getChildAt(necessaryPosition);
        if (necessaryChild == null) return;
        int secondChildAdapterPosition = parent.getChildAdapterPosition(necessaryChild);
        if (secondChildAdapterPosition == RecyclerView.NO_POSITION) return;
        View currentSecondLevelHeader = getHeaderViewForItem(secondChildAdapterPosition, parent);
        if (currentSecondLevelHeader == null) return;
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
            parent.getLeft(),
            mSecondLevelStickyHeaderTop = top,
            childView.getMeasuredWidth(),
            mSecondLevelStickyHeaderBottom = top + childView.getMeasuredHeight()
        );
    }

    private View getHeaderViewForItem(int itemPosition, RecyclerView parent) {
        int headerPosition = mSecondLevelStickyHeaderInfoProvider.getSecondLevelHeaderPositionForItem(itemPosition);
        if (headerPosition == RecyclerView.NO_POSITION) return null;
        if (mCurrentSecondLevelHeaderIndex != headerPosition || mCurrentHeader == null) {
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
        void secondLevelStickyHeaderWasClicked(Integer itemPosition);
    }
}
