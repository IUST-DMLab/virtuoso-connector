package ir.ac.iust.dml.kg.virtuoso.connector;

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
  final private Model model;

  public VirtuosoConnector(String graphName) {
    final String virtuosoServer = ConfigReader.INSTANCE.getString("virtuoso.address",
            "localhost:1111");
    final String virtuosoUser = ConfigReader.INSTANCE.getString("virtuoso.user", "dba");
    final String virtuosoPass = ConfigReader.INSTANCE.getString("virtuoso.password", "admin");
    graph = new VirtGraph(graphName, "jdbc:virtuoso://" + virtuosoServer, virtuosoUser, virtuosoPass);
    model = ModelFactory.createModelForGraph(graph);
  }

  public VirtuosoConnector(String graphName, String serverAddress, String username, String password) {
    graph = new VirtGraph(graphName, "jdbc:virtuoso://" + serverAddress, username, password);
    model = ModelFactory.createModelForGraph(graph);
  }

  public List<VirtuosoTriple> getTriplesOfSubject(String subject) {
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
      if (l.getDatatypeURI().endsWith("long")) type = VirtuosoTripleType.Long;
      else if (l.getDatatypeURI().endsWith("int")) type = VirtuosoTripleType.Int;
      else if (l.getDatatypeURI().endsWith("double")) type = VirtuosoTripleType.Double;
      else type = VirtuosoTripleType.String;
      return new VirtuosoTripleObject(type, o.toString(), ((Literal) o).getLanguage());
    }
    return null;
  }
}
