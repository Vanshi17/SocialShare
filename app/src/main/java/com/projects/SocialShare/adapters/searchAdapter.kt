package com.projects.SocialShare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projects.SocialShare.databinding.SearchRvBinding
import com.projects.SocialShare.utils.FOLLOW

class searchAdapter(
    var context: android.content.Context,
    var userList: ArrayList<com.projects.SocialShare.models.User>
) : RecyclerView.Adapter<searchAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: SearchRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var isFollowed = false

        Glide.with(context).load(userList[position].image)
            .placeholder(com.projects.SocialShare.R.drawable.user)
            .into(holder.binding.profileImage)
        holder.binding.name.text = userList[position].name?.split(" ")?.first()

        FirebaseFirestore.getInstance().collection(FirebaseAuth.getInstance().currentUser!!.uid + FOLLOW)
            .whereEqualTo("email", userList[position].email).get().addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    isFollowed = false
                } else {
                    holder.binding.follow.text = "Unfollow"
                    isFollowed = true
                }
            }

        holder.binding.follow.setOnClickListener {
            if (isFollowed) {
                FirebaseFirestore.getInstance().collection(FirebaseAuth.getInstance().currentUser!!.uid + FOLLOW)
                    .whereEqualTo("email", userList[position].email).get()
                    .addOnSuccessListener {
                        FirebaseFirestore.getInstance().collection(FirebaseAuth.getInstance().currentUser!!.uid + FOLLOW)
                            .document(it.documents[0].id).delete()
                        holder.binding.follow.text = "Follow"
                        isFollowed = false

                        // Show toast for unfollow action
                        Toast.makeText(context, "Unfollowed ${userList[position].name}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                FirebaseFirestore.getInstance().collection(FirebaseAuth.getInstance().currentUser!!.uid + FOLLOW).document()
                    .set(userList[position])
                holder.binding.follow.text = "Unfollow"
                isFollowed = true

                // Show toast for follow action
                Toast.makeText(context, "Followed ${userList[position].name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}