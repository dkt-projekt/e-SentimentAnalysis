package de.dkt.eservices.esentimentanalysis;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;

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
		String inputString = "1297685	24-03-2015	Debt collection	Cont'd attempts collect debt not owed	Debt is not mine	\"I find this medical debt reported on my credit report  but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist. It appears to have impact on my credit score. I am willing to pay it if I could get an itemized bill showing the date of the procedure and when and what my medical insurance company reports about it. Also  if the company no longer exist  IT WILL NEED TO BE REMOVE FROM MY CREDIT REPORT. My complaint is that this company has not sent me a bill showing the details of my so called debt  but has reported to the credit bureau that I owe this debt. This is harming my credit score and it is not making it 's contact information available to me. How am I to clear up my credit report if this company does n't exist  but they have reported that I owe a debt that I have no explanation for? _\" Company chooses not to provide a public response \"Healthcare Collections-I";

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
