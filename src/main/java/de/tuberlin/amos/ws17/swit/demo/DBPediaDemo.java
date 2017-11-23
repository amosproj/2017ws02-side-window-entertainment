package de.tuberlin.amos.ws17.swit.demo;


import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;

public class DBPediaDemo {

    public static void main(String[] args) {

        String desiredPOI = "Berlin Cathedral";

        String requestString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "PREFIX dbpedia0: <http://dbpedia.org/ontology/> \n" +
                "PREFIX dbpedia2: <http://dbpedia.org/property/> \n" +
                "PREFIX dbp: <http://dbpedia.org/property/> \n" +
                "SELECT ?label ?architect ?name ?location WHERE { \n" +
                "?label a dbpedia0:Building. \n" +
                "?label dbpedia2:architect ?architect. \n" +
                "?label rdfs:label ?name. \n" +
                "?label dbp:location ?location. \n" +
                "FILTER (?name=\"" + desiredPOI +"\"@en) }";

        ParameterizedSparqlString parameterizedSparqlRequestString = new ParameterizedSparqlString(requestString);

        System.out.println(parameterizedSparqlRequestString);

        QueryExecution exec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", parameterizedSparqlRequestString.asQuery());

        // Normally you'd just do results = exec.execSelect(), but I want to
        // use this ResultSet twice, so I'm making a copy of it.
        ResultSet results = ResultSetFactory.copyResults(exec.execSelect());

        // A simpler way of printing the results.
        ResultSetFormatter.out(results);
    }

}
