package view

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import setting.MyStyles
import setting.SettingProps
import tornadofx.*

/** 도움말 뷰
 * 여기에 한해서만 텍스트, 스타일을 이곳에 작성*/
class HelpView : View() {
    private var isFirst = true
    private var imageView: ImageView by singleAssign()

    override val root = vbox {
        addClass(MyStyles.main)
        style { fontFamily = SettingProps.font }
        paddingAll = 8
        spacing = 3.0

        whenDocked {
            // 현재 창 중앙으로 이동
            val x = primaryStage.x + (primaryStage.width - currentStage!!.width) / 2
            val y = primaryStage.y + (primaryStage.height - currentStage!!.height) / 2
            currentStage?.x = if (x > 0) x else .0
            currentStage?.y = if (y > 0) y else .0
        }

        vbox {
            style {
                backgroundColor += Color.WHITE
                backgroundRadius += box(10.px)
            }
            paddingAll = 6
            spacing = 5.0

            imageView = imageview(Image("images/manual1.png"))
        }
        button("다음") {
            action {
                if (isFirst) {
                    text = "이전"
                    imageView.image = Image("images/manual2.png")
                    isFirst = false
                } else {
                    text = "다음"
                    imageView.image = Image("images/manual1.png")
                    isFirst = true
                }
            }
        }
    }
}