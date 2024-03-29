package com.hms.readinghabittracker.ui.mybooks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.data.model.Book
import com.hms.readinghabittracker.databinding.BookItemBinding
import com.hms.readinghabittracker.ui.mybooks.MyBooksFragmentDirections
import com.hms.readinghabittracker.utils.ImageUtils.convertByteArrayToBitmap

class BookAdapter(private val books: List<Book>) :  //Child Adapter
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = BookItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.binding.apply {
            val book = books[position]
            textViewBookTitle.text = book.title
            imageViewBook.setImageBitmap(convertByteArrayToBitmap(book.image))

            myBooksItem.setOnClickListener {
                val action =
                    MyBooksFragmentDirections.actionMyBooksFragmentToBookDetailFragment(
                        book.title,
                        convertByteArrayToBitmap(book.image),
                        book.author,
                        book.pages
                    )
                Navigation.findNavController(myBooksItem).navigate(action)
            }
        }
    }

    override fun getItemCount() = books.size
}