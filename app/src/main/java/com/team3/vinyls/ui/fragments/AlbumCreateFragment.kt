package com.team3.vinyls.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.team3.vinyls.R
import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.core.network.NetworkModule
import com.team3.vinyls.data.models.AlbumCreateDto
import com.team3.vinyls.data.repositories.AlbumRepository
import com.team3.vinyls.data.services.AlbumsService
import com.team3.vinyls.databinding.FragmentAlbumCreateBinding
import com.team3.vinyls.viewmodels.AlbumsViewModel
import java.util.Calendar

class AlbumCreateFragment : Fragment() {

    private var _binding: FragmentAlbumCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AlbumsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val retrofit = NetworkModule.retrofit(ApiConstants.BASE_URL)
                val service = retrofit.create(AlbumsService::class.java)
                val repo = AlbumRepository(service)
                return AlbumsViewModel(repo) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImagePreview()
        setupDatePicker()
        setupButtons()
        observeCreateAlbumResult()
        setupGenreDropdown()
        setupLabelDropdown()
    }

    private fun setupImagePreview() {
        binding.inputCoverUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) loadCoverImage()
        }

        binding.inputCoverUrl.setOnEditorActionListener { _, _, _ ->
            loadCoverImage()
            false
        }
    }

    private fun loadCoverImage() {
        val url = binding.inputCoverUrl.text.toString()

        if (url.isNotBlank()) {
            Glide.with(this)
                .load(url)
                .placeholder(com.team3.vinyls.R.drawable.ic_broken_image)
                .error(com.team3.vinyls.R.drawable.ic_broken_image)
                .into(binding.imgAlbumCover)
        }
    }

    private fun setupDatePicker() {
        binding.inputReleaseDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val context = ContextThemeWrapper(requireContext(), R.style.MyDatePickerDialog)

            val dialog = DatePickerDialog(
                context,
                R.style.MyDatePickerDialog,
                { _, year, month, day ->
                    val date = "%04d-%02d-%02d".format(year, month + 1, day)
                    binding.inputReleaseDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            dialog.show()
        }
    }

    private fun setupGenreDropdown() {
        val genres = listOf("Classical", "Salsa", "Rock", "Folk")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_genre_dropdown,   // 👈 our custom row with white text
            genres
        )
        binding.inputGenre.setAdapter(adapter)

        binding.inputGenre.setOnClickListener {
            binding.inputGenre.showDropDown()
        }
    }

    private fun setupLabelDropdown() {
        val labels = listOf(
            "Sony Music",
            "EMI",
            "Discos Fuentes",
            "Elektra",
            "Fania Records"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_genre_dropdown, // reuse SAME layout for white text
            labels
        )

        binding.inputLabel.setAdapter(adapter)

        binding.inputLabel.setOnClickListener {
            binding.inputLabel.showDropDown()
        }
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            saveAlbum()
        }
    }

    private fun saveAlbum() {
        val album = AlbumCreateDto(
            name = binding.inputTitle.text.toString(),
            cover = binding.inputCoverUrl.text.toString(),
            recordLabel = binding.inputLabel.text.toString(),
            releaseDate = binding.inputReleaseDate.text.toString(),
            genre = binding.inputGenre.text.toString(),
            description = binding.inputDescription.text.toString()
        )

        if (album.name.isBlank()) {
            Toast.makeText(requireContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable button to avoid double taps
        binding.btnSave.isEnabled = false
        viewModel.createAlbum(album)
    }

    private fun observeCreateAlbumResult() {
        viewModel.createAlbumResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess { createdAlbum ->
                Toast.makeText(requireContext(), "Álbum creado con éxito", Toast.LENGTH_SHORT).show()

                val action = AlbumCreateFragmentDirections
                    .actionAlbumsCreateFragmentToAlbumDetailFragment(createdAlbum.id.toString())

                findNavController().navigate(action)

                viewModel.clearCreateResult()
            }?.onFailure {
                Toast.makeText(requireContext(), "Error al crear álbum", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}