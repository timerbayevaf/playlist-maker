package com.example.playlistmaker.playlist.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MedialibrariesFragmentCreatePlaylistBinding
import com.example.playlistmaker.playlist.domain.api.PlaylistImageStorage
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.mappers.toDomain
import com.example.playlistmaker.playlist.presentation.models.PlaylistUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.FileOutputStream

class PlaylistEditFragment : Fragment() {
    companion object {
        const val ARG_PLAYLIST = "playlist_arg"
    }
    private var _binding: MedialibrariesFragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistCreateViewModel>()

    private var currentPlaylist: Playlist? = null
    private var isDataChanged = false
    private var isImageSelected = false
    private var urlImageForPlaylist: String? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { handleSelectedImage(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MedialibrariesFragmentCreatePlaylistBinding.inflate(inflater, container, false)

        setupToolbar()
        setupTextChangeListener()
        setupListeners()
        setupBackPressedHandler()

        currentPlaylist = getPlaylistFromArgs()
        currentPlaylist?.let { bindPlaylistData(it) }

        return binding.root
    }

    private fun bindPlaylistData(playlist: Playlist) {
        binding.playlistName.setText(playlist.name)
        binding.playlistDescription.setText(playlist.description)
        if (!playlist.imageUrl.isNullOrEmpty()) {
            binding.playlistImage.setImageURI(Uri.parse(playlist.imageUrl))
            binding.addImageIcon.visibility = View.GONE
        } else {
            binding.playlistImage.setImageResource(R.drawable.track_placeholder)
            binding.addImageIcon.visibility = View.VISIBLE
        }
        binding.createPlaylist.text = getString(R.string.save)
        binding.createPlaylist.isEnabled = !playlist.name.isNullOrBlank()
        binding.toolbar.title = ""
        binding.addImageIcon.visibility = View.GONE
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            handleBackPressed()
        }
    }

    private fun setupBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPressed()
        }
    }

    private fun setupTextChangeListener() {
        binding.playlistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.createPlaylist.isEnabled = s?.isNotEmpty() == true
                isDataChanged = true
            }
        })
        binding.playlistDescription.addTextChangedListener {
            isDataChanged = true
        }
    }

    private fun setupListeners() {
        binding.playlistImage.setOnClickListener { chooseAndUploadImage() }
        binding.addImageIcon.setOnClickListener { chooseAndUploadImage() }
        binding.createPlaylist.setOnClickListener { saveChanges() }
    }

    private fun handleSelectedImage(uri: Uri) {
        binding.playlistImage.setImageURI(uri)
        binding.addImageIcon.visibility = View.GONE
        isImageSelected = true
        isDataChanged = true
        saveImageToPrivateStorage(uri)
    }

    private fun chooseAndUploadImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val file = PlaylistImageStorage.getTemporaryImageFile(requireContext())
        requireActivity().contentResolver.openInputStream(uri).use { input ->
            FileOutputStream(file).use { output ->
                BitmapFactory.decodeStream(input).compress(Bitmap.CompressFormat.JPEG, 30, output)
            }
        }
    }


    private fun saveChanges() {
        val updatedName = binding.playlistName.text.toString()
        val updatedDescription = binding.playlistDescription.text.toString()

        lifecycleScope.launch {
            currentPlaylist?.let { playlist ->
                var newImageUrl = playlist.imageUrl

                if (isImageSelected) {
                    // ✅ Удаляем старую картинку, если она была
                    PlaylistImageStorage.deleteImage(requireContext(), playlist.imageUrl)

                    // ✅ Переносим temp → новый уникальный файл (UUID)
                    newImageUrl = PlaylistImageStorage.moveTempToFinal(requireContext()) ?: newImageUrl
                }

                // создаём обновлённую копию плейлиста
                val updatedPlaylist = playlist.copy(
                    name = updatedName,
                    description = updatedDescription,
                    imageUrl = newImageUrl
                )

                // сохраняем через интерактор
                viewModel.updatePlaylist(updatedPlaylist)

                showSavedSnackbar()
                findNavController().navigateUp()
            }
        }
        setFragmentResult("playlist_edited",Bundle())
    }


    private fun showSavedSnackbar() {
        val snackbar = Snackbar.make(requireView(), getString(R.string.playlist_updated), Snackbar.LENGTH_SHORT)
        val isNightMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        if (isNightMode) {
            snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
            snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        } else {
            snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.black))
            snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        snackbar.show()
    }

    private fun handleBackPressed() {
        if (isDataChanged) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.exit_edit_title))
            .setMessage(getString(R.string.exit_edit_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.finish)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun getPlaylistFromArgs(): Playlist? {
        val playlistUI: PlaylistUI? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_PLAYLIST, PlaylistUI::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_PLAYLIST)
        }
        return playlistUI?.toDomain()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
