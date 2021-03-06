package com.coderschool.vinh.nytimes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.coderschool.vinh.nytimes.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleNoImageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSnippet)
    TextView tvSnippet;

    ArticleNoImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
