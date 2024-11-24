package com.projects.SocialShare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.projects.SocialShare.R
import com.projects.SocialShare.models.Post
import com.squareup.picasso.Picasso

class MyPostRvAdapter(private var postList: List<Post>) :
    RecyclerView.Adapter<MyPostRvAdapter.ViewHolder>() {

    inner class ViewHolder(view: android.view.View) :
        RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_post_rv_design, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        val postImageView = holder.itemView.findViewById<ImageView>(R.id.post_image)

        if (post?.postURL.isNullOrEmpty()) {
            postImageView.setImageResource(R.drawable.loading)
        } else {
            Picasso.get()
                .load(post.postURL)
                .fit() // Fit the image into the ImageView size without explicitly resizing
                .centerCrop()
                .placeholder(R.drawable.loading) // Crop image to fit and avoid distortion
                .into(postImageView)
        }
    }
}
