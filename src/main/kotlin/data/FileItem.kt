package data

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.nio.file.Path

class FileItem(path: Path) {
    val pathProperty = SimpleObjectProperty<Path>(path)
    var path: Path by pathProperty

    val extension = pathProperty.objectBinding { ".${it!!.toString().substringAfterLast('.')}" }

    val beforeName = pathProperty.objectBinding { it!!.fileName.toString().substringBeforeLast('.') }

    val afterNameProperty = SimpleStringProperty(path.fileName.toString().substringBeforeLast('.'))
    var afterName: String by afterNameProperty

    val filePath = pathProperty.objectBinding { it!!.parent }
}