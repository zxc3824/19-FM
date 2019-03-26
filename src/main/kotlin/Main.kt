import javafx.scene.image.Image
import javafx.stage.Stage
import setting.MyStyles
import tornadofx.App
import tornadofx.loadFont
import tornadofx.reloadStylesheetsOnFocus
import tornadofx.setStageIcon
import view.MainView

class Main : App(MainView::class, MyStyles::class) {

    override fun start(stage: Stage) {
        stage.isResizable = false
        setStageIcon(Image("images/icon.png"))
        super.start(stage)
    }

    override fun stop() {
        println("program shutting down...")
        super.stop()
    }

    init {
        reloadStylesheetsOnFocus()
    }
}