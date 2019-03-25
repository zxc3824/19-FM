package setting
import java.io.File

class ChangeProps {

    /** Settings.properties 변경 메소드
     * @param propertyName 변경하고 싶은 property
     * @param replacement 변경할 내용*/
    fun changeSettings(propertyName: String, replacement: String) {

        val file = if (File("Settings.properties").exists()) File("Settings.properties")
        else File("src/main/resources/Settings.properties")

        // 기존 내용 불러오기
        var oldContent = ""
        val reader = file.bufferedReader()
        var line = reader.readLine()
        while (line != null) {
            oldContent += line + System.lineSeparator()
            line = reader.readLine()
        }

        // 내용 변경
        val newContent = oldContent.replace("$propertyName=.*".toRegex(), "$propertyName=$replacement")

        // 파일 수정
        val writer = file.writer()
        writer.write(newContent)

        // 입출력 중지
        reader.close()
        writer.close()
    }
}