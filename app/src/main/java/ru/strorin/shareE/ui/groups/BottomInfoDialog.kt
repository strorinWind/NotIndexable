package ru.strorin.shareE.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import ru.strorin.shareE.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri
import ru.strorin.shareE.vk.requests.VkLastWallRequest
import ru.strorin.shareE.vk.models.VkPostList


class BottomInfoDialog: BottomSheetDialogFragment() {

    private lateinit var title: TextView
    private lateinit var population: TextView
    private lateinit var groupInfo: TextView
    private lateinit var lastPost: TextView
    private lateinit var openButton: LinearLayout
    private lateinit var closeButton: ImageView

    private var group: VkGroupUi? = null

    companion object {
        fun newInstance(group: VkGroupUi): BottomInfoDialog {
            val infoDialog = BottomInfoDialog()
            infoDialog.group = group
            return infoDialog
        }
        private val GROUP_KEY = "GROUP_KEY"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=  inflater.inflate(R.layout.group_info_bottom_sheet, container, false)
        findViews(view)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (group != null) {
            outState.putSerializable(GROUP_KEY, group)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null && savedInstanceState.containsKey(GROUP_KEY)) {
            group = savedInstanceState[GROUP_KEY] as? VkGroupUi
        }
    }

    private fun displayGroup() {
        title.text = group?.name
        groupInfo.text = group?.description
        population.text = context?.getString(
            R.string.str_subscribers_info,
            formatNumber(group?.members_count ?: 0)
        )
    }

    override fun onStart() {
        super.onStart()
        displayGroup()
        closeButton.setOnClickListener {
            dismissAllowingStateLoss()
        }
        openButton.setOnClickListener {
            group?.let {
                val url = "https://vk.com/club${it.id}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }
        loadLastPost()
    }

    private fun loadLastPost() {
        group?.let { g ->
            VK.execute(VkLastWallRequest(g.id), object : VKApiCallback<VkPostList>{
                override fun success(result: VkPostList) {
                    val max = result.items.map { post -> post.date }.max()?.times(1000)
                    max?.let {
                        val date = Date(it)
                        val cal = Calendar.getInstance(TimeZone.getDefault())
                        cal.time = date
                        val year = cal.get(Calendar.YEAR)
                        val monthDate: SimpleDateFormat
                        monthDate = if (year < Calendar.getInstance().get(Calendar.YEAR)){
                            SimpleDateFormat("d MMMM y", Locale.getDefault())
                        } else {
                            SimpleDateFormat("d MMMM", Locale.getDefault())
                        }
                        val dateDisplay = monthDate.format(max)
                        lastPost.text = context?.getString(R.string.str_last_post, dateDisplay)
                    }
                }

                override fun fail(error: Exception) {
                    loadLastPost()
                }
            })
        }
    }

    private fun formatNumber(members: Int): String {
        val fmt = DecimalFormat()
        val fmts = DecimalFormatSymbols()

        fmts.groupingSeparator = ' '

        fmt.groupingSize = 3
        fmt.isGroupingUsed = true
        fmt.decimalFormatSymbols = fmts

        return fmt.format(members)
    }

    private fun findViews(view: View) {
        title = view.findViewById(R.id.group_title)
        population = view.findViewById(R.id.group_population)
        groupInfo = view.findViewById(R.id.article_outline)
        lastPost = view.findViewById(R.id.last_post)
        openButton = view.findViewById(R.id.open_button)
        closeButton = view.findViewById(R.id.close_button)
    }
}