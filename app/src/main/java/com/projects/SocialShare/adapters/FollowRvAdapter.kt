package com.projects.SocialShare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projects.SocialShare.databinding.FollowRvBinding
import com.projects.SocialShare.models.User

class FollowRvAdapter(var context: Context, var followList: ArrayList<User>) :
    RecyclerView.Adapter<FollowRvAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: FollowRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = FollowRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return followList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(followList.get(position).image).placeholder(com.projects.SocialShare.R.drawable.user).into(holder.binding.profileImage)
        holder.binding.name.text = followList.get(position).name?.split(" ")?.first()
    }
}