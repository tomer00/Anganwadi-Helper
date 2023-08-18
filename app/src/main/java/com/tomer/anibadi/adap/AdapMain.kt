package com.tomer.anibadi.adap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.tomer.anibadi.R
import com.tomer.anibadi.modal.MotherRv


class AdapMain(
    private val ml: M_L,
) : RecyclerView.Adapter<AdapMain.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_row, parent, false)
        return Holder(v, ml)
    }

    override fun onBindViewHolder(Holder: Holder, position: Int) {
        val item = mainL[position]
        Holder.tvDp.setImageDrawable(item.icon)
        Holder.tv.text = item.name
        "Husband: ${item.husbandName}".also { Holder.tvH.text = it }
        Holder.card.setCardBackgroundColor(item.bgColor)
    }

    override fun getItemCount() = mainL.size()

    interface M_L {
        fun onMainLis(pos: Int)
    }


    private val sortCall = object : SortedList.Callback<MotherRv>() {
        override fun compare(o1: MotherRv, o2: MotherRv): Int {
            return o1.name.compareTo(o2.name)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun areContentsTheSame(oldItem: MotherRv, newItem: MotherRv): Boolean {
            return oldItem.ID == newItem.ID
        }

        override fun areItemsTheSame(item1: MotherRv, item2: MotherRv): Boolean {
            return item1.ID == item2.ID
        }

    }

    val mainL = SortedList(MotherRv::class.java, sortCall)

    fun search(newItem: List<MotherRv>, doRmove: Boolean) {
        mainL.beginBatchedUpdates()
        val lSize = mainL.size()
        val mutableLis = mutableListOf<MotherRv>()
        mutableLis.addAll(newItem)

        if (lSize > 0) {
            for (i in (lSize - 1) downTo 0) {
                if (!newItem.contains(mainL[i])) {
                    mainL.remove(mainL[i])
                } else {
                    if (doRmove) {
                        mutableLis.remove(mainL[i])
                    }
                }
            }
        }
        mainL.addAll(mutableLis)
        mainL.endBatchedUpdates()
    }


    class Holder(itemView: View, ml: M_L) : RecyclerView.ViewHolder(itemView) {
        val tvDp: ImageView = itemView.findViewById(R.id.tv_dp)
        val tv: TextView = itemView.findViewById(R.id.tvName)
        val tvH: TextView = itemView.findViewById(R.id.tvHName)
        val card: CardView = itemView.findViewById(R.id.cardView)

        init {
            itemView.setOnClickListener {
                ml.onMainLis(adapterPosition)
            }
        }
    }
}