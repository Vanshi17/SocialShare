package com.projects.SocialShare.post


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.projects.SocialShare.HomeActivity
import com.projects.SocialShare.databinding.ActivityPostBinding
import com.projects.SocialShare.models.Post
import com.projects.SocialShare.models.User
import com.projects.SocialShare.utils.POST
import com.projects.SocialShare.utils.POST_FOLDER
import com.projects.SocialShare.utils.USER_NODE
import com.projects.SocialShare.utils.uploadImage


class PostActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }
    var imageUrl: String? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri ->
        uri?.let {
            uploadImage(uri, POST_FOLDER)
            { url ->
                if (url != null) {
                    binding.selectImage.setImageURI(uri)
                    imageUrl = url
                    Toast.makeText(this, "Photo selected successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }
        binding.selectImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            val currentUser = Firebase.auth.currentUser
            if (currentUser == null) {
                // Handle the case where the user is not logged in
                Toast.makeText(this@PostActivity, "User is not logged in", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (imageUrl == null) {
                // Show an error message to the user or handle the case where no image is selected
                Toast.makeText(this@PostActivity, "Please select an image", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            Firebase.firestore.collection(USER_NODE).document(currentUser.uid).get()
                .addOnSuccessListener {
                    var user = it.toObject<User>()
                    if (user == null) {
                        // Handle null user object (e.g., document not found or invalid data)
                        Toast.makeText(
                            this@PostActivity,
                            "Failed to fetch user data",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnSuccessListener
                    }

                    val post = Post(
                        postURL = imageUrl!!,  // Safe because we check for null before
                        caption = binding.caption.editText?.text.toString(),
                        uid = Firebase.auth.currentUser!!.uid,
                        time = System.currentTimeMillis()
                    )
                    Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                        Toast.makeText(this, "Photo posted successfully!", Toast.LENGTH_SHORT).show()
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document()
                            .set(post)
                            .addOnSuccessListener {
                                startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                                finish()
                            }
                    }
                }
        }

    }
}