/* Created by yegor on 4/28/16. */

package com.sumologic.epigraph.raw

import com.sumologic.epigraph.core

trait Types extends core.Types {this: core.Names =>


  abstract class Type(private val tname: QualifiedTypeName) extends TypeApi {

    override def name: QualifiedTypeName = tname

  }


  class VarType(
      name: QualifiedTypeName,
      override val defaultMember: Option[TypeMember],
      membersSeq: Seq[TypeMember] // TODO add method
  ) extends Type(name) with VarTypeApi {

    // TODO parameterized method to return any supported view of this?
    override def membersMap: Map[TypeMemberName, TypeMember] = ???

  }


  class TypeMember(private val mname: TypeMemberName, override val dataType: DataType) extends TypeMemberApi {

    override def name: TypeMemberName = mname

  }


  abstract class DataType(name: QualifiedTypeName, override val supertypes: Seq[DataType]) extends Type(
    name
  ) with DataTypeApi


  class RecordDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[RecordDataType],
      override val declaredFields: Map[FieldName, Field]
  ) extends DataType(name, supertypes) with RecordDataTypeApi


  class Field(private val fname: FieldName, override val valueType: VarType) extends FieldApi {

    override def name: FieldName = fname

  }


  class UnionDataType(
      name: QualifiedTypeName,
      supertypes: Seq[UnionDataType],
      override val declaredTags: Map[TagName, Tag]
  ) extends DataType(name, supertypes) with UnionDataTypeApi


  class Tag(private val tname: TagName, override val valueType: VarType) extends TagApi {

    override def name: TagName = tname

  }


  class MapDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[MapDataType],
      override val keyType: DataType,
      override val valueType: VarType
  ) extends DataType(name, supertypes) with MapDataTypeApi


  class ListDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[ListDataType],
      override val valueType: VarType
  ) extends DataType(name, supertypes) with ListDataTypeApi


  class EnumDataType(
      name: QualifiedTypeName,
      override val members: Seq[EnumTypeMember]
  ) extends DataType(name, core.Types.EmptySeq) with EnumDataTypeApi


  class EnumTypeMember(private val ename: EnumMemberName) extends EnumTypeMemberApi {

    override def name: EnumMemberName = ename

  }


  class PrimitiveDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[PrimitiveDataType]
  ) extends DataType(name, supertypes) with PrimitiveDataTypeApi


  class StringDataType(
      name: QualifiedTypeName,
      override val supertypes: Seq[StringDataType]
  ) extends PrimitiveDataType(name, supertypes) with StringDataTypeApi


}