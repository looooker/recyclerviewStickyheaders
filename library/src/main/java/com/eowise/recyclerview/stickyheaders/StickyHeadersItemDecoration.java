package com.eowise.recyclerview.stickyheaders;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewHelper;
import android.util.Log;
import android.view.View;

/**
 * Created by aurel on 22/09/14.
 */
public class StickyHeadersItemDecoration extends RecyclerView.ItemDecoration
{

    private final HeaderStore headerStore;
    private final AdapterDataObserver adapterDataObserver;
    private boolean overlay;
    private DrawOrder drawOrder;

    public StickyHeadersItemDecoration(HeaderStore headerStore)
    {
        this(headerStore, false);
    }

    public StickyHeadersItemDecoration(HeaderStore headerStore, boolean overlay)
    {
        this(headerStore, overlay, DrawOrder.OverItems);
    }

    public StickyHeadersItemDecoration(HeaderStore headerStore, boolean overlay, DrawOrder drawOrder)
    {
        this.overlay = overlay;
        this.drawOrder = drawOrder;
        this.headerStore = headerStore;
        this.adapterDataObserver = new AdapterDataObserver();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        if (drawOrder == DrawOrder.UnderItems)
        {
            drawHeaders(c, parent, state);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        if (drawOrder == DrawOrder.OverItems)
        {
            drawHeaders(c, parent, state);
        }
    }


    private void drawHeaders(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        final int childCount = parent.getChildCount();
        final RecyclerView.LayoutManager lm = parent.getLayoutManager();
        Float lastY = null;
        Float lastX = null;
        for (int i = childCount - 1; i >= 0; i--)
        {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(child);
            int itemPosition = RecyclerViewHelper.convertPreLayoutPositionToPostLayout(parent, holder.getPosition());

            if (!lp.isItemRemoved() && !lp.isViewInvalid())
            {

                float translationY = ViewCompat.getTranslationY(child);
                float translationX = ViewCompat.getTranslationX(child);

                if ((i == 0 && headerStore.isSticky()) || headerStore.isHeader(holder))
                {

                    View header = headerStore.getHeaderViewByItem(holder);

                    if (header.getVisibility() == View.VISIBLE)
                    {


                        int headerHeight = headerStore.getHeaderHeight(holder);
                        int headerWidth = headerStore.getHeaderWidth(holder);
                        float y = getHeaderY(child, lm) + translationY;
                        float x = getHeaderX(child, lm) + translationX;

                        View topView = headerStore.getTopView();

                        Log.i("TAG", itemPosition + "位置" + getHeaderX(child, lm) + "X" + x + "lastX" + lastX);

                        if (headerStore.isSticky() && lastY != null && lastY < y + headerHeight)
                        {
                            y = lastY - headerHeight;
                        }
                        if (headerStore.isSticky() && lastX != null && lastX < x + headerWidth)
                        {
                            x = lastX - headerWidth;
                        }

                        c.save();


                        int orientation = 1;

                        if (lm instanceof LinearLayoutManager)
                        {
                            orientation = ((LinearLayoutManager) lm).getOrientation();
                        }

                        if (orientation == LinearLayoutManager.HORIZONTAL)
                        {
                            if (itemPosition == 0 && lastX != null)
                            {

                                int distance = child.getLeft() - (headerStore.getTopWidth() + headerWidth);
                                if (-distance < headerStore.getTopWidth())
                                {
                                    c.translate(distance, 0);
                                    if (topView != null)
                                    {
                                        topView.draw(c);
                                    }
                                    c.translate(headerStore.getTopWidth(), 0);
                                }
                            }
                            c.translate(x, 0);
                        }
                        else
                        {
                            if (itemPosition == 0 && lastY != null)
                            {

                                int distance = child.getTop() - (headerStore.getTopHeight() + headerHeight);
                                if (-distance < headerStore.getTopHeight())
                                {
                                    c.translate(0, distance);
                                    if (topView != null)
                                    {
                                        topView.draw(c);
                                    }
                                    c.translate(0, headerStore.getTopHeight());
                                }
                            }
                            c.translate(0, y);
                        }
                        header.draw(c);
                        c.restore();

                        lastY = y;
                        lastX = x;
                    }
                }
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {

        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
        RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);

        boolean isHeader = lp.isItemRemoved() ? headerStore.wasHeader(holder) : headerStore.isHeader(holder);
        final RecyclerView.LayoutManager lm = parent.getLayoutManager();
        int itemPosition = RecyclerViewHelper.convertPreLayoutPositionToPostLayout(parent, holder.getPosition());


        if (overlay || !isHeader)
        {
            outRect.set(0, 0, 0, 0);
        }
        else
        {

            int orientation = 1;
            if (lm instanceof LinearLayoutManager)
            {
                orientation = ((LinearLayoutManager) lm).getOrientation();
            }
            if (orientation == LinearLayoutManager.HORIZONTAL)
            {
                outRect.set(headerStore.getHeaderWidth(holder) + (itemPosition == 0 ? headerStore.getTopWidth() : 0), 0, 0, 0);
            }
            else
            {
                outRect.set(0, headerStore.getHeaderHeight(holder) + (itemPosition == 0 ? headerStore.getTopHeight() : 0), 0, 0);
            }
            //TODO: Handle layout direction
        }
    }

    public void registerAdapterDataObserver(RecyclerView.Adapter adapter)
    {
        adapter.registerAdapterDataObserver(adapterDataObserver);
    }

    private float getHeaderY(View item, RecyclerView.LayoutManager lm)
    {
        return headerStore.isSticky() && lm.getDecoratedTop(item) < 0 ? 0 : lm.getDecoratedTop(item);
    }

    private float getHeaderX(View item, RecyclerView.LayoutManager lm)
    {
        return headerStore.isSticky() && lm.getDecoratedLeft(item) < 0 ? 0 : lm.getDecoratedLeft(item);
    }


    private class AdapterDataObserver extends RecyclerView.AdapterDataObserver
    {

        public AdapterDataObserver()
        {
        }

        @Override
        public void onChanged()
        {
            headerStore.clear();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            headerStore.onItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            headerStore.onItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount)
        {
            headerStore.onItemRangeMoved(fromPosition, toPosition, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount)
        {
            headerStore.onItemRangeChanged(positionStart, itemCount);
        }
    }

}
