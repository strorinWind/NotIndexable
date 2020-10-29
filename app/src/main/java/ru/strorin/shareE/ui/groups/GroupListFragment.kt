package ru.strorin.shareE.ui.groups

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.content.res.Configuration
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.strorin.shareE.R
import ru.strorin.shareE.widgets.ButtonCounter


class GroupListFragment: Fragment(), GroupListView {

    private lateinit var unsubscribeButton: ButtonCounter
    private lateinit var bottomPanel: LinearLayout
    private lateinit var groupListRecyclerView: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: GroupsAdapter

    private val model: GroupsViewModel by activityViewModels()


    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.group_list_fragment, container, false)
        groupListRecyclerView = view.findViewById(R.id.group_list_rv)
        unsubscribeButton = view.findViewById(R.id.unsubscribe_button)
        bottomPanel = view.findViewById(R.id.bottom_panel)
        progress = view.findViewById(R.id.progress_bar)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = GroupsAdapter(context, parentFragmentManager, itemClickListener)
    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()

        model.onViewIsReady(this)
        unsubscribeButton.setOnClickListener {
            model.unsubscribeButtonClicked(this)
        }
        updateUnsubscribeVisibility()
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    override fun setLoading(loading: Boolean) {
        if (loading) {
            groupListRecyclerView.visibility = View.GONE
            progress.visibility = View.VISIBLE
        } else {
            groupListRecyclerView.visibility = View.VISIBLE
            progress.visibility = View.GONE
        }
    }

    override fun setGroupsList(list: List<VkGroupUi>) {
        setLoading(false)
        adapter.setDataset(list)
        updateUnsubscribeVisibility()
    }

    override fun updateUnsubscribeVisibility() {
        bottomPanel.visibility = if (model.getSelectedNumber() != 0) View.VISIBLE else View.GONE
        unsubscribeButton.setNumber(model.getSelectedNumber())
    }

    private fun setupRecyclerView(){
        val orientation = resources.configuration.orientation
        val numColumns: Int
        numColumns = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            3
        } else {
            4
        }
        groupListRecyclerView.layoutManager = GridLayoutManager(context, numColumns)
        groupListRecyclerView.adapter = adapter
    }

    private val itemClickListener = object : GroupsAdapter.OnItemClickListener {
        override fun onItemClick(group: VkGroupUi, number: Int) {
            model.setSelected(group, number)
            adapter.updateItem(number)
            updateUnsubscribeVisibility()
        }
    }
}