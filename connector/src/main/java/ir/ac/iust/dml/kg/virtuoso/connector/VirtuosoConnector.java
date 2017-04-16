package ir.ac.iust.dml.kg.virtuoso.connector;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTriple;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTripleObject;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTripleType;
import ir.ac.iust.dml.kg.virtuoso.jena.driver.VirtGraph;

import java.util.ArrayList;
import java.util.List;

public class VirtuosoConnector {

  final private VirtGraph graph;
  final private String graphName;
  final private Model model;

  public VirtuosoConnector() {
    this("http://fkg.iust.ac.ir/");
  }

  public VirtuosoConnector(String graphName) {
    this(graphName, ConfigReader.INSTANCE.getString("virtuoso.address",
            "localhost:1111"),
            ConfigReader.INSTANCE.getString("virtuoso.user", "dba"),
            ConfigReader.INSTANCE.getString("virtuoso.password", "admin"));
  }

  public VirtuosoConnector(String graphName, String serverAddress, String username, String password) {
    this.graphName = graphName;
    graph = new VirtGraph(graphName, "jdbc:virtuoso://" + serverAddress, username, password);
    model = ModelFactory.createModelForGraph(graph);
  }

  public void clear() {
    graph.clear();
  }

  public void addResource(String subject, String predicate, String object) {
    final Node s = Node.createURI(subject.contains("://") ? subject : graphName + subject);
    final Node p = Node.createURI(predicate.contains("://") ? predicate : graphName + predicate);
    final Node o = Node.createURI(object.contains("://") ? object : graphName + object);
    graph.add(new Triple(s, p, o));
  }

  public void addLiteral(String subject, String predicate, Object object) {
    final Node s = Node.createURI(subject.contains("://") ? subject : graphName + subject);
    final Node p = Node.createURI(predicate.contains("://") ? predicate : graphName + predicate);
    final Literal o = ResourceFactory.createTypedLiteral(object);
    graph.add(new Triple(s, p, o.asNode()));
  }

  public List<VirtuosoTriple> getTriplesOfSubject(String subject) {
    subject = subject.contains("://") ? subject : graphName + subject;
    String queryString =
            "SELECT ?p ?o\n" +
                    "WHERE {\n" +
                    "<" + subject + "> ?p ?o .\n" +
                    "}";
    final Query query = QueryFactory.create(queryString);
    final QueryExecution exec = QueryExecutionFactory.create(query, model);
    final ResultSet results = exec.execSelect();

    final List<VirtuosoTriple> result = new ArrayList<>();
    while (results.hasNext()) {
      final QuerySolution binding = results.nextSolution();
      final Resource p = (Resource) binding.get("p");
      final RDFNode o = binding.get("o");
      result.add(new VirtuosoTriple(subject, p.toString(), convertObject(o)));
    }
    return result;
  }

  public List<VirtuosoTriple> getTriples(String subject, String predicate) {
    subject = subject.contains("://") ? subject : graphName + subject;
    predicate = predicate.contains("://") ? predicate : graphName + predicate;
    String queryString =
            "SELECT ?p ?o\n" +
                    "WHERE {\n" +
                    "<" + subject + "> <" + predicate + "> ?o .\n" +
                    "}";
    final Query query = QueryFactory.create(queryString);
    final QueryExecution exec = QueryExecutionFactory.create(query, model);
    final ResultSet results = exec.execSelect();

    final List<VirtuosoTriple> result = new ArrayList<>();
    while (results.hasNext()) {
      final QuerySolution binding = results.nextSolution();
      final RDFNode o = binding.get("o");
      result.add(new VirtuosoTriple(subject, predicate, convertObject(o)));
    }
    return result;
  }

  public List<QuerySolution> query(String queryString) {
    final Query query = QueryFactory.create(queryString);
    final QueryExecution exec = QueryExecutionFactory.create(query, model);
    final ResultSet results = exec.execSelect();

    final List<QuerySolution> result = new ArrayList<>();
    while (results.hasNext()) result.add(results.nextSolution());
    return result;
  }

  public VirtuosoTripleObject convertObject(RDFNode o) {
    if (o instanceof Resource)
      return new VirtuosoTripleObject(VirtuosoTripleType.Resource, o.toString(), null);
    else if (o instanceof Literal) {
      final Literal l = (Literal) o;
      final VirtuosoTripleType type;
      final Object value;
      if (l.getDatatypeURI().endsWith("long")) {
        type = VirtuosoTripleType.Long;
        value = l.getLong();
      } else if (l.getDatatypeURI().endsWith("int")) {
        type = VirtuosoTripleType.Int;
        value = l.getInt();
      } else if (l.getDatatypeURI().endsWith("double")) {
        type = VirtuosoTripleType.Double;
        value = l.getDouble();
      } else {
        type = VirtuosoTripleType.String;
        value = l.getString();
      }
      return new VirtuosoTripleObject(type, value, l.getLanguage());
    }
    return null;
  }

  public void close() {
    graph.close();
  }
}
