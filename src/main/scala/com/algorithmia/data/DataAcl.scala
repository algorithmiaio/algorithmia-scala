package com.algorithmia.data

case class DataAcl(read: DataAclType)

sealed trait DataAclType

case object DataPublic extends DataAclType
case object DataMyAlgorithms extends DataAclType
case object DataPrivate extends DataAclType
