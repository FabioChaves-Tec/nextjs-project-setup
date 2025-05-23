package gov.rr.iteraima.ui.admin.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gov.rr.iteraima.R
import gov.rr.iteraima.data.model.Schedule
import gov.rr.iteraima.databinding.ItemScheduleBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ScheduleAdapter(
    private val onItemClick: (Schedule) -> Unit
) : ListAdapter<Schedule, ScheduleAdapter.ScheduleViewHolder>(ScheduleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScheduleViewHolder(
        private val binding: ItemScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("pt", "BR"))

        fun bind(schedule: Schedule) {
            with(binding) {
                tvTitle.text = schedule.title
                tvDescription.text = schedule.description
                tvDate.text = dateFormatter.format(schedule.date)
                tvTime.text = "${schedule.startTime} - ${schedule.endTime}"

                // Set status chip
                statusChip.apply {
                    text = if (schedule.isAvailable) {
                        context.getString(R.string.status_available)
                    } else {
                        context.getString(R.string.status_unavailable)
                    }
                    setChipBackgroundColorResource(
                        if (schedule.isAvailable) R.color.success else R.color.error
                    )
                }

                // Set click listeners
                btnOptions.setOnClickListener {
                    onItemClick(schedule)
                }

                root.setOnClickListener {
                    onItemClick(schedule)
                }
            }
        }
    }

    private class ScheduleDiffCallback : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem == newItem
        }
    }
}
