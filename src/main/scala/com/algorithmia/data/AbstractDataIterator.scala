package com.algorithmia.data

import java.util.NoSuchElementException

/**
 * Implements an Iterator over the results of an algorithmia data api listing.
 * Handles API paging.
 */
abstract class AbstractDataIterator[T](val dir: DataDirectory) extends Iterator[T] {

  protected var marker: Option[String] = None
  private var offset: Int = 0
  private var children: List[String] = List.empty
  private var loadedFirstPage: Boolean = false

  override def hasNext: Boolean = {
    attemptToLoadFirstPage()
    (offset < children.size) || // We have data in memory
      (offset >= children.size && marker.isDefined) // There is another page to fetch
  }

  override def next(): T = {
    attemptToLoadFirstPage()
    if (children.isEmpty) throw new NoSuchElementException
    if (marker.isDefined && offset >= children.size) {
      try {
        loadNextPage()
      } catch {
        case e: Exception => throw new NoSuchElementException(e.getMessage)
      }
    }
    if (offset < children.size) {
      offset += 1
      newDataObjectInstance(dir.trimmedPath + "/" + children(offset - 1))
    } else {
      throw new NoSuchElementException
    }
  }

  private def attemptToLoadFirstPage() = {
    if (!loadedFirstPage) {
      loadedFirstPage = true
      try {
        loadNextPage()
      } catch {
        case e: Exception => throw new NoSuchElementException(e.getMessage)
      }
    }
  }

  final protected def setChildrenAndMarker(newChildren: List[String], marker: Option[String]): Unit = {
    if (offset < children.size) throw new IllegalStateException("Skipping elements")
    children = newChildren
    offset = 0
    this.marker = marker
  }

  protected def loadNextPage(): Unit

  // Because this can be statically checked, as opposed to doing something like:
  //   storedClass.getDeclaredConstructor(String.class).newInstance(path)
  protected def newDataObjectInstance(dataUri: String): T

}
