package com.eowise.recyclerview.stickyheaders.samples.data;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.eowise.recyclerview.stickyheaders.samples.R;

public class NLRecyclerView extends RecyclerView
{
    private static final int LAYOUT_MANGER_TYPE_NONE = -1;

    private static final int LAYOUT_MANGER_TYPE_LINEAR = 0;

    private static final int LAYOUT_MANGER_TYPE_GRID = 1;

    private static final int LAYOUT_MANGER_TYPE_STAGGERED_GRID = 2;

    private static final int LAYOUT_MANGER_ORIENTATION_HORIZONTAL = 0;

    private static final int LAYOUT_MANGER_ORIENTATION_VERTICAL = 1;

    private static final int DECORATION_TYPE_NONE = -1;

    private static final int DECORATION_TYPE_VERTICAL_DIVIDER = 0;

    private static final int DEFAULT_SPAN_COUNT = 2;

    private ItemDecoration mDefaultItemDecoration;

    public NLRecyclerView(Context context)
    {
        this(context, null);
    }

    public NLRecyclerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, R.attr.recyclerViewStyle);
    }

    public NLRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NLRecyclerView, 0, 0);

        setHasFixedSize(a.getBoolean(R.styleable.NLRecyclerView_hasFixedSize, hasFixedSize()));

        switch (a.getInt(R.styleable.NLRecyclerView_extLayoutManager, LAYOUT_MANGER_TYPE_NONE))
        {
            case LAYOUT_MANGER_TYPE_LINEAR:

                setLayoutManager(NLLinearLayoutManager.createInstance(context, a));

                break;

            case LAYOUT_MANGER_TYPE_GRID:

                setLayoutManager(NLGridLayoutManager.createInstance(context, a));

                break;

            case LAYOUT_MANGER_TYPE_STAGGERED_GRID:

                setLayoutManager(NLStaggeredGridLayoutManager.createInstance(a));

                break;
        }

        switch (a.getInt(R.styleable.NLRecyclerView_decoration, DECORATION_TYPE_NONE))
        {
            case DECORATION_TYPE_VERTICAL_DIVIDER:

                setDefaultItemDecoration(VerticalDividerItemDecoration.tryCreateInstance(a));

                break;
        }

        a.recycle();
    }

    public void setDefaultItemDecoration(ItemDecoration decoration)
    {
        if (mDefaultItemDecoration != decoration)
        {
            if (mDefaultItemDecoration != null)
            {
                removeItemDecoration(mDefaultItemDecoration);
            }

            mDefaultItemDecoration = decoration;

            if (decoration != null)
            {
                addItemDecoration(decoration, 0);
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public ItemDecoration getDefaultItemDecoration()
    {
        return mDefaultItemDecoration;
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    // Resolve Attributes (LayoutManager)
    // -------------------------------------------------------------------------------------------------------------------------------------

    private static int resolveSpanCount(TypedArray a, int defaultValue)
    {
        return a.getInteger(R.styleable.NLRecyclerView_layoutManager_spanCount, defaultValue);
    }

    private static int resolveOrientation(TypedArray a, int defaultValue)
    {
        final int orientation = a.getInt(R.styleable.NLRecyclerView_layoutManager_orientation, -1);

        if (orientation == LAYOUT_MANGER_ORIENTATION_HORIZONTAL) return OrientationHelper.HORIZONTAL;

        if (orientation == LAYOUT_MANGER_ORIENTATION_VERTICAL) return OrientationHelper.VERTICAL;

        return defaultValue;
    }

    private static boolean resolveReverseLayout(TypedArray a, boolean defaultValue)
    {
        return a.getBoolean(R.styleable.NLRecyclerView_layoutManager_reverseLayout, defaultValue);
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    // Resolve Attributes (ItemDecoration)
    // -------------------------------------------------------------------------------------------------------------------------------------

    private static Drawable resolveDivider(TypedArray a)
    {
        return a.getDrawable(R.styleable.NLRecyclerView_decoration_verticalDivider);
    }

    private static int resolveDividerHeight(TypedArray a, int defaultValue)
    {
        return a.getDimensionPixelSize(R.styleable.NLRecyclerView_decoration_verticalDividerHeight, defaultValue);
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    // NLLinearLayoutManager
    // -------------------------------------------------------------------------------------------------------------------------------------

    public static class NLLinearLayoutManager extends LinearLayoutManager
    {
        @SuppressWarnings("UnusedDeclaration")
        public NLLinearLayoutManager(Context context)
        {
            super(context);
        }

        @SuppressWarnings("UnusedDeclaration")
        public NLLinearLayoutManager(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);
        }

        static NLLinearLayoutManager createInstance(Context context, TypedArray a)
        {
            return new NLLinearLayoutManager(context,

            /* RESOLVE ATTRIBUTES */ resolveOrientation(a, VERTICAL),

            /* RESOLVE ATTRIBUTES */ resolveReverseLayout(a, false));
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    // NLGridLayoutManager
    // -------------------------------------------------------------------------------------------------------------------------------------

    public static class NLGridLayoutManager extends GridLayoutManager
    {
        @SuppressWarnings("UnusedDeclaration")
        public NLGridLayoutManager(Context context, int spanCount)
        {
            super(context, spanCount);
        }

        @SuppressWarnings("UnusedDeclaration")
        public NLGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout)
        {
            super(context, spanCount, orientation, reverseLayout);
        }

        static NLGridLayoutManager createInstance(Context context, TypedArray a)
        {
            return new NLGridLayoutManager(context,

            /* RESOLVE ATTRIBUTES */ resolveSpanCount(a, DEFAULT_SPAN_COUNT),

            /* RESOLVE ATTRIBUTES */ resolveOrientation(a, VERTICAL),

            /* RESOLVE ATTRIBUTES */ resolveReverseLayout(a, false));
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    // NLStaggeredGridLayoutManager
    // -------------------------------------------------------------------------------------------------------------------------------------

    public static class NLStaggeredGridLayoutManager extends StaggeredGridLayoutManager
    {
        public NLStaggeredGridLayoutManager(int spanCount, int orientation)
        {
            super(spanCount, orientation);
        }

        public NLStaggeredGridLayoutManager(int spanCount, int orientation, boolean reverseLayout)
        {
            this(spanCount, orientation);

            if (reverseLayout != getReverseLayout())
            {
                setReverseLayout(reverseLayout);
            }
        }

        static NLStaggeredGridLayoutManager createInstance(TypedArray a)
        {
            return new NLStaggeredGridLayoutManager(

            /* RESOLVE ATTRIBUTES */ resolveSpanCount(a, DEFAULT_SPAN_COUNT),

            /* RESOLVE ATTRIBUTES */ resolveOrientation(a, VERTICAL),

            /* RESOLVE ATTRIBUTES */ resolveReverseLayout(a, false));
        }

        @Override
        public RecyclerView.LayoutParams generateDefaultLayoutParams()
        {
            return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @Override
        public RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attrs)
        {
            return new LayoutParams(context, attrs);
        }

        @Override
        public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams)
        {
            if (layoutParams instanceof MarginLayoutParams)
            {
                return new LayoutParams((MarginLayoutParams) layoutParams);
            }
            else
            {
                return new LayoutParams(layoutParams);
            }
        }

        @Override
        public boolean checkLayoutParams(RecyclerView.LayoutParams layoutParams)
        {
            return layoutParams instanceof LayoutParams;
        }

        public static class LayoutParams extends StaggeredGridLayoutManager.LayoutParams
        {
            public LayoutParams(Context context, AttributeSet attrs)
            {
                super(context, attrs);

                final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NLStaggeredGridLayoutManager_Layout, 0, 0);

                setFullSpan(a.getBoolean(R.styleable.NLStaggeredGridLayoutManager_Layout_layout_fullSpan, isFullSpan()));

                a.recycle();
            }

            public LayoutParams(int width, int height)
            {
                super(width, height);
            }

            public LayoutParams(MarginLayoutParams source)
            {
                super(source);
            }

            public LayoutParams(ViewGroup.LayoutParams source)
            {
                super(source);
            }

            @SuppressWarnings("UnusedDeclaration")
            public LayoutParams(RecyclerView.LayoutParams source)
            {
                super(source);
            }
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    // VerticalDividerItemDecoration
    // -------------------------------------------------------------------------------------------------------------------------------------

    public static class VerticalDividerItemDecoration extends RecyclerView.ItemDecoration
    {
        private int mDividerHeight;

        private Drawable mDivider;

        @SuppressWarnings("UnusedDeclaration")
        public VerticalDividerItemDecoration(Drawable divider)
        {
            this(divider, 0);
        }

        public VerticalDividerItemDecoration(Drawable divider, int dividerHeight)
        {
            setDivider(divider);

            if (dividerHeight != 0)
            {
                setDividerHeight(dividerHeight);
            }
        }

        static VerticalDividerItemDecoration tryCreateInstance(TypedArray a)
        {
            final Drawable divider = resolveDivider(a);

            return divider != null ? new VerticalDividerItemDecoration(divider, resolveDividerHeight(a, 0)) : null;
        }

        public void setDivider(Drawable divider)
        {
            mDivider = divider;

            mDividerHeight = divider != null ? Math.max(divider.getIntrinsicHeight(), 0) : 0;
        }

        public void setDividerHeight(int dividerHeight)
        {
            mDividerHeight = dividerHeight;
        }

        @SuppressWarnings("UnusedDeclaration")
        public Drawable getDivider()
        {
            return mDivider;
        }

        @SuppressWarnings("UnusedDeclaration")
        public int getDividerHeight()
        {
            return mDividerHeight;
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, State state)
        {
            super.onDraw(canvas, parent, state);

            final int left = parent.getPaddingLeft();

            final int right = parent.getWidth() - parent.getPaddingRight();

            final int itemCount = state.getItemCount();

            final int childCount = parent.getChildCount();

            for (int i = 0; i < childCount; i++)
            {
                final View child = parent.getChildAt(i);

                if (parent.getChildPosition(child) != itemCount - 1)
                {
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                    final int top = child.getBottom() + params.bottomMargin;

                    final int bottom = top + mDividerHeight;

                    mDivider.setBounds(left, top, right, bottom);

                    mDivider.draw(canvas);
                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state)
        {
            outRect.set(0, 0, 0, mDividerHeight);
        }
    }
}