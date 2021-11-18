package com.example.gagyeboost.ui.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.gagyeboost.R

abstract class BaseActivity<T : ViewDataBinding>(private val layoutId: Int) : AppCompatActivity() {

    private lateinit var _binding: T
    protected val binding get() = _binding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        _binding = DataBindingUtil.setContentView(this, layoutId)
        _binding.lifecycleOwner = this
        _binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    }
}
