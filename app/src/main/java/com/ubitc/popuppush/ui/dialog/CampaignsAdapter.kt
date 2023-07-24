package com.ubitc.popuppush.ui.dialog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.ubitc.popuppush.R
import com.ubitc.popuppush.models.CampaignSimple
@SuppressLint("NotifyDataSetChanged")
class CampaignsAdapter : RecyclerView.Adapter<CampaignsAdapter.CampaignsViewHolder>() {
    private lateinit var onSuccess: (id: String) -> Unit
    var array: ArrayList<CampaignSimple> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_campaigns_dialog, parent, false)
        return CampaignsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 0
    }


//    fun addItems(array: ArrayList<CampaignSimple>) {
//        this.array.clear()
//        this.array.addAll(array)
//        notifyDataSetChanged()
//    }

    override fun onBindViewHolder(holder: CampaignsViewHolder, position: Int) {
        holder.bind(array[position])
    }

//    fun setOnClickListener(onSuccess: ((id:String) -> Unit)) {
//       this.onSuccess = onSuccess
//    }

    inner class CampaignsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(s: CampaignSimple) {
            button?.text = s.name
            button?.setOnClickListener {
                onSuccess(s.id!!)

            }
        }

        private var button: MaterialButton? = null

        init {
            button = itemView.findViewById(R.id.button)
        }


    }
}