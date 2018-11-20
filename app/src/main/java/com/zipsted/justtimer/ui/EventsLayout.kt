/*
 * Copyright (c) 2018, Ed Letner.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zipsted.justtimer.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class EventsLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        var width = 0
        var height = 0

        var row = 0
        var rowWidth = 0
        var rowMaxHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (child.visibility != View.GONE) {
                rowWidth += childWidth
                if (rowWidth > widthSize) {
                    rowWidth = childWidth
                    height += rowMaxHeight
                    rowMaxHeight = childHeight
                    row++
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight)
                }
            }
        }

        height += rowMaxHeight
        height += paddingTop + paddingBottom

        if (row == 0) {
            width = rowWidth
            width += paddingLeft + paddingRight
        } else {
            width = widthSize
        }


        setMeasuredDimension(
                if (widthMode == MeasureSpec.EXACTLY) widthSize else width,
                if (heightMode == MeasureSpec.EXACTLY) heightSize else height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childRight = measuredWidth - paddingRight
        val childBottom = measuredHeight - paddingBottom

        val childWidth = childRight - childLeft
        val childHeight = childBottom - childTop

        var maxHeight = 0
        var currentLeft = childLeft
        var currentTop = childTop

        var currentWidth: Int
        var currentHeight: Int

        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                return
            }

            child.measure(
                    MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
            )

            currentWidth = child.measuredWidth
            currentHeight = child.measuredHeight

            if (currentLeft + currentWidth >= childRight) {
                currentLeft = childLeft
                currentTop += maxHeight
                maxHeight = 0
            }

            child.layout(currentLeft, currentTop, currentLeft + currentWidth, currentTop + currentHeight)

            if (maxHeight < currentHeight) {
                maxHeight = currentHeight
            }
            currentLeft += currentWidth
        }
    }

}