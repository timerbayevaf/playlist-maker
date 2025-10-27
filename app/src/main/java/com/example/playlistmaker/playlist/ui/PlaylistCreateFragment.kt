package com.example.playlistmaker.playlist.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.text.TextWatcher
import com.google.android.material.snackbar.Snackbar
import androidx.activity.addCallback
import com.example.playlistmaker.R
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.MedialibrariesFragmentCreatePlaylistBinding
import com.example.playlistmaker.playlist.domain.api.PlaylistImageStorage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.FileOutputStream

class PlaylistCreateFragment: Fragment() {
    companion object {
        fun newInstance() = PlaylistCreateFragment()
    }
    private var _binding: MedialibrariesFragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistCreateViewModel>()
    private var urlImageForNewPlaylist: String? = null
    private var isImageSelected = false
    private var isDataChanged = false

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                handleSelectedImage(it)
            } ?: run {}
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

        viewModel.observeImageUrl().observe(viewLifecycleOwner, Observer { url ->
            urlImageForNewPlaylist = url
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
    }

    private fun setupListeners() {
        binding.addImageIcon.setOnClickListener { chooseAndUploadImage() }
        binding.createPlaylist.setOnClickListener { createNewPlaylist() }
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

    private fun createNewPlaylist() {
        val playlistName = binding.playlistName.text.toString()
        viewModel.renameImageFile(playlistName)

        lifecycleScope.launch {
            viewModel.createNewPlaylist(
                playlistName,
                binding.playlistDescription.text.toString(),
                urlImageForNewPlaylist,
                emptyList(),
            )
            viewModel.getImageUrlFromStorage(playlistName)
            showToastPlaylistCreated(playlistName)

            val fromPlayer = arguments?.getBoolean("from_player", false) ?: false
            val args = Bundle().apply {
                putBoolean("from_player", fromPlayer)
            }
            setFragmentResult("playlist_created", args)
            findNavController().navigateUp()
        }
    }

    // --- Проверка при выходе
    private fun handleBackPressed() {
        if (isDataChanged || isImageSelected) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(),R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.exit_creation_title))
            .setMessage(getString(R.string.exit_creation_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.finish)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showToastPlaylistCreated(playlistName: String) {
        val message = getString(R.string.playlist_created, playlistName)

        val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)

        // применяем цвета вручную, если хотим гарантировать одинаковое поведение на старых Android
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

        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snackbar.show()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val file = PlaylistImageStorage.getTemporaryImageFile(requireContext())
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        inputStream?.close()
        outputStream.close()
    }
}