package com.yocto.wetodo.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yocto.wetodo.R;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class SimplePaddingSection extends Section {
    private final int paddingInPixel;

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.getLayoutParams().height = paddingInPixel;
            itemView.setLayoutParams(itemView.getLayoutParams());
        }
    }

    public SimplePaddingSection(int paddingInPixel) {
        super(SectionParameters.builder().itemResourceId(R.layout.simple_padding_item_section)
                .build());
        this.paddingInPixel = paddingInPixel;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

    }
}
