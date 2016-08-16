package com.sumologic.epigraph.schema.parser.psi;

public enum TypeKind {
  VAR("vartype"),
  RECORD("record"),
  MAP("map"),
  LIST("list"),
  ENUM("enum"),
  PRIMITIVE("primitive");

  public final String name;

  TypeKind(String name) {
    this.name = name;
  }
}
