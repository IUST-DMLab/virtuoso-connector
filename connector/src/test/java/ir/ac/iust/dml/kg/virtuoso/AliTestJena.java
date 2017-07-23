package ir.ac.iust.dml.kg.virtuoso;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.virtuoso.connector.VirtuosoConnector;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTriple;
import ir.ac.iust.dml.kg.virtuoso.jena.driver.VirtGraph;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by ali on 4/16/17.
 */
public class AliTestJena {
    @Test
    public void test() throws UnsupportedEncodingException {
        final String virtuosoServer = ConfigReader.INSTANCE.getString("virtuoso.address",
                "localhost:1111");
        final String virtuosoUser = ConfigReader.INSTANCE.getString("virtuoso.user", "dba");
        final String virtuosoPass = ConfigReader.INSTANCE.getString("virtuoso.password", "admin");
        final VirtGraph graph = new VirtGraph("http://localhost:8890/knowledgeGraphV2",
                "jdbc:virtuoso://" + virtuosoServer, virtuosoUser, virtuosoPass);

        final Model model = ModelFactory.createModelForGraph(graph);
        String queryString =
                "SELECT ?o \n" +
                        "WHERE {\n" +
                        "<http://fkg.iust.ac.ir/ontology/OfficeHolder> <http://www.w3.org/2000/01/rdf-schema#label> ?o. \n" +
                        "FILTER (lang(?o) = \"fa\")" +
                        "}";
        final Query query = QueryFactory.create(queryString);
        final QueryExecution qexec = QueryExecutionFactory.create(query, model);
        final ResultSet results = qexec.execSelect();

        while (results.hasNext()) {
            final QuerySolution binding = results.nextSolution();
            final RDFNode o = binding.get("o");
            System.out.println("className: " + o.toString());
            assert o.toString().substring(0,o.toString().lastIndexOf("@")).equals("مقام دولتی");
        }
    }
}
