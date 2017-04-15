package ir.ac.iust.dml.kg.virtuoso.connector.data;

public class VirtuosoTriple {
  private String source;
  private String predicate;
  private VirtuosoTripleObject object;

  public VirtuosoTriple() {
  }

  public VirtuosoTriple(String source, String predicate, VirtuosoTripleObject object) {
    this.source = source;
    this.predicate = predicate;
    this.object = object;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getPredicate() {
    return predicate;
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public VirtuosoTripleObject getObject() {
    return object;
  }

  public void setObject(VirtuosoTripleObject object) {
    this.object = object;
  }
}
