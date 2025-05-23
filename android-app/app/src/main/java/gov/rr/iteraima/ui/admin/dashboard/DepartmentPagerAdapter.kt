package gov.rr.iteraima.ui.admin.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import gov.rr.iteraima.data.model.Department
import gov.rr.iteraima.data.model.Schedule
import gov.rr.iteraima.ui.admin.schedule.ScheduleListFragment

class DepartmentPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    private var schedules: Map<Department, List<Schedule>> = emptyMap()

    override fun getItemCount(): Int = Department.values().size

    override fun createFragment(position: Int): Fragment {
        val department = Department.values()[position]
        return ScheduleListFragment.newInstance(department).apply {
            setSchedules(schedules[department] ?: emptyList())
        }
    }

    fun submitList(newSchedules: Map<Department, List<Schedule>>) {
        schedules = newSchedules
        notifyDataSetChanged()
    }

    fun getSchedulesForDepartment(department: Department): List<Schedule> {
        return schedules[department] ?: emptyList()
    }

    companion object {
        private const val TAG = "DepartmentPagerAdapter"
    }
}
