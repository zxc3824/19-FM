package view

import data.FileItem
import data.FileManager
import data.SelectDialog
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import org.controlsfx.control.PopOver
import setting.MyStyles
import setting.SettingProps
import setting.StringProps
import tornadofx.*
import tornadofx.controlsfx.borders
import java.io.*
import java.nio.file.Files
import java.util.regex.PatternSyntaxException


/*
-----작성 규칙-----

--Kotlin--
각 레이아웃 요소의 설정 내용은 가능하면 상단에 적고 하위 레이아웃 내용과는 1줄 이상 띄워서 분리
children 전체에 대한 적용 내용은 하위 레이아웃 아래에 적으며 1줄 이상 띄워서 분리

변수와 메소드들은 가급적이면 종류별로 묶은 후 주석으로 분류

텍스트는 Strings.properties 파일에 작성 후 이용
스타일은 가능하면 setting.MyStyles 클래스에 작성 후 이용

이벤트 처리는 짧으면 해당 요소 안에 내용 작성
길면 아래에 별도의 메소드를 작성하여 호출

View 클래스에는 View 와 Control 부분을 같이 작성하되 긴 Control 부분은 하단으로 분리

--Properties--
Settings, Strings 내에서 겹치는 부분은 변수명을 통일

새 Property 선언 시엔 같은 이름의 변수를 ChangeProps 클래스에 만들어서 사용하고
기존 프로퍼티 변경 시에도 이름을 통일(일일이 변경할 필요 x)
 */

class MainView : View("File Manager") {

    // Layouts to use
    private var searchFolder: TextField by singleAssign()
    private var moveFolder: TextField by singleAssign()
    private var conditionsChk: CheckBox by singleAssign()
    private var changeChk: CheckBox by singleAssign()
    private var innerSearchChk: CheckBox by singleAssign()
    private var innerMoveChk: CheckBox by singleAssign()
    private var searchText: TextField by singleAssign()
    private var searchExtension: TextField by singleAssign()
    private var remainTextChk: CheckBox by singleAssign()
    private var noIgnoreChk: CheckBox by singleAssign()
    private var regexChk: CheckBox by singleAssign()
    private var applyAllChk: CheckBox by singleAssign()
    private var fileTable: TableView<FileItem> by singleAssign()

    // Settings
    private val selectDialog by lazy { SelectDialog() }
    private val fileManager by lazy { FileManager() }

    // Init
    private val helpView by lazy { HelpView() }
    private val textNamingView by lazy { TextNamingView() }
    private val numberNamingView by lazy { NumberNamingView() }
    private val changeProperties by lazy { setting.ChangeProps() }
    private var innerSearchChecked by SimpleBooleanProperty(SettingProps.innerSearchCheck).toProperty()
    private var regexChecked by SimpleBooleanProperty(SettingProps.useRegexCheck).toProperty()
    private val existsToggle by lazy { ToggleGroup() }
    private val files = mutableListOf<FileItem>().observable()
    private var autoScrollThread: AutoScrollableTableThread? = null

    companion object {
        private val SERIALIZED_MIME_TYPE = DataFormat("application/x-java-serialized-object")
    }

    ////////////
    // Layout //
    ////////////
    override val root = vbox {

        // Settings
        runLater {
            addClass(MyStyles.main)
            style { fontFamily = SettingProps.font }
            searchFolder.requestFocus() // 포커스 변경
            primaryStage.sizeToScene() // 사이즈 맞춤 조절
        }

        // 상단 텍스트 & 도움말
        hbox {
            alignment = Pos.CENTER_LEFT
            paddingAll = 5.0
            padding = Insets(5.0, 8.0, 5.0, 8.0)
            style {
                backgroundColor += c("white")
            }
            textflow {
                text(StringProps.flowText1) {
                    fill = c("#0067ff")
                    style {
                        fontFamily = SettingProps.font
                        fontSize = 28.px
                        fontWeight = FontWeight.BOLD
                    }
                }
                text(StringProps.flowText2) {
                    fill = c("#ff6700")
                    style {
                        fontFamily = SettingProps.font
                        fontSize = 20.px
                        fontWeight = FontWeight.BOLD
                    }
                }
                text(StringProps.flowText3) {
                    fill = c("black")
                    style {
                        fontFamily = SettingProps.font
                        fontSize = 13.px
                        fontWeight = FontWeight.BOLD
                    }
                    translateY -= 2
                }
            }
            spacer()
            button(graphic = imageview(Image("images/questionMark.png"))) {
                (graphic as ImageView).fitWidth = 25.0
                (graphic as ImageView).fitHeight = 25.0
                addClass("help")
                action { helpView.openWindow(resizable = false) }
            }
        }

        vbox {
            prefWidth = SettingProps.width
            vgrow = Priority.ALWAYS

            // Folder
            vbox {
                borders {
                    lineBorder()
                        .title(StringProps.folderBorder)
                        .thickness(0.5)
                        .innerPadding(2.0)
                        .outerPadding(10.0, 5.0, 5.0, 5.0)
                        .color(c("black"))
                        .build()
                }

                hbox {
                    spacing = 3.0
                    alignment = Pos.CENTER_LEFT
                    paddingAll = 4.0

                    label(StringProps.folderSearchLabel).addClass(MyStyles.folderLabel)
                    spacer(Priority.NEVER)
                    searchFolder = textfield {
                        hgrow = Priority.ALWAYS
                    }
                    spacer(Priority.NEVER)
                    button(StringProps.folderSelect) {
                        addClass(MyStyles.folderBtn)

                        action {
                            onFolderSelect(searchFolder)
                        }
                    }
                }
                hbox {
                    spacing = 3.0
                    alignment = Pos.CENTER_LEFT
                    padding = Insets(0.0, 4.0, 4.0, 4.0)

                    label(StringProps.folderMoveLabel).addClass(MyStyles.folderLabel)
                    spacer(Priority.NEVER)
                    moveFolder = textfield {
                        hgrow = Priority.ALWAYS
                    }
                    spacer(Priority.NEVER)
                    button(StringProps.folderSelect) {
                        addClass(MyStyles.folderBtn)

                        action {
                            onFolderSelect(moveFolder)
                        }
                    }
                }
                hbox {
                    spacing = 3.0
                    alignment = Pos.CENTER_LEFT
                    paddingAll = 4.0

                    conditionsChk = checkbox(StringProps.searchBorder) { action { primaryStage.sizeToScene() } }
                    changeChk = checkbox(StringProps.namingBorder) { action { primaryStage.sizeToScene() } }

                    spacer()

                    // 내부 폴더 검색
                    innerSearchChk = checkbox(StringProps.innerSearchCheck, innerSearchChecked) {
                        isSelected = SettingProps.innerSearchCheck

                        selectedProperty().addListener { _, _, isChecked ->
                            changeProperties.changeSettings("innerSearchCheck", isChecked.toString())
                            innerSearchChecked.set(isChecked)
                            SettingProps.innerSearchCheck = isChecked
                        }
                    }

                    spacer(Priority.NEVER).removeWhen { !innerSearchChecked }

                    // 내부 폴더 이동
                    innerMoveChk = checkbox(StringProps.innerMoveCheck) {
                        isSelected = SettingProps.innerMoveCheck

                        selectedProperty().addListener { _, _, isChecked ->
                            changeProperties.changeSettings("innerMoveCheck", isChecked.toString())
                            SettingProps.innerMoveCheck = isChecked
                        }
                        removeWhen { !innerSearchChecked }
                        disableWhen { changeChk.selectedProperty() }
                    }
                }

                spacer(Priority.NEVER)

                // 중복 처리
                hbox {
                    spacing = 3.0
                    alignment = Pos.CENTER_LEFT
                    padding = Insets(0.0, 4.0, 4.0, 4.0)

                    label(StringProps.existsLabel)
                    radiobutton(StringProps.exists1, existsToggle) {
                        userData = 1
                        isSelected = SettingProps.existsSelected == userData
                    }
                    radiobutton(StringProps.exists2, existsToggle) {
                        userData = 2
                        isSelected = SettingProps.existsSelected == userData
                    }
                    radiobutton(StringProps.exists3, existsToggle) {
                        userData = 3
                        isSelected = SettingProps.existsSelected == userData
                    }

                    existsToggle.selectedToggleProperty().addListener { _, _, selected ->
                        changeProperties.changeSettings("existsSelected", selected.userData.toString())
                        SettingProps.existsSelected = selected.userData as Int
                    }
                }
            }

            separator().removeWhen { conditionsChk.selectedProperty().not() }

            // Search & Settings
            vbox {
                borders {
                    lineBorder()
                        .title(StringProps.searchBorder)
                        .thickness(0.5)
                        .innerPadding(7.0)
                        .outerPadding(12.0, 5.0, 5.0, 5.0)
                        .color(c("black"))
                        .build()
                }.removeWhen { conditionsChk.selectedProperty().not() }
                removeWhen { conditionsChk.selectedProperty().not() }
                spacing = 3.0

                // 검색란
                hbox {
                    spacing = 3.0
                    alignment = Pos.CENTER_LEFT

                    label(StringProps.searchTextLabel)
                    spacer(Priority.NEVER)
                    searchText = textfield {
                        hgrow = Priority.ALWAYS
                    }
                    spacer(Priority.NEVER)
                    label(StringProps.searchExtensionLabel).removeWhen { regexChecked }
                    searchExtension = textfield {
                        maxWidth = 60.0
                        removeWhen { regexChecked }
                    }
                }

                spacer(Priority.NEVER)

                // 검색어 옵션
                hbox {
                    spacing = 3.0
                    alignment = Pos.CENTER_LEFT

                    remainTextChk = checkbox(StringProps.remainCheck) {
                        isSelected = SettingProps.remainCheck

                        selectedProperty().addListener { _, _, isChecked ->
                            changeProperties.changeSettings("remainCheck", isChecked.toString())
                            SettingProps.remainCheck = isChecked
                        }
                    }
                    spacer(Priority.NEVER)
                    noIgnoreChk = checkbox(StringProps.noIgnoreCaseCheck) {
                        isSelected = SettingProps.noIgnoreCaseCheck

                        selectedProperty().addListener { _, _, isChecked ->
                            changeProperties.changeSettings("noIgnoreCaseCheck", isChecked.toString())
                            SettingProps.noIgnoreCaseCheck = isChecked
                        }
                    }
                    spacer()
                    regexChk = checkbox(StringProps.useRegexCheck, regexChecked) {
                        selectedProperty().addListener { _, _, isChecked ->
                            changeProperties.changeSettings("useRegexCheck", isChecked.toString())
                            regexChecked.set(isChecked)
                            SettingProps.useRegexCheck = isChecked
                        }
                    }
                }
            }

            separator().removeWhen { changeChk.selectedProperty().not() }

            // 이름 변경
            vbox {
                spacing = 3.0
                padding = Insets(6.0, 4.0, 4.0, 4.0)
                vgrow = Priority.ALWAYS

                borders {
                    lineBorder()
                        .title(StringProps.namingBorder)
                        .thickness(0.5)
                        .innerPadding(2.0)
                        .outerPadding(10.0, 5.0, 5.0, 5.0)
                        .color(c("black"))
                        .build()
                }.removeWhen { changeChk.selectedProperty().not() }

                hbox {
                    alignment = Pos.CENTER_LEFT
                    vgrow = Priority.ALWAYS

                    button(StringProps.namingSearchBtn).action { onSearchOnly() }
                    button(StringProps.namingAddFileBtn).action { onAddFiles() }

                    spacer()

                    applyAllChk = checkbox(StringProps.namingAll) {
                        isSelected = true
                    }
                    spacer(Priority.NEVER) { prefWidth = 3.0 }
                    button(StringProps.namingDelete).action { onNameDelete() }
                    button(StringProps.namingRollback).action { onNameRollback() }
                    button(StringProps.namingText).action { onNameText() }
                    button(StringProps.namingNumber).action { onNumberText() }
                }

                fileTable = tableview {
                    items = files
                    prefHeight = SettingProps.tableHeight

                    multiSelect(true)

                    column(StringProps.extension, FileItem::extension) {
                        prefWidth = SettingProps.extensionWidth
                    }
                    column(StringProps.beforeFileName, FileItem::beforeName) {
                        prefWidth = SettingProps.beforeWidth
                    }
                    column(StringProps.afterFileName, FileItem::afterName) {
                        makeEditable()
                        prefWidth = SettingProps.afterWidth

                        onEditCommit {
                            if (rowValue.afterName == "") rowValue.afterName = rowValue.beforeName.value!!
                            refresh()
                        }
                    }
                    column(StringProps.filePath, FileItem::filePath) {
                        prefWidth = SettingProps.pathWidth
                    }
                }
            }.removeWhen { changeChk.selectedProperty().not() }

            // File Move or Copy
            hbox {
                padding = Insets(1.0, 5.0, 5.0, 5.0)

                button(StringProps.moveBtn).action { onPressMove(FileManager.Mode.MOVE) }
                button(StringProps.copyBtn).action { onPressMove(FileManager.Mode.COPY) }
            }
        }
    }

    ///////////
    // Event //
    ///////////
    init { // 긴 부분은 이곳에 작성
        fileTable.apply {
            // 리스트 삭제
            setOnKeyPressed {
                if (it.code == KeyCode.DELETE) {
                    if (items.size == selectionModel.selectedItems.size) items.clear()
                    else items.removeAll(selectionModel.selectedItems)
                    selectionModel.clearSelection()
                }
            }
            // 리스트 추가(드래그 앤 드랍)
            setOnDragOver { event ->
                if (event.dragboard.hasFiles()) event.acceptTransferModes(TransferMode.COPY)
                else event.consume()
            }
            setOnDragDropped { event ->
                if (event.dragboard.hasFiles()) {
                    var lastSelected = 0
                    event.dragboard.files.forEach { file ->
                        if (file.isFile) files.add(FileItem(file.toPath()))
                        else if (file.isDirectory) {
                            var allChk: CheckBox by singleAssign()
                            when (lastSelected) {
                                1 -> files.addAll(fileManager.searchFile(file.path, innerSearch = false)
                                    .map { FileItem(it) })
                                2 -> files.addAll(fileManager.searchFile(file.path, innerSearch = true)
                                    .map { FileItem(it) })
                                3 -> {
                                }
                                else -> {
                                    Stage(StageStyle.UTILITY).apply {
                                        title = "폴더 추가"
                                        scene = Scene(vbox {
                                            paddingAll = 8.0
                                            spacing = 3.0
                                            style { fontFamily = SettingProps.font }
                                            setResizable(false)
                                            val buttonWidth = 320.0

                                            whenDocked {
                                                currentStage?.x =
                                                    primaryStage.x + (primaryStage.width - currentStage!!.width) / 2
                                                currentStage?.y =
                                                    primaryStage.y + (primaryStage.height - currentStage!!.height) / 2
                                            }

                                            label(file.path) { style { fontSize = 13.px } }
                                            button {
                                                vbox {
                                                    label("해당 폴더 파일 추가")
                                                    label("현재 선택한 폴더 내의 파일만 추가합니다.")
                                                }
                                                alignment = Pos.CENTER_LEFT
                                                prefWidth = buttonWidth
                                                action {
                                                    files.addAll(fileManager.searchFile(file.path, innerSearch = false)
                                                        .map { FileItem(it) })
                                                    if (allChk.isSelected) {
                                                        lastSelected = 1
                                                    }
                                                    close()
                                                }
                                            }
                                            button {
                                                vbox {
                                                    label("내부 폴더 파일 추가")
                                                    label("안쪽 폴더 내의 파일도 같이 추가합니다.(파일 개수에 유의)")
                                                }
                                                alignment = Pos.CENTER_LEFT
                                                prefWidth = buttonWidth
                                                action {
                                                    files.addAll(fileManager.searchFile(file.path, innerSearch = true)
                                                        .map { FileItem(it) })
                                                    if (allChk.isSelected) {
                                                        lastSelected = 2
                                                    }
                                                    close()
                                                }
                                            }
                                            button {
                                                vbox {
                                                    label("폴더 제외")
                                                    label("폴더 내의 파일을 넣지 않습니다.")
                                                }
                                                alignment = Pos.CENTER_LEFT
                                                prefWidth = buttonWidth
                                                action {
                                                    if (allChk.isSelected) {
                                                        lastSelected = 3
                                                    }
                                                    close()
                                                }
                                            }
                                            allChk = checkbox("모두 적용")
                                            spacer()
                                        })
                                    }.showAndWait()
                                }
                            }
                        }
                    }
                }
            }
            // 리스트 순서 변경(드래그 앤 드랍)
            val selections = mutableListOf<FileItem>()
            setRowFactory {
                val row = TableRow<FileItem>()

                row.setOnDragDetected { event ->
                    if (!row.isEmpty) {
                        val index = row.index

                        selections.clear()
                        selections.addAll(selectionModel.selectedItems.toMutableList())
                        val dragBoard = row.startDragAndDrop(TransferMode.MOVE)
                        dragBoard.dragView = row.snapshot(null, null)
                        val cc = ClipboardContent()
                        cc[SERIALIZED_MIME_TYPE] = index
                        dragBoard.setContent(cc)
                        event.consume()
                    }
                }

                row.setOnDragOver { event ->
                    val dragBoard = event.dragboard
                    if (dragBoard.hasContent(SERIALIZED_MIME_TYPE)) {
                        if (row.index != (dragBoard.getContent(SERIALIZED_MIME_TYPE) as Int).toInt()) {
                            event.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
                            event.consume()
                        }
                    }
                }

                row.setOnDragDropped { event ->
                    val dragBoard = event.dragboard

                    if (dragBoard.hasContent(SERIALIZED_MIME_TYPE)) {
                        var dropIndex: Int
                        var dragItem: FileItem? = null

                        if (row.isEmpty) dropIndex = items.size // 빈 곳에 놓은 경우 인덱스를 마지막 아이템으로 지정
                        else {
                            dropIndex = row.index
                            dragItem = items[dropIndex]
                        }
                        var delta = 0
                        if (dragItem != null) {
                            while (selections.contains(dragItem)) {
                                delta = 1
                                --dropIndex
                                if (dropIndex < 0) {
                                    dragItem = null
                                    dropIndex = 0
                                    break
                                }
                                dragItem = items[dropIndex]
                            }
                        }

                        selections.forEach { items.remove(it) }

                        if (dragItem != null) dropIndex = items.indexOf(dragItem) + delta
                        else if (dropIndex != 0) dropIndex = items.size

                        selectionModel.clearSelection()

                        selections.forEach {
                            items.add(dropIndex, it)
                            selectionModel.select(dropIndex)
                            dropIndex++
                        }

                        event.isDropCompleted = true
                        selections.clear()
                        event.consume()
                    }
                }

                // 아이템 정보 표시
                var popOver = PopOver()
                row.setOnMouseClicked { event ->
                    if (!row.isEmpty && event.button == MouseButton.SECONDARY) {
                        println(Files.probeContentType(row.item.path))
                        val type = Files.probeContentType(row.item.path) ?: "other"
                        popOver = PopOver(
                            VBox(
                                when {
                                    type.contains("image/") -> ImageView(Image("file:/${row.item.path}"))
                                        .apply {
                                            if (image.width > 800.0) fitWidth = 800.0
                                            if (image.height > 800.0) fitHeight = 800.0
                                            isPreserveRatio = true
                                        }
                                    type.contains("text/") -> {
                                        var str = ""
                                        var fis: FileInputStream
                                        var isr: InputStreamReader
                                        var br: BufferedReader
                                        val charsets = listOf("UTF-8", "EUC-KR", "UTF-16", "MS949", "CP949", "x-windows-949", "ISO-8859-1", "EUC-JP", "SHIFT-JIS")
                                        for (charset in charsets) {
                                            fis = FileInputStream(row.item.path.toFile())
                                            isr = InputStreamReader(fis, charset)
                                            br = BufferedReader(isr, 1024)
                                            var temp: String?
                                            for (i in 1..5) {
                                                temp = br.readLine()
                                                if (temp != null) str += "$temp\n"
                                                else break
                                            }
                                            if (!str.contains("\ufffd")) {
                                                if (str != "") {
                                                    for (i in 1..15) {
                                                        temp = br.readLine()
                                                        if (temp != null) str += "$temp\n"
                                                        if (str.length > 1024) {
                                                            str.substring(0 until 1024)
                                                            break
                                                        }
                                                    }
                                                    if (br.read() != -1) str += "...\n\n"
                                                }
                                                br.close()
                                                isr.close()
                                                fis.close()
                                                break
                                            }
                                            br.close()
                                            isr.close()
                                            fis.close()
                                            str = ""
                                        }
                                        Label(if (str != "") str else "내용 없음")
                                    }
                                    else -> Label("미리보기 없음")
                                },
                                Label("파일명: ${row.item.path.fileName}"),
                                Label("파일 종류: $type"),
                                Label("용량: ").apply {
                                    var unit = 0
                                    var length = row.item.path.toFile().length().toDouble()
                                    while (length >= 1024 && unit < 6) {
                                        length /= 1024
                                        unit++
                                    }
                                    length = String.format("%.3f", length).toDouble()
                                    when (unit) {
                                        0 -> text += "$length Byte"
                                        1 -> text += "$length KB"
                                        2 -> text += "$length MB"
                                        3 -> text += "$length GB"
                                        4 -> text += "$length TB"
                                        5 -> text += "$length PB"
                                    }
                                }
                            ).apply {
                                paddingAll = 5.0
                                children.forEach { it.style {
                                    textFill = c("black")
                                    fontFamily = SettingProps.font
                                    fontSize = SettingProps.previewFontSize.pt
                                } }
                            }
                        )
                        popOver.show(row)
                    }
                }
                row.setOnMouseExited {
                    popOver.hide()
                }
                row
            }
            addEventFilter(DragEvent.DRAG_DROPPED) {
                if (autoScrollThread != null) {
                    autoScrollThread!!.stopScrolling()
                    autoScrollThread = null
                }
            }

            addEventFilter(DragEvent.DRAG_OVER) { event ->
                val proximity = 100.0 // 스크롤을 시작할 간격
                val tableBounds = layoutBounds
                val dragY = event.y

                // 스크롤을 시작할 위치
                val topYProximity = tableBounds.minY + proximity
                val bottomYProximity = tableBounds.maxY - proximity

                if (dragY < topYProximity) {
                    if (autoScrollThread == null) {
                        autoScrollThread = AutoScrollableTableThread(this)
                        autoScrollThread!!.scrollUp()
                        autoScrollThread!!.start()
                    }

                } else if (dragY > bottomYProximity) {
                    if (autoScrollThread == null) {
                        autoScrollThread = AutoScrollableTableThread(this)
                        autoScrollThread!!.scrollDown()
                        autoScrollThread!!.start()
                    }

                } else {
                    if (autoScrollThread != null) {
                        autoScrollThread!!.stopScrolling()
                        autoScrollThread = null
                    }
                }
            }
        }
    }

    enum class ScrollMode { UP, DOWN, NONE }

    inner class AutoScrollableTableThread(tableView: TableView<FileItem>) : Thread() {
        private var running = true
        private var scrollMode = ScrollMode.NONE
        private var verticalScrollBar: ScrollBar? = null

        init {
            isDaemon = true
            verticalScrollBar = tableView.lookup(".scroll-bar:vertical") as ScrollBar
        }

        override fun run() {
            Thread.sleep(100) // 스크롤 시작 대기
            while (running) {
                runLater {
                    if (verticalScrollBar != null && scrollMode === ScrollMode.UP) {
                        verticalScrollBar!!.value = verticalScrollBar!!.value - 0.28 / (files.size - 20)
                    } else if (verticalScrollBar != null && scrollMode === ScrollMode.DOWN) {
                        verticalScrollBar!!.value = verticalScrollBar!!.value + 0.28 / (files.size - 20)
                    }
                }
                Thread.sleep(20) // 스크롤 시간 간격
            }
        }

        fun scrollUp() {
            scrollMode = ScrollMode.UP
            running = true
        }
        fun scrollDown() {
            scrollMode = ScrollMode.DOWN
            running = true
        }
        fun stopScrolling() {
            running = false
            scrollMode = ScrollMode.NONE
        }
    }

    private fun onFolderSelect(textField: TextField) {
        val before = textField.text
        val after = selectDialog.folderDialog(primaryStage)

        if (after != "" && before != after) {
            textField.text = after

            changeProperties.changeSettings(
                "lastSelectedFolder",
                after.replace("\\\\".toRegex(), "/")
            )
        }
    }

    private fun onSearchOnly() {
        val path = File(searchFolder.text)
        if (searchFolder.text == "") alert(Alert.AlertType.ERROR, "검색 경로 에러", "검색 경로를 설정해야 합니다.")
        else if (!path.exists() || path.isFile) alert(Alert.AlertType.ERROR, "검색 경로 에러", "잘못된 경로입니다.")
        else {
            try {
                val beforeSize = files.size
                files.addAll(fileManager.searchFile(
                    searchFolder.text,
                    searchText.text,
                    searchExtension.text,
                    innerSearchChk.isSelected
                ).map { FileItem(it) })
                if (beforeSize != files.size) files.replaceDistinct()
            } catch (e: PatternSyntaxException) {
                alert(Alert.AlertType.ERROR, "정규식 에러", "Java/Kotlin/Go 에서의 정규식을 사용해야 합니다.")
            } catch (e: UncheckedIOException) {
                if (e.localizedMessage.contains("java.nio.file.AccessDeniedException"))
                    alert(Alert.AlertType.ERROR, "검색 에러", "경로 내에 접근이 거부된 파일이 있습니다.")
            }
        }
    }

    private fun onAddFiles() {
        val beforeSize = files.size
        val list = selectDialog.fileDialog(primaryStage)
        files.addAll(list.map { FileItem(it) })
        files.replaceDistinct()
        if (beforeSize != files.size) files.replaceDistinct()
        if (list.isNotEmpty()) {
            changeProperties.changeSettings(
                "lastSelectedFolder",
                list[0].parent.toString().replace("\\\\".toRegex(), "/")
            )
        }
    }

    private fun ObservableList<FileItem>.replaceDistinct() {
        val replaceList = distinctBy { it.path }
        clear()
        addAll(replaceList)
    }

    private fun onNameDelete() {
        if (applyAllChk.isSelected) fileTable.items.forEach { it.afterName = "" }
        else fileTable.selectionModel.selectedItems.forEach { it.afterName = "" }
        fileTable.refresh()
    }

    private fun onNameRollback() {
        if (applyAllChk.isSelected) fileTable.items.forEach { it.afterName = it.beforeName.value!! }
        else fileTable.selectionModel.selectedItems.forEach { it.afterName = it.beforeName.value!! }
        fileTable.refresh()
    }

    private fun onNameText() {
        textNamingView.openModal(StageStyle.TRANSPARENT, resizable = false)?.apply {
            setOnHiding {
                if (textNamingView.isApply && textNamingView.resultText != "") {
                    if (applyAllChk.isSelected) fileTable.items.forEach {
                        it.afterName =
                            if (textNamingView.resultPos == TextNamingView.Position.LEFT) textNamingView.resultText + it.afterName
                            else it.afterName + textNamingView.resultText
                    } else fileTable.selectionModel.selectedItems.forEach {
                        it.afterName =
                            if (textNamingView.resultPos == TextNamingView.Position.LEFT) textNamingView.resultText + it.afterName
                            else it.afterName + textNamingView.resultText
                    }
                    fileTable.refresh()
                }
            }
        }
    }

    private fun onNumberText() {
        numberNamingView.openModal(StageStyle.TRANSPARENT, resizable = false)?.apply {
            setOnHiding {
                if (numberNamingView.isApply) {
                    var num = numberNamingView.resultInit
                    if (applyAllChk.isSelected) fileTable.items.forEach {
                        it.afterName =
                            if (numberNamingView.resultPos == NumberNamingView.Position.LEFT) num++.toString().padStart(
                                numberNamingView.resultCount,
                                '0'
                            ) + it.afterName
                            else it.afterName + num++.toString().padStart(numberNamingView.resultCount, '0')
                    }
                    fileTable.refresh()
                }
            }
        }
    }

    private fun onPressMove(mode: FileManager.Mode) {
        var result: Int

        if (!checkField(mode)) return

        try {
            if (!changeChk.isSelected) { // 이름 변경 X
                if (conditionsChk.isSelected) { // 검색 조건 O
                    result = fileManager.moveFile(
                        searchFolder.text,
                        moveFolder.text,
                        searchText.text,
                        searchExtension.text,
                        mode
                    )
                    if (!remainTextChk.isSelected) { // 검색어 유지 X
                        searchText.text = ""
                        searchExtension.text = ""
                    }
                } else { // 검색 조건 X
                    result = fileManager.moveFile(searchFolder.text, moveFolder.text, mode = mode)
                }
            } else { // 이름 변경 O
                result = fileManager.moveFile(files, moveFolder.text, mode)
            }
        } catch (e: PatternSyntaxException) {
            result = -4
        } catch (e: UncheckedIOException) {
            result = if (e.localizedMessage.contains("java.nio.file.AccessDeniedException")) -5
            else -99
        }

        moveResult(result, mode)
    }

    private fun checkField(mode: FileManager.Mode): Boolean {
        val path = File(searchFolder.text)
        val move = File(moveFolder.text)
        if (!changeChk.isSelected) { // 이름 변경 X
            if (searchFolder.text == "") {
                alert(Alert.AlertType.ERROR, "검색 경로 에러", "검색 경로를 설정해야 합니다.")
                return false
            }
            if (!path.exists() || path.isFile) {
                alert(Alert.AlertType.ERROR, "검색 경로 에러", "잘못된 경로입니다.")
                return false
            }
            if (!conditionsChk.isSelected) { // 검색 조건 X
                if (SettingProps.existsSelected == 3) { // 중복 시 중단
                    if (moveFolder.text == "") {
                        alert(Alert.AlertType.ERROR, "이동 경로 에러", "이동 경로를 설정해야 합니다.")
                        return false
                    }
                    if (!move.exists() || move.isFile) {
                        alert(Alert.AlertType.ERROR, "이동 경로 에러", "잘못된 경로입니다.")
                        return false
                    }
                }
            }
            if (mode == FileManager.Mode.MOVE && moveFolder.text == "") {
                alert(Alert.AlertType.ERROR, "동작 에러", "이름 변경 없는 동일 경로로의 이동")
                return false
            }
            return true
        } else return true // 이름 변경 O
    }

    /** -1: 검색 리스트 문제
     *
     * -2: 작업 중단(중복 불가 상태에서 중복 감지, 예상치 못한 리스트 문제)
     *
     * -3: 변경 중단(사용 중인 파일 감지)
     *
     * -4: 정규식 문제
     *
     * -5: 접근 거부 파일에 접근 시도*/
    private fun moveResult(result: Int, mode: FileManager.Mode) {
        when (result) {
            -1 -> alert(Alert.AlertType.ERROR, "조건 에러", "검색된 파일이 없습니다.")
            -2 -> alert(Alert.AlertType.ERROR, "작업 중 에러", "중복된 파일이 있습니다.")
            -3 -> alert(Alert.AlertType.ERROR, "작업 중 에러", "열려있는 파일이 있습니다.")
            -4 -> alert(Alert.AlertType.ERROR, "정규식 에러", "Java/Kotlin/Go 에서의 정규식을 사용해야 합니다.")
            -5 -> alert(Alert.AlertType.ERROR, "검색 에러", "경로 내에 접근이 거부된 파일이 있습니다.")
            -99 -> alert(Alert.AlertType.ERROR, "에러", "예상치 못한 에러가 발생했습니다.\n")
            else -> {
                val text = if (mode == FileManager.Mode.MOVE) "이동" else "복사"
                alert(Alert.AlertType.INFORMATION, "$text 완료", "$result 개의 파일을 ${text}하였습니다.")
            }
        }
    }

    // 아이콘, 스타일을 위해 원본 함수를 가져와서 내용 추가
    private fun alert(type: Alert.AlertType,
                    header: String,
                    content: String? = null,
                    vararg buttons: ButtonType,
                    owner: Window? = null,
                    title: String? = null,
                    actionFn: Alert.(ButtonType) -> Unit = {}): Alert {
        val alert = Alert(type, content ?: "", *buttons)
        title?.let { alert.title = it }
        alert.headerText = header
        alert.dialogPane.apply {
            (scene.window as Stage).icons += Image("images/icon.png")
            style {
                fontFamily = SettingProps.font
            }
        }
        owner?.also { alert.initOwner(it) }
        val buttonClicked = alert.showAndWait()
        if (buttonClicked.isPresent) {
            alert.actionFn(buttonClicked.get())
        }
        return alert
    }
}