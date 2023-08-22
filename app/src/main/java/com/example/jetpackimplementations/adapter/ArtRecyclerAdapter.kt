package com.example.jetpackimplementations.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.jetpackimplementations.databinding.ArtRecyclerRowBinding
import com.example.jetpackimplementations.model.Art
import com.example.jetpackimplementations.view.FirstFragmentDirections


class ArtRecyclerAdapter(private val artList: ArrayList<Art>): RecyclerView.Adapter<ArtRecyclerAdapter.ArtHolder>() {

    //custom recycler adapter cell
    class ArtHolder(val binding: ArtRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val binding = ArtRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ArtHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        if (position < artList.size) {
            val art = artList[position]
            holder.binding.recyclerViewTextView.text = art.artName
        }
            holder.itemView.setOnClickListener{
            val info="old";
            val id = artList[position].id
            val action = FirstFragmentDirections.firstSecondFrg(info,id)
            Navigation.findNavController(it).navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return artList.size
    }

}