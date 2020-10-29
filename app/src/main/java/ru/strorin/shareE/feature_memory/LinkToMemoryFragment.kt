package ru.strorin.shareE.feature_memory

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.strorin.shareE.R
import ru.strorin.shareE.feature_share.ui.SharePostFragmentDirections

class LinkToMemoryFragment: DialogFragment() {

    private lateinit var continueBtn: LinearLayout

    override fun onStart() {
        super.onStart()
        continueBtn.setOnClickListener {
            findNavController().navigateUp()
            val action =
                SharePostFragmentDirections.actionSharePostFragmentToGroupListFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.memory_link_layout, container, false)
        continueBtn = view.findViewById(R.id.continue_button)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        return view
    }
}