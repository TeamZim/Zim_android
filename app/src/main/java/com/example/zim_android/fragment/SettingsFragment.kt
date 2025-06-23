package com.example.zim_android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zim_android.R
import com.example.zim_android.databinding.SettingsFragmentBinding

class SettingsFragment: Fragment(R.layout.settings_fragment){

    // 뷰바인딩 사용
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.commonHeader.tvTitle.text = "설정"
        binding.commonHeader.settingsBtn.visibility = View.GONE
        binding.commonHeader.exitBtn.visibility = View.VISIBLE

        binding.commonHeader.exitBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mypageFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
