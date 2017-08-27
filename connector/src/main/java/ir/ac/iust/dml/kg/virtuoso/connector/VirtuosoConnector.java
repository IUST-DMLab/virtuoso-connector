package ir.ac.iust.dml.kg.virtuoso.connector;

import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTriple;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTripleObject;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTripleType;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import virtuoso.rdf4j.driver.VirtuosoRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VirtuosoConnector {

  private static ValueFactory factory = SimpleValueFactory.getInstance();
  private final RepositoryConnection con;
  final private String graphName;

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
    VirtuosoRepository repository = new VirtuosoRepository("jdbc:virtuoso://" + serverAddress,
        username, password, graphName);
    con = repository.getConnection();
  }

  public void clear() {
    con.clear();
  }

  public void removeResource(String subject, String predicate, String object) {
    subject = subject.contains("://") ? subject : graphName + subject;
    predicate = predicate.contains("://") ? predicate : graphName + predicate;
    object = object.contains("://") ? object : graphName + object;
    con.remove(factory.createIRI(subject), factory.createIRI(predicate), factory.createIRI(object));
  }

  public void removeLiteral(String subject, String predicate, Object object) {
    subject = subject.contains("://") ? subject : graphName + subject;
    predicate = predicate.contains("://") ? predicate : graphName + predicate;
    con.remove(factory.createIRI(subject), factory.createIRI(predicate), createLiteral(object));
  }

  public void addResource(String subject, String predicate, String object) {
    subject = subject.contains("://") ? subject : graphName + subject;
    predicate = predicate.contains("://") ? predicate : graphName + predicate;
    object = object.contains("://") ? object : graphName + object;
    con.add(factory.createIRI(subject), factory.createIRI(predicate), factory.createIRI(object));
  }

  public void addLiteral(String subject, String predicate, Object object) {
    subject = subject.contains("://") ? subject : graphName + subject;
    predicate = predicate.contains("://") ? predicate : graphName + predicate;
    con.add(factory.createIRI(subject), factory.createIRI(predicate), createLiteral(object));
  }

  public List<VirtuosoTriple> getTriplesOfSubject(String subject) {
    String queryString =
        "SELECT ?p ?o\n" +
            "WHERE {\n" +
            "<" + subject + "> ?p ?o .\n" +
            "}";
    final List<VirtuosoTriple> converted = new ArrayList<>();
    TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
    final TupleQueryResult results = query.evaluate();

    try {
      while (results.hasNext()) {
        final BindingSet d = results.next();
        final String p = d.getBinding("p").getValue().stringValue();
        final Value o = d.getBinding("o").getValue();
        converted.add(new VirtuosoTriple(subject, p, convertObject(o)));
      }
    } catch (Throwable th) {
      th.printStackTrace();
    }
    return converted;
  }

  public List<VirtuosoTriple> getTriples(String subject, String predicate) {
    subject = subject.contains("://") ? subject : graphName + subject;
    predicate = predicate.contains("://") ? predicate : graphName + predicate;
    String queryString =
        "SELECT ?o\n" +
            "WHERE {\n" +
            "<" + subject + "> <" + predicate + "> ?o .\n" +
            "}";

    final List<VirtuosoTriple> converted = new ArrayList<>();
    TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
    final TupleQueryResult results = query.evaluate();

    try {
      while (results.hasNext()) {
        final BindingSet d = results.next();
        final Value o = d.getBinding("o").getValue();
        converted.add(new VirtuosoTriple(subject, predicate, convertObject(o)));
      }
    } catch (Throwable th) {
      th.printStackTrace();
    }
    return converted;
  }

  public List<VirtuosoTriple> getTriplesOfObject(String predicate, String object) {
    return getTriplesOfObject(predicate, object, 0, -1);
  }

  public List<VirtuosoTriple> getTriplesOfObject(String predicate, String object, int page, int pageSize) {
    object = object.contains("://") ? object : graphName + object;
    predicate = predicate.contains("://") ? predicate : graphName + predicate;
    String queryString =
        "SELECT ?s\n" +
            "WHERE {\n" +
            "?s <" + predicate + "> <" + object + "> .\n" +
            "}";

    final List<VirtuosoTriple> converted = new ArrayList<>();
    TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
    final TupleQueryResult results = query.evaluate();

    try {
      while (results.hasNext()) {
        final BindingSet d = results.next();
        final String subject = d.getBinding("s").getValue().stringValue();
        converted.add(new VirtuosoTriple(subject, predicate, convertObject(object)));
      }
    } catch (Throwable th) {
      th.printStackTrace();
    }
    return converted;
  }

  public TupleQueryResult query(String queryString) {
    TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
    return query.evaluate();
  }

  public static VirtuosoTripleObject convertObject(String o) {
    if (o.startsWith("http://"))
      return new VirtuosoTripleObject(VirtuosoTripleType.Resource, o, null);
    return new VirtuosoTripleObject(VirtuosoTripleType.String, o, null);
  }

  public static Value createLiteral(Object object) {
    if (object instanceof Boolean) return factory.createLiteral((Boolean) object);
    if (object instanceof Byte) return factory.createLiteral((Byte) object);
    if (object instanceof Short) return factory.createLiteral((Short) object);
    if (object instanceof Integer) return factory.createLiteral((Integer) object);
    if (object instanceof Long) return factory.createLiteral((Long) object);
    if (object instanceof Float) return factory.createLiteral((Float) object);
    if (object instanceof Double) return factory.createLiteral((Double) object);
    if (object instanceof Date) return factory.createLiteral((Date) object);
    if (object instanceof String) return factory.createLiteral((String) object);
    return null;
  }

  public static VirtuosoTripleObject convertObject(Value o) {
    if (o instanceof Resource)
      return new VirtuosoTripleObject(VirtuosoTripleType.Resource, o.toString(), null);
    else if (o instanceof Literal) {
      final Literal l = (Literal) o;
      final VirtuosoTripleType type;
      final Object value;
      if (l.getDatatype() == null) {
        type = VirtuosoTripleType.String;
        value = l.stringValue();
      } else {
        final String dataType = l.getDatatype().toString();
        if (dataType.endsWith("long")) {
          type = VirtuosoTripleType.Long;
          value = l.longValue();
        } else if (dataType.endsWith("int")) {
          type = VirtuosoTripleType.Int;
          value = l.intValue();
        } else if (dataType.endsWith("short")) {
          type = VirtuosoTripleType.Short;
          value = l.intValue();
        } else if (dataType.endsWith("double")) {
          type = VirtuosoTripleType.Double;
          value = l.doubleValue();
        } else if (dataType.endsWith("float")) {
          type = VirtuosoTripleType.Float;
          value = l.doubleValue();
        } else {
          type = VirtuosoTripleType.String;
          value = l.stringValue();
        }
      }
      return new VirtuosoTripleObject(type, value,
          l.getLanguage().isPresent() ? l.getLanguage().get() : "en");
    }
    return null;
  }

  public void close() {
    con.close();
  }
}
