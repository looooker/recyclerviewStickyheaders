package com.eowise.recyclerview.stickyheaders.samples.data;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeaderRecyclerView extends NLRecyclerView
{
    private List<FixedHolder> mHeaders;

    private List<FixedHolder> mFooters;

    private Adapter mBaseAdapter;

    public HeaderRecyclerView(Context context)
    {
        super(context);
    }

    public HeaderRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HeaderRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter)
    {

        mBaseAdapter = adapter;

        if ((mHeaders != null && !mHeaders.isEmpty()) || (mFooters != null && !mFooters.isEmpty()))
        {
            adapter = new HeaderAdapter(adapter, mHeaders, mFooters);
        }

        super.setAdapter(adapter);
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    // Basic
    // -------------------------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    public View addHeader(int layout)
    {
        return addHeader(LayoutInflater.from(getContext()), layout);
    }

    public View addHeader(LayoutInflater inflater, int layout)
    {
        return addHeader(inflater.inflate(layout, this, false));
    }

    public View addHeader(View view)
    {
        if (mHeaders == null)
        {
            mHeaders = new ArrayList<>(2);
        }

        mHeaders.add(new FixedHolder(view));

        final Adapter adapter = getAdapter();

        if (adapter != null)
        {
            setAdapter(mBaseAdapter);
        }

        return view;
    }

    @SuppressWarnings("unused")
    public boolean removeHeader(View view)
    {
        if (mHeaders != null)
        {
            for (int i = 0, size = mHeaders.size(); i < size; i++)
            {
                final FixedHolder holder = mHeaders.get(i);

                if (holder.itemView == view)
                {
                    mHeaders.remove(i);

                    setAdapter(mBaseAdapter);

                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unused")
    public View addFooter(int layout)
    {
        return addFooter(LayoutInflater.from(getContext()), layout);
    }

    public View addFooter(LayoutInflater inflater, int layout)
    {
        return addFooter(inflater.inflate(layout, this, false));
    }

    public void showFooter(int layoutID)
    {
        if (mFooters == null)
        {
            mFooters = new ArrayList<>(2);
        }
        if (mFooters.size() == 0)
        {
            addFooter(layoutID);
        }

    }
    public void showFooter(View footerView)
    {
        if (mFooters == null)
        {
            mFooters = new ArrayList<>(2);
        }
        if (mFooters.size() == 0)
        {
            addFooter(footerView);
        }

    }

    public View addFooter(View view)
    {
        if (mFooters == null)
        {
            mFooters = new ArrayList<>(2);
        }

        mFooters.add(new FixedHolder(view));

        final Adapter adapter = getAdapter();

        if (adapter != null)
        {
            setAdapter(mBaseAdapter);
        }

        return view;
    }

    @SuppressWarnings("unused")
    public boolean removeFooter(View view)
    {
        if (mFooters != null)
        {
            for (int i = 0, size = mFooters.size(); i < size; i++)
            {
                final FixedHolder holder = mFooters.get(i);

                if (holder.itemView == view)
                {
                    mFooters.remove(i);

                    setAdapter(mBaseAdapter);

                    return true;
                }
            }
        }

        return false;
    }

    public boolean removeFooter()
    {
        if (mFooters != null)
        {
            for (int i = 0, size = mFooters.size(); i < size; i++)
            {
                final FixedHolder holder = mFooters.get(i);

                mFooters.remove(i);
            }

            setAdapter(mBaseAdapter);

            return true;
        }

        return false;
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    // Header Adapter
    // -------------------------------------------------------------------------------------------------------------------------------------

    public static class HeaderAdapter extends Adapter
    {
        private static final List<FixedHolder> EMPTY = Collections.emptyList();

        private static final int VIEW_TYPE_HEADERS_START = Integer.MIN_VALUE;

        private final int mFootersViewTypeStart;

        private final int mAdapterViewTypeStart;

        private final int mAdapterViewTypeOffset;

        private final Adapter mAdapter;

        private final List<FixedHolder> mHeaders;

        private final List<FixedHolder> mFooters;

        public HeaderAdapter(Adapter adapter, List<FixedHolder> headers, List<FixedHolder> footers)
        {
            mAdapter = adapter;

            mHeaders = headers != null ? headers : EMPTY;

            mFooters = footers != null ? footers : EMPTY;

            // compute.

            final int headersCount = getHeadersCount();

            final int footersCount = getFootersCount();

            mFootersViewTypeStart = VIEW_TYPE_HEADERS_START + headersCount;

            mAdapterViewTypeStart = VIEW_TYPE_HEADERS_START + headersCount + footersCount;

            mAdapterViewTypeOffset = headersCount + footersCount;
        }

        public int getHeadersCount()
        {
            return mHeaders.size();
        }

        public int getFootersCount()
        {
            return mFooters.size();
        }

        @Override
        public int getItemCount()
        {
            if (mAdapter != null)
            {
                return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
            }
            else
            {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position)
        {
            final int headersCount = getHeadersCount();

            if (position < headersCount)
            {
                return position + VIEW_TYPE_HEADERS_START;
            }

            final int adjustedPosition = position - headersCount;

            int adapterCount = 0;

            if (mAdapter != null)
            {
                adapterCount = mAdapter.getItemCount();

                if (adjustedPosition < adapterCount)
                {
                    return mAdapter.getItemViewType(adjustedPosition) + mAdapterViewTypeOffset;
                }
            }

            return adjustedPosition - adapterCount + mFootersViewTypeStart;
        }

        @Override
        public long getItemId(int position)
        {
            if (mAdapter != null)
            {
                final int headersCount = getHeadersCount();

                if (position >= headersCount)
                {
                    final int adjustedPosition = position - headersCount;

                    final int adapterCount = mAdapter.getItemCount();

                    if (adjustedPosition < adapterCount)
                    {
                        return mAdapter.getItemId(adjustedPosition);
                    }
                }
            }

            return RecyclerView.NO_ID;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            if (viewType < mFootersViewTypeStart)
            {
                return mHeaders.get(viewType - VIEW_TYPE_HEADERS_START);
            }
            else if (viewType < mAdapterViewTypeStart)
            {
                return mFooters.get(viewType - mFootersViewTypeStart);
            }
            else
            {
                return mAdapter.onCreateViewHolder(parent, viewType - mAdapterViewTypeOffset);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            if (mAdapter != null)
            {
                final int headersCount = getHeadersCount();

                if (position >= headersCount)
                {
                    final int adjustedPosition = position - headersCount;

                    final int adapterCount = mAdapter.getItemCount();

                    if (adjustedPosition < adapterCount)
                    {
                        mAdapter.onBindViewHolder(holder, adjustedPosition);
                    }
                }
            }
        }

        @Override
        public void setHasStableIds(boolean hasStableIds)
        {
            super.setHasStableIds(hasStableIds);

            if (mAdapter != null)
            {
                mAdapter.setHasStableIds(hasStableIds);
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView)
        {
            if (mAdapter != null)
            {
                mAdapter.onAttachedToRecyclerView(recyclerView);
            }

            if (getFootersCount()==0&&getHeadersCount()==0){
                return;
            }
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager)
            {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
                {
                    @Override
                    public int getSpanSize(int position)
                    {
                        return getItemViewType(position) < mFootersViewTypeStart||getItemViewType(position) < mAdapterViewTypeStart
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView)
        {
            if (mAdapter != null)
            {
                mAdapter.onDetachedFromRecyclerView(recyclerView);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onViewRecycled(ViewHolder holder)
        {
            if (mAdapter != null)
            {
                if (!(holder instanceof FixedHolder))
                {
                    mAdapter.onViewRecycled(holder);
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onViewAttachedToWindow(ViewHolder holder)
        {
            if (mAdapter != null)
            {
                if (!(holder instanceof FixedHolder))
                {
                    mAdapter.onViewAttachedToWindow(holder);
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onViewDetachedFromWindow(ViewHolder holder)
        {
            if (mAdapter != null)
            {
                if (!(holder instanceof FixedHolder))
                {
                    mAdapter.onViewDetachedFromWindow(holder);
                }
            }
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer)
        {
            super.registerAdapterDataObserver(observer);

            if (mAdapter != null)
            {
                mAdapter.registerAdapterDataObserver(observer);
            }
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer)
        {
            super.unregisterAdapterDataObserver(observer);

            if (mAdapter != null)
            {
                mAdapter.unregisterAdapterDataObserver(observer);
            }
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    // Holder
    // -------------------------------------------------------------------------------------------------------------------------------------

    private static class FixedHolder extends ViewHolder
    {
        public FixedHolder(View itemView)
        {
            super(itemView);
        }
    }
}