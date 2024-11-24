package com.projects.SocialShare.post

import android.app.ProgressDialog
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
import com.projects.SocialShare.databinding.ActivityReelBinding
import com.projects.SocialShare.models.Reel
import com.projects.SocialShare.models.User
import com.projects.SocialShare.utils.REEL
import com.projects.SocialShare.utils.REEL_FOLDER
import com.projects.SocialShare.utils.USER_NODE
import com.projects.SocialShare.utils.uploadVideo

class ReelActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityReelBinding.inflate(layoutInflater)
    }
    private lateinit var videoUrl: String
    lateinit var progressDialog: ProgressDialog
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadVideo(uri, REEL_FOLDER, progressDialog) { url ->
                if (url != null) {
                    videoUrl = url
                    Toast.makeText(this, "Video selected successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this@ReelActivity, HomeActivity::class.java))
            finish()
        }

        progressDialog = ProgressDialog(this)

        binding.selectReel.setOnClickListener {
            launcher.launch("video/*")
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@ReelActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val user: User = it.toObject<User>()!!

                    val reel = Reel(
                        reelURL = videoUrl,
                        caption = binding.caption.editText?.text.toString(),
                        profileLink = user.image.toString(), // Use the `image` field for profile picture URL
                        time = System.currentTimeMillis()
                    )

                    // Save reel to global REEL collection
                    Firebase.firestore.collection(REEL).document().set(reel).addOnSuccessListener {
                        Toast.makeText(this, "Reel posted successfully!", Toast.LENGTH_SHORT).show()
                        // Save reel to the user's personal REEL collection
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + REEL)
                            .document()
                            .set(reel)
                            .addOnSuccessListener {
                                startActivity(Intent(this@ReelActivity, HomeActivity::class.java))
                                finish()
                            }
                    }
                }
        }
    }
}
