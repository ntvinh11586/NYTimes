package com.coderschool.vinh.nytimes;

/**
 * Created by Vinh on 10/20/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ArticleArrayAdapter extends
        RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {

    private final int IMAGE = 0, NO_IMAGE = 1;

    private ArrayList<Article> mArticles;

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }

    public ArticleArrayAdapter(Context context, ArrayList<Article> contacts) {
        mArticles = contacts;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ArticleArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ArticleArrayAdapter.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case IMAGE:
                View v1 = inflater.inflate(R.layout.item_article_result_image, parent, false);
                viewHolder = new ArticleImageViewHolder(v1);
                break;
            case NO_IMAGE:
                View v2 = inflater.inflate(R.layout.item_article_result_no_image, parent, false);
                viewHolder = new ArticleNoImageViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleArrayAdapter.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case IMAGE:
                ArticleImageViewHolder vh1 = (ArticleImageViewHolder) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case NO_IMAGE:
                ArticleNoImageViewHolder vh2 = (ArticleNoImageViewHolder) viewHolder;
                configureViewHolder2(vh2, position);
                break;
        }
    }

    private void configureViewHolder1(ArticleImageViewHolder vh1, int position) {
        Article contact = mArticles.get(position);

        TextView textView = vh1.tvTitle;
        textView.setText(contact.getHeadline());

        String thumbnail = contact.getThumbNail();

        if (!TextUtils.isEmpty(thumbnail)) {
            Glide.with(getContext()).load(thumbnail).into(vh1.ivImage);
        }
    }

    private void configureViewHolder2(ArticleNoImageViewHolder vh2, int position) {
        Article contact = mArticles.get(position);

        TextView textView = vh2.tvTitle;
        TextView textView1 = vh2.tvSnippet;

        textView.setText(contact.getHeadline());
        textView1.setText(contact.getSnippet());
    }



    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (!mArticles.get(position).thumbNail.equals("")) {
            return IMAGE;
        } else {
            return NO_IMAGE;
        }
    }


}