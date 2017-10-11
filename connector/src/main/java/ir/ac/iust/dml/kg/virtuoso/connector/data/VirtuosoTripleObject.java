/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.virtuoso.connector.data;

public class VirtuosoTripleObject {
  private VirtuosoTripleType type;
  private String dataType;
  private Object value;
  private String language;

  public VirtuosoTripleObject() {
  }

  public VirtuosoTripleObject(VirtuosoTripleType type, Object value, String language) {
    this.type = type;
    this.value = value;
    this.language = language;
  }

  public VirtuosoTripleObject(VirtuosoTripleType type, String dataType, Object value, String language) {
    this.type = type;
    this.dataType = dataType;
    this.value = value;
    this.language = language;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public VirtuosoTripleType getType() {
    return type;
  }

  public void setType(VirtuosoTripleType type) {
    this.type = type;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
