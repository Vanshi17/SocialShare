package com.projects.SocialShare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.projects.SocialShare.databinding.ActivityLoginBinding
import com.projects.SocialShare.models.User

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.loginBtn.setOnClickListener {
            if (binding.email.editText?.text.toString()
                    .equals("") or binding.pass.editText?.text.toString().equals("")
            ) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please fill all the details",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var User = User(
                    binding.email.editText?.text.toString(),
                    binding.pass.editText?.text.toString()
                )
                Firebase.auth.signInWithEmailAndPassword(User.email!!, User.password!!)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                                startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                                finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                it.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        binding.newAccount.setOnClickListener{
            startActivity(Intent(this@LoginActivity,SignUpActivity::class.java))
            finish()
        }
    }
}