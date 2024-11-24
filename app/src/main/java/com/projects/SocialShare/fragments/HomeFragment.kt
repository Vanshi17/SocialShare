package com.projects.SocialShare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.projects.SocialShare.R
import com.projects.SocialShare.adapters.FollowRvAdapter
import com.projects.SocialShare.adapters.PostAdapter
import com.projects.SocialShare.databinding.FragmentHomeBinding
import com.projects.SocialShare.models.Post
import com.projects.SocialShare.models.User
import com.projects.SocialShare.utils.FOLLOW
import com.projects.SocialShare.utils.POST
import com.projects.SocialShare.utils.USER_NODE
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var postList = ArrayList<Post>()
    private lateinit var adapter: PostAdapter
    private var followList = ArrayList<User>()
    private lateinit var followRvAdapter: FollowRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = PostAdapter(requireContext(), postList)
        binding.postRv.layoutManager = LinearLayoutManager(requireContext())
        binding.postRv.adapter = adapter

        followRvAdapter = FollowRvAdapter(requireContext(),followList)
        binding.followRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.followRv.adapter = followRvAdapter

        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW).get().addOnSuccessListener {
            var tempList = ArrayList<User>()
            followList.clear()
            for(i in it.documents)
            {
                var user: User = i.toObject<User>()!!
                tempList.add(user)
            }
            followList.addAll(tempList)
            followRvAdapter.notifyDataSetChanged()
        }

        Firebase.firestore.collection(POST)
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                postList.clear()
                for (document in querySnapshot.documents) {
                    val post = document.toObject<Post>()!!
                    postList.add(post)
                }
                adapter.notifyDataSetChanged()
            }


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onStart() {
        super.onStart()
        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user:User = it.toObject(User::class.java)!!
                if(!user.image.isNullOrEmpty())
                {
                    Picasso.get().load(user.image).into(binding.userImage)
                }
            }
    }

}