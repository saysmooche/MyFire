package com.bb.myfire;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView fnameView;
        public TextView lnameView;
        public ImageView starView;
        public TextView numStarsView;

        public PostViewHolder(View itemView) {
            super(itemView);

            fnameView = itemView.findViewById(R.id.fir_nam);
            lnameView = itemView.findViewById(R.id.las_nam);
            starView = itemView.findViewById(R.id.imgview);
            numStarsView = itemView.findViewById(R.id.comment1);
        }

        public void bindToPost(Post post, View.OnClickListener starClickListener) {
            fnameView.setText(post.fname);
            lnameView.setText(post.lname);
            numStarsView.setText(String.valueOf(post.starCount));

            starView.setOnClickListener(starClickListener);
        }
    }
