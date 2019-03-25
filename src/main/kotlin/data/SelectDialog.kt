package data

import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Window
import setting.SettingProps
import java.io.File
import java.nio.file.Path

class SelectDialog {

    // Settings
    private var lastFolder = SettingProps.lastSelectedFolder

    private fun lastDir(): File {
        val lastDir = File(lastFolder)
        return if (lastDir.exists()) File(lastFolder) else File(SettingProps.initialFolder)
    }

    /** 폴더 다이얼로그 띄우기
     * @return - 선택한 경로 String(null 인 경우 "" 반환)*/
    fun folderDialog(ownerWindow: Window? = null): String {
        val directoryChooser = DirectoryChooser().apply {
            initialDirectory = lastDir()
        }

        val selectedDirectory = directoryChooser.showDialog(ownerWindow)

        lastFolder = selectedDirectory?.absolutePath ?: lastFolder

        return selectedDirectory?.absolutePath ?: ""
    }

    fun fileDialog(ownerWindow: Window? = null): List<Path> {
        val fileChooser = FileChooser().apply {
            initialDirectory = lastDir()
        }

        val selectedFiles = fileChooser.showOpenMultipleDialog(ownerWindow)

        lastFolder = if (selectedFiles != null) selectedFiles[0].parent else lastFolder

        return selectedFiles?.map { it.toPath() } ?: emptyList()
    }
}