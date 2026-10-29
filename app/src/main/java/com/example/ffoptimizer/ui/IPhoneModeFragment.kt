package com.example.ffoptimizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ffoptimizer.databinding.FragmentIphoneModeBinding
import com.example.ffoptimizer.util.AnimationKiller
import com.example.ffoptimizer.util.DpiHelper

class IPhoneModeFragment : Fragment() {

    private var _binding: FragmentIphoneModeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIphoneModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnZeroAnimations.setOnClickListener {
            AnimationKiller.zeroAnimations(requireContext())
            Toast.makeText(requireContext(), "Анимации сброшены в 0 (нужен WRITE_SECURE_SETTINGS)", Toast.LENGTH_LONG).show()
        }

        binding.btnDpi320.setOnClickListener {
            DpiHelper.setDensity(requireContext(), 320)
            Toast.makeText(requireContext(), "Попытка установить DPI 320", Toast.LENGTH_SHORT).show()
        }

        binding.btnDpi360.setOnClickListener {
            DpiHelper.setDensity(requireContext(), 360)
            Toast.makeText(requireContext(), "Попытка установить DPI 360", Toast.LENGTH_SHORT).show()
        }

        binding.btnDpi400.setOnClickListener {
            DpiHelper.setDensity(requireContext(), 400)
            Toast.makeText(requireContext(), "Попытка установить DPI 400", Toast.LENGTH_SHORT).show()
        }

        binding.btnResetDpi.setOnClickListener {
            DpiHelper.resetDensity(requireContext())
            Toast.makeText(requireContext(), "Сброс DPI", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
