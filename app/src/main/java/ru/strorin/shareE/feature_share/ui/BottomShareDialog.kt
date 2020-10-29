package ru.strorin.shareE.feature_share.ui

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import ru.strorin.shareE.R
import ru.strorin.shareE.vk.commands.VKWallPostCommand
import ru.strorin.shareE.utils.PathUtils


class BottomShareDialog: BottomSheetDialogFragment() {

    private lateinit var sendButton: LinearLayout
    private lateinit var closeButton: ImageView
    private lateinit var image: ImageView
    private lateinit var commentEditText: EditText
    private lateinit var progress: ProgressBar

    private lateinit var imageUri: Uri
    private var commentStringHint: String = ""

    val args: BottomShareDialogArgs by navArgs()

    companion object {
        fun newInstance(imageUri: Uri, commentString: String = ""): BottomShareDialog {
            val dialog = BottomShareDialog()
            dialog.imageUri = imageUri
            dialog.commentStringHint = commentString
            return dialog
        }

        private const val IMAGE_URI = "IMAGE_URI"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        expandAfterCreating(dialog)

        imageUri = args.imageUri
        commentStringHint = args.hint

        val view = inflater.inflate(R.layout.share_bottom_sheet, container, false)
        closeButton = view.findViewById(R.id.close_button)
        sendButton = view.findViewById(R.id.send_button)
        image = view.findViewById(R.id.image_view)
        commentEditText = view.findViewById(R.id.comment_edit_text)
        if (commentStringHint != "") {
            commentEditText.hint = commentStringHint
        }
        progress = view.findViewById(R.id.progress_bar)
        return view
    }

    override fun onStart() {
        super.onStart()
        closeButton.setOnClickListener {
            dismissAllowingStateLoss()
        }
        sendButton.setOnClickListener {
            tryToPostPhoto()
        }
    }

    private fun tryToPostPhoto() {
        sendButton.setOnClickListener { }
        progress.visibility = View.VISIBLE
        context?.let {
            executeVkCommandsToPostPhoto(it)
        }
    }

    private fun executeVkCommandsToPostPhoto(ctx: Context) {
        val callback = object : VKApiCallback<Int> {
            override fun fail(error: Exception) {
                Toast.makeText(
                    ctx,
                    getString(R.string.str_error_posting_toast),
                    Toast.LENGTH_SHORT
                ).show()
                progress.visibility = View.GONE
                sendButton.setOnClickListener {
                    tryToPostPhoto()
                }
            }

            override fun success(result: Int){
                Toast.makeText(
                    ctx,
                    getString(R.string.str_success_posting_toast),
                    Toast.LENGTH_SHORT
                ).show()
                dismissAllowingStateLoss()
            }
        }

        VK.execute(
            VKWallPostCommand(
                commentEditText.text.toString(),
                arrayListOf(Uri.parse(PathUtils.getPath(ctx, imageUri)))
            ),
            callback
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(IMAGE_URI, imageUri)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState?.containsKey(IMAGE_URI) == true) {
            savedInstanceState
                .getParcelable<Uri>(IMAGE_URI)
                ?.let { uri ->
                    imageUri = uri
                }
        }
        context?.let {
            loadImage(it)
        }
    }

    private fun loadImage(context: Context) {
        Glide.with(context).load(imageUri).into(image)
    }

    private fun expandAfterCreating(dialog: Dialog?) {
        dialog?.setOnShowListener { dlg ->
            val d = dlg as? BottomSheetDialog ?: return@setOnShowListener
            val bottomSheetInternal =
                d.findViewById<View>(R.id.design_bottom_sheet) ?: return@setOnShowListener
            BottomSheetBehavior.from<View?>(bottomSheetInternal).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }
}