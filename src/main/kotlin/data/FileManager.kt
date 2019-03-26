package data

import javafx.beans.property.SimpleStringProperty
import org.controlsfx.dialog.ProgressDialog
import setting.MyStyles
import setting.SettingProps
import tornadofx.style
import tornadofx.task
import java.nio.file.*
import java.util.function.BiPredicate
import kotlin.streams.toList
import java.nio.file.Paths
import java.nio.file.Files



class FileManager {

    enum class Mode { MOVE, COPY }

    /* 고려 사항

    종류
    검색 경로, 이동 경로, 검색 조건, 이름 변경, 내부 폴더 검색, 내부 폴더 이동, 중복 처리(숫자부여, 덮어쓰기, 작업중단)
    검색어, 확장자, 검색어 유지(여기에선 미사용), 대소문자 구분, 정규식 사용
    파일 목록(Path 객체, 변경될 이름)

    설명
    검색 경로 - 검색 시에 사용될 경로
    이동 경로 - 이동 시에 사용될 경로
    검색 조건 - 검색 조건 사용 여부
    이름 변경 - 이름 변경 사용 여부
    내부 폴더 검색 - 검색 시 폴더 안의 폴더 내부도 검색할 지 여부
    내부 폴더 이동 - 내부 폴더 검색 O 상태일 경우 내부 폴더도 같이 옮길 지 여부
    중복 처리 - 옮길 위치에 동일 파일이 있는 경우의 처리 방식
        이름변경 - '이름 (1).확장자' 형식으로 이름을 변경
        덮어쓰기 - 삭제 후 이동
        작업중단 - 오류 메시지 및 작업 중단
    검색어 - 검색 조건 사용 시에 검색할 파일 이름
    확장자 - 검색 조건 사용 시에 검색할 확장자 이름
    검색어 유지 - 검색 조건 사용 시, 검색 후 검색어와 확장자를 삭제할 지 여부
    대소문자 구분 - 검색어(확장자는 X)에서 대소문자를 구분할 지 여부
    정규식 사용 - 검색어에 정규식을 사용할 지 여부. 사용 시 확장자 텍스트 필드는 제거
    파일 목록 - 이름 변경 사용 시에 변경을 위해 검색한 아이템 목록 및 변경할 이름

    유의
    내부 폴더 이동
    - 이름 변경 시엔 각 파일 별로 경로가 다를 수 있으므로 사용 불가. 무조건 한 폴더에 몰아서 이동
    - 이동 경로가 빈 경우 동작하지 않음(의미 없음)

    상황 별 사용 종류
    이름 변경 X, 검색 조건 X
    - 이동,복사: 검색 경로, 이동 경로, 내부 폴더 검색, 내부 폴더 이동, 중복 처리
    이름 변경 X, 검색 조건 O
    - 이동,복사: 검색 경로, 이동 경로, 내부 폴더 검색, 내부 폴더 이동, 중복 처리, 검색어, 확장자, 대소문자 구분, 정규식 사용
    이름 변경 O, 검색 조건 X
    - 검색: 검색 경로, 내부 폴더 검색
    - 이동,복사: 이동 경로, 중복 처리, 파일 목록
    이름 변경 O, 검색 조건 O
    - 검색: 검색 경로, 내부 폴더 검색
    - 이동,복사: 이동 경로, 중복 처리, 파일 목록*/

    // 이름 변경 X
    fun moveFile(
        searchPath: String,
        movePath: String,
        searchText: String = "",
        searchExtension: String = "",
        mode: Mode
    ): Int {
        val searchFiles = searchFile(searchPath, searchText, searchExtension, SettingProps.innerSearchCheck)
        val moveFiles =
            if (movePath != "") searchFile(movePath, searchText, searchExtension, SettingProps.innerMoveCheck)
            else searchFile(searchPath, searchText, searchExtension, SettingProps.innerMoveCheck)

        if (searchFiles.isEmpty()) return -1

        val changedSearchFiles =
            if (SettingProps.existsSelected != 2) changedList(searchPath, movePath, searchFiles) else moveFiles

        if (changedSearchFiles.isEmpty() || searchFiles.size != changedSearchFiles.size) return -2

        return move(searchFiles, changedSearchFiles, SettingProps.existsSelected == 2, mode)
    }

    // 이름 변경 O
    fun moveFile(fileList: List<FileItem>, movePath: String = "", mode: Mode): Int {
        if (fileList.isEmpty()) return -1

        // 원래 파일명
        val originalFiles = fileList.map { it.path }

        // 변경할 파일명
        val listFiles = fileList.map { Paths.get("${it.filePath.value}\\${it.afterName}${it.extension.value}") }
        val moveFiles = if (movePath != "") searchFile(movePath, innerSearch = false) // 이동할 위치에 있는 파일 검색
        else { // movePath 가 빈 경우
            val list = mutableListOf<Path>()
            fileList.map { it.path.parent.toString() }
                .distinct()
                .map { searchFile(it, innerSearch = false) }
                .forEach { list.addAll(it) }
            list.distinct()
        }
        val changedSearchFiles =
            if (SettingProps.existsSelected != 2) changedList("", movePath, listFiles) else moveFiles

        if (changedSearchFiles.isEmpty() || listFiles.size != changedSearchFiles.size) return -2

        return move(originalFiles, changedSearchFiles, SettingProps.existsSelected == 2, mode)
    }

    private fun move(fromFiles: List<Path>, toFiles: List<Path>, overwrite: Boolean = false, mode: Mode): Int {
        try {
            val t = SimpleStringProperty("")
            val total = fromFiles.size.toLong()
            ProgressDialog(task {
                updateProgress(0, total)
                updateMessage("0 of $total")

                if (overwrite) {
                    for (i in 0 until fromFiles.size) {
                        if (Files.exists(toFiles[i], LinkOption.NOFOLLOW_LINKS)) Files.delete(toFiles[i])
                        if (mode == Mode.MOVE) Files.move(fromFiles[i], toFiles[i])
                        else Files.copy(fromFiles[i], toFiles[i])

                        updateProgress(i.toLong(), total)
                        updateMessage("$i of $total")
                        tornadofx.runLater {
                            t.value = (((i.toDouble() / total.toDouble()) * 100).toInt()).toString() + "% 완료"
                        }
                    }
                } else {
                    for (i in 0 until fromFiles.size) {
                        Files.move(fromFiles[i], fromFiles[i]) // 원자성을 위해서 파일이 열렸는지 체크
                    }
                    for (i in 0 until fromFiles.size) {
                        if (Files.notExists(toFiles[i].parent)) Files.createDirectories(toFiles[i].parent)
                        if (mode == Mode.MOVE) Files.move(fromFiles[i], toFiles[i])
                        else Files.copy(fromFiles[i], toFiles[i])

                        updateProgress(i.toLong(), total)
                        updateMessage("$i of $total")
                        tornadofx.runLater {
                            t.value = (((i.toDouble() / total.toDouble()) * 100).toInt()).toString() + "% 완료"
                        }
                    }
                }
            }).apply {
                titleProperty().bind(t)
                dialogPane.style { fontFamily = SettingProps.font }
                contentText = if (mode == Mode.MOVE) "이동 중" else "복사 중"
            }.showAndWait()
        } catch (e: FileSystemException) {
            return -3
        }

        return toFiles.size
    }

    fun searchFile(
        path: String,
        searchText: String = "",
        searchExtension: String = "",
        innerSearch: Boolean
    ): List<Path> {
        val stream = Files.find(
            Paths.get(path),
            if (innerSearch) 100 else 1,
            BiPredicate { filePath, basicFileAttributes ->
                val file = filePath.toFile()
                val fileName = file.nameWithoutExtension
                val fileExtension = file.extension
                !basicFileAttributes.isDirectory
                        && if (!SettingProps.useRegexCheck) {
                    fileName.contains(searchText, !SettingProps.noIgnoreCaseCheck)
                            && fileExtension.contains(searchExtension, true) // 확장자는 대소문자 구분X
                } else {
                    fileName.contains(
                        "${if (SettingProps.noIgnoreCaseCheck) "" else "(?i)"}$searchText".toRegex()
                    )
                }
            }
        )

        return stream.toList() // Stream 은 명령을 한 번 실행한 후엔 없어지므로 테스트는 여기에서
    }

    // 변경될 파일 이름 정하기
    private fun changedList(searchPath: String, movePath: String, searchFiles: List<Path>): List<Path> {
        val isSelf = movePath == "" // 경로 동일시 여부
        val isNaming = searchPath == "" // 이름 변경 체크 여부

        val searchStrings = searchFiles.map {
            // 변경될 경로 반영
            "${when {
                isSelf -> it.parent.toString()
                !isNaming && SettingProps.innerSearchCheck && SettingProps.innerMoveCheck ->
                    movePath + it.parent.toString().substringAfter(searchPath)
                else -> movePath
            }}\\${it.fileName}"
        }.toMutableList()

        val changedSearchStrings = mutableListOf<String>()

        when (SettingProps.existsSelected) {
            1 -> { // 이름에 숫자 붙이기(검색된 리스트와 이동할 경로 양 쪽 다 통과할 때까지 숫자 증가)
                var num = 1
                for (i in 0 until searchStrings.size) {
                    val fileName =
                        "${searchStrings[i].substringBeforeLast("\\")}\\${searchFiles[i].toFile().nameWithoutExtension}"
                    val fileExtension = searchFiles[i].toFile().extension

                    while (true) {
                        if (Files.exists(Paths.get(searchStrings[i]))
                            || (isSelf && changedSearchStrings.contains(searchStrings[i])))
                            searchStrings[i] = "$fileName (${num++}).$fileExtension"
                        else {
                            changedSearchStrings.add(searchStrings[i])
                            num = 1
                            break
                        }
                    }
                }
            }
            3 -> { // 겹치면 빈 리스트 반환(작업 중지)
                for (i in 0 until searchFiles.size) {
                    if (Files.exists(Paths.get(searchStrings[i]))) return mutableListOf()
                    else changedSearchStrings.add(searchStrings[i])
                }
            }
        }

        return changedSearchStrings.map { Paths.get(it) }
    }
}