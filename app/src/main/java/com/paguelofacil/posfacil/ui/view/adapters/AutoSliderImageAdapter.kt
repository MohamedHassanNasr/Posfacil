package com.paguelofacil.posfacil.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.ui.view.adapters.AutoSliderImageAdapter.SliderViewHolder

class AutoSliderImageAdapter(
    sliderItems: MutableList<SliderItem>,
    viewPager: ViewPager2
): RecyclerView.Adapter<SliderViewHolder>() {
    
    private val sliderImages: MutableList<SliderItem> = sliderItems
    private val vp2 = viewPager

    class SliderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val imageView: ImageView = itemView.findViewById(R.id.imageView_container)

        fun image(sliderItem: SliderItem){
            imageView.setImageResource(sliderItem.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.imager_container,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.image(sliderImages[position])
        if (position == sliderImages.size - 2) {
            vp2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return sliderImages.size
    }

    private val runnable = Runnable {
        sliderImages.addAll(sliderImages)
        notifyDataSetChanged()
    }
}