package de.dkt.eservices.esentimentanalysis.dfki;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.esentimentanalysis.dfki.linguistic.SpanText;
import de.dkt.eservices.esentimentanalysis.dfki.sentimentassigner.FrequencySentimentAssigner;
import de.dkt.eservices.esentimentanalysis.dfki.sentimentassigner.ISentimentAssigner;
import de.dkt.eservices.esentimentanalysis.dfki.sentimentassigner.SentimentAssigner;
import de.dkt.eservices.esentimentanalysis.dfki.values.SentimentValue;
import eu.freme.common.conversion.rdf.JenaRDFConversionService;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.exception.ExternalServiceFailedException;

@Component
public class SentimentAnalyzer {

	static Logger logger = Logger.getLogger(SentimentAnalyzer.class);
	
	ISentimentAssigner sentimentAssigner;
	
//	@Value("${luceneIndexPath}")
	@Value("${dkt.sentimentanalysis.type}")
	private String sentimentAnalysisType;


	public SentimentAnalyzer() {
		initializeAssigner(sentimentAnalysisType);
	}

	public SentimentAnalyzer(String sentimentAnalysisType) {
		initializeAssigner(sentimentAnalysisType);
	}
	
	public void initializeAssigner(String sentimentAnalysisType){
		System.out.println(sentimentAnalysisType);
		if(sentimentAnalysisType.equalsIgnoreCase("baseline-dictionary")){
			sentimentAssigner = new SentimentAssigner();
		}
		else if(sentimentAnalysisType.equalsIgnoreCase("frequency-dictionary")){
			System.out.println(sentimentAnalysisType);
			sentimentAssigner = new FrequencySentimentAssigner();
		}
		else{
			sentimentAssigner = new SentimentAssigner();
		}
	}

	public double analyzeSentiment(String inputText, RDFSerialization format) throws ExternalServiceFailedException {
		try{
        	Model nifModel = null;
        	RDFConversionService rdfConversionService = new JenaRDFConversionService();
        	if(format.equals(RDFConstants.RDFSerialization.PLAINTEXT)){
        		nifModel = NIFWriter.initializeOutputModel();
        		NIFWriter.addInitialString(nifModel, inputText, DKTNIF.getDefaultPrefix());
        	}
        	else{
            	nifModel = rdfConversionService.unserializeRDF(inputText, format);
        	}
			return analyzeSentiment(nifModel);
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public double analyzeSentiment(Model nifModel) throws ExternalServiceFailedException {
		try{
        	String textForProcessing = NIFReader.extractIsString(nifModel);
        	SpanText text = new SpanText(textForProcessing);
        	
//        	text.indentedPrintToScreen(" ");
        	
			double d = sentimentAssigner.computeSentiment(text).getSentimentValue();
	        return d;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public Model analyzeSentimentToModel(String inputText, RDFSerialization format) throws ExternalServiceFailedException {
		try{
        	Model nifModel = null;
        	RDFConversionService rdfConversionService = new JenaRDFConversionService();
        	if(format.equals(RDFConstants.RDFSerialization.PLAINTEXT)){
        		nifModel = NIFWriter.initializeOutputModel();
        		NIFWriter.addInitialString(nifModel, inputText, DKTNIF.getDefaultPrefix());
        	}
        	else{
            	nifModel = rdfConversionService.unserializeRDF(inputText, format);
        	}
        	return analyzeSentimentToModel(nifModel);
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public Model analyzeSentimentToModel(Model nifModel) throws ExternalServiceFailedException {
		try{
			double d = analyzeSentiment(nifModel);
			String documentURI = NIFReader.extractDocumentURI(nifModel);
//	        Resource documentResource = nifModel.getResource(documentURI);
//	        nifModel.add(documentResource, DKTNIF.sentimentValue, nifModel.createTypedLiteral(s.numericValue(), XSDDatatype.XSDstring));
			NIFWriter.addSentimentAnnotation(nifModel, NIFReader.extractIsString(nifModel), documentURI, d);
	        return nifModel;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	public static void main(String[] args) throws Exception {
//		String inputFile = "";
//		String inputText = FileReadUtilities.readFile2String(inputFile);
		SentimentAnalyzer sa = new SentimentAnalyzer("frequency-dictionary");
		String inputText = "1297685	24-03-2015	Debt collection	Cont'd attempts collect debt not owed	Debt is not mine	\"I find this medical debt reported on my credit report  but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist. It appears to have impact on my credit score. I am willing to pay it if I could get an itemized bill showing the date of the procedure and when and what my medical insurance company reports about it. Also  if the company no longer exist  IT WILL NEED TO BE REMOVE FROM MY CREDIT REPORT. My complaint is that this company has not sent me a bill showing the details of my so called debt  but has reported to the credit bureau that I owe this debt. This is harming my credit score and it is not making it 's contact information available to me. How am I to clear up my credit report if this company does n't exist  but they have reported that I owe a debt that I have no explanation for? _\" Company chooses not to provide a public response \"Healthcare Collections-I";
		double sv1 = sa.analyzeSentiment(inputText, RDFSerialization.PLAINTEXT);
		System.out.println("SENTIMENTVALUE1: "+sv1);
		Date d1 = new Date();
		try {
			//String fileContent = readFile("C:\\Users\\pebo01\\Desktop\\data\\sentimentData\\michiganMovieReviews\\michiganReviewsTestData.txt", StandardCharsets.UTF_8);
			String fileContent = readFile("C:\\Users\\pebo01\\Desktop\\data\\sentimentData\\convote_v1.1\\sentTestData.txt", StandardCharsets.UTF_8);
			String[] lines = fileContent.split("\\n");
			for (String l : lines){
				String v = null;
				Double polarity = sa.analyzeSentiment(l, RDFSerialization.PLAINTEXT);
				
				if (polarity >= 1){
					v = "1";
				}
				else if (polarity <= -1) {
					v = "0";
				}
				System.out.println(v + "\t" + l);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date d2 = new Date();
		System.out.println("Start time:" + d1);
		System.out.println("End time:" + d2);
		
//		String inputText1 = "There is nothing admirably acumen into the ways I did the things and the worries worn by others.";
//		SentimentValue sv1 = sa.analyzeSentiment(inputText1,RDFSerialization.PLAINTEXT);
//		System.out.println("SENTIMENTVALUE1: "+sv1);
//		String inputText2 = "There are any achievements or nothing admirably acumen into the ways I did the things and the worries worn by others.";
//		SentimentValue sv2 = sa.analyzeSentiment(inputText2,RDFSerialization.PLAINTEXT);
//		System.out.println("SENTIMENTVALUE1: "+sv2);
//		String inputText3 = "There is nothing admirably acumen into the ways I did the things and the worries worn by others and their worthlessness.";
//		SentimentValue sv3 = sa.analyzeSentiment(inputText3,RDFSerialization.PLAINTEXT);
//		System.out.println("SENTIMENTVALUE1: "+sv3);
		
//		Model nifModel = ModelFactory.createDefaultModel();
//		NIFWriter.addInitialString(nifModel, inputText, "http://dkt.dfki.de/examples/sentimentanalysistext");
//		SentimentValue sv = analyzeSentiment(nifModel);
	}

}
