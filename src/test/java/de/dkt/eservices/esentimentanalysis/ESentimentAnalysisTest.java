package de.dkt.eservices.esentimentanalysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;

import com.hp.hpl.jena.rdf.model.Model;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import de.dkt.common.niftools.NIFReader;
import edu.stanford.nlp.io.FileSequentialCollection;
import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ESentimentAnalysisTest {

	TestHelper testHelper;
	ValidationHelper validationHelper;

	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup
				.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
	}
	
	private HttpRequestWithBody genericRequest(String path) {
		String url = testHelper.getAPIBaseUrl() + "/e-sentimentanalysis"+path;
		Unirest.setTimeouts(10000, 10000000);
		return Unirest.post(url);
	}
	
	@Test
	public void test0_SanityCheck() throws UnirestException, IOException,
			Exception {
		HttpResponse<String> response = genericRequest("/testURL")
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();
		Assert.assertTrue(response.getStatus() == 200);
		Assert.assertTrue(response.getBody().length() > 0);
	}
	
	@Test
	public void test2_sentimentAnalysis_CORENLP() throws UnirestException, IOException,Exception {
		String inputString = "1297685 24-03-2015 Debt collection Cont'd attempts collect debt not owed Debt is not mine \"I find this medical debt reported on my credit report  but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist. It appears to have impact on my credit score. I am willing to pay it if I could get an itemized bill showing the date of the procedure and when and what my medical insurance company reports about it. Also  if the company no longer exist  IT WILL NEED TO BE REMOVE FROM MY CREDIT REPORT. My complaint is that this company has not sent me a bill showing the details of my so called debt  but has reported to the credit bureau that I owe this debt. This is harming my credit score and it is not making it 's contact information available to me. How am I to clear up my credit report if this company does n't exist  but they have reported that I owe a debt that I have no explanation for? _\" Company chooses not to provide a public response \"Healthcare Collections-I";

		HttpResponse<String> response = genericRequest("")
				.queryString("informat", "text")
				.queryString("input", inputString)
				.queryString("language", "en")
				.queryString("sentimentEngine", "corenlp")
				.queryString("outformat", "turtle").asString();
		Assert.assertTrue(response.getStatus() == 200);
		Assert.assertTrue(response.getBody().length() > 0);
//		System.out.println(response.getBody());
		Assert.assertEquals(TestConstants.outputTest2, response.getBody());
	}
	
	@Test
	public void test3_sentimentAnalysis_CORENLP() throws UnirestException, IOException,Exception {
		String inputString = "I find this medical debt reported on my credit report but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist.";

		HttpResponse<String> response = genericRequest("")
				.queryString("informat", "text")
				.queryString("input", inputString)
				.queryString("language", "en")
				.queryString("sentimentEngine", "corenlp")
				.queryString("sentenceLevel", true)
				.queryString("outformat", "turtle").asString();
				
		Assert.assertTrue(response.getStatus() == 200);
		Assert.assertTrue(response.getBody().length() > 0);
//		System.out.println(response.getBody());
		Assert.assertEquals(TestConstants.outputTest21, response.getBody());
	}

	
	
//	static String readFile(String path, Charset encoding) 
//			  throws IOException 
//			{
//			  byte[] encoded = Files.readAllBytes(Paths.get(path));
//			  return new String(encoded, encoding);
//			}
//	@Test
//	public void debugTest() throws UnirestException, IOException,Exception {
//		
//		
//		String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\FRONTEO\\complaintsIndividualFiles";
//		String fileIds = "1297939\n" +
//				"1297758\n" +
//				"1297773\n" +
//				"1297594\n" +
//				"1297676\n" +
//				"1297316\n" +
//				"1290543\n" +
//				"1297609\n" +
//				"1297626\n" +
//				"1296777\n" +
//				"1296890\n" +
//				"1297784\n" +
//				"1299258\n" +
//				"1296880\n" +
//				"1296785\n" +
//				"1292139\n" +
//				"1297850\n" +
//				"1297371\n" +
//				"1295409\n" +
//				"1297327\n" +
//				"1297377\n" +
//				"1299120\n" +
//				"1297500\n" +
//				"1290514\n" +
//				"1296773\n" +
//				"1296955\n" +
//				"1295146\n" +
//				"1292065\n" +
//				"1297783\n" +
//				"1297644\n" +
//				"1297685\n" +
//				"1297507\n" +
//				"1296823\n" +
//				"1299216\n" +
//				"1297662\n" +
//				"1297382\n" +
//				"1292137\n" +
//				"1295056\n" +
//				"1296727\n" +
//				"1290516\n" +
//				"1296593\n" +
//				"1290545\n" +
//				"1296774\n" +
//				"1296693\n" +
//				"1295228\n" +
//				"1296831\n" +
//				"1297589\n" +
//				"1297629\n" +
//				"1299207\n" +
//				"1297874";
//				
//		PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
//		
//		
//		for (String fileId : fileIds.split("\n")){
//			fileId = fileId.trim();
//			String fileContent = readFile(docFolder + File.separator + fileId + ".txt", StandardCharsets.UTF_8);
//			HttpResponse<String> response = genericRequest("")
//					.queryString("informat", "text")
//					.queryString("input", fileContent)
//					.queryString("language", "en")
//					.queryString("sentimentEngine", "corenlp")
//					.queryString("outformat", "turtle").asString();
//			Assert.assertTrue(response.getStatus() == 200);
//			Assert.assertTrue(response.getBody().length() > 0);
//			
//			Model nifModel = NIFReader.extractModelFromFormatString(response.getBody(), RDFSerialization.TURTLE); 
//			String sentVal = NIFReader.extractSentimentAnnotation(nifModel);
//			out.write(fileId + "\t" + sentVal + "\n");
//			System.out.println("INFO HERE:" + fileId + "\t" + sentVal + "\n");
////			System.out.println(response.getBody());
//			//Assert.assertEquals(TestConstants.outputTest2, response.getBody());
//			
//		}
//		out.close();
//
//		
//	}
	

	@Test
	public void test3_sentimentAnalysis_DFKI() throws UnirestException, IOException,Exception {
		String inputString = "1297685	24-03-2015	Debt collection	Cont'd attempts collect debt not owed	Debt is not mine	\"I find this medical debt reported on my credit report  but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist. It appears to have impact on my credit score. I am willing to pay it if I could get an itemized bill showing the date of the procedure and when and what my medical insurance company reports about it. Also  if the company no longer exist  IT WILL NEED TO BE REMOVE FROM MY CREDIT REPORT. My complaint is that this company has not sent me a bill showing the details of my so called debt  but has reported to the credit bureau that I owe this debt. This is harming my credit score and it is not making it 's contact information available to me. How am I to clear up my credit report if this company does n't exist  but they have reported that I owe a debt that I have no explanation for? _\" Company chooses not to provide a public response \"Healthcare Collections-I";
		HttpResponse<String> response = genericRequest("")
				.queryString("informat", "text")
				.queryString("input", inputString)
				.queryString("language", "en")
				.queryString("sentimentEngine", "dfki")
				.queryString("outformat", "turtle").asString();
		Assert.assertTrue(response.getStatus() == 200);
		Assert.assertTrue(response.getBody().length() > 0);
		System.out.println("DFKI: "+response.getBody());
		Assert.assertEquals(TestConstants.outputTest3, response.getBody());
	}

	@Test
	public void test31_sentimentAnalysis_DFKI() throws UnirestException, IOException,Exception {
		String inputString = "I find this medical debt reported on my credit report but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist.";

		HttpResponse<String> response = genericRequest("")
				.queryString("informat", "text")
				.queryString("input", inputString)
				.queryString("language", "en")
				.queryString("sentimentEngine", "dfki")
				.queryString("sentenceLevel", true)
				.queryString("outformat", "turtle").asString();
				
		Assert.assertTrue(response.getStatus() == 200);
		Assert.assertTrue(response.getBody().length() > 0);
//		System.out.println(response.getBody());
		Assert.assertEquals(TestConstants.outputTest31, response.getBody());
	}

	
	@Test
	public void test4_sentimentAnalysis_UNK() throws UnirestException, IOException,Exception {
		String inputString = "1297685	24-03-2015	Debt collection	Cont'd attempts collect debt not owed	Debt is not mine	\"I find this medical debt reported on my credit report  but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist. It appears to have impact on my credit score. I am willing to pay it if I could get an itemized bill showing the date of the procedure and when and what my medical insurance company reports about it. Also  if the company no longer exist  IT WILL NEED TO BE REMOVE FROM MY CREDIT REPORT. My complaint is that this company has not sent me a bill showing the details of my so called debt  but has reported to the credit bureau that I owe this debt. This is harming my credit score and it is not making it 's contact information available to me. How am I to clear up my credit report if this company does n't exist  but they have reported that I owe a debt that I have no explanation for? _\" Company chooses not to provide a public response \"Healthcare Collections-I";
		HttpResponse<String> response = genericRequest("")
				.queryString("informat", "text")
				.queryString("input", inputString)
				.queryString("language", "en")
				.queryString("sentimentEngine", "unk")
				.queryString("outformat", "turtle").asString();
		Assert.assertTrue(response.getStatus() == 400);
		Assert.assertTrue(response.getBody().contains("SentimentEngine value not supported"));
	}

}
