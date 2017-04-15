package ir.ac.iust.dml.kg.virtuoso;

import ir.ac.iust.dml.kg.virtuoso.connector.VirtuosoConnector;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTriple;
import org.junit.Test;

import java.util.List;

public class Tester {
  @Test
  public void test() {
    final String GRAPH_NAME = "http://fkg.iust.ac.ir/test/";

    final VirtuosoConnector connector = new VirtuosoConnector(GRAPH_NAME);
    connector.clear();
    connector.addResource("s1", "p1", "o1");
    connector.addLiteral("s1", "p2", "string");
    connector.addLiteral("s1", "p1", 0.2);

    List<VirtuosoTriple> triples = connector.getTriplesOfSubject("s1");
    for (VirtuosoTriple t : triples) {
      final String value = t.getObject().getValue().toString();
      switch (t.getObject().getType()) {
        case Double:
          assert value.equals("0.2");
          break;
        case Resource:
          assert value.equals(GRAPH_NAME + "o1");
          break;
        default:
          assert value.equals("string");
      }
    }

    triples = connector.getTriples("s1", "p2");
    assert triples.size() == 1;
    assert triples.get(0).getObject().getValue().equals("string");
  }
}
