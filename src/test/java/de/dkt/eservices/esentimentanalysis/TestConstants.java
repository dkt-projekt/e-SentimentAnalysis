package de.dkt.eservices.esentimentanalysis;

public class TestConstants {
	
	public static final String pathToPackage = "rdftest/esentimentanalysis-test-package.xml";
	
	
	public static final String outputTest2 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" + 
"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" + 
"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" + 
"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" + 
"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" + 
"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" + 
"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" + 
"\n" + 
"<http://dkt.dfki.de/documents/#char=0,1138>\n" + 
"        a                      nif:RFC5147String , nif:String , nif:Context ;\n" + 
"        dktnif:sentimentValue  \"1.5\"^^xsd:double ;\n" + 
"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex           \"1138\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:isString           \"1297685 24-03-2015 Debt collection Cont'd attempts collect debt not owed Debt is not mine \\\"I find this medical debt reported on my credit report  but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist. It appears to have impact on my credit score. I am willing to pay it if I could get an itemized bill showing the date of the procedure and when and what my medical insurance company reports about it. Also  if the company no longer exist  IT WILL NEED TO BE REMOVE FROM MY CREDIT REPORT. My complaint is that this company has not sent me a bill showing the details of my so called debt  but has reported to the credit bureau that I owe this debt. This is harming my credit score and it is not making it 's contact information available to me. How am I to clear up my credit report if this company does n't exist  but they have reported that I owe a debt that I have no explanation for? _\\\" Company chooses not to provide a public response \\\"Healthcare Collections-I\"^^xsd:string .\n" +
"";

	public static final String outputTest21 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
			"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
			"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
			"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
			"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
			"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=131,199>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        dktnif:sentimentValue  \"2.0\"^^xsd:double ;\n" +
			"        nif:beginIndex         \"131\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"199\"^^xsd:nonNegativeInteger .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=0,130>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        dktnif:sentimentValue  \"1.0\"^^xsd:double ;\n" +
			"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"130\"^^xsd:nonNegativeInteger .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=200,283>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        dktnif:sentimentValue  \"2.0\"^^xsd:double ;\n" +
			"        nif:beginIndex         \"200\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"283\"^^xsd:nonNegativeInteger .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=0,283>\n" +
			"        a                      nif:RFC5147String , nif:String , nif:Context ;\n" +
			"        dktnif:sentimentValue  \"1.6666666666666667\"^^xsd:double ;\n" +
			"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"283\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:isString           \"I find this medical debt reported on my credit report but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist.\"^^xsd:string .\n" +
			"";
	
	public static final String outputTest3 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" + 
"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" + 
"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" + 
"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" + 
"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" + 
"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" + 
"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" + 
"\n" + 
"<http://dkt.dfki.de/documents/#char=0,1138>\n" + 
"        a                      nif:RFC5147String , nif:String , nif:Context ;\n" + 
"        dktnif:sentimentValue  \"-3.0\"^^xsd:double ;\n" + 
"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex           \"1138\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:isString           \"1297685\\t24-03-2015\\tDebt collection\\tCont'd attempts collect debt not owed\\tDebt is not mine\\t\\\"I find this medical debt reported on my credit report  but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist. It appears to have impact on my credit score. I am willing to pay it if I could get an itemized bill showing the date of the procedure and when and what my medical insurance company reports about it. Also  if the company no longer exist  IT WILL NEED TO BE REMOVE FROM MY CREDIT REPORT. My complaint is that this company has not sent me a bill showing the details of my so called debt  but has reported to the credit bureau that I owe this debt. This is harming my credit score and it is not making it 's contact information available to me. How am I to clear up my credit report if this company does n't exist  but they have reported that I owe a debt that I have no explanation for? _\\\" Company chooses not to provide a public response \\\"Healthcare Collections-I\"^^xsd:string .\n";

	public static final String outputTest31 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
			"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
			"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
			"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
			"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
			"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=131,199>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        dktnif:sentimentValue  \"0.0\"^^xsd:double ;\n" +
			"        nif:beginIndex         \"131\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"199\"^^xsd:nonNegativeInteger .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=0,130>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        dktnif:sentimentValue  \"-1.0\"^^xsd:double ;\n" +
			"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"130\"^^xsd:nonNegativeInteger .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=200,283>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        dktnif:sentimentValue  \"0.0\"^^xsd:double ;\n" +
			"        nif:beginIndex         \"200\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"283\"^^xsd:nonNegativeInteger .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=0,283>\n" +
			"        a                      nif:RFC5147String , nif:String , nif:Context ;\n" +
			"        dktnif:sentimentValue  \"-1.0\"^^xsd:double ;\n" +
			"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"283\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:isString           \"I find this medical debt reported on my credit report but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist.\"^^xsd:string .\n" +
			"";
	
}
