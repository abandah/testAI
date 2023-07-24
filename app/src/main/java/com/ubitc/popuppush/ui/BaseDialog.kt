package com.ubitc.popuppush.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModelProvider
import com.ubitc.popuppush.App
import com.ubitc.popuppush.R
import com.ubitc.popuppush.databinding.DialogBaseBinding

class BaseDialog(
    private val parent: BaseActivity,
    private val gravity: Int,
    val root: View,
    private val isFull: Boolean,
    private val backGroundColor: Int,
    private val enableDim: Boolean,
    private val cancelable: Boolean,
    private val cancelOnTouchOutside: Boolean,
    private val blurBehind: Boolean,
    private val onShowListener: (() -> Unit)?,
    private val onDismissListener: ((BaseDialog) -> Unit)?
) : Dialog(parent, if (isFull) R.style.Theme_DialogFullscreen else R.style.Theme_Dialog) {
    var viewModel: BaseViewModel? = null
    private var binding: DialogBaseBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!enableDim)
            window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        super.onCreate(savedInstanceState)

        if (blurBehind)
            window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)

        setCancelable(cancelable)
        setCanceledOnTouchOutside(cancelOnTouchOutside)

        window?.setGravity(gravity)

        setOwnerActivity(parent)

        if (isFull)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

        viewModel = ViewModelProvider(parent)[BaseViewModel::class.java]
        binding = DialogBaseBinding.inflate(layoutInflater)

        binding!!.cardView.setCardBackgroundColor(App.getColor(backGroundColor))
        binding!!.viewModel = viewModel
        binding!!.body.addView(root)
        setContentView(binding!!.root)


    }


    override fun show() {
        onShowListener?.invoke()
        super.show()
    }

    override fun dismiss() {
      //  onDismissListener?.invoke()
        try {
            super.dismiss()
        } catch (_: Exception) {
        }
    }

    fun finishedDialog() {
        onDismissListener?.invoke(this)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                finishedDialog()
                return true
            }
        }
        return false
        // return App.currentActivity?.onKeyDown(keyCode, event)!!
    }


    data class Builder(val parent: BaseActivity = App.currentActivity!!) {

        private var isFull: Boolean = false
        private var gravity: Int = Gravity.CENTER
        private var root: View? = null
        private var enableDim = true
        private var cancelable = true
        private var cancelOnTouchOutside = true
        private var cancelOnBackPress = true
        private var onDismissListener: ((BaseDialog) -> Unit)? = null
        private var onShowListener: (() -> Unit)? = null
        private var blurBehind = false
        private var backGroundColor = R.color.lb_tv_white

        fun withTheme(
            dim: Boolean = true,
            cancelable: Boolean = true,
            cancelOnTouchOutside: Boolean = true,
            cancelOnBackPress: Boolean = true,
            blurBehind: Boolean = false,
            gravity: Int = Gravity.CENTER,
            @ColorRes backGroundColor: Int = R.color.lb_tv_white
        ) = apply {
            this@Builder.enableDim = dim
            this.cancelable = cancelable
            this.cancelOnTouchOutside = cancelOnTouchOutside
            this.cancelOnBackPress = cancelOnBackPress
            this.blurBehind = blurBehind
            this.gravity = gravity
            this.backGroundColor = backGroundColor
        }

        fun content(root: View) = apply {
            this.root = root
        }

        fun whenShowDo(onShowListener: (() -> Unit)?) = apply {
            this.onShowListener = onShowListener
        }

        fun whenFinishDo(onDismissListener: ((BaseDialog) -> Unit)?) = apply {
            this.onDismissListener = onDismissListener
        }

        fun isFullscreen(isFull: Boolean) = apply {
            this.isFull = isFull
        }


        fun build(): BaseDialog {
            return BaseDialog(
                parent,
                gravity,
                root!!,
                isFull,
                backGroundColor,
                enableDim,
                cancelable,
                cancelOnTouchOutside,
                blurBehind,
                onShowListener,
                onDismissListener
            )
        }




    }

}