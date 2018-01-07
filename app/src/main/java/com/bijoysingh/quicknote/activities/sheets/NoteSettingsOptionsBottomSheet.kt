package com.bijoysingh.quicknote.activities.sheets

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.View
import com.bijoysingh.quicknote.R
import com.bijoysingh.quicknote.activities.MainActivity
import com.bijoysingh.quicknote.activities.ThemedActivity
import com.bijoysingh.quicknote.activities.external.ImportNoteFromFileActivity
import com.bijoysingh.quicknote.activities.external.getStoragePermissionManager
import com.bijoysingh.quicknote.activities.sheets.SortingOptionsBottomSheet.Companion.getSortingState
import com.bijoysingh.quicknote.items.OptionsItem
import com.github.bijoysingh.starter.prefs.DataStore
import com.github.bijoysingh.starter.util.IntentUtils

class NoteSettingsOptionsBottomSheet : OptionItemBottomSheetBase() {

  override fun setupViewWithDialog(dialog: Dialog) {
    setOptions(dialog, getOptions())
  }

  private fun getOptions(): List<OptionsItem> {
    val activity = context as MainActivity
    val dataStore = DataStore.get(context)
    val options = ArrayList<OptionsItem>()
    options.add(OptionsItem(
        title = R.string.home_option_markdown_settings,
        subtitle = R.string.home_option_markdown_settings_subtitle,
        icon = R.drawable.ic_action_markdown,
        listener = View.OnClickListener {
          MarkdownBottomSheet.openSheet(activity)
        }
    ))
    options.add(OptionsItem(
        title = R.string.home_option_security,
        subtitle = R.string.home_option_security_subtitle,
        icon = R.drawable.ic_option_security,
        listener = View.OnClickListener {
          SecurityOptionsBottomSheet.openSheet(activity)
          dismiss()
        }
    ))
    return options
  }

  private fun openExportSheet() {
    val activity = context as MainActivity
    val dataStore = DataStore.get(context)
    if (!SecurityOptionsBottomSheet.hasPinCodeEnabled(dataStore)) {
      ExportNotesBottomSheet.openSheet(activity)
      return
    }
    EnterPincodeBottomSheet.openUnlockSheet(
        context as ThemedActivity,
        object : EnterPincodeBottomSheet.PincodeSuccessListener {
          override fun onFailure() {
            openExportSheet()
          }

          override fun onSuccess() {
            ExportNotesBottomSheet.openSheet(activity)
          }
        },
        dataStore)
  }

  override fun getLayout(): Int = R.layout.layout_options_sheet

  companion object {

    const val SURVEY_LINK = "https://goo.gl/forms/UbE2lARpp89CNIbl2"
    const val KEY_LIST_VIEW = "KEY_LIST_VIEW"
    const val KEY_MARKDOWN_ENABLED = "KEY_MARKDOWN_ENABLED"
    const val KEY_MARKDOWN_HOME_ENABLED = "KEY_MARKDOWN_HOME_ENABLED"

    fun openSheet(activity: MainActivity) {
      val sheet = NoteSettingsOptionsBottomSheet()
      sheet.isNightMode = activity.isNightMode
      sheet.show(activity.supportFragmentManager, sheet.tag)
    }
  }
}