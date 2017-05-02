package com.algorithmia.data

class DataFileIterator(override val dir: DataDirectory) extends AbstractDataIterator[DataFile](dir) {

  override protected def loadNextPage(): Unit = {
    val response = dir.getPage(marker, false)
    val filenames: List[String] = response.files.map(_.map(_.filename)).getOrElse(List.empty)
    // Update iterator state
    setChildrenAndMarker(filenames, response.marker)
  }

  override protected def newDataObjectInstance(dataUri: String) = new DataFile(dir.client, dataUri)

}
