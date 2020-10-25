package ru.strorin.shareE.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.strorin.shareE.R
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import ru.strorin.shareE.BuildConfig
import ru.strorin.shareE.permission.PermissionUtils
import android.widget.Toast
import android.graphics.BitmapFactory
import android.app.Activity
import java.lang.Exception
import java.lang.IllegalStateException


class SharePostFragment: Fragment() {

    private lateinit var shareButton: LinearLayout

    companion object {
        fun newInstance(): SharePostFragment {
            return SharePostFragment()
        }

        private const val PERMISSIONS_REQUEST_READ_FILES_STANDARD = 100
        private const val OPEN_SETTINGS_REQUEST = 200
        private const val PICK_IMAGE = 300
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_post_fragment, container, false)
        shareButton = view.findViewById(R.id.share_button)
        return view
    }

    override fun onStart() {
        super.onStart()
        shareButton.setOnClickListener {
            context?.let {
                checkForPermission(it)
            }
        }
    }

    private fun chooseImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    private fun showBottomShareDialogIfPossible(context: Context, data: Intent?){
        try {
            val imageUri = data?.data ?: throw IllegalStateException()
            val imageStream = context.contentResolver.openInputStream(imageUri)
            val selectedImage = BitmapFactory.decodeStream(imageStream) ?: throw IllegalStateException()
            val bottomInfoDialog = BottomShareDialog.newInstance(imageUri)
            if (isAdded) {
                bottomInfoDialog.show(parentFragmentManager, "BOTTOM")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, getString(R.string.str_error_open_file_toast), Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE) {
            context?.let {
                if (resultCode == Activity.RESULT_OK) {
                    showBottomShareDialogIfPossible(it, data)
                } else {
                    Toast.makeText(it, getString(R.string.str_no_image_picked_toast), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showNeverAskAgainDialog() {
        context?.let {
            AlertDialog
                .Builder(it)
                .setTitle(R.string.str_need_permission_title)
                .setMessage(R.string.str_need_permission_message)
                .setPositiveButton(R.string.str_go_to_settings_button
                ) { _, _ -> openApplicationSettings() }
                .create()
                .show()
        }
    }

    private fun openApplicationSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
        )
        startActivityForResult(appSettingsIntent, OPEN_SETTINGS_REQUEST)
    }

    private fun checkForPermission(ctx: Context) {

        if (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if (PermissionUtils.getRationaleDisplayStatus(ctx, Manifest.permission.READ_EXTERNAL_STORAGE)
                && !shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showNeverAskAgainDialog()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_READ_FILES_STANDARD)
            }
        } else {
            chooseImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_FILES_STANDARD -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage()
                } else {
                    context?.let {
                        PermissionUtils.setShouldShowStatus(it, Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                return
            }
            else -> { }
        }
    }
}