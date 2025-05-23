package gov.rr.iteraima.ui.admin.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import gov.rr.iteraima.R
import gov.rr.iteraima.data.model.Department
import gov.rr.iteraima.data.model.Schedule
import gov.rr.iteraima.databinding.FragmentScheduleListBinding

class ScheduleListFragment : Fragment() {

    private var _binding: FragmentScheduleListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var department: Department
    private var schedules: List<Schedule> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        department = requireArguments().getSerializable(ARG_DEPARTMENT) as Department
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        updateUI()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter(
            onItemClick = { schedule ->
                showScheduleOptionsDialog(schedule)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleAdapter
        }
    }

    private fun updateUI() {
        if (schedules.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
            scheduleAdapter.submitList(schedules)
        }
    }

    private fun showScheduleOptionsDialog(schedule: Schedule) {
        val options = arrayOf(
            getString(R.string.schedule_edit),
            getString(R.string.schedule_delete)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.schedule_options))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditScheduleDialog(schedule)
                    1 -> showDeleteConfirmationDialog(schedule)
                }
            }
            .show()
    }

    private fun showEditScheduleDialog(schedule: Schedule) {
        // TODO: Show edit schedule dialog
        // This will be implemented in the next step
    }

    private fun showDeleteConfirmationDialog(schedule: Schedule) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_confirm_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                deleteSchedule(schedule)
            }
            .setNegativeButton(R.string.dialog_no, null)
            .show()
    }

    private fun deleteSchedule(schedule: Schedule) {
        // Get parent activity's ViewModel to delete schedule
        (activity as? AdminDashboardActivity)?.let { activity ->
            activity.viewModel.deleteSchedule(schedule)
        }
    }

    fun setSchedules(newSchedules: List<Schedule>) {
        schedules = newSchedules
        if (isAdded) {
            updateUI()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_DEPARTMENT = "department"

        fun newInstance(department: Department) = ScheduleListFragment().apply {
            arguments = bundleOf(ARG_DEPARTMENT to department)
        }
    }
}
