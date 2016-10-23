package com.coderschool.vinh.nytimes.adapters;

/**
 * Created by Vinh on 10/20/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.coderschool.vinh.nytimes.R;
import com.coderschool.vinh.nytimes.models.Article;

import java.util.ArrayList;

public class ArticleArrayAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ARTICLE_RESULT_IMAGE = 0;
    private final int ARTICLE_RESULT_NO_IMAGE = 1;

    private ArrayList<Article> mArticles;
    private Context mContext;
    private Listener mListener;

    public interface Listener {
        void onLoadMore();
    }

    public ArticleArrayAdapter(Context context, ArrayList<Article> contacts) {
        mArticles = contacts;
        mContext = context;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case ARTICLE_RESULT_IMAGE:
                View articleResultImage = inflater.inflate(R.layout.item_article_result_image, parent, false);
                viewHolder = new ArticleImageViewHolder(articleResultImage);
                break;
            case ARTICLE_RESULT_NO_IMAGE:
                View articleResultNoImage = inflater.inflate(R.layout.item_article_result_no_image, parent, false);
                viewHolder = new ArticleNoImageViewHolder(articleResultNoImage);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case ARTICLE_RESULT_IMAGE:
                ArticleImageViewHolder viewHolderWithImage = (ArticleImageViewHolder) viewHolder;
                configureViewHolderWithImage(viewHolderWithImage, position);
                break;
            case ARTICLE_RESULT_NO_IMAGE:
                ArticleNoImageViewHolder viewHolderWithNoImage = (ArticleNoImageViewHolder) viewHolder;
                configureViewHolderNoImage(viewHolderWithNoImage, position);
                break;
        }

        if (position == mArticles.size() - 1 && mListener != null) {
            mListener.onLoadMore();
        }
    }

    private void configureViewHolderWithImage(ArticleImageViewHolder viewHolder, int position) {
        Article contact = mArticles.get(position);

        viewHolder.tvTitle.setText(contact.getHeadline());

        if (!TextUtils.isEmpty(contact.getMultimedia().get(0).getUrl())) {
            Glide.with(getContext())
                    .load("http://www.nytimes.com/" + contact.getMultimedia().get(0).getUrl())
                    .into(viewHolder.ivImage);
        }
    }

    private void configureViewHolderNoImage(ArticleNoImageViewHolder viewHolder, int position) {
        Article contact = mArticles.get(position);

        viewHolder.tvTitle.setText(contact.getHeadline());
        viewHolder.tvSnippet.setText(contact.getSnippet());
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (mArticles.get(position).getMultimedia().size() != 0) {
            return ARTICLE_RESULT_IMAGE;
        } else {
            return ARTICLE_RESULT_NO_IMAGE;
        }
    }

}