package com.syntxr.digitalattendance.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.syntxr.digitalattendance.R
import com.syntxr.digitalattendance.databinding.ItemHistoryBinding
import com.syntxr.digitalattendance.data.model.history.HistoryRensponse
import com.syntxr.digitalattendance.data.model.history.HistoryRensponseItem
import java.text.SimpleDateFormat

class HistoryAttendanceAdapter(val historyAttendance : HistoryRensponse) :
    RecyclerView.Adapter<HistoryAttendanceAdapter.HistoryAttendanceViewHolder>() {

    var itemClick : ((HistoryRensponseItem) -> Unit)? = null

    class HistoryAttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding : ItemHistoryBinding by viewBinding()

        fun bindView (historyRensponseItem: HistoryRensponseItem){

            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val format = SimpleDateFormat("HH:mm \n yyyy-MM-dd")


//            if (historyRensponseItem.out.isNullOrEmpty()){
//                binding.tvHistoryClockOut.text = "--:--"
//            }else{
//                val formatDateOut = format.format(parser.parse(historyRensponseItem.out)!!)
//                binding.tvHistoryClockOut.text = formatDateOut
//            }

            if (historyRensponseItem.inX.isNullOrEmpty() || historyRensponseItem.out.isNullOrEmpty()){
                binding.tvHistoryClockIn.text = "--:--"
                binding.tvHistoryClockOut.text = "--:--"
            }else{
                val formatDateIn = format.format(parser.parse(historyRensponseItem.inX)!!)
                binding.tvHistoryClockIn.text = formatDateIn

                val formatDateOut = format.format(parser.parse(historyRensponseItem.out)!!)
                binding.tvHistoryClockOut.text = formatDateOut
            }

            binding.tvHistoryUser.text = historyRensponseItem.name
            binding.tvHistoryEmail.text = historyRensponseItem.email




        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAttendanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history,parent,false)
        return HistoryAttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryAttendanceViewHolder, position: Int) {
        val attendance = historyAttendance[position]
        holder.bindView(attendance)
        holder.itemView.setOnLongClickListener {
            itemClick?.invoke(attendance)

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return historyAttendance.size
    }
}