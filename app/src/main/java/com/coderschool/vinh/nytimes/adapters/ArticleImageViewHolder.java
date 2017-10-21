package com.coderschool.vinh.nytimes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderschool.vinh.nytimes.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleImageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivImage)
    ImageView ivImage;

    ArticleImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}

