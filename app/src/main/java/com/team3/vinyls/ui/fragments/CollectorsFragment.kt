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
import com.team3.vinyls.databinding.FragmentCollectorsBinding
import com.team3.vinyls.viewmodels.CollectorsViewModel
import com.team3.vinyls.core.network.NetworkModule
import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.data.services.CollectorsService
import com.team3.vinyls.data.repositories.CollectorRepository
import com.team3.vinyls.ui.adapters.CollectorsAdapter
import com.team3.vinyls.ui.models.CollectorUiModel

class CollectorsFragment : Fragment() {

    private var _binding: FragmentCollectorsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollectorsViewModel by viewModels<CollectorsViewModel> {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val retrofit = NetworkModule.retrofit(ApiConstants.BASE_URL)
                val service = retrofit.create(CollectorsService::class.java)
                val repository = CollectorRepository(service)
                return CollectorsViewModel(repository) as T
            }
        }
    }

    private val adapter = CollectorsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerCollectors.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCollectors.adapter = adapter

        adapter.onCollectorClick = { collector ->
            // por ahora no hay pantalla detail; se puede extender
        }

        viewModel.collectors.observe(viewLifecycleOwner) { list: List<CollectorUiModel> ->
            adapter.submitList(list)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading: Boolean ->
            // opcional: mostrar loader
        }

        viewModel.error.observe(viewLifecycleOwner) { error: String? ->
            error?.let {
                // opcional: mostrar mensaje
            }
        }

        binding.btnViewAll.setOnClickListener {
            viewModel.refresh()
        }
    }
}
