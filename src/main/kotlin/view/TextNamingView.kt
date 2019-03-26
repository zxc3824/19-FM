package view

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import setting.MyStyles
import setting.SettingProps
import tornadofx.*

class TextNamingView : View("텍스트 추가") {

    enum class Position {
        LEFT, RIGHT
    }

    var resultText: String = ""
        private set
    var resultPos: Position = Position.LEFT
        private set
    var isApply: Boolean = false
        private set

    private var xOffset = .0
    private var yOffset = .0

    private var textField: TextField by singleAssign()
    private val toggleGroup = ToggleGroup()

    override val root = hbox {
        addClass(MyStyles.main)
        style { fontFamily = SettingProps.font }
        alignment = Pos.CENTER
        paddingAll = 6.0

        // 초기화
        whenDocked {
            resultText = ""
            textField.text = ""
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

            label("방향: ")
            radiobutton("왼쪽", toggleGroup) {
                isSelected = true // 주로 왼쪽에 공통 텍스트를 붙이니 왼쪽을 기본으로
                action { resultPos = Position.LEFT }
            }
            radiobutton("오른쪽", toggleGroup).action { resultPos = Position.RIGHT }

            separator(Orientation.VERTICAL)

            label("텍스트: ")
            textField = textfield {
                useMaxWidth = true
            }

            button("적용").action {
                resultText = textField.text
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