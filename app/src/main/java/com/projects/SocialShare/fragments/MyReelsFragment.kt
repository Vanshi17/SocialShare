package com.projects.SocialShare.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.projects.SocialShare.adapters.MyReelsRvAdapter
import com.projects.SocialShare.databinding.FragmentMyReelsBinding
import com.projects.SocialShare.models.Reel
import com.projects.SocialShare.utils.REEL

class MyReelsFragment : Fragment() {
    private lateinit var binding: FragmentMyReelsBinding
    private lateinit var adapter: MyReelsRvAdapter
    private val reelList = ArrayList<Reel>()
    private var recyclerViewState: Parcelable? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyReelsBinding.inflate(inflater, container, false)

        // Setup RecyclerView with GridLayoutManager
        val gridLayoutManager = GridLayoutManager(context, 3) // 3 columns
        binding.rv.layoutManager = gridLayoutManager

        adapter = MyReelsRvAdapter(reelList)
        binding.rv.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    public fun loadData() {
        val userReelsPath = "${Firebase.auth.currentUser!!.uid}$REEL"
        Firebase.firestore.collection(userReelsPath)
            .get()
            .addOnSuccessListener { querySnapshot ->
                reelList.clear()
                for (doc in querySnapshot.documents) {
                    val reel = doc.toObject<Reel>()
                    if (reel != null) {
                        reelList.add(reel)
                    }
                }
                reelList.sortByDescending { it.time }
                if (::adapter.isInitialized) {
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("MyReelsFragment", "Adapter is not initialized")
                }
            }
            .addOnFailureListener { e ->
                Log.e("MyReelsFragment", "Error loading reels: ${e.message}")
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
        if (reelList.isEmpty()) { // Reload only if necessary
            loadData()
        }
    }



}
