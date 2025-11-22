package com.team3.vinyls.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.team3.vinyls.R
import com.team3.vinyls.core.network.ApiConstants
import com.team3.vinyls.core.network.NetworkModule
import com.team3.vinyls.data.repositories.MusicianRepository
import com.team3.vinyls.data.services.MusiciansService
import com.team3.vinyls.databinding.FragmentMusicianDetailBinding
import com.team3.vinyls.viewmodels.MusiciansDetailViewModel

class MusicianDetailFragment : Fragment() {

    private var _binding: FragmentMusicianDetailBinding? = null
    private val binding get() = _binding!!

    private val args: MusicianDetailFragmentArgs by navArgs()

    private val viewModel: MusiciansDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val retrofit = NetworkModule.retrofit(ApiConstants.BASE_URL)
                val service = retrofit.create(MusiciansService::class.java)
                val repository = MusicianRepository(service)
                return MusiciansDetailViewModel(repository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicianDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val musicianId = args.musicianId.toInt()

        viewModel.loadMusicianDetail(musicianId)

        viewModel.musician.observe(viewLifecycleOwner) { musician ->

            // Nombre
            binding.txtArtistName.text = musician.name

            // Fecha nacimiento
            binding.txtBirthDate.text =
                musician.birthDate?.let { "Nació en ${it.take(4)}" } ?: "Fecha no disponible"

            // Imagen
            Glide.with(requireContext())
                .load(
                    musician.image
                        ?.toUri()
                        ?.buildUpon()
                        ?.scheme("https")
                        ?.build()
                )
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_broken_image)
                )
                .centerCrop()
                .into(binding.imgArtistPhoto)

            // Descripción
            binding.txtArtistDescription.text =
                musician.description ?: "Sin descripción disponible"

            // ÁLbumes del artista
            binding.albumsContainer.removeAllViews()

            val albums = musician.albums

            if (albums.isNullOrEmpty()) {
                binding.albumsContainer.addView(newItem("Sin álbumes asociados"))
            } else {
                albums.forEach { album ->
                    val row = newItem("• ${album.name} (${album.releaseDate.take(4)})")
                    binding.albumsContainer.addView(row)
                }
            }

            // Premios del artista
            binding.prizesContainer.removeAllViews()

            val prizes = musician.performerPrizes

            if (prizes.isNullOrEmpty()) {
                binding.prizesContainer.addView(newItem("Sin premios"))
            } else {
                prizes.forEach { prize ->
                    val text =
                        "• Premio ID ${prize.id}" +
                                (prize.premiationDate?.let { " (${it.take(4)})" } ?: "")
                    binding.prizesContainer.addView(newItem(text))
                }
            }
        }
    }

    private fun newItem(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            textSize = 15f
            setPadding(0, 8, 0, 8)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}