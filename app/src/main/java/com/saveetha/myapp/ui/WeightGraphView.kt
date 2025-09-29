package com.saveetha.myapp.ui.health

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WeightGraphView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val weights = mutableListOf<Double>()
    private val paintLine = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val paintCircle = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun setData(newWeights: List<Double>) {
        weights.clear()
        weights.addAll(newWeights)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (weights.size < 2) return

        val maxWeight = weights.maxOrNull() ?: 1.0
        val minWeight = weights.minOrNull() ?: 0.0

        val graphHeight = height - 40f
        val graphWidth = width - 40f
        val stepX = graphWidth / (weights.size - 1).toFloat()

        for (i in 0 until weights.size - 1) {
            val x1 = 20f + i * stepX
            val y1 = graphHeight - ((weights[i] - minWeight) / (maxWeight - minWeight) * graphHeight) + 20f

            val x2 = 20f + (i + 1) * stepX
            val y2 = graphHeight - ((weights[i + 1] - minWeight) / (maxWeight - minWeight) * graphHeight) + 20f

            canvas.drawLine(x1, y1.toFloat(), x2, y2.toFloat(), paintLine)
            canvas.drawCircle(x1, y1.toFloat(), 8f, paintCircle)
        }

        // draw last point
        val lastX = 20f + (weights.size - 1) * stepX
        val lastY = graphHeight - ((weights.last() - minWeight) / (maxWeight - minWeight) * graphHeight) + 20f
        canvas.drawCircle(lastX, lastY.toFloat(), 8f, paintCircle)
    }
}
