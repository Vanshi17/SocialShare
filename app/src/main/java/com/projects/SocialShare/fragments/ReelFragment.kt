package com.projects.SocialShare.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.projects.SocialShare.adapters.ReelAdapter
import com.projects.SocialShare.databinding.FragmentReelBinding
import com.projects.SocialShare.models.Reel
import com.projects.SocialShare.utils.REEL

class ReelFragment : Fragment() {

    private lateinit var binding: FragmentReelBinding
    private lateinit var adapter: ReelAdapter
    private val reelList = ArrayList<Reel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReelBinding.inflate(inflater, container, false)
        adapter = ReelAdapter(requireContext(),reelList)
        binding.viewPager.adapter = adapter
        Firebase.firestore.collection(REEL).get().addOnSuccessListener {
            var tempList = ArrayList<Reel>()
            reelList.clear()
            for (i in it.documents)
            {
                var reel = i.toObject<Reel>()!!
                tempList.add(reel)
            }
            reelList.addAll(tempList)
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapter
        adapter = ReelAdapter(requireContext(), reelList)

        // Configure RecyclerView
        binding.viewPager.adapter = adapter

        // Fetch reels and notify adapter
        fetchReels()
    }

    private fun fetchReels() {
        Log.d("ReelFragment", "Fetching from collection: $REEL")

        Firebase.firestore.collection("Reel")
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val newReels = querySnapshot.documents.mapNotNull { it.toObject<Reel>() }
                    updateReels(newReels)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ReelFragment", "Error fetching reels: ${e.message}")
            }

    }


    private fun updateReels(newReels: List<Reel>) {
        reelList.clear()
        reelList.addAll(newReels)
        adapter.notifyDataSetChanged()
    }
}
