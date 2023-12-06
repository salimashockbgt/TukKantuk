package com.example.mystory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mystory.databinding.ActivityDetailBinding
import com.example.mystory.ListStory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story: ListStory? = intent.getParcelableExtra(STORY_EXTRA)
        if (story != null) {
            populateUI(story)
        } else {
            finish()
        }
    }

    private fun populateUI(story: ListStory) {
        Glide.with(this).load(story.photoUrl).into(binding.imgItemStory)
        binding.tvItemDesc.text = story.description
        binding.tvItemName.text = story.name
    }

    companion object {
        const val STORY_EXTRA = "list_story"

        fun createIntent(activity: Activity, story: ListStory): Intent {
            return Intent(activity, DetailActivity::class.java).apply {
                putExtra(STORY_EXTRA, story)
            }
        }
    }
}
