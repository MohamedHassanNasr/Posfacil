package com.paguelofacil.posfacil.ui.view.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseActivity
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.databinding.ActivityMainBinding
import com.paguelofacil.posfacil.ui.view.account.activities.LoginActivity
import com.paguelofacil.posfacil.ui.view.adapters.AutoSliderImageAdapter
import com.paguelofacil.posfacil.ui.view.adapters.SliderItem
import com.paguelofacil.posfacil.ui.view.main.viewmodel.MainViewModel
import com.paguelofacil.posfacil.util.KeyboardUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_send_receipt_payment.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var viewPager2: ViewPager2

    lateinit var binding: ActivityMainBinding
    private val sliderHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNeptune()
        setBaseViewModel(viewModel)
        initObservers()

        viewModel.checkLanguage()

        Timber.e("ANTES DE HIDE main")
        KeyboardUtil.hideKeyboard(this)
        Timber.e("ANTES DE HIDE from view main")
        KeyboardUtil.hideKeyboard(this, view)

        binding.btnGoLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onExceptionData(requestCode: Int, exception: ApiError, data: Any?) {
        TODO("Not yet implemented")
    }

    private fun initObservers() {
        viewModel.liveDataLanguageResponse.observe(this) {
            it?.run {
                ApplicationClass.language = this.file
                initSlider()
                loadLanguage()
            }
        }
    }

    private fun loadLanguage() {
        binding.txtOnBoardingTitle.text = ApplicationClass.language.textSplashOne
        binding.btnGoLogin.text = ApplicationClass.language.logIn
    }

    private fun initSlider() {
        viewPager2 = findViewById(R.id.view_pager)
        val sliderItems = mutableListOf(
            SliderItem(R.drawable.onboarding1),
            SliderItem(R.drawable.onboarding2),
            SliderItem(R.drawable.onboarding3)
        )

        viewPager2.adapter = AutoSliderImageAdapter(sliderItems, viewPager2)

        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(30))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.25f
        }
        viewPager2.setPageTransformer(compositePageTransformer)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when {
                    position % 3 == 0 -> {
                        var currentProgressOne = 0
                        binding.pointTwo.progress = 0
                        binding.pointThird.progress = 0
                        binding.txtOnBoardingTitle.text = ApplicationClass.language.textSplashOne
                        lifecycleScope.launch {
                            for (i in 0..100){
                                currentProgressOne += i
                                binding.pointOne.progress = i
                                delay(50)
                            }
                        }
                        /*binding.pointTwo.setBackgroundColor(this@MainActivity.resources.getColor(R.color.white))
                        binding.pointThird.setBackgroundColor(this@MainActivity.resources.getColor(R.color.white))*/
                    }
                    position % 3 == 1 -> {
                        var currentProgressTwo = 0
                        binding.pointThird.progress = 0
                        binding.txtOnBoardingTitle.text = ApplicationClass.language.textSplashTwo
                        lifecycleScope.launch {
                            for (i in 0..100){
                                currentProgressTwo += i
                                binding.pointTwo.progress = i
                                delay(50)
                            }
                        }
                        /*binding.pointTwo.setBackgroundColor(this@MainActivity.resources.getColor(R.color.greenLight))
                        binding.pointThird.setBackgroundColor(this@MainActivity.resources.getColor(R.color.white))*/
                    }
                    position % 3 == 2 -> {
                        var currentProgressThird = 0
                        binding.txtOnBoardingTitle.text = ApplicationClass.language.textSplashThree
                        lifecycleScope.launch {
                            for (i in 0..100){
                                currentProgressThird += i
                                binding.pointThird.progress = i
                                delay(50)
                            }
                        }
                        /*binding.pointTwo.setBackgroundColor(this@MainActivity.resources.getColor(R.color.greenLight))
                        binding.pointThird.setBackgroundColor(this@MainActivity.resources.getColor(R.color.greenLight))*/
                    }
                }
                sliderHandler.removeCallbacks(runnable)
                sliderHandler.postDelayed(runnable, 5500)
            }
        })

    }

    private val runnable = Runnable {
        if (viewPager2.currentItem < 2){
            viewPager2.currentItem = viewPager2.currentItem + 1
        }
    }
}