package com.projects.SocialShare.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.toObject
import com.projects.SocialShare.adapters.MyPostRvAdapter
import com.projects.SocialShare.databinding.FragmentMyPostBinding
import com.projects.SocialShare.models.Post
import com.projects.SocialShare.utils.POST

class MyPostFragment : Fragment() {

    private lateinit var binding: FragmentMyPostBinding
    private lateinit var adapter: MyPostRvAdapter
    private val postList = ArrayList<Post>()
    private var recyclerViewState: Parcelable? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)

        // Setup RecyclerView with GridLayoutManager
        val gridLayoutManager = GridLayoutManager(context, 3) // 3 columns
        binding.rv.layoutManager = gridLayoutManager

        // Initialize the adapter here
        adapter = MyPostRvAdapter(postList)
        binding.rv.adapter = adapter

//        val userPostsPath = POST
//        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection(userPostsPath)
//            .whereEqualTo("uid", Firebase.auth.currentUser?.uid)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                postList.clear()
//                for (doc in querySnapshot.documents) {
//                    val post = doc.toObject<Post>()
//                    if (post != null) {
//                        Log.d("MyPostsFragment", "Fetched Post: ${post.postURL}")
//                        postList.add(post)
//                    }
//                }
//                Log.d("MyPostsFragment", "Total posts fetched: ${postList.size}")
//                adapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                Log.e("MyPostsFragment", "Error fetching posts: ${e.message}")
//            }

        loadData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    public fun loadData() {



        val userPostsPath = POST
        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection(userPostsPath)
            .whereEqualTo("uid", Firebase.auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                postList.clear()
                for (doc in querySnapshot.documents) {
                    val post = doc.toObject<Post>()
                    if (post != null) {
                        postList.add(post)
                    }
                }
                postList.sortByDescending { it.time }
                if (::adapter.isInitialized) {
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("MyPostsFragment", "Adapter is not initialized")
                }
            }
            .addOnFailureListener { e ->
                Log.e("MyPostsFragment", "Error loading posts: ${e.message}")
            }
    }

    fun reloadData() {
        loadData()
    }

    override fun onPause() {
        super.onPause()
        recyclerViewState = binding.rv.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        binding.rv.layoutManager?.onRestoreInstanceState(recyclerViewState)
//        if (postList.isEmpty()) { // Reload only if necessary
//            loadData()
//        }
    }

}

