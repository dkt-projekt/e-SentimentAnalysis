package de.dkt.eservices.esentimentanalysis.modules;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.dkt.common.filemanagement.FileFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class OpenNLPSentimentAnalyzer {
	
	public static String sentimentModels ="sentimentModels"; 
	
	public static String trainModel(String trainingFilePath, String modelName) {
		DoccatModel model;
		
		File newModel = null;
		InputStream dataIn = null;
		try {
			dataIn = new FileInputStream(trainingFilePath);
			ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
			ObjectStream sampleStream = new DocumentSampleStream(lineStream);
			// Specifies the minimum number of times a feature must be seen
			int cutoff = 2;
			int trainingIterations = 30;
			//model = DocumentCategorizerME.train("en", sampleStream, cutoff, trainingIterations); // TODO find out why this is deprecated/not defined/how to include cutoff and iterations
			//model = DocumentCategorizerME.train("en", sampleStream);
			model = DocumentCategorizerME.train(modelName, sampleStream); // NOTE: if there is too few data, this may crash on indexoutofbounds. Maybe check data first and warn/error if too few...
			newModel = FileFactory.generateOrCreateFileInstance(sentimentModels + File.separator + modelName);
			//newModel = FileFactory.generateOrCreateFileInstance("C:\\Users\\pebo01\\workspace\\e-SentimentAnalysis\\src\\main\\resources\\sentimentModels" + File.separator + modelName);
			//newModel = new File(sentimentModels, modelName);
			//newModel.createNewFile();
			OutputStream modelOut = null;
			modelOut = new BufferedOutputStream(new FileOutputStream(newModel));
			model.serialize(modelOut);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataIn != null) {
				try {
					dataIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "INFO: Successfully trained model:" + modelName; // FIXME, not sure if this also return this if it failed
	}
	
	
	public static String classifyText(String text, String modelName) {
		
		String returnValue = null;
		
		try {
			DoccatModel model = new DoccatModel(new FileInputStream(FileFactory.generateFileInstance(sentimentModels + File.separator + modelName)));
			DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
			double[] outcomes = myCategorizer.categorize(text);
			String category = myCategorizer.getBestCategory(outcomes);

			if (category.equalsIgnoreCase("1")) { // TODO: experiment with more fine-grained classes and then modify this here accordingly
				returnValue = "POSTIVE";
			} else {
				returnValue = "NEGATIVE";
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
		
	}
	
	public static void main(String[] args){
		
		/*
		 * input should look like this, with a 1 for positive, 0 for negative: // TODO: experiment with non binary values, but 0-0.25 for very negative, 0.25-0.75 for neutral, 0.75-1 for positive. I guess the classification stuff will work not so good, but we can give it a try... 
		 * 	1	Watching a nice movie
			0	The painting is ugly, will return it tomorrow...
			1	One of the best soccer games, worth seeing it
			1	Very tasty, not only for vegetarians
			1	Super party!
			0	Too early to travel..need a coffee
			0	Damn..the train is late again...
			0	Bad news, my flight just got cancelled.
			1	Happy birthday mr. president
			1	Just watch it. Respect.
			1	Wonderful sunset.
			1	Bravo, first title in 2014!
		 */
		
		//String result = trainModel("C:\\Users\\pebo01\\Desktop\\dummyData.txt", "dummyModel");
		//System.out.println(result);
		
		System.out.println(classifyText("So happy to be home", "dummyModel"));
		System.out.println(classifyText("The painting is ugly, will return it tomorrow...", "dummyModel"));
		
	}
	
	

}
