package com.example.reto2grupo1

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.reto2grupo1.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView: TextView = findViewById(R.id.tituloInicio)

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 2000

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation) {
                IntentCall()
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })


        textView.visibility = View.VISIBLE

        textView.startAnimation(fadeIn)
        val logoAnimation = findViewById<ImageView>(R.id.logoElorrieta)
        logoAnimation.setImageResource(R.drawable.logoanimation)
        val runningVessel = logoAnimation.drawable as AnimationDrawable
        runningVessel.start()
    }

    fun IntentCall(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}