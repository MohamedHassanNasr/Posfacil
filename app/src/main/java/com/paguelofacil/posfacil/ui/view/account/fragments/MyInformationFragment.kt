package com.paguelofacil.posfacil.ui.view.account.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.data.database.entity.UserEntity
import com.paguelofacil.posfacil.databinding.FragmentMyInformationBinding
import com.paguelofacil.posfacil.ui.view.account.viewmodel.MyInformationViewModel


class MyInformationFragment : Fragment() {

    private val viewModel: MyInformationViewModel by activityViewModels()
    lateinit var binding: FragmentMyInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInformationBinding.inflate(inflater, container, false)

        getDataUser()
        loadLanguage()
        initListener()
        return binding.root
    }

    private fun initListener(){
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun loadLanguage() {
        binding.tvTitle.text = ApplicationClass.language.myInformation
        binding.tvFirstNameHint.text = ApplicationClass.language.lastName
        binding.tvUserName.text = ApplicationClass.language.username
        binding.tvNumberPhoneTitle.text = ApplicationClass.language.numberPhone
        binding.tvCorreoTitle.text = ApplicationClass.language.email
    }

    private fun getDataUser() {
        viewModel.getDataUser()
    }

    private fun initObservers() {
        viewModel.liveDataUser.observe(this) {
            fillDataUser(it)
        }
    }

    private fun fillDataUser(dataUser: UserEntity) {
        binding.tvUserFullName.text = dataUser.userName
        binding.tvUserName.text = dataUser.getFullName()
        binding.tvUserEmail.text = dataUser.email
        binding.etFirstName.text = dataUser.firstName
        binding.etUserName.text = dataUser.userName
        binding.tvNumberPhone.text = dataUser.phone
        binding.tvCorreo.text = dataUser.email

        binding.includeBadgeUser.textviewInitiales.text = dataUser.getInitiales()
    }
}