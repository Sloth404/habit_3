package com.moehr.habit_3.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.moehr.habit_3.R
import com.moehr.habit_3.data.preferences.SharedPreferencesManager

/**
 * Fragment that manages user settings including theme, notifications, and dialogs.
 */
class SettingsFragment : Fragment() {

    // UI components for toggling sections
    private lateinit var pushToggle: LinearLayout
    private lateinit var themeToggle: LinearLayout

    // Expandable sections for settings details
    private lateinit var pushSection: ConstraintLayout
    private lateinit var themeSection: ConstraintLayout

    // Switches for icon and app theme toggling
    private lateinit var iconSwitch: Switch
    private lateinit var appSwitch: Switch

    // Button to save the settings
    private lateinit var btnSettingsSave: Button

    // Selection states
    private var isPushSelectionOpen : Boolean = false
    private var isThemeSelectionOpen : Boolean = false

    // Id keys for savedInstanceState
    private val KEY_PUSH : String = "isPushSelectionOpen"
    private val KEY_TEHEME : String = "isThemeSelectionOpen"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize toggle headers and expandable sections
        pushToggle = view.findViewById(R.id.push_toggle)
        themeToggle = view.findViewById(R.id.theme_toggle)
        pushSection = view.findViewById(R.id.push_section)
        themeSection = view.findViewById(R.id.theme_section)

        // Initialize Selection States
        isPushSelectionOpen = savedInstanceState?.getBoolean(KEY_PUSH) ?: false
        isThemeSelectionOpen = savedInstanceState?.getBoolean(KEY_TEHEME) ?: false
        pushSection.visibility = if (isPushSelectionOpen) View.VISIBLE else View.GONE
        themeSection.visibility = if (isThemeSelectionOpen) View.VISIBLE else View.GONE


        Log.d("THEME OR SOME SHIT...", "PUSH: $isPushSelectionOpen")
        Log.d("THEME OR SOME SHIT...", "THEME: $isThemeSelectionOpen")

        // Initialize switches and buttons
        appSwitch = view.findViewById(R.id.switch_app)
        btnSettingsSave = view.findViewById(R.id.btnSettingsSave)

        // Set listener for theme switch
        appSwitch.setOnCheckedChangeListener { _, isChecked ->
            appSwitch.text = if (isChecked) getString(R.string.settings_dark) else getString(R.string.settings_light)
            SharedPreferencesManager.setTheme(
                requireContext(),
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Save settings when button clicked
        btnSettingsSave.setOnClickListener {
            saveSettings()
        }

        // Show credits dialog on clicking related text
        view.findViewById<TextView>(R.id.textView19).setOnClickListener {
            showCreditsDialog()
        }

        // Show inspiration dialog on clicking related text
        view.findViewById<TextView>(R.id.textView18).setOnClickListener {
            showInspirationDialog()
        }

        // Load current theme preference and set switch accordingly
        val prefs = requireContext().getSharedPreferences("habit3_prefs", Context.MODE_PRIVATE)
        val isDark = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) == AppCompatDelegate.MODE_NIGHT_YES
        appSwitch.isChecked = isDark

        // Setup expandable toggles for sections
        setupToggleSections()

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_PUSH, isPushSelectionOpen)
        outState.putBoolean(KEY_TEHEME, isThemeSelectionOpen)
    }

    /**
     * Shows an AlertDialog with inspiration content loaded from local HTML.
     */
    private fun showInspirationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_inspiration, null)
        val webView = dialogView.findViewById<WebView>(R.id.markdownWebView)

        // Configure WebView for simple display of HTML content
        webView.settings.apply {
            javaScriptEnabled = false
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        // Load local asset HTML file with markdown content
        webView.loadUrl("file:///android_asset/README.md.html")

        // Create and show dialog with transparent background
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    /**
     * Shows an AlertDialog with credits information.
     */
    private fun showCreditsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_credits, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    /**
     * Collects current user inputs and saves settings to SharedPreferences.
     */
    private fun saveSettings() {
        // Retrieve user inputs from EditTexts
        val pushMorning = view?.findViewById<EditText>(R.id.editText4)?.text.toString()
        val pushNoon = view?.findViewById<EditText>(R.id.editText)?.text.toString()
        val pushEvening = view?.findViewById<EditText>(R.id.editText2)?.text.toString()
        val pushCustom = view?.findViewById<EditText>(R.id.editText3)?.text.toString()

        // Read switch states
        val icon = iconSwitch.isChecked
        val app = appSwitch.isChecked

        // Save settings via SharedPreferencesManager
        SharedPreferencesManager.saveSettings(requireContext(), pushMorning, pushNoon, pushEvening, pushCustom, icon, app)
    }

    /**
     * Setup click listeners on toggle headers to expand/collapse sections.
     */
    private fun setupToggleSections() {
        pushToggle.setOnClickListener {
            toggleSection(pushSection)
            isPushSelectionOpen = !isPushSelectionOpen
        }

        themeToggle.setOnClickListener {
            toggleSection(themeSection)
            isThemeSelectionOpen = !isThemeSelectionOpen
        }
    }

    /**
     * Toggles visibility of the given section.
     */
    private fun toggleSection(section: ConstraintLayout) {
        section.visibility = if (section.isVisible) View.GONE else View.VISIBLE
    }
}
