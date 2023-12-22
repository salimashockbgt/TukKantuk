package com.example.tukkantuk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tukkantuk.ListStory

import com.example.tukkantuk.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name").toString()
        binding.tvItemName.text = name
        val photoUrl = intent.getStringExtra("photoUrl").toString()
        Glide.with(binding.root.context).load(
            photoUrl
        ).into(binding.imgItemStory)
        val desc = intent.getStringExtra("description").toString()
        binding.tvItemDesc.text = desc


    }

    companion object {
        fun createIntent(context: Context, story: ListStory): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra("name", story.name)
                putExtra("description", story.description)
                putExtra("photoUrl", story.photoUrl)
            }
        }
    }

}