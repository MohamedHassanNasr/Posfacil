package com.paguelofacil.posfacil.ui.view.home.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityHomeBinding
import com.paguelofacil.posfacil.model.SessionStatus
import com.paguelofacil.posfacil.ui.view.home.viewmodel.HomeViewModel
import com.paguelofacil.posfacil.util.Constantes.AppConstants.Companion.IDIOMA_ESPANOL
import com.paguelofacil.posfacil.util.Constantes.AppConstants.Companion.IDIOMA_INGLES
import com.paguelofacil.posfacil.util.KeyboardUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    var selectIdioma: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getDataUser()

        setSupportActionBar(binding.appBarHome.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        drawerLayout.addDrawerListener(MyDrawerListener())

        loadListeners(navView)

        initObservers()
        drawArrows(navView)
        loadLanguage()

        val navController = findNavController(R.id.nav_host_fragment_content_home)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_transactions, R.id.nav_reports, R.id.nav_settings,
                R.id.nav_support
            ), drawerLayout
        )

        viewModel.titleObserver.observe(this){
            Timber.e("BIEBIEBIEBIE ${it.first}")
            if (it.second){
                binding.appBarHome.titleApp.text = "Panel de cobro"//ApplicationClass.language.panelCobro
            }else{
                binding.appBarHome.titleApp.text = it.first
            }
        }

        navView.setNavigationItemSelectedListener {
            Timber.e("SJJSJSJS ${it.title}")
            val view = layoutInflater.inflate(R.layout.app_bar_home, null)
            val title = view.findViewById<TextView>(R.id.titleApp)

            title.text = it.title
            true
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    private fun loadLanguage() {
        val menuNav = binding.navView.menu
        val itemInicio = menuNav.findItem(R.id.nav_home)
        val itemTransacciones = menuNav.findItem(R.id.nav_transactions)
        val itemInformes = menuNav.findItem(R.id.nav_reports)
        val itemAjustes = menuNav.findItem(R.id.nav_settings)
        val itemSoporte = menuNav.findItem(R.id.nav_support)
        val itemCerrarSesion = menuNav.findItem(R.id.nav_sign_off)

        itemInicio.title = ApplicationClass.language.menuHome
        itemTransacciones.title = ApplicationClass.language.menuTransactions
        itemInformes.title = ApplicationClass.language.informe
        itemAjustes.title = ApplicationClass.language.menuSettings
        itemSoporte.title = ApplicationClass.language.menuSupport
        itemCerrarSesion.title = ApplicationClass.language.menuCerrarSession

        Timber.e("CHECKED ${itemInicio.isChecked} ${itemTransacciones.isChecked} ${itemInformes.isChecked} ${itemAjustes.isChecked} ${itemSoporte.isChecked}")
    }

    private fun initObservers() {
        var isGoToIntro = false

        viewModel.liveDataUser.observe(this) {
            binding.appBarHome.textView.text = it.getInitiales()
        }

        viewModel.titleObserver.observe(this){
            if (it.second){
                binding.appBarHome.titleApp.text = "Panel de cobro"
            }else{
                binding.appBarHome.titleApp.text = it.first
            }
        }

        viewModel.liveDataLanguageResponse.observe(this) {
            it?.run {
                ApplicationClass.language = this.file
                viewModel.mutableUpdateLanguage.value = true
                loadLanguage()
            }
        }

        lifecycleScope.launch {
            val sessionState = viewModel.sessionStatusTimer.collect(
                collector = {
                    //Timber.e("FLOW! ${it.name}")
                    when (it) {
                        SessionStatus.ACTIVE -> {

                        }
                        SessionStatus.SHOW_IMAGE -> {
                            isGoToIntro = !isGoToIntro
                            if (isGoToIntro and lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                                Timber.e(
                                    "SHOW IMAGE INTRO ${
                                        lifecycle.currentState.isAtLeast(
                                            Lifecycle.State.STARTED
                                        )
                                    }"
                                )
                                if (isGoToIntro) {
                                    Timber.e("SHOW IMAGE INTRO")
                                    KeyboardUtil.hideKeyboard(this@HomeActivity)
                                    delay(500)
                                    val intent =
                                        Intent(this@HomeActivity, IntroActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                        SessionStatus.REFRESH -> {
                            lifecycleScope.launch(Dispatchers.IO) {
                                Timber.e("ONREFRESH")
                                viewModel.refreshUser()
                                viewModel.resetTimerRefresh()
                            }
                        }
                    }
                }
            )
        }
    }


    private fun loadListeners(navView: NavigationView) {

        val cvEspanol = navView.findViewById<CardView>(R.id.cv_idioma_espanol)
        val tvEspanol = navView.findViewById<TextView>(R.id.tv_idioma_espanol)

        val cvIngles = navView.findViewById<CardView>(R.id.cv_idioma_ingles)
        val tvIngles = navView.findViewById<TextView>(R.id.tv_idioma_ingles)

        tvEspanol.text = ApplicationClass.language.menuIdiomaEspanol
        tvIngles.text = ApplicationClass.language.menuIdiomaIngles

        updateButtonsLanguage(cvEspanol, cvIngles, tvIngles, tvEspanol)

        cvIngles.setOnClickListener {
            viewModel.setLanguageDevice(IDIOMA_INGLES)
            viewModel.checkLanguage()
            updateButtonsLanguage(cvEspanol, cvIngles, tvIngles, tvEspanol)
        }

        cvEspanol.setOnClickListener {
            viewModel.setLanguageDevice(IDIOMA_ESPANOL)
            viewModel.checkLanguage()
            updateButtonsLanguage(cvEspanol, cvIngles, tvIngles, tvEspanol)
        }

        //sign Off button
        navView.menu.findItem(R.id.nav_sign_off).setOnMenuItemClickListener {
            goHome()
            true
        }
    }

    private fun updateButtonsLanguage(
        cvEspanol: CardView, cvIngles: CardView, tvIngles: TextView, tvEspanol: TextView
    ) {
        if (viewModel.getLanguageDevice() == IDIOMA_INGLES) {
            cvIngles.setCardBackgroundColor(ContextCompat.getColor(this, R.color.color_6BBE22))
            tvIngles.setTextColor(Color.WHITE);
            cvEspanol.setCardBackgroundColor(Color.WHITE)
            tvEspanol.setTextColor(ContextCompat.getColor(this, R.color.color_7D889B));
            selectIdioma = 1
        } else if (viewModel.getLanguageDevice() == IDIOMA_ESPANOL) {
            cvEspanol.setCardBackgroundColor(ContextCompat.getColor(this, R.color.color_6BBE22))
            tvEspanol.setTextColor(Color.WHITE);
            cvIngles.setCardBackgroundColor(Color.WHITE)
            tvIngles.setTextColor(ContextCompat.getColor(this, R.color.color_7D889B));
            selectIdioma = 0
        }
    }

    private fun goHome(): Boolean {
        finish()
        return false
    }

    private fun drawArrows(navView: NavigationView) {
        navView.menu.forEach {
            if (it.itemId != R.id.nav_sign) {
                it.setActionView(R.layout.menu_image)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private inner class MyDrawerListener : DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {


        }

        override fun onDrawerOpened(drawerView: View) {
            if (KeyboardUtil.isKeyboardOpen(drawerView)) {
                KeyboardUtil.hideKeyboard(this@HomeActivity)
            }
        }

        override fun onDrawerClosed(drawerView: View) {

        }

        override fun onDrawerStateChanged(newState: Int) {

        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        viewModel.resetTimer()
    }

}