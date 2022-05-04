package com.example.calotteryapp.presentation.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calotteryapp.R
import com.example.calotteryapp.databinding.FragmentLotteryDrawsBinding
import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.domain.preferences.AppPreferences
import com.example.calotteryapp.presentation.adapters.LotteryDrawAdapter
import com.example.calotteryapp.presentation.viewmodels.LotteryViewModel
import com.example.calotteryapp.services.AlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LotteryDrawsFragment : Fragment() {

    companion object {
        fun newInstance() = LotteryDrawsFragment()

        const val REQUEST_CODE_0 = 0
        const val REQUEST_CODE_1 = 1
    }

    @Inject
    lateinit var simpleDateFormat: SimpleDateFormat

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var alarmManager: AlarmManager

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    private var adapter: LotteryDrawAdapter? = null

    private var lotteryDrawList = mutableListOf<Any>()

    private val viewModel: LotteryViewModel by activityViewModels()

    private var _binding: FragmentLotteryDrawsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLotteryDrawsBinding.inflate(inflater, container, false)

        adapter = LotteryDrawAdapter(
            lotteryDraws = lotteryDrawList,
            simpleDateFormat = simpleDateFormat,
            appPreferences = appPreferences
        )

        setupAlarmManager()

        binding.recyclerViewLotteryDraws.setHasFixedSize(true)
        binding.recyclerViewLotteryDraws.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewLotteryDraws.adapter = adapter
        binding.recyclerViewLotteryDraws.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayout.VERTICAL
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayoutLotteryDraws.setOnRefreshListener {
            if (isNetworkConnected()) {
                viewModel.loadLotteryDraws()
            } else {
                createNoConnectionAlertDialog()
                binding.swipeRefreshLayoutLotteryDraws.isRefreshing = false
            }
        }

        setupObservers()

        if (viewModel.lotteryDraws.value.isNullOrEmpty()) {
            viewModel.loadLotteryDraws()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> {
                    binding.swipeRefreshLayoutLotteryDraws.isRefreshing = true
                    binding.recyclerViewLotteryDraws.visibility = INVISIBLE
                }
                false -> {
                    binding.swipeRefreshLayoutLotteryDraws.isRefreshing = false
                    binding.recyclerViewLotteryDraws.visibility = VISIBLE
                }
            }
        })

        viewModel.lotteryDraws.observe(viewLifecycleOwner, Observer {
            updateAndNotifyAdapter(it)
        })
    }

    private fun updateAndNotifyAdapter(observedLotteryDraws: List<LotteryDraw>) {
        lotteryDrawList.clear()
        lotteryDrawList.add(
            resources.getString(
                R.string.most_recent_lottery_draw
            )
        )     // Want this to show up even if list is empty

        if (!isNetworkConnected()) {
            createNoConnectionAlertDialog()
            return
        }

        val firstElementToInsert = if (observedLotteryDraws.isNotEmpty()) {
            observedLotteryDraws.first()
        } else {
            LotteryDraw()
        }
        lotteryDrawList.add(firstElementToInsert)

        if (observedLotteryDraws.size > 1) {
            val amountOfPrevDraws = observedLotteryDraws.size - 1
            lotteryDrawList.add(
                resources.getQuantityString(
                    R.plurals.amount_of_prev_lottery_draws,
                    amountOfPrevDraws,
                    amountOfPrevDraws
                )
            )
            lotteryDrawList.addAll(observedLotteryDraws.drop(1))
        }
        adapter?.notifyDataSetChanged()
    }

    private fun isNetworkConnected(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    return when (this.type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return false
    }

    private fun createNoConnectionAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.no_connection_alert_dialog_title))
            .setMessage(getString(R.string.no_connection_alert_dialog_message))
            .setCancelable(true)
            .show()
    }

    private fun setupAlarmManager() {
        var currTime = getCurrTime(Calendar.WEDNESDAY)

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIndent1 = getPendingIntent(intent, REQUEST_CODE_0)
        // need 2 pending intents because the 2nd one will replace the first one

        setRepeatingAlarm(currTime, pendingIndent1)

        currTime = getCurrTime(Calendar.SATURDAY)

        val pendingIndent2 = getPendingIntent(intent, REQUEST_CODE_1)

        setRepeatingAlarm(currTime, pendingIndent2)
    }

    private fun setRepeatingAlarm(
        currTime: Calendar,
        pendingIndent: PendingIntent
    ) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            currTime.timeInMillis,
            7 * 24 * 60 * 60 * 1000,
            pendingIndent
        )
    }

    private fun getPendingIntent(
        intent: Intent,
        requestCode: Int
    ) = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getCurrTime(dayOfWeek: Int): Calendar {
        val currTime = Calendar.getInstance(Locale.getDefault())
        currTime.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        currTime.set(Calendar.HOUR, 8)
        currTime.set(Calendar.AM_PM, Calendar.PM)
        currTime.set(Calendar.MINUTE, 30)
        currTime.set(Calendar.SECOND, 10)

        // adds time until it is past the current time, ex: alarm is set for yesterday
        while (currTime.timeInMillis < System.currentTimeMillis()) {
            currTime.add(Calendar.DATE, 7)
        }
        return currTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }

}