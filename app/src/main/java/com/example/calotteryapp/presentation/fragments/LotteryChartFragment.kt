package com.example.calotteryapp.presentation.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.calotteryapp.R
import com.example.calotteryapp.databinding.FragmentLotteryChartBinding
import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.domain.preferences.AppPreferences
import com.example.calotteryapp.presentation.viewmodels.LotteryViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class LotteryChartFragment : Fragment() {

    companion object {
        fun newInstance() = LotteryChartFragment()

        private const val MIN_X_AXIS = 0f
        private const val MAX_X_AXIS_MEGA = 28f
        private const val MAX_X_AXIS_REGULAR = 48f

        private const val SHOULD_DRAW_GRID_LINES_PREF = "should_draw_grid_lines"
    }

    @Inject
    lateinit var appPreferences: AppPreferences

    private val viewModel: LotteryViewModel by activityViewModels()

    private var _binding: FragmentLotteryChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentLotteryChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.lotteryDraws.observe(viewLifecycleOwner) { lotteryDraws ->
            setupBarCharts(lotteryDraws)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toggle_grid) {
            onGridMenuItemPressed()
        }
        return true
    }

    private fun setupBarCharts(lotteryDraws: List<LotteryDraw>) {
        setupRegularLotteryChart(lotteryDraws)

        setupMegaLotteryChart(lotteryDraws)
    }

    private fun setupRegularLotteryChart(lotteryDraws: List<LotteryDraw>) {
        val regularLotteryNumbers = getCounterMapOfLotteryDraws(
            lotteryDraws = lotteryDraws,
            isMega = false
        )

        binding.barChartRegular.barChartTitle.text = getString(R.string.regular_lottery_numbers)
        binding.barChartRegular.barChartXLabel.text = getString(R.string.times_drawn)
        binding.barChartRegular.barChartYLabel.text = getString(R.string.numbers)

        initBarChart(binding.barChartRegular.barChart, regularLotteryNumbers)
    }

    private fun setupMegaLotteryChart(lotteryDraws: List<LotteryDraw>) {
        val megaLotteryNumbers = getCounterMapOfLotteryDraws(
            lotteryDraws = lotteryDraws,
            isMega = true
        )

        binding.barChartMega.barChartTitle.text = getString(R.string.mega_lottery_numbers)
        binding.barChartMega.barChartXLabel.text = getString(R.string.times_drawn)
        binding.barChartMega.barChartYLabel.text = getString(R.string.numbers)

        initBarChart(binding.barChartMega.barChart, megaLotteryNumbers)
    }

    private fun getCounterMapOfLotteryDraws(
        lotteryDraws: List<LotteryDraw>,
        isMega: Boolean
    ): Map<Int, Int> = lotteryDraws
        .map { it.winningNumbers }
        .flatten()
        .filterIndexed { idx, _ -> if (isMega) idx % 6 == 5 else idx % 6 != 5 }
        .groupingBy { it }
        .eachCount()

    private fun initBarChart(
        barChart: BarChart,
        lotteryNumbersMap: Map<Int, Int>
    ) {
        // used for setting different values between charts
        val isMega = (barChart.parent as View).id == R.id.bar_chart_mega

        // setup barchart data
        val barEntries = getBarChartEntries(lotteryNumbersMap)
        val barDataSet = BarDataSet(barEntries, "")
        barDataSet.setColors(
            ContextCompat.getColor(
                requireContext(),
                if (isMega) R.color.red else R.color.light_blue
            )
        )
        barDataSet.valueTextSize = 14f
        val data = BarData(barDataSet)
        data.setValueFormatter(IntValueFormatter())

        barChart.data = data

        // adjust range of x and y axis
        barChart.xAxis.axisMinimum = MIN_X_AXIS
        barChart.xAxis.axisMaximum = if (isMega) MAX_X_AXIS_MEGA else MAX_X_AXIS_REGULAR
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.axisMaximum = data.yMax
        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            val maxX = if (isMega) MAX_X_AXIS_MEGA else MAX_X_AXIS_REGULAR
            barChart.setVisibleXRange(0F, maxX)
            barChart.xAxis.labelCount = 24
        } else {
            barChart.setVisibleXRange(0F, 20F)
            barChart.xAxis.labelCount = 10
        }

        // grid lines
        val shouldDrawGridLines = appPreferences.getBoolean(SHOULD_DRAW_GRID_LINES_PREF)
        setGridLines(barChart, shouldDrawGridLines)

        // adjust text size
        barChart.xAxis.textSize = 14f
        barChart.axisLeft.textSize = 14f

        // disable default values
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false

        // animate chart lines
        barChart.animateY(1000)

        barChart.invalidate()
    }

    private fun getBarChartEntries(lotteryNumberCount: Map<Int, Int>): List<BarEntry> =
        lotteryNumberCount.toSortedMap().map { (key, value) ->
            BarEntry(
                key.toFloat(),
                value.toFloat()
            )
        }

    private fun onGridMenuItemPressed() {
        val prevGridLinesPref = appPreferences.getBoolean(SHOULD_DRAW_GRID_LINES_PREF)
        appPreferences.insertBoolean(SHOULD_DRAW_GRID_LINES_PREF, !prevGridLinesPref)

        setGridLines(binding.barChartRegular.barChart, !prevGridLinesPref)
        setGridLines(binding.barChartMega.barChart, !prevGridLinesPref)

        binding.barChartRegular.barChart.invalidate()
        binding.barChartMega.barChart.invalidate()
    }

    private fun setGridLines(
        barChart: BarChart,
        shouldDrawGridLines: Boolean
    ) {
        barChart.axisRight.setDrawGridLines(shouldDrawGridLines)
        barChart.axisLeft.setDrawGridLines(shouldDrawGridLines)
        barChart.xAxis.setDrawGridLines(shouldDrawGridLines)
    }

    private fun getOrientation() = resources.configuration.orientation

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class IntValueFormatter : ValueFormatter() {
        private val format: DecimalFormat = DecimalFormat("#")

        override fun getFormattedValue(value: Float): String {
            return format.format(value)
        }
    }
}
