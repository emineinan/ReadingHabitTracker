package com.hms.readinghabittracker.ui.mybooks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.data.model.Collection
import com.hms.readinghabittracker.databinding.MyBooksItemBinding

class MyBooksAdapter(private val collections: List<Collection>) :  //Parent Adapter
    RecyclerView.Adapter<MyBooksAdapter.MyBooksViewHolder>() {

    class MyBooksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = MyBooksItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBooksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.my_books_item, parent, false)
        return MyBooksViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyBooksViewHolder, position: Int) {
        holder.binding.apply {
            val collection = collections[position]
            textViewCollectionName.text = collection.name
            //val booksAdapter = BookAdapter(we need to our collection's book list, how do we reach this book list?)
            //recyclerViewBooks.adapter = booksAdapter
        }
    }

    override fun getItemCount() = collections.size
}