package com.coderschool.vinh.nytimes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.coderschool.vinh.nytimes.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vinh on 10/20/2016.
 */
public class ArticleNoImageViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvTitle) public TextView tvTitle;
    @BindView(R.id.tvSnippet) public TextView tvSnippet;

    public ArticleNoImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
