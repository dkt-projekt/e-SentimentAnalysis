package de.dkt.eservices.esentimentanalysis.modules;
		
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.omg.Messaging.SyncScopeHelper;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

	         


public class CoreNLPSentimentAnalyzer {
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	public static void main(String[] args) throws Exception {

		String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\FRONTEO\\complaintsIndividualFiles";
		File df = new File(docFolder);
		for (File f : df.listFiles()){
			String fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
			double sentVal = getSentiment(fileContent);
			System.out.println("Filename:" + f.getAbsolutePath());
			System.out.println("sentiment value:" + sentVal);
			System.out.println("\n");
		}
		

		//getSentiment("love great wonderful awesome cool nice good");
		//getSentiment("shit fuck damn piss off screw bad angry");
		//String str = "1297685	24-03-2015	Debt collection	Cont'd attempts collect debt not owed	Debt is not mine	\"I find this medical debt reported on my credit report  but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist. It appears to have impact on my credit score. I am willing to pay it if I could get an itemized bill showing the date of the procedure and when and what my medical insurance company reports about it. Also  if the company no longer exist  IT WILL NEED TO BE REMOVE FROM MY CREDIT REPORT. My complaint is that this company has not sent me a bill showing the details of my so called debt  but has reported to the credit bureau that I owe this debt. This is harming my credit score and it is not making it 's contact information available to me. How am I to clear up my credit report if this company does n't exist  but they have reported that I owe a debt that I have no explanation for? _\" Company chooses not to provide a public response \"Healthcare Collections-I";
		//int sentVal = getSentiment(str);
		//System.out.println("Sentiment value:" + sentVal);

	}
	
	
	public static Model getSentimentForModel(Model nifModel){
		
		String s = NIFReader.extractIsString(nifModel);
		double sentVal = getSentiment(s);
		NIFWriter.addSentimentAnnotation(nifModel, s, NIFReader.extractDocumentURI(nifModel), sentVal);
		
		return nifModel;
		
	}
	

	public static double getSentiment(String text) {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		double mainSentiment = 0;
		Annotation annotation = pipeline.process(text);
		List<CoreMap> sentences =  annotation.get(CoreAnnotations.SentencesAnnotation.class);
		double numSentences = sentences.size();
		for (CoreMap sentence : sentences) {
			Tree tree = sentence.get(SentimentAnnotatedTree.class);
			double sentiment = RNNCoreAnnotations.getPredictedClass(tree);
			mainSentiment += sentiment;
		
		}
		double sentVal = mainSentiment / numSentences;
		return sentVal;

	}

}
