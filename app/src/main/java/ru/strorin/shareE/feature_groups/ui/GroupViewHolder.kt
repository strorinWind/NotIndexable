package ru.strorin.shareE.feature_groups.ui

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.strorin.shareE.R
import ru.strorin.shareE.feature_groups.VkGroupUi


class GroupViewHolder(
    view: View,
    context: Context
): RecyclerView.ViewHolder(view) {

    private val nameView: TextView = view.findViewById(R.id.group_name)
    private val imageView: ImageView = view.findViewById(R.id.group_image)
    private val selectedIndicator: ImageView = view.findViewById(R.id.selected)
    private val backgroundStroke: View = view.findViewById(R.id.background_stroke)
    private val imageLoader: RequestManager

    init {
        val imageOption = RequestOptions()
            .centerCrop()
        imageLoader = Glide.with(context).applyDefaultRequestOptions(imageOption)
    }

    fun bind(groupItem: VkGroupUi){
        nameView.text = groupItem.name
        imageView.clipToOutline = true
        if (!TextUtils.isEmpty(groupItem.photo_100)) {
            imageLoader.load(groupItem.photo_100).into(imageView);
        }
        if (groupItem.selected){
            backgroundStroke.visibility = View.VISIBLE
            selectedIndicator.visibility = View.VISIBLE
        } else {
            backgroundStroke.visibility = View.GONE
            selectedIndicator.visibility = View.GONE
        }
    }

    fun setOnClickListener(listener: ((view: View) -> Unit)) {
        itemView.setOnClickListener {
            listener.invoke(itemView)
        }
    }
}