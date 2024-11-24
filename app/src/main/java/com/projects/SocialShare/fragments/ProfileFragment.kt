package com.projects.SocialShare.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import androidx.appcompat.widget.Toolbar
import com.projects.SocialShare.R
import com.projects.SocialShare.SignUpActivity
import com.projects.SocialShare.databinding.FragmentProfileBinding
import com.projects.SocialShare.models.User
import com.projects.SocialShare.utils.USER_NODE
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private val myPostFragment by lazy { MyPostFragment() }
    private val myReelsFragment by lazy { MyReelsFragment() }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find and set up the toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        // Enable options menu
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.addFragments(myPostFragment, "My Posts")
        viewPagerAdapter.addFragments(myReelsFragment, "My Reels")
        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.editProfile.setOnClickListener{
            val intent = Intent(activity,SignUpActivity::class.java)
            intent.putExtra("MODE",1)
            activity?.startActivity(intent)
        }
        
        drawerLayout = binding.root.findViewById(R.id.drawer_layout)
        navigationView = binding.root.findViewById(R.id.navigation_view)
        headerView = navigationView.getHeaderView(0)

        setupNavigationDrawer()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadUserData()  // Ensure this is called to load user data
    }

    private fun loadUserData() {
        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user: User = it.toObject(User::class.java)!!
                binding.name.text = user.name // Ensure this is the correct ID for the name
                binding.bio.text = user.email // Or user.bio if you have it in the User model
                if (!user.image.isNullOrEmpty()) {
                    Picasso.get().load(user.image).into(binding.profileImage)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun setupNavigationDrawer() {
        Firebase.firestore.collection(USER_NODE)
            .document(Firebase.auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    val navProfileImage =
                        headerView.findViewById<CircleImageView>(R.id.nav_profile_image)
                    val navProfileName = headerView.findViewById<TextView>(R.id.nav_profile_name)

                    if (!it.image.isNullOrEmpty()) {
                        Picasso.get().load(it.image).into(navProfileImage)
                    }
                    navProfileName.text = it.name
                }
            }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> handleLogout()
            }
            true
        }
    }

    private fun handleLogout() {
        Firebase.auth.signOut()
        val intent = Intent(requireContext(), SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_open_drawer -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}