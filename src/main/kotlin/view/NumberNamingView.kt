package view

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Spinner
import javafx.scene.control.ToggleGroup
import setting.MyStyles
import tornadofx.*

class NumberNamingView : View("숫자 추가") {

    enum class Position {
        LEFT, RIGHT
    }

    var resultPos: Position = Position.RIGHT
        private set
    var resultCount = 0
        private set
    var resultInit = 0
        private set
    var isApply = false
        private set

    private var xOffset = .0
    private var yOffset = .0

    private var spinnerCount: Spinner<Int> by singleAssign()
    private var spinnerInit: Spinner<Int> by singleAssign()
    private val toggleGroup = ToggleGroup()

    override val root = hbox {
        addClass(MyStyles.main)
        alignment = Pos.CENTER
        paddingAll = 6.0

        // 초기화(주로 쓸 거 같은 값으로 설정)
        whenDocked {
            resultCount = 1
            resultInit = 1
            spinnerCount.editor.text = "1"
            spinnerInit.editor.text = "1"
            isApply = false

            // 현재 창 중앙으로 이동
            currentStage?.x = primaryStage.x + (primaryStage.width - currentStage!!.width) / 2
            currentStage?.y = primaryStage.y + (primaryStage.height - currentStage!!.height) / 2
        }

        hbox {
            spacing = 3.0
            alignment = Pos.CENTER
            paddingAll = 4.0
            style {
                backgroundColor += c("white")
                backgroundRadius += box(4.px)
            }

            label("방향:")
            radiobutton("왼쪽", toggleGroup).action { resultPos = Position.LEFT }
            radiobutton("오른쪽", toggleGroup) {
                isSelected = true // 주로 오른쪽에 숫자를 다니 오른쪽을 기본으로
                action { resultPos = Position.RIGHT }
            }

            separator(Orientation.VERTICAL)

            label("자릿수:")
            spinnerCount = spinner(1, 20, 1, editable = true, enableScroll = true) {
                prefWidth = 50.0

                editor.filterInput { it.text.isInt() }
                editor.textProperty().addListener { _, _, newValue ->
                    if (newValue.isInt() && newValue.toInt() > 20) runLater { editor.text = "20" }
                }
            }
            label("시작 숫자:")
            spinnerInit = spinner(0, 10000, 1, editable = true, enableScroll = true) {
                prefWidth = 70.0

                editor.filterInput { it.text.isInt() }
                editor.textProperty().addListener { _, _, newValue ->
                    if (newValue.isInt() && newValue.toInt() > 10000) runLater { editor.text = "10000" }
                }
            }

            button("적용").action {
                resultCount = spinnerCount.editor.text.toInt()
                resultInit = spinnerInit.editor.text.toInt()
                isApply = true
                close()
            }
            button("취소").action { close() }
        }

        // 창 이동
        setOnMousePressed { pressEvent ->
            xOffset = pressEvent.sceneX
            yOffset = pressEvent.sceneY
        }
        setOnMouseDragged { dragEvent ->
            currentStage?.x = dragEvent.screenX - xOffset
            currentStage?.y = dragEvent.screenY - yOffset
        }
    }
}