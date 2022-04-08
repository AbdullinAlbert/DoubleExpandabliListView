package com.example.doubleexpandablelistview;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirstLevelStickyHeaderItemDecoration extends RecyclerView.ItemDecoration {
    private final StickyHeaderInfoProvider stickyHeaderInfoProvider;
    private int mStickyHeaderHeight;
    private View mCurrentHeader;
    private int mCurrentHeaderIndex = -1;

    public static final String TAG = FirstLevelStickyHeaderItemDecoration.class.getSimpleName();

    public FirstLevelStickyHeaderItemDecoration(RecyclerView recyclerView, @NonNull StickyHeaderInfoProvider stickHeaderHelper) {
       this.stickyHeaderInfoProvider = stickHeaderHelper;
       recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
           @Override
           public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
               if (e.getY() <= mStickyHeaderHeight) {
                   View clickedView = rv.findChildViewUnder(e.getX(), e.getY());
                   if (clickedView != null) {
                       int viewPosition = rv.getChildAdapterPosition(clickedView);
                       if (viewPosition == RecyclerView.NO_POSITION) return false;
                       int groupPosition = stickyHeaderInfoProvider.getHeaderPositionForItem(viewPosition);
                       stickyHeaderInfoProvider.headerWasClicked(groupPosition);
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
        View topChildOfRV = parent.getChildAt(0);
        if (topChildOfRV == null) return;
        int topChildPosition = parent.getChildAdapterPosition(topChildOfRV);
        if (topChildPosition == RecyclerView.NO_POSITION) return;
        View currentHeader = getHeaderViewForItem(topChildPosition, parent);
        fixLayoutSize(parent, currentHeader);
        int contactPoint = currentHeader.getBottom();
        View childInContact = getChildInContact(parent, contactPoint);
        if (childInContact == null) return;
        int childPos = parent.getChildAdapterPosition(childInContact);
        if (childPos == RecyclerView.NO_POSITION) return;
        if (stickyHeaderInfoProvider.isHeader(childPos)) {
            moveHeader(c, currentHeader, childInContact);
            return;
        }
        drawHeader(c, currentHeader);
    }

    private void drawHeader(Canvas c, View header) {
        c.save();
        c.translate(0, 0);
        header.draw(c);
        c.restore();
    }

    private void moveHeader(Canvas c, View currentHeader, View nextOrPreviousHeader) {
        c.save();
        int top = nextOrPreviousHeader.getTop();
        int height = currentHeader.getHeight();
        int dy = top - height;
        c.translate(0, dy);
        currentHeader.draw(c);
        c.restore();
    }

    private View getChildInContact(RecyclerView parent, int contactPoint) {
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

    private View getHeaderViewForItem(int itemPosition, RecyclerView parent) {
        int headerPosition = stickyHeaderInfoProvider.getHeaderPositionForItem(itemPosition);
        if(headerPosition != mCurrentHeaderIndex || mCurrentHeader == null) {
            int layoutResId = stickyHeaderInfoProvider.getHeaderLayout(headerPosition);
            View header = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
            stickyHeaderInfoProvider.bindHeaderData(header, headerPosition);
            mCurrentHeader = header;
            mCurrentHeaderIndex = headerPosition;
        }
        return mCurrentHeader;
    }

    private void fixLayoutSize(ViewGroup parent, View view) {
        int parentWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int parentHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(parentWidthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(parentHeightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);
        view.measure(childWidthSpec, childHeightSpec);
        view.layout(0, 0, view.getMeasuredWidth(),mStickyHeaderHeight = view.getMeasuredHeight());
    }

    public interface StickyHeaderInfoProvider {

        /**
         * This method gets called by {@link FirstLevelStickyHeaderItemDecoration} to fetch the position of the header item in the adapter
         * that is used for (represents) item at specified position.
         * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
         * @return int. Position of the header item in the adapter.
         */
        int getHeaderPositionForItem(int itemPosition);

        /**
         * This method gets called by {@link FirstLevelStickyHeaderItemDecoration} to get layout resource id for the header item at specified adapter's position.
         * @param headerPosition int. Position of the header item in the adapter.
         * @return int. Layout resource id.
         */
        int getHeaderLayout(int headerPosition);

        /**
         * This method gets called by {@link FirstLevelStickyHeaderItemDecoration} to setup the header View.
         * @param header View. Header to set the data on.
         * @param headerPosition int. Position of the header item in the adapter.
         */
        void bindHeaderData(View header, int headerPosition);

        /**
         * This method gets called by {@link FirstLevelStickyHeaderItemDecoration} to verify whether the item represents a header.
         * @param itemPosition int.
         * @return true, if item at the specified adapter's position represents a header.
         */
        boolean isHeader(int itemPosition);

        void headerWasClicked(Integer groupPosition);

    }
}
