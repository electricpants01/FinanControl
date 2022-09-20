package com.locotoDevTeam.financontrol.data.dialog

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.locotoDevTeam.financontrol.R

class MaterialAlert {

    companion object {
        fun showDialog(title: String, description: String, context: Context, onPositiveCompletion: () -> Unit, onNegativeCompletion: () -> Unit) {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(description)
                .setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                    onNegativeCompletion()
                }
                .setPositiveButton(context.getString(R.string.accept)) { dialog, which ->
                    onPositiveCompletion()
                }.show()
        }
    }
}