package ir.ac.iust.dml.kg.virtuoso;

import ir.ac.iust.dml.kg.virtuoso.connector.VirtuosoConnector;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTriple;
import org.junit.Test;

import java.util.List;

public class Tester {
  @Test
  public void test() {
    final String GRAPH_NAME = "http://fkg.iust.ac.ir/test/";
    final String resourcePrefix = GRAPH_NAME + "resource/";
    final String ontologyPrefix = GRAPH_NAME + "ontology/";
    final VirtuosoConnector connector = new VirtuosoConnector(GRAPH_NAME);
    connector.clear();
    connector.addResource(resourcePrefix + "s1", ontologyPrefix + "p1", resourcePrefix + "o1");
    connector.addLiteral(resourcePrefix + "s1", ontologyPrefix + "p2", "string");
    connector.addLiteral(resourcePrefix + "s1", ontologyPrefix + "p1", 0.2);

    List<VirtuosoTriple> triples = connector.getTriplesOfSubject(resourcePrefix + "s1");
    for (VirtuosoTriple t : triples) {
      final String value = t.getObject().getValue().toString();
      switch (t.getObject().getType()) {
        case Double:
          assert value.equals("0.2");
          break;
        case Resource:
          assert value.equals(resourcePrefix + "o1");
          break;
        default:
          assert value.equals("string");
      }
    }

    triples = connector.getTriples(resourcePrefix + "s1", ontologyPrefix + "p2");
    assert triples.size() == 1;
    assert triples.get(0).getObject().getValue().equals("string");

    connector.removeResource(resourcePrefix + "s1", ontologyPrefix + "p1", resourcePrefix + "o1");
    connector.removeLiteral(resourcePrefix + "s1", ontologyPrefix + "p2", "string");
    connector.removeLiteral(resourcePrefix + "s1", ontologyPrefix + "p1", 0.2);
    triples = connector.getTriplesOfSubject(resourcePrefix + "s1");
    assert triples.isEmpty();
  }
}
