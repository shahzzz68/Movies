package com.example.movies.fragments


import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.example.movies.R
import com.example.movies.common.interfaces.FilterInterface
import com.example.movies.common.utils.onclick
import com.example.movies.common.utils.showToast
import kotlinx.android.synthetic.main.dialog_fragment_filter.view.*
import java.util.*
import java.lang.reflect.AccessibleObject.setAccessible
import android.widget.Spinner





/**
 * A simple [Fragment] subclass.
 */
class FilterFragment : AppCompatDialogFragment() {

    lateinit var list: MutableList<String>
    lateinit var filterInterface: FilterInterface
    lateinit var langList: MutableList<String>
    lateinit var langCodeList: MutableList<String>
    var selectedLangCode: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        filterInterface = context as FilterInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_filter, container, false)

        langList()
        generateNumList()
        setUpYearSpinner(v)
        setUpLangSpinner(v)

        v.bt_apply_filters.onclick {

            val selectedYear = if (v.sp_releaseYear.selectedItemPosition > 0)
                v.sp_releaseYear.selectedItem.toString()
            else null

//            val selectedLang = if (v.sp_language.selectedItemPosition > 0)
//                v.sp_language.selectedItem.toString()
//            else null

            filterInterface.filterData(selectedYear, selectedLangCode)
            dismiss()
        }


        return v
    }

    override fun onResume() {
        super.onResume()
        val window = dialog!!.window

        val width = resources.getDimensionPixelSize(R.dimen.popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.popup_height)

        window?.run {

            setLayout(width, height)
            setBackgroundDrawableResource(R.drawable.bg_filter_fragment)
            setGravity(Gravity.CENTER)
        }
    }


    fun setUpYearSpinner(v: View) {
        v.sp_releaseYear.apply {
            adapter =
                activity?.let {
                    ArrayAdapter(
                        it,
                        R.layout.row_spinner,
                        list
                    )
                }

            dropDownVerticalOffset=48
        }
    }

    fun setUpLangSpinner(v: View) {
        v.sp_language.apply {
            adapter =
                activity?.let {
                    ArrayAdapter(
                        it,
                        R.layout.row_spinner,
                        langList
                    )
                }

        }

        val listener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 > 0)

                langCodeList[p2].run {
                    selectedLangCode = this
                }
            }
        }
        v.sp_language.onItemSelectedListener = listener
    }

    fun generateNumList() {
        list = mutableListOf()
        list.add(0, "year")
        val year = Calendar.getInstance().get(Calendar.YEAR);
        for (i in 1965..year) {

            list.add(i.toString())
        }
    }

    fun langList() {
        langList = mutableListOf()
        langCodeList = mutableListOf()

        val map = mutableMapOf<String, String>()
        map.put("Language", "")
        map.put("English", "en-US")
        map.put("Tamil", "ta")
        map.put("Telugu", "te")
        map.put("Hindi", "hi")
        map.put("French", "fr")

        map.forEach {m->
            langList.add(m.key)
            langCodeList.add(m.value)
        }
    }

}
