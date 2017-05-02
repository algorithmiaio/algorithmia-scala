package com.algorithmia.data

class DataDirectoryIterator(override val dir: DataDirectory) extends AbstractDataIterator[DataDirectory](dir) {

  override protected def loadNextPage(): Unit = {
    val response = dir.getPage(marker, false)
    val dirnames: List[String] = response.folders.map(_.map(_.name)).getOrElse(List.empty)
    // Update iterator state
    setChildrenAndMarker(dirnames, response.marker)
  }

  override protected def newDataObjectInstance(dataUri: String) = new DataDirectory(dir.client, dataUri)

}
