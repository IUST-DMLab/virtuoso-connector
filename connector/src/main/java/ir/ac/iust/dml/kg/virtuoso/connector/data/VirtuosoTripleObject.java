package ir.ac.iust.dml.kg.virtuoso.connector.data;

public class VirtuosoTripleObject {
  private VirtuosoTripleType type;
  private String value;
  private String language;

  public VirtuosoTripleObject() {
  }

  public VirtuosoTripleObject(VirtuosoTripleType type, String value, String language) {
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

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
