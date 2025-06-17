package com.moehr.habit_3.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
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
import com.moehr.habit_3.R
import com.moehr.habit_3.data.preferences.SharedPreferencesManager
import androidx.core.view.isVisible

class SettingsFragment : Fragment() {

    private lateinit var pushToggle: LinearLayout
    private lateinit var themeToggle: LinearLayout
    private lateinit var pushSection: ConstraintLayout
    private lateinit var themeSection: ConstraintLayout

    private lateinit var iconSwitch: Switch
    private lateinit var appSwitch: Switch

    private lateinit var btnSettingsSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Toggle headers
        pushToggle = view.findViewById(R.id.push_toggle)
        themeToggle = view.findViewById(R.id.theme_toggle)

        // Expandable sections
        pushSection = view.findViewById(R.id.push_section)
        themeSection = view.findViewById(R.id.theme_section)

        pushSection.visibility = View.GONE
        themeSection.visibility = View.GONE

        appSwitch = view.findViewById(R.id.switch_app)

        btnSettingsSave = view.findViewById(R.id.btnSettingsSave)

        appSwitch.setOnCheckedChangeListener { _, isChecked ->
            appSwitch.text = if (isChecked) getString(R.string.settings_dark) else getString(R.string.settings_light)
            SharedPreferencesManager.setTheme(requireContext(), if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }

        btnSettingsSave.setOnClickListener {
            saveSettings()
        }

        view.findViewById<TextView>(R.id.textView19).setOnClickListener {
            showCreditsDialog()
        }

        view.findViewById<TextView>(R.id.textView18).setOnClickListener {
            showInspirationDialog()
        }

        val prefs = requireContext().getSharedPreferences("habit3_prefs", Context.MODE_PRIVATE)
        val isDark = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) == AppCompatDelegate.MODE_NIGHT_YES
        appSwitch.isChecked = isDark

        setupToggleSections()
        return view
    }

    private fun showInspirationDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_inspiration, null)
        val webView = dialogView.findViewById<WebView>(R.id.markdownWebView)

        // Optional: enable basic styling
        webView.settings.apply {
            javaScriptEnabled = false
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        webView.loadUrl("file:///android_asset/README.md.html")

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showCreditsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_credits, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun saveSettings() {
        val pushMorning = view?.findViewById<EditText>(R.id.editText4)?.text.toString()
        val pushNoon = view?.findViewById<EditText>(R.id.editText)?.text.toString()
        val pushEvening = view?.findViewById<EditText>(R.id.editText2)?.text.toString()
        val pushCustom = view?.findViewById<EditText>(R.id.editText3)?.text.toString()

        val icon = iconSwitch.isChecked
        val app = appSwitch.isChecked

        SharedPreferencesManager.saveSettings(requireContext(), pushMorning, pushNoon, pushEvening, pushCustom, icon, app)
    }

    private fun setupToggleSections() {
        pushToggle.setOnClickListener {
            toggleSection(pushSection)
        }

        themeToggle.setOnClickListener {
            toggleSection(themeSection)
        }
    }

    private fun toggleSection(section: ConstraintLayout) {
        section.visibility = if (section.isVisible) View.GONE else View.VISIBLE
    }
}

