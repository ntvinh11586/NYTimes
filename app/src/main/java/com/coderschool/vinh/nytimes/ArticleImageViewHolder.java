package com.coderschool.vinh.nytimes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Vinh on 10/20/2016.
 */

public class ArticleImageViewHolder extends ArticleArrayAdapter.ViewHolder {

    public TextView tvTitle;
    public ImageView ivImage;

    public ArticleImageViewHolder(View itemView) {
        super(itemView);

        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
    }

}
