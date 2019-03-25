package setting

import java.io.File
import java.io.FileInputStream
import java.util.*

class StringProps {
    companion object {

        // 한 번만 불러올 내용은 val, 아니면 var
        private val file =
            if (File("Strings.properties").exists()) File("Strings.properties")
            else File("src/main/resources/Strings.properties")
        private val strings = Properties().apply {
            val fis = FileInputStream(file)
            load(fis)
            fis.close()
        }

        // 상단 이름
        internal val flowText1 = strings["flowText1"] as String
        internal val flowText2 = strings["flowText2"] as String
        internal val flowText3 = strings["flowText3"] as String

        // 폴더 경로
        internal val folderBorder = strings["folderBorder"] as String
        internal val folderSearchLabel = strings["folderSearchLabel"] as String
        internal val folderMoveLabel = strings["folderMoveLabel"] as String
        internal val folderSelect = strings["folderSelect"] as String

        // 검색 조건(폴더 경로)
        internal val existsLabel = strings["existsLabel"] as String
        internal val exists1 = strings["exists1"] as String
        internal val exists2 = strings["exists2"] as String
        internal val exists3 = strings["exists3"] as String
        internal val innerSearchCheck = strings["innerSearchCheck"] as String
        internal val innerMoveCheck = strings["innerMoveCheck"] as String

        // 검색 조건
        internal val searchBorder = strings["searchBorder"] as String
        internal val searchTextLabel = strings["searchTextLabel"] as String
        internal val remainCheck = strings["remainCheck"] as String
        internal val noIgnoreCaseCheck = strings["noIgnoreCaseCheck"] as String
        internal val useRegexCheck = strings["useRegexCheck"] as String
        internal val searchExtensionLabel = strings["searchExtensionLabel"] as String

        // 이름 변경
        internal val namingBorder = strings["namingBorder"] as String
        internal val namingSearchBtn = strings["namingSearchBtn"] as String
        internal val namingAddFileBtn = strings["namingAddFileBtn"] as String
        internal val namingAll = strings["namingAll"] as String
        internal val namingDelete = strings["namingDelete"] as String
        internal val namingRollback = strings["namingRollback"] as String
        internal val namingText = strings["namingText"] as String
        internal val namingNumber = strings["namingNumber"] as String
        internal val extension = strings["extension"] as String
        internal val beforeFileName = strings["beforeFileName"] as String
        internal val afterFileName = strings["afterFileName"] as String
        internal val filePath = strings["filePath"] as String

        // 이동/복사
        internal val moveBtn = strings["moveBtn"] as String
        internal val copyBtn = strings["copyBtn"] as String
    }
}