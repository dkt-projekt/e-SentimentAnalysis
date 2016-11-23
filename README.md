# e-SentimentAnalysis
This service analyses input for sentiment and provides it with a value. The value that is assigned depends on the parameter value for sentiment engine that is used (see description of parameters below).

##Input
The following parameters must be specified:
`language`: Currently only English (`en`) is supported.

`informat`: The usual NIF set if accepted input formats (http://persistence.uni-leipzig.org/nlp2rdf/specification/api.html).

`sentimentEngine`: Currently, two engines are offered. `corenlp` (based on the corresponding Stanford CoreNLP code) returns an integer value, where the higher the value, the more positive the sentiment. `dfki` is a dictionary-based approach, where the higher the value, the more positive the sentiment.

##Output
An annotation for sentiment value, as in the example below:
```
@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .
@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .
@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .
@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .
@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .
@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .

<http://dkt.dfki.de/documents/#char=0,52>
        a                      nif:RFC5147String , nif:String , nif:Context ;
        dktnif:sentimentValue  "1.0"^^xsd:double ;
        nif:beginIndex         "0"^^xsd:nonNegativeInteger ;
        nif:endIndex           "52"^^xsd:nonNegativeInteger ;
        nif:isString           "My opinion of this is very low. It is a bad product."^^xsd:string .
```

##Example
`curl -X GET -H -d "https://api.digitale-kuratierung.de/api/e-sentimentanalysis?sentimentEngine=corenlp&language=en&informat=text&input=My%20opinion%20of%20this%20is%20very%20low.%20It%20is%20a%20bad%20product."`
