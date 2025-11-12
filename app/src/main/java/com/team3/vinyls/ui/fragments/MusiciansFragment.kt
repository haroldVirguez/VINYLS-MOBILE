package com.team3.vinyls.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.team3.vinyls.databinding.FragmentMusiciansBinding
import com.team3.vinyls.viewmodels.MusiciansViewModel
import com.team3.vinyls.core.network.NetworkModule
import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.data.service.MusiciansService
import com.team3.vinyls.data.repository.MusicianRepository
import com.team3.vinyls.ui.adapters.MusiciansAdapter

class MusiciansFragment : Fragment() {

    private var _binding: FragmentMusiciansBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MusiciansViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val retrofit = NetworkModule.retrofit(ApiConstants.BASE_URL)
                val service = retrofit.create(MusiciansService::class.java)
                val repository = MusicianRepository(service)
                return MusiciansViewModel(repository) as T
            }
        }
    }

    private val adapter = MusiciansAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusiciansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerMusicians.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMusicians.adapter = adapter

        viewModel.musicians.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // opcional: mostrar loader
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // opcional: mostrar mensaje
            }
        }

        binding.btnViewAll.setOnClickListener {
            viewModel.refresh()
        }
    }
}

