package com.projects.SocialShare.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.projects.SocialShare.adapters.searchAdapter
import com.projects.SocialShare.databinding.FragmentSearchBinding
import com.projects.SocialShare.utils.USER_NODE

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var adapter: searchAdapter
    var userList = ArrayList<com.projects.SocialShare.models.User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = searchAdapter(requireContext(), userList)
        binding.rv.adapter = adapter

        Firebase.firestore.collection(USER_NODE)
            .orderBy("name") // Sort users alphabetically by name
            .get()
            .addOnSuccessListener {
                val tempList = ArrayList<com.projects.SocialShare.models.User>()
                userList.clear()
                for (i in it.documents) {
                    if (i.id != Firebase.auth.currentUser!!.uid) {
                        val user: com.projects.SocialShare.models.User =
                            i.toObject<com.projects.SocialShare.models.User>()!!
                        tempList.add(user)
                    }
                }
                userList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("SearchFragment", "Error fetching users: ${e.message}", e)
            }

        binding.searchButton.setOnClickListener {
            val searchText = binding.searchView.text.toString().trim()
            if (searchText.isNotBlank()) {
                Firebase.firestore.collection(USER_NODE)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val tempList = ArrayList<com.projects.SocialShare.models.User>()
                        userList.clear()

                        for (document in querySnapshot.documents) {
                            if (document.id != Firebase.auth.currentUser!!.uid) {
                                val user = document.toObject<com.projects.SocialShare.models.User>()
                                if (user != null && user.name?.contains(searchText, ignoreCase = true) == true) {
                                    tempList.add(user)
                                }
                            }
                        }

                        // Check if no results were found
                        if (tempList.isEmpty()) {
                            // Show "No results found" message
                            binding.noResultsText.visibility = View.VISIBLE
                        } else {
                            // Hide the "No results found" message
                            binding.noResultsText.visibility = View.GONE
                        }

                        // Update the user list with results
                        userList.addAll(tempList)
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Log.e("SearchFragment", "Error fetching users: ${e.message}", e)
                    }
            } else {
                // Optionally handle the case where the search query is empty
                binding.noResultsText.visibility = View.GONE
            }
        }


        return binding.root
    }


}