package com.example.tukkantuk

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.util.Pair
import com.bumptech.glide.Glide


class StoryAdapter :
    ListAdapter<ListStory, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

        }
    }

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        private val tvItemDesc: TextView = itemView.findViewById(R.id.tv_item_desc)
        private val imgItemStory: ImageView = itemView.findViewById(R.id.img_item_story)

        fun bind(story: ListStory) {
            tvItemName.text = story.name
            tvItemDesc.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(imgItemStory)

            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(imgItemStory, "story"),
                        Pair(tvItemName, "name"),
                        Pair(tvItemDesc, "desc"),
                    )
                val intent = DetailActivity.createIntent(itemView.context as Activity, story)
                intent.putExtra("name", story.name)
                intent.putExtra("description", story.description)
                intent.putExtra("photoUrl", story.photoUrl)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }
}
