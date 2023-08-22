package com.example.jetpackimplementations.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jetpackimplementations.databinding.CryptoRecyclerRowBinding
import com.example.jetpackimplementations.model.Crypto

class CryptoRecyclerAdapter (private val cryptoList:ArrayList<Crypto>, private val listener : Listener) : RecyclerView.Adapter<CryptoRecyclerAdapter.CryptoHolder>()
{
    class CryptoHolder (val binding: CryptoRecyclerRowBinding): RecyclerView.ViewHolder(binding.root) {}

    private val colors: Array<String> = arrayOf("#13bd27","#29c1e1","#b129e1","#d3df13","#f6bd0c","#a1fb93","#0d9de3","#ffe48f")

    interface Listener {
        fun onItemClick(crypto: Crypto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoHolder {
        val binding = CryptoRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return CryptoHolder(binding);
    }

    override fun getItemCount(): Int {
        return cryptoList.count()
    }

    override fun onBindViewHolder(holder: CryptoHolder, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onItemClick(cryptoList.get(position))
        }
        holder.itemView.setBackgroundColor(Color.parseColor(colors[position % 8]))

        holder.binding.textName.text = cryptoList[position].name
        holder.binding.textPrice.text = cryptoList[position].quote.usd.price.toString()
    }

}