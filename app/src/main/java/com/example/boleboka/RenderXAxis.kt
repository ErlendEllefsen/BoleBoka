package com.example.boleboka

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.*

class RenderXAxis(viewPortHandler: ViewPortHandler, xAxis: XAxis, trans: Transformer, labelCount:Int, valueFormatter: IndexAxisValueFormatter):
    XAxisRenderer(viewPortHandler, xAxis, trans, ) {
    private var mLabelCount:Int = 0
    private var mValueFormatter: IndexAxisValueFormatter
    init{
        this.mLabelCount = labelCount
        this.mValueFormatter = valueFormatter
    }
    override fun computeAxisValues(min:Float, max:Float) {
        val labelCount = mLabelCount // This is the only change
        val range = abs(max - min).toDouble()
        if (labelCount == 0 || range <= 0 || java.lang.Double.isInfinite(range))
        {
            mAxis.mEntries = floatArrayOf()
            mAxis.mCenteredEntries = floatArrayOf()
            mAxis.mEntryCount = 0
            return
        }
        // Find out how much spacing (in y value space) between axis values
        val rawInterval = range / labelCount
        var interval = Utils.roundToNextSignificant(rawInterval)
        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (mAxis.isGranularityEnabled)
            interval = if (interval < mAxis.granularity) mAxis.granularity else interval
        // Normalize interval
        val intervalMagnitude = Utils.roundToNextSignificant(
            10.0.pow(
                log10(interval.toDouble()).toInt().toDouble()
            )
        )
        val intervalSigDigit = (interval / intervalMagnitude).toInt()
        if (intervalSigDigit > 5)
        {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = floor((10 * intervalMagnitude).toDouble()).toFloat()
        }
        var n = if (mAxis.isCenterAxisLabelsEnabled) 1 else 0
        // force label count
        if (mAxis.isForceLabelsEnabled)
        {
            interval = (range.toFloat() / (labelCount - 1).toFloat()).toDouble().toFloat()
            mAxis.mEntryCount = labelCount
            if (mAxis.mEntries.size < labelCount)
            {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = FloatArray(labelCount)
            }
            var v = min
            for (i in 0 until labelCount)
            {
                mAxis.mEntries[i] = v
                v += interval
            }
            n = labelCount
            // no forced count
        }
        else
        {
            var first = if (interval.toDouble() == 0.0) 0.0 else ceil((min / interval).toDouble()) * interval
            if (mAxis.isCenterAxisLabelsEnabled)
            {
                first -= interval
            }
            val last = if (interval.toDouble() == 0.0) 0.0 else Utils.nextUp(floor((max / interval).toDouble()) * interval)
            var f:Double
            if (interval.toDouble() != 0.0)
            {
                f = first
                while (f <= last)
                {
                    ++n
                    f += interval
                }
            }
            mAxis.mEntryCount = n
            if (mAxis.mEntries.size < n)
            {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = FloatArray(n)
            }
            f = first
            var i = 0
            while (i < n)
            {
                if (f == 0.0)
                // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0.0
                mAxis.mEntries[i] = f.toFloat()
                f += interval
                ++i
            }
        }
        // set decimals
        if (interval < 1)
        {
            mAxis.mDecimals = ceil(-log10(interval.toDouble())).toInt()
        }
        else
        {
            mAxis.mDecimals = 0
        }
        if (mAxis.isCenterAxisLabelsEnabled)
        {
            if (mAxis.mCenteredEntries.size < n)
            {
                mAxis.mCenteredEntries = FloatArray(n)
            }
            val offset = interval / 2f
            for (i in 0 until n)
            {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset
            }
        }
        computeSize()
    }
}