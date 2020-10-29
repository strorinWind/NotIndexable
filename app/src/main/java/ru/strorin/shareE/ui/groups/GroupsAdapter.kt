package ru.strorin.shareE.ui.groups

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import ru.strorin.shareE.R

class GroupsAdapter(
    context: Context,
    private val fragmentManager: FragmentManager,
    private val clickListener: OnItemClickListener
): RecyclerView.Adapter<GroupViewHolder>() {

    private var groupList = ArrayList<VkGroupUi>()
    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val holder = GroupViewHolder(
            inflater.inflate(R.layout.group_item_layout, parent, false),
            inflater.context
        )
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            clickListener.onItemClick(groupList[position], position)
        }
        holder.itemView.setOnLongClickListener {
            val bottomInfoDialog = BottomInfoDialog.newInstance(groupList[holder.adapterPosition])
            bottomInfoDialog.show(fragmentManager, "BOTTOM")
            true
        }

        return holder
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groupList[position])
    }

    fun setDataset(list: List<VkGroupUi>) {
        groupList.clear()
        groupList.addAll(list)
        notifyDataSetChanged()
    }

    fun addItem(item: VkGroupUi) {
        groupList.add(item)
        notifyItemInserted(groupList.size - 1)
    }

    fun updateItem(num: Int){
        notifyItemChanged(num)
    }

    fun selectItem(number: Int, select: Boolean) {
        if (select != groupList[number].selected) {
            groupList[number].selected = select
            notifyItemChanged(number)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(group: VkGroupUi, number: Int)
    }
}