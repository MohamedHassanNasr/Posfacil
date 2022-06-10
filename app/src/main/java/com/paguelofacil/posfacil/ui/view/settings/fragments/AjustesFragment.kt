package com.paguelofacil.posfacil.ui.view.settings.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseFragment
import com.paguelofacil.posfacil.databinding.FragmentAjustesBinding
import com.paguelofacil.posfacil.ui.view.home.viewmodel.HomeViewModel
import com.paguelofacil.posfacil.ui.view.settings.activities.AjustesActivity
import com.paguelofacil.posfacil.util.Constantes.ConstantesView
import timber.log.Timber


class AjustesFragment : BaseFragment() {
    lateinit var binding: FragmentAjustesBinding

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
        binding = FragmentAjustesBinding.inflate(inflater, container, false)

        loadLanguage()
        loadListeners()
        changeTitle(boolean = false)
        return binding.root
    }

    private fun changeTitle(text: String? = null, boolean: Boolean){
        /*val view = layoutInflater.inflate(R.layout.app_bar_home, null)
        val title = view.findViewById<TextView>(R.id.titleApp)

        title.text = "Ajustes"*/
        val viewModel = ViewModelProvider(requireActivity()).get<HomeViewModel>(modelClass = HomeViewModel::class.java)

        viewModel.setTitle(text ?: ApplicationClass.language.menuSettings, boolean)
        Timber.e("VIEEE ${viewModel.x}")
    }

    private fun initObservers() {
        viewModel.mutableUpdateLanguage.observe(this) {
            if (it) {
                loadLanguage()
            }
        }
    }

    private fun loadLanguage() {
        binding.textViewMiInformacion.text = ApplicationClass.language.myInformation
        binding.textViewCambiarContrasena.text = ApplicationClass.language.changePassword
    }

    private fun loadListeners() {
        binding.llChangePass.setOnClickListener {
            goViewAjustes(ConstantesView.PARAM_PASSWORD)
        }

        binding.llMyInfo.setOnClickListener {
            goViewAjustes(ConstantesView.PARAM_PROFILE)
        }
    }

    private fun goViewAjustes(value: String) {
        val params = Bundle()
        params.putString(ConstantesView.PARAM_FRAGMENT, value)
        val intent = Intent(context, AjustesActivity::class.java)
        intent.putExtras(params)
        startActivityForResult(intent, 200)
    }

}