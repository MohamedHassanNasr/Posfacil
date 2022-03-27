package com.paguelofacil.posfacil.ui.view.home.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityHomeBinding
import com.paguelofacil.posfacil.ui.view.MainActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.TransactionsFragment
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.VerificarCobroFragment
import com.paguelofacil.posfacil.util.KeyboardUtil


class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    var selectIdioma:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        drawerLayout.addDrawerListener(MyDrawerListener())

        loadListeners(navView)


        drawArrows(navView)

        val navController = findNavController(R.id.nav_host_fragment_content_home)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_transactions, R.id.nav_reports,R.id.nav_settings,
                R.id.nav_support
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



    }

    private fun loadListeners(navView: NavigationView) {


        //language buttons

        val cvEspanol=navView.findViewById<CardView>(R.id.cv_idioma_espanol)
        val tvEspanol=navView.findViewById<TextView>(R.id.tv_idioma_espanol)

        val cvIngles=navView.findViewById<CardView>(R.id.cv_idioma_ingles)
        val tvIngles=navView.findViewById<TextView>(R.id.tv_idioma_ingles)


        cvIngles.setOnClickListener {
            if (selectIdioma==0)
            {

                cvIngles.setCardBackgroundColor(ContextCompat.getColor(this,R.color.color_6BBE22))
                tvIngles.setTextColor(Color.WHITE);

                cvEspanol.setCardBackgroundColor(Color.WHITE)
                tvEspanol.setTextColor(ContextCompat.getColor(this,R.color.color_7D889B));

                selectIdioma=1
            }

        }

        cvEspanol.setOnClickListener {
            if (selectIdioma==1)
            {

                cvEspanol.setCardBackgroundColor(ContextCompat.getColor(this,R.color.color_6BBE22))
                tvEspanol.setTextColor(Color.WHITE);

                cvIngles.setCardBackgroundColor(Color.WHITE)
                tvIngles.setTextColor(ContextCompat.getColor(this,R.color.color_7D889B));

                selectIdioma=0
            }

        }


        //sign Off button
        navView.menu.findItem(R.id.nav_sign_off).setOnMenuItemClickListener {
            goHome()
            true
        }


    }


    private fun goHome():Boolean
    {
        val intent= Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        return false
    }



    private fun drawArrows(navView:NavigationView) {

        navView.menu.forEach {

            if(it.itemId!=R.id.nav_sign)
            {
                it.setActionView(R.layout.menu_image)
            }


        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private inner class MyDrawerListener(): DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {


        }

        override fun onDrawerOpened(drawerView: View) {

           if(KeyboardUtil.isKeyboardOpen(drawerView))
            {
              KeyboardUtil.hideKeyboard(this@HomeActivity)
            }

        }

        override fun onDrawerClosed(drawerView: View) {

        }

        override fun onDrawerStateChanged(newState: Int) {

        }
    }


}