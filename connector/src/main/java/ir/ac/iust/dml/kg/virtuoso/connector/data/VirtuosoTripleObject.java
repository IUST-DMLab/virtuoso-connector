package ir.ac.iust.dml.kg.virtuoso.connector.data;

public class VirtuosoTripleObject {
  private VirtuosoTripleType type;
  private Object value;
  private String language;

  public VirtuosoTripleObject() {
  }

  public VirtuosoTripleObject(VirtuosoTripleType type, Object value, String language) {
    this.type = type;
    this.value = value;
    this.language = language;
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
