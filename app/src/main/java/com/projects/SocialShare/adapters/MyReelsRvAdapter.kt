package com.projects.SocialShare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projects.SocialShare.R
import com.projects.SocialShare.models.Reel

class MyReelsRvAdapter(private val reelList: List<Reel>) :
    RecyclerView.Adapter<MyReelsRvAdapter.ReelViewHolder>() {

    inner class ReelViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_reel_rv_design, parent, false)
        return ReelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        val reel = reelList[position]

        with(holder.itemView) {
            // Map views explicitly
            val reelThumbnail = findViewById<ImageView>(R.id.reelThumbnail)

            // Load thumbnail
            Glide.with(context)
                .load(reel.reelURL)
                .placeholder(R.drawable.loading) // Optional placeholder
                .into(reelThumbnail)
        }
    }


    override fun getItemCount(): Int = reelList.size
}
