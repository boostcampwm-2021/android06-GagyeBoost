package com.example.gagyeboost.ui.intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.ActivityIntroBinding
import com.example.gagyeboost.ui.MainActivity
import com.example.gagyeboost.ui.base.BaseActivity

class IntroActivity : BaseActivity<ActivityIntroBinding>(R.layout.activity_intro) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.baseColor))
        window.statusBarColor = ContextCompat.getColor(this, R.color.baseColor)

        val bounce = AnimationUtils.loadAnimation(this, R.anim.overshoot_from_top_to_bottom)
        val leftToRight = AnimationUtils.loadAnimation(this, R.anim.appear_from_left_to_right)
        val rightToLeft = AnimationUtils.loadAnimation(this, R.anim.appear_from_right_to_left)

        binding.ivIcon.startAnimation(bounce)
        binding.ivLeftWing.startAnimation(rightToLeft)
        binding.ivRightWing.startAnimation(leftToRight)

        Handler(mainLooper).postDelayed(2000) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
