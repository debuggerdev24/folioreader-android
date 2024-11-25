package com.folioreader.ui.view

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.folioreader.Config
import com.folioreader.R
import com.folioreader.databinding.ViewConfigBinding
import com.folioreader.model.event.ReloadDataEvent
import com.folioreader.ui.activity.FolioActivity
import com.folioreader.ui.activity.FolioActivityCallback
import com.folioreader.ui.adapter.FontAdapter
import com.folioreader.ui.fragment.MediaControllerFragment
import com.folioreader.util.AppUtil
import com.folioreader.util.UiUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.greenrobot.eventbus.EventBus

/**
 * Created by mobisys2 on 11/16/2016.
 */
class ConfigBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val FADE_DAY_NIGHT_MODE = 500

        @JvmField
        val LOG_TAG: String = ConfigBottomSheetDialogFragment::class.java.simpleName
    }

    private lateinit var config: Config
    private lateinit var binding: ViewConfigBinding
    private var isNightMode = false
    private lateinit var activityCallback: FolioActivityCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is FolioActivity)
            activityCallback = activity as FolioActivity

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }

        config = AppUtil.getSavedConfig(activity)!!
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.viewTreeObserver?.addOnGlobalLayoutListener(null)
    }

    private fun initViews() {
        inflateView()
        configFonts()
        binding.viewConfigFontSizeSeekBar.progress = config.fontSize
        configSeekBar()
        selectFont(config.font, false)
        isNightMode = config.isNightMode
        if (isNightMode) {
            binding.container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.night))
        } else {
            binding.container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
        }

        if (isNightMode) {
            binding.viewConfigIbDayMode.isSelected = false
            binding.viewConfigIbNightMode.isSelected = true
            UiUtil.setColorIntToDrawable(
                config.currentThemeColor,
                binding.viewConfigIbNightMode.drawable
            )
            UiUtil.setColorResToDrawable(R.color.app_gray, binding.viewConfigIbDayMode.drawable)
        } else {
            binding.viewConfigIbDayMode.isSelected = true
            binding.viewConfigIbNightMode.isSelected = false
            UiUtil.setColorIntToDrawable(
                config.currentThemeColor,
                binding.viewConfigIbDayMode.drawable
            )
            UiUtil.setColorResToDrawable(R.color.app_gray, binding.viewConfigIbNightMode.drawable)
        }
    }

    private fun inflateView() {

        if (config.allowedDirection != Config.AllowedDirection.VERTICAL_AND_HORIZONTAL) {
            binding.view5.visibility = View.GONE
            binding.buttonVertical.visibility = View.GONE
            binding.buttonHorizontal.visibility = View.GONE
        }

        binding.viewConfigIbDayMode.setOnClickListener {
            isNightMode = true
            toggleBlackTheme()
            binding.viewConfigIbDayMode.isSelected = true
            binding.viewConfigIbNightMode.isSelected = false
            setToolBarColor()
            setAudioPlayerBackground()
            UiUtil.setColorResToDrawable(R.color.app_gray, binding.viewConfigIbNightMode.drawable)
            UiUtil.setColorIntToDrawable(config.currentThemeColor, binding.viewConfigIbDayMode.drawable)
            dialog?.hide()
        }

        binding.viewConfigIbNightMode.setOnClickListener {
            isNightMode = false
            toggleBlackTheme()
            binding.viewConfigIbDayMode.isSelected = false
            binding.viewConfigIbNightMode.isSelected = true
            UiUtil.setColorResToDrawable(R.color.app_gray, binding.viewConfigIbDayMode.drawable)
            UiUtil.setColorIntToDrawable(
                config.currentThemeColor,
                binding.viewConfigIbNightMode.drawable
            )
            setToolBarColor()
            setAudioPlayerBackground()
            dialog?.hide()
        }

        if (activityCallback.direction == Config.Direction.HORIZONTAL) {
            binding.buttonHorizontal.isSelected = true
        } else if (activityCallback.direction == Config.Direction.VERTICAL) {
            binding.buttonVertical.isSelected = true
        }

        binding.buttonVertical.setOnClickListener {
            config = AppUtil.getSavedConfig(context)!!
            config.direction = Config.Direction.VERTICAL
            AppUtil.saveConfig(context, config)
            activityCallback.onDirectionChange(Config.Direction.VERTICAL)
            binding.buttonHorizontal.isSelected = false
            binding.buttonVertical.isSelected = true
        }

        binding.buttonHorizontal.setOnClickListener {
            config = AppUtil.getSavedConfig(context)!!
            config.direction = Config.Direction.HORIZONTAL
            AppUtil.saveConfig(context, config)
            activityCallback.onDirectionChange(Config.Direction.HORIZONTAL)
            binding.buttonHorizontal.isSelected = true
            binding.buttonVertical.isSelected = false
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun configFonts() {

        val colorStateList = UiUtil.getColorList(
            config.currentThemeColor,
            ContextCompat.getColor(context!!, R.color.grey_color)
        )

        binding.buttonVertical.setTextColor(colorStateList)
        binding.buttonHorizontal.setTextColor(colorStateList)

        val adapter = FontAdapter(config, context!!)

        binding.viewConfigFontSpinner.adapter = adapter

        binding.viewConfigFontSpinner.background.setColorFilter(
            if (config.isNightMode) {
                R.color.night_default_font_color
            } else {
                R.color.day_default_font_color
            },
            PorterDuff.Mode.SRC_ATOP
        )

        val fontIndex = adapter.fontKeyList.indexOf(config.font)
        binding.viewConfigFontSpinner.setSelection(if (fontIndex < 0) 0 else fontIndex)

        binding.viewConfigFontSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectFont(adapter.fontKeyList[position], true)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun selectFont(selectedFont: String, isReloadNeeded: Boolean) {
        // parse font from name
        config.font = selectedFont

        if (isAdded && isReloadNeeded) {
            AppUtil.saveConfig(activity, config)
            EventBus.getDefault().post(ReloadDataEvent())
        }
    }

    private fun toggleBlackTheme() {

        val day = ContextCompat.getColor(context!!, R.color.white)
        val night = ContextCompat.getColor(context!!, R.color.night)

        val colorAnimation = ValueAnimator.ofObject(
            ArgbEvaluator(),
            if (isNightMode) night else day, if (isNightMode) day else night
        )
        colorAnimation.duration = FADE_DAY_NIGHT_MODE.toLong()

        colorAnimation.addUpdateListener { animator ->
            val value = animator.animatedValue as Int
            binding.container.setBackgroundColor(value)
        }

        colorAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}

            override fun onAnimationEnd(animator: Animator) {
                isNightMode = !isNightMode
                config.isNightMode = isNightMode
                AppUtil.saveConfig(activity, config)
                EventBus.getDefault().post(ReloadDataEvent())
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })

        colorAnimation.duration = FADE_DAY_NIGHT_MODE.toLong()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val attrs = intArrayOf(android.R.attr.navigationBarColor)
            val typedArray = activity?.theme?.obtainStyledAttributes(attrs)
            val defaultNavigationBarColor = typedArray?.getColor(
                0,
                ContextCompat.getColor(context!!, R.color.white)
            )
            val black = ContextCompat.getColor(context!!, R.color.black)

            val navigationColorAnim = ValueAnimator.ofObject(
                ArgbEvaluator(),
                if (isNightMode) black else defaultNavigationBarColor,
                if (isNightMode) defaultNavigationBarColor else black
            )

            navigationColorAnim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                activity?.window?.navigationBarColor = value
            }

            navigationColorAnim.duration = FADE_DAY_NIGHT_MODE.toLong()
            navigationColorAnim.start()
        }

        colorAnimation.start()
    }

    private fun configSeekBar() {
        val thumbDrawable = ContextCompat.getDrawable(activity!!, R.drawable.seekbar_thumb)
        UiUtil.setColorIntToDrawable(config.currentThemeColor, thumbDrawable)
        UiUtil.setColorResToDrawable(
            R.color.grey_color,
            binding.viewConfigFontSizeSeekBar.progressDrawable
        )
        binding.viewConfigFontSizeSeekBar.thumb = thumbDrawable

        binding.viewConfigFontSizeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                config.fontSize = progress
                AppUtil.saveConfig(activity, config)
                EventBus.getDefault().post(ReloadDataEvent())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setToolBarColor() {
        if (isNightMode) {
            activityCallback.setDayMode()
        } else {
            activityCallback.setNightMode()
        }
    }

    private fun setAudioPlayerBackground() {

        var mediaControllerFragment: Fragment? =
            fragmentManager?.findFragmentByTag(MediaControllerFragment.LOG_TAG)
                ?: return
        mediaControllerFragment = mediaControllerFragment as MediaControllerFragment
        if (isNightMode) {
            mediaControllerFragment.setDayMode()
        } else {
            mediaControllerFragment.setNightMode()
        }
    }
}
