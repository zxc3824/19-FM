package setting

import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.set

class SettingProps {
    companion object {
        // 한 번만 불러올 내용은 val, 아니면 var
        private val file =
            if (File("Settings.properties").exists()) File("Settings.properties")
            else File("src/main/resources/Settings.properties")
        private val settings = Properties().apply {
            val fis = FileInputStream(file)
            load(fis)
            fis.close()
        }

        /** 주의
         * 프로퍼티명 변경 시 changeSettings 함수 내에 인자로 넣은 프로퍼티명도 변경해야 함 */

        // 기본 설정 경로(처음 또는 문제 발생 시 사용)
        internal val initialFolder = get("initialFolder")
        // 마지막 설정 경로
        internal var lastSelectedFolder
            get() = get("lastSelectedFolder")
            set(value) { set("lastSelectedFolder", value) }
        // 검색어 유지
        internal var remainCheck
            get() = settings["remainCheck"].toString().toBoolean()
            set(value) { set("remainCheck", value.toString()) }
        // 대소문자 구분
        internal var noIgnoreCaseCheck
            get() = settings["noIgnoreCaseCheck"].toString().toBoolean()
            set(value) { set("noIgnoreCaseCheck", value.toString()) }
        // 정규식 사용
        internal var useRegexCheck
            get() = settings["useRegexCheck"].toString().toBoolean()
            set(value) { set("useRegexCheck", value.toString()) }
        // 중복 처리
        /** 1-이름변경, 2-덮어쓰기, 3-작업중단 */
        internal var existsSelected
            get() = settings["existsSelected"].toString().toInt()
            set(value) { set("existsSelected", value.toString()) }
        // 내부 폴더 검색
        internal var innerSearchCheck
            get() = settings["innerSearchCheck"].toString().toBoolean()
            set(value) { set("innerSearchCheck", value.toString()) }
        // 내부 폴더 이동
        internal var innerMoveCheck
            get() = settings["innerMoveCheck"].toString().toBoolean()
            set(value) { set("innerMoveCheck", value.toString()) }
        // 폰트
        internal val font = get("font")
        // 설정
        internal val width = get("width").toDouble()
        internal val fontSize = get("fontSize").toDouble()
        // 테이블 설정
        internal val tableHeight = get("tableHeight").toDouble()
        internal val extensionWidth = get("extensionWidth").toDouble()
        internal val beforeWidth = get("beforeWidth").toDouble()
        internal val afterWidth = get("afterWidth").toDouble()
        internal val pathWidth = get("pathWidth").toDouble()
        internal val cellSIze = get("cellSIze").toDouble()
        internal val cellFontSize = get("cellFontSize").toDouble()
        // 미리보기
        internal val previewFontSize = get("previewFontSize").toDouble()

        private fun get(prop: String) =
            String(settings.getProperty(prop).toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))

        private fun set(prop: String, value: String) {
            settings[prop] = value
        }
    }
}