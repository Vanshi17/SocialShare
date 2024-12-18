package com.projects.SocialShare.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.projects.SocialShare.R
import com.projects.SocialShare.databinding.PostRvBinding
import com.projects.SocialShare.models.Post
import com.projects.SocialShare.models.User
import com.projects.SocialShare.utils.USER_NODE


class PostAdapter(var context: android.content.Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.myHolder>() {
    inner class myHolder(var binding: PostRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        var binding = PostRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return myHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: myHolder, position: Int) {

        try {
            Firebase.firestore.collection(USER_NODE).document(postList.get(position).uid).get()
                .addOnSuccessListener {
                    var user = it.toObject<User>()
                    Glide.with(context).load(user!!.image).placeholder(R.drawable.user)
                        .into(holder.binding.profileImage)
                    holder.binding.name.text = user.name
                }

        } catch (e: Exception) {

        }

        Glide.with(context).load(postList.get(position).postURL).placeholder(R.drawable.loading)
            .into(holder.binding.postImage)
        try {
            val text = TimeAgo.using(postList.get(position).time.toLong())
            holder.binding.time.text = text

        } catch (e: Exception) {
            holder.binding.time.text = ""

        }
        holder.binding.share.setOnClickListener{
            var i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT,postList.get(position).postURL)
            context.startActivity(i)
        }
        try {
            Firebase.firestore.collection(USER_NODE).document(postList.get(position).uid).get()
                .addOnSuccessListener{
                    var user = it.toObject<User>()
                    if (user != null) {
                        holder.binding.caption.text = "@" + user.name + " - " + postList.get(position).caption
                    }
                    holder.binding.like.setOnClickListener {
                        holder.binding.like.setImageResource(R.drawable.heart_red)
                    }
                }

        }catch (e: Exception){}


    }
}