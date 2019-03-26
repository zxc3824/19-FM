package setting

import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
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
        internal val flowText1 = get("flowText1")
        internal val flowText2 = get("flowText2")
        internal val flowText3 = get("flowText3")

        // 폴더 경로
        internal val folderBorder = get("folderBorder")
        internal val folderSearchLabel = get("folderSearchLabel")
        internal val folderMoveLabel = get("folderMoveLabel")
        internal val folderSelect = get("folderSelect")

        // 검색 조건(폴더 경로)
        internal val existsLabel = get("existsLabel")
        internal val exists1 = get("exists1")
        internal val exists2 = get("exists2")
        internal val exists3 = get("exists3")
        internal val innerSearchCheck = get("innerSearchCheck")
        internal val innerMoveCheck = get("innerMoveCheck")

        // 검색 조건
        internal val searchBorder = get("searchBorder")
        internal val searchTextLabel = get("searchTextLabel")
        internal val remainCheck = get("remainCheck")
        internal val noIgnoreCaseCheck = get("noIgnoreCaseCheck")
        internal val useRegexCheck = get("useRegexCheck")
        internal val searchExtensionLabel = get("searchExtensionLabel")

        // 이름 변경
        internal val namingBorder = get("namingBorder")
        internal val namingSearchBtn = get("namingSearchBtn")
        internal val namingAddFileBtn = get("namingAddFileBtn")
        internal val namingAll = get("namingAll")
        internal val namingDelete = get("namingDelete")
        internal val namingRollback = get("namingRollback")
        internal val namingText = get("namingText")
        internal val namingNumber = get("namingNumber")
        internal val extension = get("extension")
        internal val beforeFileName = get("beforeFileName")
        internal val afterFileName = get("afterFileName")
        internal val filePath = get("filePath")

        // 이동/복사
        internal val moveBtn = get("moveBtn")
        internal val copyBtn = get("copyBtn")

        private fun get(prop: String) =
            String(strings.getProperty(prop).toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
    }
}