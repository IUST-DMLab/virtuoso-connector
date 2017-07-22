package ir.ac.iust.dml.kg.virtuoso;

import ir.ac.iust.dml.kg.virtuoso.connector.VirtuosoConnector;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTriple;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by ali on 4/16/17.
 */
public class AliTest {
    @Test
    public void test() throws UnsupportedEncodingException {
        final String GRAPH_NAME = "http://localhost:8890/knowledgeGraph";

        final VirtuosoConnector connector = new VirtuosoConnector(GRAPH_NAME);

        List<VirtuosoTriple> triples = connector.getTriplesOfSubject(
                "http://fkg.iust.ac.ir/resources/حسن_روحانی");
        for (VirtuosoTriple t : triples) {
            System.out.println(t.getPredicate() + " " + t.getObject().getValue());
        }

//        triples = connector.getTriples("s1", "p2");
//        assert triples.size() == 1;
//        assert triples.get(0).getObject().getValue().equals("string");
    }
}