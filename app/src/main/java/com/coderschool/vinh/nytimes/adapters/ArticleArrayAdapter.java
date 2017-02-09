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
import com.coderschool.vinh.nytimes.models.Multimedia;

import java.util.ArrayList;

public class ArticleArrayAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ARTICLE_RESULT_WITH_IMAGE = 0;
    private final int ARTICLE_RESULT_NO_IMAGE = 1;

    private ArrayList<Article> mArticles;
    private Context mContext;

    // Handle load more item listener - callback pattern
    private OnLoadMoreItemListener mOnLoadMoreItemListener;

    public interface OnLoadMoreItemListener {
        void onLoadMoreItem();
    }

    public void setOnLoadMoreListener(OnLoadMoreItemListener onLoadMoreItemListener) {
        mOnLoadMoreItemListener = onLoadMoreItemListener;
    }

    public ArticleArrayAdapter(Context context, ArrayList<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    private Article getArticle(int position) {
        return mArticles.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ARTICLE_RESULT_WITH_IMAGE) {
            View articleResultWithImage = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_article_result_with_image, parent, false);
            return new ArticleImageVH(articleResultWithImage);
        } else {
            View articleResultNoImage = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_article_result_no_image, parent, false);
            return new ArticleNoImageVH(articleResultNoImage);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ARTICLE_RESULT_WITH_IMAGE:
                ArticleImageVH vhWithImage = (ArticleImageVH) viewHolder;
                setVHWithImage(vhWithImage, position);
                break;
            case ARTICLE_RESULT_NO_IMAGE:
                ArticleNoImageVH vhWithNoImage = (ArticleNoImageVH) viewHolder;
                setVHNoImage(vhWithNoImage, position);
                break;
        }

        // handle load more item
        if (position == mArticles.size() - 1 && mOnLoadMoreItemListener != null) {
            // callback
            mOnLoadMoreItemListener.onLoadMoreItem();
        }
    }

    private void setVHWithImage(ArticleImageVH viewHolder, int position) {
        Article article = getArticle(position);
        Multimedia multimedia = article.getMultimedia().get(0);

        viewHolder.tvTitle.setText(article.getHeadline());
        if (!TextUtils.isEmpty(multimedia.getUrl())) {
            Glide.with(getContext())
                    .load(multimedia.getFullUrl())
                    .into(viewHolder.ivImage);
        }
    }

    private void setVHNoImage(ArticleNoImageVH viewHolder, int position) {
        Article contact = getArticle(position);
        viewHolder.tvTitle.setText(contact.getHeadline());
        viewHolder.tvSnippet.setText(contact.getSnippet());
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getArticle(position).getMultimedia().size() > 0) {
            return ARTICLE_RESULT_WITH_IMAGE;
        } else {
            return ARTICLE_RESULT_NO_IMAGE;
        }
    }
}