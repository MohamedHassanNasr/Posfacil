package com.paguelofacil.posfacil.ui.view.settings.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.databinding.FragmentSupportBinding
import com.paguelofacil.posfacil.repository.ConfigurationsRepo
import com.paguelofacil.posfacil.ui.view.home.viewmodel.HomeViewModel
import com.paguelofacil.posfacil.ui.view.settings.activities.AboutActivity
import org.json.JSONObject
import timber.log.Timber

class SupportFragment : BaseFragment() {
    lateinit var binding: FragmentSupportBinding
    private var urlTerminos: String = ""
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState)

        setBaseViewModel(viewModel)

        initObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSupportBinding.inflate(inflater, container, false)
        getDataLegal()
        loadListeners()
        loadLanguage()
        changeTitle(boolean = false)
        return binding.root
    }

    private fun changeTitle(text: String? = null, boolean: Boolean){
        /*val view = layoutInflater.inflate(R.layout.app_bar_home, null)
        val title = view.findViewById<TextView>(R.id.titleApp)

        title.text = "Ajustes"*/
        val viewModel = ViewModelProvider(requireActivity()).get<HomeViewModel>(modelClass = HomeViewModel::class.java)

        viewModel.setTitle(text ?: ApplicationClass.language.menuSupport, boolean)
        Timber.e("VIEEE ${viewModel.x}")
    }

    private fun getDataLegal() {
        val systemsParam = ConfigurationsRepo.getSystemParamsLocal()
        val dataScreen = systemsParam._url_terms
        val json = JSONObject(dataScreen)
        urlTerminos = json.getString("es")
    }

    private fun loadListeners() {
        binding.llAboutApp.setOnClickListener {
            val intent = Intent(context, AboutActivity::class.java)
            startActivityForResult(intent, 100)
        }

        binding.llLegalApp.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(urlTerminos)
            startActivity(i)
        }
    }

    private fun initObservers() {
        viewModel.mutableUpdateLanguage.observe(this) {
            if (it) {
                loadLanguage()
            }
        }
    }

    private fun loadLanguage() {
        binding.textViewTitle.text = ApplicationClass.language.titleSupport
        binding.textViewAcercaDe.text = ApplicationClass.language.aboutSupport
        binding.textViewLegal.text = ApplicationClass.language.legalSupport
    }

}