package ir.ac.iust.dml.kg.virtuoso;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.virtuoso.jena.driver.VirtGraph;
import org.junit.Test;

public class JenaTester {

  @Test
  public void test() {
    final Node subject1 = Node.createURI("http://fkg.iust.ac.ir/test/s1");
    final Node subject2 = Node.createURI("http://fkg.iust.ac.ir/test/s2");
    final Node predicate1 = Node.createURI("http://fkg.iust.ac.ir/test/p1");
    final Node predicate2 = Node.createURI("http://fkg.iust.ac.ir/test/p2");
    final Node object1 = Node.createURI("http://fkg.iust.ac.ir/test/o1");
    final Node object2 = Node.createURI("http://fkg.iust.ac.ir/test/o2");
    final Node literal1 = ResourceFactory.createTypedLiteral("Test 1").asNode();
    final Node literal2 = ResourceFactory.createTypedLiteral(1.2).asNode();

    final String virtuosoServer = ConfigReader.INSTANCE.getString("virtuoso.address",
            "localhost:1111");
    final String virtuosoUser = ConfigReader.INSTANCE.getString("virtuoso.user", "dba");
    final String virtuosoPass = ConfigReader.INSTANCE.getString("virtuoso.password", "admin");
    final VirtGraph graph = new VirtGraph("http://fkg.iust.ac.ir/test",
            "jdbc:virtuoso://" + virtuosoServer, virtuosoUser, virtuosoPass);

    graph.clear();

    // adding multiple triples to graph
    final Triple[] triples = new Triple[]{
            new Triple(subject1, predicate1, object1),
            new Triple(subject1, predicate1, object2),
            new Triple(subject2, predicate2, object1),
            new Triple(subject2, predicate2, object2),
            new Triple(subject1, predicate1, literal1),
            new Triple(subject1, predicate1, literal2)
    };


    for (Triple t : triples) graph.add(t);
    final int size = graph.size();
    assert size >= 6;

    final Model model = ModelFactory.createModelForGraph(graph);
    String queryString =
            "SELECT ?p ?o\n" +
                    "WHERE {\n" +
                    "<http://fkg.iust.ac.ir/test/s1> ?p ?o .\n" +
                    "}";
    final Query query = QueryFactory.create(queryString);
    final QueryExecution qexec = QueryExecutionFactory.create(query, model);
    final ResultSet results = qexec.execSelect();

    while (results.hasNext()) {
      final QuerySolution binding = results.nextSolution();
      final Resource p = (Resource) binding.get("p");
      final RDFNode o = binding.get("o");
      System.out.println("predicate: " + p.toString() + " object: " + o.toString());
      assert p.toString().equals(predicate1.toString());
    }

    try {
      qexec.close();
    } catch (Throwable th) {
      th.printStackTrace();
    }
    for (Triple t : triples) graph.remove(t);
    assert size - 6 == graph.size();

//    PREFIX fkgt:  <http://fkg.iust.ac.ir/test/>
//    SELECT ?s ?p ?o
//    WHERE {
//    ?s ?p ?o .
//    }
  }
}
