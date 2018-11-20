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

package com.zipsted.justtimer.editevent

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.NumberPicker
import com.zipsted.justtimer.R

class DurationPickerFragment : DialogFragment() {

    private lateinit var listener: DurationPickerListener

    interface DurationPickerListener {
        fun onDurationSet(min: Int, sec: Int)
        fun onDurationCancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val min = arguments?.getInt(MIN_KEY)
        val sec = arguments?.getInt(SEC_KEY)

        val builder: AlertDialog.Builder = activity.let {
            AlertDialog.Builder(it)
        }

        val pickerView = activity?.layoutInflater?.inflate(R.layout.duration_picker_dialog, null)

        val minPicker: NumberPicker? = pickerView?.findViewById(R.id.minPicker)
        val secPicker: NumberPicker? = pickerView?.findViewById(R.id.secPicker)

        minPicker?.minValue = MIN_VALUE
        minPicker?.maxValue = MAX_VALUE
        minPicker?.value = min ?: 1
        secPicker?.setFormatter {
            String.format("%02d", it)
        }
        secPicker?.minValue = MIN_VALUE
        secPicker?.maxValue = MAX_VALUE
        secPicker?.value = sec ?: 0

        builder.setView(pickerView)
                .setTitle(R.string.eventDuration)
        builder.apply {
            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                listener.onDurationCancel()
                dialog.cancel()
            }
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                listener.onDurationSet(minPicker?.value ?: 1, secPicker?.value ?: 0)
                dialog.dismiss()
            }
        }

        return builder.create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as DurationPickerListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString()) + " must implement DurationPickerListener")
        }
    }

    companion object {
        const val TAG = "DurationPicker"
        const val MIN_KEY = "MIN_KEY"
        const val SEC_KEY = "SEC_KEY"
        const val MIN_VALUE = 0
        const val MAX_VALUE = 59

        fun newInstance(min: Int, sec: Int): DurationPickerFragment {
            return DurationPickerFragment().apply {
                arguments = Bundle().apply {
                    putInt(MIN_KEY, min)
                    putInt(SEC_KEY, sec)
                }
            }
        }
    }

}