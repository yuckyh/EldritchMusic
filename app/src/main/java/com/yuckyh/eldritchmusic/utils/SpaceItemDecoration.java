package com.yuckyh.eldritchmusic.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int mVerticalSpaceHeight;
    private final int mHorizontalSpaceHeight;

    public SpaceItemDecoration(int verticalSpaceHeight, int horizontalSpaceHeight) {
        mVerticalSpaceHeight = verticalSpaceHeight;
        mHorizontalSpaceHeight = horizontalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = mVerticalSpaceHeight;
        outRect.right = mHorizontalSpaceHeight;

        if (parent.getAdapter() != null && parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = mVerticalSpaceHeight;
            outRect.right = mHorizontalSpaceHeight;
        }
    }
}
