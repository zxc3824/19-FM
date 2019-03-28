package setting
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class MyStyles : Stylesheet() {
    companion object {
        val main by cssclass()
        val help by cssclass()
        val folderLabel by cssclass()
        val folderBtn by cssclass()
    }

    // 색상 팔레트
    private val black = c("black")
    private val white = c("white")
    private val grayC = c("#cccccc")
    private val gray7 = c("#777777")

    init {
        // 메인
        main {
            backgroundColor += Color.LIGHTSKYBLUE
            fontSize = SettingProps.fontSize.pt
        }

        // 레이아웃
        s(radio, box, button, textField) {
            backgroundColor += white
            borderColor += box(grayC)
            backgroundInsets += box(0.px) // 밑줄 등 제거
        }
        s(radioButton.and(hover).contains(dot), radioButton.and(pressed).contains(radio),
            checkBox.and(hover).contains(mark), checkBox.and(pressed).contains(box),
            button.and(pressed)) {
            backgroundColor += grayC
        }
        s(radioButton.and(hover).and(selected).contains(dot), checkBox.and(hover).and(selected).contains(mark)) {
            backgroundColor += gray7
        }
        s(radioButton.and(selected).contains(dot), checkBox.and(selected).contains(mark)) {
            backgroundColor += black
        }
        s(radioButton.and(pressed).contains(dot), radioButton.and(pressed).and(focused).contains(dot),
            checkBox.and(pressed).contains(mark), checkBox.and(pressed).and(focused).contains(mark)) {
            backgroundColor += white
        }
        s(radioButton.and(focused).contains(radio), checkBox.and(focused).contains(box),
            button.and(focused), textField.and(focused)) {
            borderColor += box(gray7)
        }
        radioButton {
            radio {
                padding = box(3.px)
                borderRadius += box(10.px)
            }
            dot {
                padding = box(3.5.px)
            }
            and(selected) {
                dot {
                    translateY = (-1).px
                }
            }
        }
        checkBox {
            box {
                padding = box(2.px)
            }
            and(selected) {
                mark {
                    translateY = (-1).px
                }
            }
        }
        button {
            textFill = black
            and(hover) {
                textFill = grayC
            }
            and(pressed) {
                textFill = white
            }
        }
        textField {
            backgroundRadius += box(4.px)
            borderRadius += box(4.px)
            padding = box(4.px)
        }
        tableView {
            fillHeight = true
        }
        tableCell {
            alignment = Pos.CENTER_LEFT
            fontFamily = SettingProps.font
            padding = box(0.px, 0.px, 0.px, 2.px)
            labelPadding = box(0.px)
            backgroundInsets += box(0.px)
        }
        tableRowCell {
            cellSize = SettingProps.cellSIze.px
            fontSize = SettingProps.cellFontSize.pt
            padding = box(0.px)
            textField {
                padding = box(0.px)
                backgroundRadius += box(0.px)
                borderColor += box(c("transparent"))
            }
        }

        // 클래스
        help {
            padding = box(0.px)
            backgroundColor += Color.LIGHTSKYBLUE
            backgroundRadius += box(4.px)
            borderRadius += box(4.px)
            effect = DropShadow(2.0, black)
            and(hover) {
                borderColor += box(black)
            }
            and(focused) {
                borderColor += box(black)
            }
        }
        folderLabel {
            minWidth = 35.px
        }
        folderBtn {
            padding = box(3.0.px, 4.0.px)
            /** NOTE: top = topLeft, right = topRight, bottom = bottomRight, left = bottomLeft
             * ∴ topLeft -> clockwise */
            backgroundRadius += CssBox(top = 0.px, right = 4.px, bottom = 0.px, left = 0.px)
            borderRadius += CssBox(top = 0.px, right = 4.px, bottom = 0.px, left = 0.px)
        }
    }
}