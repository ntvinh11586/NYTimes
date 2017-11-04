package com.coderschool.vinh.nytimes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.coderschool.vinh.nytimes.R;
import com.coderschool.vinh.nytimes.callbacks.OnArticleItemClickCallback;
import com.coderschool.vinh.nytimes.callbacks.OnLoadMoreItemCallback;
import com.coderschool.vinh.nytimes.models.Article;
import com.coderschool.vinh.nytimes.models.Multimedia;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ARTICLE_RESULT_WITH_IMAGE = 0;
    private final int ARTICLE_RESULT_NO_IMAGE = 1;

    private ArrayList<Article> mArticles;
    private Context mContext;

    private OnLoadMoreItemCallback onLoadMoreItemCallback;
    private OnArticleItemClickCallback onArticleItemClickCallback;

    public void setOnLoadMoreListener(OnLoadMoreItemCallback onLoadMoreItemCallback) {
        this.onLoadMoreItemCallback = onLoadMoreItemCallback;
    }

    public void setOnArticleItemClickListener(OnArticleItemClickCallback onArticleItemClickCallback) {
        this.onArticleItemClickCallback = onArticleItemClickCallback;
    }

    public ArticleAdapter(Context context, ArrayList<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ARTICLE_RESULT_WITH_IMAGE) {
            View articleResultWithImage = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_article_result_with_image, parent, false);
            return new ArticleImageViewHolder(articleResultWithImage);
        } else {
            View articleResultNoImage = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_article_result_no_image, parent, false);
            return new ArticleNoImageViewHolder(articleResultNoImage);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ARTICLE_RESULT_WITH_IMAGE:
                ArticleImageViewHolder vhWithImage = (ArticleImageViewHolder) viewHolder;
                setVHWithImage(vhWithImage, position);
                break;
            case ARTICLE_RESULT_NO_IMAGE:
                ArticleNoImageViewHolder vhWithNoImage = (ArticleNoImageViewHolder) viewHolder;
                setVHNoImage(vhWithNoImage, position);
                break;
        }

        viewHolder
                .itemView
                .setOnClickListener(v -> {
                    if (onArticleItemClickCallback != null) {
                        onArticleItemClickCallback.onArticleItemClick(getArticle(position));
                    }
                });

        // handle load more item
        if (position == mArticles.size() - 1 && onLoadMoreItemCallback != null) {
            // callback
            onLoadMoreItemCallback.onLoadMoreItem();
        }
    }

    private void setVHWithImage(ArticleImageViewHolder viewHolder, int position) {
        Article article = getArticle(position);
        Multimedia multimedia = article.getMultimedia().get(0);

        viewHolder.tvTitle.setText(article.getHeadline());
        if (!TextUtils.isEmpty(multimedia.getUrl())) {
            Glide.with(getContext())
                    .load(multimedia.getFullUrl())
                    .into(viewHolder.ivImage);
        }
    }

    private void setVHNoImage(ArticleNoImageViewHolder viewHolder, int position) {
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

    public Article getArticle(int position) {
        return mArticles.get(position);
    }

    public void clearAll() {
        this.mArticles.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Article> articles) {
        this.mArticles.addAll(articles);
        notifyDataSetChanged();
    }
}