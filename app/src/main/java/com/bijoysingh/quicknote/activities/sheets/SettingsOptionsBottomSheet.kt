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

class SettingsOptionsBottomSheet : OptionItemBottomSheetBase() {

  override fun setupViewWithDialog(dialog: Dialog) {
    setOptions(dialog, getOptions())
  }

  private fun getOptions(): List<OptionsItem> {
    val activity = context as MainActivity
    val dataStore = DataStore.get(context)
    val options = ArrayList<OptionsItem>()
    options.add(OptionsItem(
        title = R.string.home_option_ui_experience,
        subtitle = R.string.home_option_ui_experience_subtitle,
        icon = R.drawable.ic_action_grid,
        listener = View.OnClickListener {
          UISettingsOptionsBottomSheet.openSheet(activity)
          dismiss()
        }
    ))
    options.add(OptionsItem(
        title = R.string.home_option_note_settings,
        subtitle = R.string.home_option_note_settings_subtitle,
        icon = R.drawable.ic_subject_white_48dp,
        listener = View.OnClickListener {
          NoteSettingsOptionsBottomSheet.openSheet(activity)
          dismiss()
        }
    ))
    options.add(OptionsItem(
        title = R.string.home_option_export,
        subtitle = R.string.home_option_export_subtitle,
        icon = R.drawable.ic_export,
        listener = View.OnClickListener {
          val manager = getStoragePermissionManager(activity)
          if (manager.hasAllPermissions()) {
            openExportSheet()
            dismiss()
          } else {
            PermissionBottomSheet.openSheet(activity)
          }
        }
    ))
    options.add(OptionsItem(
        title = R.string.home_option_import,
        subtitle = R.string.home_option_import_subtitle,
        icon = R.drawable.ic_import,
        listener = View.OnClickListener {
          val manager = getStoragePermissionManager(activity)
          if (manager.hasAllPermissions()) {
            IntentUtils.startActivity(activity, ImportNoteFromFileActivity::class.java)
            dismiss()
          } else {
            PermissionBottomSheet.openSheet(activity)
          }
        }
    ))
    options.add(OptionsItem(
        title = R.string.home_option_about,
        subtitle = R.string.home_option_about_subtitle,
        icon = R.drawable.ic_info,
        listener = View.OnClickListener {
          AboutSettingsOptionsBottomSheet.openSheet(activity)
          dismiss()
        }
    ))
    options.add(OptionsItem(
        title = R.string.home_option_rate_and_review,
        subtitle = R.string.home_option_rate_and_review_subtitle,
        icon = R.drawable.ic_rating,
        listener = View.OnClickListener {
          IntentUtils.openAppPlayStore(activity)
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
      val sheet = SettingsOptionsBottomSheet()
      sheet.isNightMode = activity.isNightMode
      sheet.show(activity.supportFragmentManager, sheet.tag)
    }
  }
}