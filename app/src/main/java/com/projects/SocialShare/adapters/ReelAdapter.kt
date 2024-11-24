package com.projects.SocialShare.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.SocialShare.R
import com.projects.SocialShare.databinding.ReelDgBinding
import com.projects.SocialShare.models.Reel
import com.squareup.picasso.Picasso

class ReelAdapter(val context: Context, private val reelList: ArrayList<Reel>) :
    RecyclerView.Adapter<ReelAdapter.ViewHolder>() {


    // Inner ViewHolder class
    inner class ViewHolder(val binding: ReelDgBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReelDgBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            Picasso.get()
                .load(reelList[position].profileLink)
                .placeholder(R.drawable.user) // Placeholder image
                .error(R.drawable.user) // Fallback image if the URL is invalid
                .into(holder.binding.profileImage)
        } catch (e: Exception) {
            Log.e("ReelAdapter", "Error loading profile image: ${e.message}")
        }

        holder.binding.caption?.text = reelList[position].caption
        holder.binding.videoView.setVideoPath(reelList[position].reelURL)
        holder.binding.progressBar?.visibility = View.VISIBLE // Show the progress bar initially

        // Listener for when the video is prepared
        holder.binding.videoView.setOnPreparedListener {
            holder.binding.progressBar?.visibility = View.GONE // Hide progress bar
            holder.binding.videoView.start() // Start the video
        }

        // Listener for when the video completes
        holder.binding.videoView.setOnCompletionListener {
            holder.binding.videoView.seekTo(0) // Reset video to the beginning
            holder.binding.videoView.pause()  // Pause the video
        }

        // Add touch listener for toggling play/pause
        holder.binding.videoView.setOnClickListener {
            if (holder.binding.videoView.isPlaying) {
                holder.binding.videoView.pause() // Pause the video if it's playing
            } else {
                holder.binding.videoView.start() // Resume the video if it's paused
            }
        }
    }



    override fun getItemCount(): Int = reelList.size

}