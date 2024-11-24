package com.projects.SocialShare

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.projects.SocialShare.databinding.ActivitySignUpBinding
import com.projects.SocialShare.models.User
import com.projects.SocialShare.utils.USER_NODE
import com.projects.SocialShare.utils.USER_PROFILE_FOLDER
import com.projects.SocialShare.utils.uploadImage
import com.squareup.picasso.Picasso


class SignUpActivity : AppCompatActivity() {

    val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER)
            {
                if (it != null) {
                    user.image = it
                    binding.profileImage.setImageURI((uri))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val text =
            "<font color=#FF000000>Already have an account</font> <font color=#1E88E5>Login?</font>"
        binding.login.setText(Html.fromHtml(text))
        user = User()

        if (intent.hasExtra("MODE")) {
            if (intent.getIntExtra("MODE", -1) == 1) {
                binding.signUpBtn.text = "Update Profile"
                binding.login.visibility = View.INVISIBLE
                binding.email.editText?.apply {
                    setText(user.email)
                    isEnabled = false // Disable user interaction
                    // Prevent focus
                    // Remove background to look like plain text
                    setTextColor(resources.getColor(android.R.color.black)) // Set visible text color
                }

                // Hide password field
                binding.password.visibility = View.INVISIBLE

                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener {
                        this.user =
                            it.toObject(com.projects.SocialShare.models.User::class.java)!!
                        if (!user.image.isNullOrEmpty()) {
                            Picasso.get().load(user.image).into(binding.profileImage)
                        }

                        binding.name.editText?.setText(user.name)
                        binding.email.editText?.setText(user.email)
//                        binding.password.editText?.setText(user.password)
                    }
            }
        }
        binding.signUpBtn.setOnClickListener {
            if (intent.hasExtra("MODE")) {
                if (intent.getIntExtra("MODE", -1) == 1) {
                    // Update user profile data
                    user.name = binding.name.editText?.text.toString()
                    user.email = binding.email.editText?.text.toString()
                    user.password = binding.password.editText?.text.toString()

                    // Save updated data to Firestore
                    Firebase.firestore.collection(USER_NODE)
                        .document(Firebase.auth.currentUser!!.uid)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this@SignUpActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            // Return to ProfileFragment or HomeActivity
                            startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@SignUpActivity,
                                "Update failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            } else {
                // Handle sign-up logic for new users
                if (binding.name.editText?.text.toString().isEmpty() ||
                    binding.email.editText?.text.toString().isEmpty() ||
                    binding.password.editText?.text.toString().isEmpty()
                ) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Please fill all information",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.email.editText?.text.toString(),
                        binding.password.editText?.text.toString()
                    ).addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            user.name = binding.name.editText?.text.toString()
                            user.password = binding.password.editText?.text.toString()
                            user.email = binding.email.editText?.text.toString()
                            Firebase.firestore.collection(USER_NODE)
                                .document(Firebase.auth.currentUser!!.uid)
                                .set(user)
                                .addOnSuccessListener {
                                    startActivity(
                                        Intent(
                                            this@SignUpActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                result.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }
        binding.login.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }
    }
}




