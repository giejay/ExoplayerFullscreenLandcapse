package com.fernandocejas.sample.features.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fernandocejas.sample.R
import java.time.ZonedDateTime

class HistoryDayFragment : Fragment() {
    private var date: ZonedDateTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getSerializable("date") as ZonedDateTime?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_day_history, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: ZonedDateTime) =
            HistoryDayFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("date", param1)
                }
            }
    }
}