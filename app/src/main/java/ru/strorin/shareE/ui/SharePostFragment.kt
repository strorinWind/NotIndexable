package ru.strorin.shareE.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.internal.ContextUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.strorin.shareE.BuildConfig
import ru.strorin.shareE.R
import ru.strorin.shareE.image.CameraHelper
import ru.strorin.shareE.image.Image
import ru.strorin.shareE.permission.PermissionUtils
import ru.strorin.shareE.utils.getLocale
import java.text.SimpleDateFormat
import java.util.*


class SharePostFragment: Fragment() {

    private lateinit var shareButton: LinearLayout
    private lateinit var shareRandomButton: LinearLayout

    private val compositeDisposable = CompositeDisposable()


    companion object {
        fun newInstance(): SharePostFragment {
            return SharePostFragment()
        }

        private const val PERMISSIONS_REQUEST_READ_FILES_STANDARD = 100
        private const val PERMISSIONS_REQUEST_READ_FILES_RANDOM = 101
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
        shareRandomButton = view.findViewById(R.id.share_random_button)
        return view
    }

    override fun onStart() {
        super.onStart()
        shareButton.setOnClickListener {
            context?.let {
                checkForPermission(it, PERMISSIONS_REQUEST_READ_FILES_STANDARD, ::chooseImage)
            }
        }
        shareRandomButton.setOnClickListener {
            context?.let {
                checkForPermission(it, PERMISSIONS_REQUEST_READ_FILES_RANDOM, ::chooseRandomImage)
            }
        }
    }

    private fun chooseImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    private fun chooseRandomImage() {
        //TODO: show progress somehow while search
        context?.let { ctx ->
            val disposable = Single
                .fromCallable { CameraHelper.getRandomCameraImage(ctx) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ image ->
                    //TODO: handle if no image
                    openHistoryImage(image)
                }, { e ->
                    //TODO: handle error
                })
            compositeDisposable.add(disposable)
        }
    }

    private fun openHistoryImage(image: Image) {
        if (image.date != 0L) {
            val locale = getLocale(context)
            val date = SimpleDateFormat("dd MMM y", locale).format(Date(image.date * 1000))
            val hint = context?.getString(R.string.str_tell_about_memory, date) ?: ""
            showBottomShareDialog(image.uri, hint)
        } else {
            showBottomShareDialog(image.uri)
        }
    }

    private fun showBottomShareDialogIfPossible(context: Context, data: Intent?){
        try {
            val imageUri = data?.data ?: throw IllegalStateException()
            val imageStream = context.contentResolver.openInputStream(imageUri)
            val selectedImage = BitmapFactory.decodeStream(imageStream) ?: throw IllegalStateException()
            showBottomShareDialog(imageUri)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, getString(R.string.str_error_open_file_toast), Toast.LENGTH_LONG).show()
        }
    }

    private fun showBottomShareDialog(uri: Uri, hint: String = "") {
        val bottomInfoDialog = BottomShareDialog.newInstance(uri, hint)
        if (isAdded) {
            bottomInfoDialog.show(parentFragmentManager, "BOTTOM")
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

    private fun checkForPermission(ctx: Context, requestCode: Int, action: () -> Unit) {

        if (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if (PermissionUtils.getRationaleDisplayStatus(ctx, Manifest.permission.READ_EXTERNAL_STORAGE)
                && !shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showNeverAskAgainDialog()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
            }
        } else {
            action()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_FILES_STANDARD,
            PERMISSIONS_REQUEST_READ_FILES_RANDOM -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == PERMISSIONS_REQUEST_READ_FILES_STANDARD) {
                        chooseImage()
                    } else {
                        chooseRandomImage()
                    }
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