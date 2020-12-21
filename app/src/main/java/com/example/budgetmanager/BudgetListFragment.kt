package com.example.budgetmanager

import android.app.Activity
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.budgetmanager.database.DatabaseHelper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BudgetListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BudgetListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.budget_list_fragment, container, false)

        val textViewSampleBudgetList = view.findViewById<TextView>(R.id.textViewSampleDismiss)

        textViewSampleBudgetList.setOnClickListener {
            textViewSampleBudgetList.visibility = View.GONE // when click it will be gone
        }

        // Budget Data
        val databaseHelper = DatabaseHelper(activity!!.applicationContext)
        val budgetData = databaseHelper.readBudget()
        while (budgetData.moveToNext()) {
            val textView = TextView(activity)
            textView.text = budgetData.getString(0)
            textView.textSize = 20f
            textView.setOnClickListener{

            }
            view.findViewById<LinearLayout>(R.id.budgetListLinearLayout).addView(textView)
        }

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BudgetListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BudgetListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}