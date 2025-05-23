package gov.rr.iteraima.ui.admin.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import gov.rr.iteraima.R
import gov.rr.iteraima.data.model.Department
import gov.rr.iteraima.databinding.ActivityAdminDashboardBinding
import gov.rr.iteraima.ui.admin.login.AdminLoginActivity
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val viewModel: AdminDashboardViewModel by viewModels()
    private lateinit var departmentPagerAdapter: DepartmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
        setupFab()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupViewPager() {
        departmentPagerAdapter = DepartmentPagerAdapter(this)
        binding.viewPager.adapter = departmentPagerAdapter

        // Link TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val department = Department.values()[position]
            tab.text = getString(
                when (department) {
                    Department.DIPRE -> R.string.dipre
                    Department.DIGOF -> R.string.digof
                    Department.DSF -> R.string.dsf
                    Department.DIRAD -> R.string.dirad
                    Department.DIGEF -> R.string.digef
                }
            )
        }.attach()

        // Update selected department when page changes
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.selectDepartment(Department.values()[position])
            }
        })
    }

    private fun setupFab() {
        binding.fabAddSchedule.setOnClickListener {
            // Show create schedule dialog
            showCreateScheduleDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AdminDashboardViewModel.DashboardUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.emptyState.visibility = View.GONE
                    }
                    is AdminDashboardViewModel.DashboardUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyState.visibility = View.GONE
                        departmentPagerAdapter.submitList(state.schedules)
                    }
                    is AdminDashboardViewModel.DashboardUiState.Empty -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyState.visibility = View.VISIBLE
                    }
                    is AdminDashboardViewModel.DashboardUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showError(state.message)
                    }
                }
            }
        }

        viewModel.selectedDepartment.observe(this) { department ->
            // Update UI based on selected department if needed
        }
    }

    private fun showCreateScheduleDialog() {
        // TODO: Implement create schedule dialog
        // This will be implemented in the next step
    }

    private fun showError(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("OK") { dismiss() }
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_confirm_title)
            .setMessage(R.string.dialog_logout_message)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                // Clear auth token and navigate to login
                logout()
            }
            .setNegativeButton(R.string.dialog_no, null)
            .show()
    }

    private fun logout() {
        // Clear authentication
        // Navigate to login screen
        startActivity(AdminLoginActivity.createIntent(this))
        finish()
    }

    companion object {
        private const val TAG = "AdminDashboardActivity"
    }
}
