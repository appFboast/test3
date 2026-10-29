package com.example.ffoptimizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ffoptimizer.MainActivity
import com.example.ffoptimizer.databinding.FragmentNetworkBinding
import com.example.ffoptimizer.util.WifiHighPerf

class NetworkFragment : Fragment() {

    private var _binding: FragmentNetworkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNetworkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartVpn.setOnClickListener {
            (activity as? MainActivity)?.startVpn()
        }

        binding.btnStopVpn.setOnClickListener {
            (activity as? MainActivity)?.stopVpn()
        }

        binding.btnWifiHighPerf.setOnClickListener {
            WifiHighPerf.enable(requireContext())
            Toast.makeText(requireContext(), "Wi-Fi High Performance включён", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
