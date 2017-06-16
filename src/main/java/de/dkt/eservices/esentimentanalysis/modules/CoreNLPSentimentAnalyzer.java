package de.dkt.eservices.esentimentanalysis.modules;
		
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.commons.io.FilenameUtils;
import org.omg.Messaging.SyncScopeHelper;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations.GoldClass;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.TreeBinarizer;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.sentiment.BuildBinarizedDataset;
import edu.stanford.nlp.sentiment.CollapseUnaryTransformer;
import edu.stanford.nlp.sentiment.RNNOptions;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.sentiment.SentimentCostAndGradient;
import edu.stanford.nlp.sentiment.SentimentModel;
import edu.stanford.nlp.sentiment.SentimentTraining;
import edu.stanford.nlp.sentiment.SentimentUtils;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.Trees;
import edu.stanford.nlp.trees.international.negra.NegraHeadFinder;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.StringUtils;
import edu.stanford.nlp.util.logging.Redwood;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.util.Span;

	         


public class CoreNLPSentimentAnalyzer {
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
//	 public static void extractLabels(Map<Pair<Integer, Integer>, String> spanToLabels, List<HasWord> tokens, String line) {
//		String[] pieces = line.trim().split("\\s+");
//		if (pieces.length == 0) {
//			return;
//		}
//		if (pieces.length == 1) {
//			String error = "Found line with label " + line + " but no tokens to associate with that line";
//			throw new RuntimeException(error);
//		}
//		for (int i = 0; i < tokens.size() - pieces.length + 2; ++i) {
//			boolean found = true;
//			for (int j = 1; j < pieces.length; ++j) {
//				if (!tokens.get(i + j - 1).word().equals(pieces[j])) {
//					found = false;
//					break;
//				}
//			}
//			if (found) {
//				spanToLabels.put(new Pair<>(i, i + pieces.length - 1), pieces[0]);
//			}
//		}
//	}

	public static void setPredictedLabels(Tree tree) {
		if (tree.isLeaf()) {
			return;
		}
	}

	public static void setUnknownLabels(Tree tree, Integer defaultLabel) {
		if (tree.isLeaf()) {
			return;
		}
	}
	
	public static Tree traverseTreeAndChangePosTagsToNumbers(Tree tree) {

		for (Tree subtree : tree.getChildrenAsList()) {
			if (subtree.label().toString().matches("\\D+")) { 
				subtree.label().setValue("2");
				
			}if (Integer.parseInt(subtree.label().toString())<0||Integer.parseInt(subtree.label().toString())>4){
				subtree.label().setValue("2");
			}
			if (!(subtree.isPreTerminal())) {
				traverseTreeAndChangePosTagsToNumbers(subtree);
			}
		}

		return tree;
	}

	  public static boolean setSpanLabel(Tree tree, Pair<Integer, Integer> span, String value) {
		    if (!(tree.label() instanceof CoreLabel)) {
		      throw new AssertionError("Expected CoreLabels");
		    }
		    CoreLabel label = (CoreLabel) tree.label();
		    if (label.get(CoreAnnotations.BeginIndexAnnotation.class).equals(span.first) &&
		        label.get(CoreAnnotations.EndIndexAnnotation.class).equals(span.second)) {
		      label.setValue(value);
		      return true;
		    }
		    if (label.get(CoreAnnotations.BeginIndexAnnotation.class) > span.first &&
		        label.get(CoreAnnotations.EndIndexAnnotation.class) < span.second) {
		      return false;
		    }
		    for (Tree child : tree.children()) {
		      if (setSpanLabel(child, span, value)) {
		        return true;
		      }
		    }
		    return false;
		  }
	
	@SuppressWarnings("unused")
	public String prepareTrainingDataDebug (String inputPath){
		String tmpFilePath = null;
		PrintWriter tempFile = null;
		try {
			tmpFilePath = FileFactory.generateOrCreateFileInstance("sentimentModels" + File.separator + "corenlpTraining.tmp").getAbsolutePath(); // would rather not do this with temp files and return List<Tree> directly here, but that was problematic later on with reading the gold labels...
			tempFile = new PrintWriter(tmpFilePath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("FilePath: "+tmpFilePath);
		
		CollapseUnaryTransformer transformer = new CollapseUnaryTransformer();

	    String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";


	    String sentimentModelPath = null;
	    SentimentModel sentimentModel = null;


	    if (inputPath == null) {
	      throw new IllegalArgumentException("Must specify input file with -input");
	    }

	    LexicalizedParser parser = LexicalizedParser.loadModel(parserModel);
	    TreeBinarizer binarizer = TreeBinarizer.simpleTreeBinarizer(parser.getTLPParams().headFinder(), parser.treebankLanguagePack());

	    if (sentimentModelPath != null) {
	      sentimentModel = SentimentModel.loadSerialized(sentimentModelPath);
	    }

	    String text = IOUtils.slurpFileNoExceptions(inputPath);
	    String[] chunks = text.split("\\n\\s*\\n+"); // need blank line to make a new chunk

	    for (String chunk : chunks) {
	      if (chunk.trim().isEmpty()) {
	        continue;
	      }
	      // The expected format is that line 0 will be the text of the
	      // sentence, and each subsequence line, if any, will be a value
	      // followed by the sequence of tokens that get that value.

	      // Here we take the first line and tokenize it as one sentence.
	      String[] lines = chunk.trim().split("\\n");
	      String sentence = lines[0];
	      StringReader sin = new StringReader(sentence);
	      DocumentPreprocessor document = new DocumentPreprocessor(sin);
	      document.setSentenceFinalPuncWords(new String[] {"\n"});
	      List<HasWord> tokens = document.iterator().next();
	      Integer mainLabel = new Integer(tokens.get(0).word());

	      tokens = tokens.subList(1, tokens.size());
	      //log.info(tokens);

	      //spanToLabels maps the span of a word or phrase to the sentiment value assigned to this word. 
	      Map<Pair<Integer, Integer>, String> spanToLabels = Generics.newHashMap();
	      for (int i = 1; i < lines.length; ++i) {
	        edu.stanford.nlp.sentiment.BuildBinarizedDataset.extractLabels(spanToLabels, tokens, lines[i]);
	      }

	      // TODO: add an option which treats the spans as constraints when parsing
	      Tree tree = parser.apply(tokens);
	      Tree binarized = binarizer.transformTree(tree);
	      Tree collapsedUnary = transformer.transformTree(binarized);

	      // if there is a sentiment model for use in prelabeling, we
	      // label here and then use the user given labels to adjust
	      if (sentimentModel != null) {
	        Trees.convertToCoreLabels(collapsedUnary);
	        SentimentCostAndGradient scorer = new SentimentCostAndGradient(sentimentModel, null);
	        scorer.forwardPropagateTree(collapsedUnary);
	        setPredictedLabels(collapsedUnary);
	      } else {
	        setUnknownLabels(collapsedUnary, mainLabel);
	      }

	      Trees.convertToCoreLabels(collapsedUnary);
	      collapsedUnary.indexSpans();

	      //Here the POS tags are changed to numbers whenever we have annotated some phrase with sentiment values
	      //If not all subphrases of the sentence are annotated with sentiment values, POS tags remain
	      //We change them to a neutral value afterwards. 
	      for (Map.Entry<Pair<Integer, Integer>, String> pairStringEntry : spanToLabels.entrySet()) {
	        setSpanLabel(collapsedUnary, pairStringEntry.getKey(), pairStringEntry.getValue());
	        collapsedUnary.pennPrint();
	      }
	      
	      //we change "ROOT" to the mainLabel of the sentence
	      collapsedUnary.label().setValue(Integer.toString(mainLabel));
	      //we change all remaining POS tags to a neutral number 
	      traverseTreeAndChangePosTagsToNumbers(collapsedUnary);

	      //System.out.println(collapsedUnary);
	      tempFile.println(collapsedUnary);
	    }
	    tempFile.close();
		return tmpFilePath;
	}

//	// https://github.com/stanfordnlp/CoreNLP/blob/master/src/edu/stanford/nlp/sentiment/BuildBinarizedDataset.java
//	public String prepareTrainingData(String inputPath){
//		// code below is copied/stripped from stanford github (check link above
//		// for more details/comments/explanation
//		
//		List<Tree> trainingTrees = new ArrayList<Tree>();
//		
//		CollapseUnaryTransformer transformer = new CollapseUnaryTransformer();
//		String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz"; // TODO: make argument (allow/test german)
//		SentimentModel sentimentModel = null;
//		LexicalizedParser parser = LexicalizedParser.loadModel(parserModel);
//		parser.setOptionFlags("-tagSeparator ");
//		TreeBinarizer binarizer = TreeBinarizer.simpleTreeBinarizer(parser.getTLPParams().headFinder(),	parser.treebankLanguagePack());
//		String tmpFilePath = null;
//		PrintWriter tempFile = null;
//		try {
//			tmpFilePath = FileFactory.generateOrCreateFileInstance("sentimentModels" + File.separator + "corenlpTraining.tmp").getAbsolutePath(); // would rather not do this with temp files and return List<Tree> directly here, but that was problematic later on with reading the gold labels...
//			tempFile = new PrintWriter(tmpFilePath);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//
//		String text = IOUtils.slurpFileNoExceptions(inputPath);
//		String[] chunks = text.split("\\n\\s*\\n+"); // need blank line to
//		for (String chunk : chunks) {
//			if (chunk.trim().isEmpty()) {
//				continue;
//			}
//			String[] lines = chunk.trim().split("\\n");
//			String sentence = lines[0];
//			StringReader sin = new StringReader(sentence);
//			DocumentPreprocessor document = new DocumentPreprocessor(sin);
//			document.setSentenceFinalPuncWords(new String[] { "\n" });
//			List<HasWord> tokens = document.iterator().next();
//			Integer mainLabel = new Integer(tokens.get(0).word());  
//			tokens = tokens.subList(1, tokens.size());
//			Map<Pair<Integer, Integer>, String> spanToLabels = Generics.newHashMap();
//			for (int i = 1; i < lines.length; ++i) {
//				//extractLabels(spanToLabels, tokens, lines[i]);
//				edu.stanford.nlp.sentiment.BuildBinarizedDataset.extractLabels(spanToLabels, tokens, lines[i]);
//			}
//			Tree tree = parser.apply(tokens);
//			Tree binarized = binarizer.transformTree(tree);
//			Tree collapsedUnary = transformer.transformTree(binarized);
//			if (sentimentModel != null) {
//				Trees.convertToCoreLabels(collapsedUnary);
//				SentimentCostAndGradient scorer = new SentimentCostAndGradient(sentimentModel, null);
//				scorer.forwardPropagateTree(collapsedUnary);
//				setPredictedLabels(collapsedUnary);
//			} else {
//				setUnknownLabels(collapsedUnary, mainLabel);
//			}
//			Trees.convertToCoreLabels(collapsedUnary);
//			collapsedUnary.indexSpans();
//			for (Map.Entry<Pair<Integer, Integer>, String> pairStringEntry : spanToLabels.entrySet()) {
//				edu.stanford.nlp.sentiment.BuildBinarizedDataset.setSpanLabel(collapsedUnary, pairStringEntry.getKey(), pairStringEntry.getValue());
//			}
//			
//			//trainingTrees.add(collapsedUnary);
//			//System.out.println("Debugging collaped Unary:" + collapsedUnary);
//			tempFile.println(collapsedUnary);
//		}
//		tempFile.close();
//		
//		
//		return tmpFilePath;
//		
//	}
	

	
	
	@SuppressWarnings("static-access")
	public String trainModel(String trainingTreesFile, String modelName){
		
		SentimentTraining st = new SentimentTraining();
		String devPath = "dummy"; //TODO: check what this is supposed to contain (the same trees as the training ones? Some tuning set?)

		RNNOptions op = new RNNOptions();
		boolean runGradientCheck = false;
		boolean runTraining = true; //  TODO: check why the hell you would want to have this to false in a trainModel method? Is there a reason for this?
		boolean filterUnknown = false;

		String modelPath = null;
		try {
			//modelPath = FileFactory.generateFileInstance("sentimentModels" + File.separator + modelName + ".bin").getAbsolutePath();
			modelPath = FileFactory.generateOrCreateFileInstance("sentimentModels" + File.separator + modelName).getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: check if I want any of this below...
//		for (int argIndex = 0; argIndex < args.length;) {
//			if (args[argIndex].equalsIgnoreCase("-train")) {
//				runTraining = true;
//				argIndex++;
//			} else if (args[argIndex].equalsIgnoreCase("-gradientcheck")) {
//				runGradientCheck = true;
//				argIndex++;
//			} else if (args[argIndex].equalsIgnoreCase("-trainpath")) {
//				trainPath = args[argIndex + 1];
//				argIndex += 2;
//			} else if (args[argIndex].equalsIgnoreCase("-devpath")) {
//				devPath = args[argIndex + 1];
//				argIndex += 2;
//			} else if (args[argIndex].equalsIgnoreCase("-model")) {
//				modelPath = args[argIndex + 1];
//				argIndex += 2;
//			} else if (args[argIndex].equalsIgnoreCase("-filterUnknown")) {
//				filterUnknown = true;
//				argIndex++;
//			} else {
//				int newArgIndex = op.setOption(args, argIndex);
//				if (newArgIndex == argIndex) {
//					throw new IllegalArgumentException("Unknown argument " + args[argIndex]);
//				}
//				argIndex = newArgIndex;
//			}
//		}

//		if (filterUnknown) {
//			trainingTrees = SentimentUtils.filterUnknownRoots(trainingTrees);
//		}

//		List<Tree> devTrees = null;
//		if (devPath != null) {
//			devTrees = SentimentUtils.readTreesWithGoldLabels(devPath);
//			if (filterUnknown) {
//				devTrees = SentimentUtils.filterUnknownRoots(devTrees);
//			}
//		}

		List<Tree> trainingTrees = SentimentUtils.readTreesWithGoldLabels(trainingTreesFile);
		for(Tree t : trainingTrees){
		}
		SentimentModel model = new SentimentModel(op, trainingTrees);

//		if (op.trainOptions.initialMatrixLogPath != null) {
//			StringUtils.printToFile(new File(op.trainOptions.initialMatrixLogPath), model.toString(), false, false,
//					"utf-8");
//		}

		// TODO: need to handle unk rules somehow... at test time the tree
		// structures might have something that we never saw at training
		// time. for example, we could put a threshold on all of the
		// rules at training time and anything that doesn't meet that
		// threshold goes into the unk. perhaps we could also use some
		// component of the accepted training rules to build up the "unk"
		// parameter in case there are no rules that don't meet the
		// threshold

//		if (runGradientCheck) {
//			st.runGradientCheck(model, trainingTrees);
//		}

		List<Tree> devTrees = null;
//		if (devPath != null) {
//			devTrees = SentimentUtils.readTreesWithGoldLabels(devPath);
//			if (filterUnknown) {
//				devTrees = SentimentUtils.filterUnknownRoots(devTrees);
//			}
//		}
		//TODO: check what the devTrees are supposed to contain? tuning data? we don't have any, so:
		devTrees = trainingTrees; 
		 	    Label label = trainingTrees.get(0).label();
			    if (!(label instanceof CoreLabel)) {
			      throw new IllegalArgumentException("CoreLabels required to get the attached gold class");
			    }
			    System.out.println("DEBUGGING gold class:" + ((CoreLabel) label).get(GoldClass.class));
		
		if (runTraining) {
//			System.out.println("DEBUGGING MODEL:" + model);
//			System.out.println("DEBUGGING MODELPATH:" + modelPath);
//			System.out.println("DEBUGGING trainingTrees:" + trainingTrees);
//			System.out.println("DEBUGGING trainindevTrees:" + devTrees);
			st.train(model, modelPath, trainingTrees, devTrees);
			System.out.println("DEBUG NUM CLASSES:" + model.numClasses);
			model.saveSerialized(modelPath);
		}
		return modelPath;
	}

		
	
	
	
	public static void main(String[] args) throws Exception {

//		CoreNLPSentimentAnalyzer sa = new CoreNLPSentimentAnalyzer();
//		String fp = sa.prepareTrainingDataDebug("C:\\Users\\Sabine\\Desktop\\WörkWörk\\e-Sentimentanalysis\\100linesExample.txt");
//		String modelPath = sa.trainModel(fp, "corenlpTrainingDummy");
		// TODO: the BuildBinarizedDataset output is in the wrong format. Figure out how to fix this (see also https://stackoverflow.com/questions/44458405/corenlp-sentiment-training-data-in-wrong-format)
		
		
//		System.out.println(getSentiment("This is good. It is very great. Better."));
//		System.exit(1);
		
//		String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\FRONTEO\\complaintsIndividualFiles";
//		File df = new File(docFolder);
//		for (File f : df.listFiles()){
//			String fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
//			double sentVal = getSentiment(fileContent);
//			System.out.println("Filename:" + f.getAbsolutePath());
//			System.out.println("sentiment value:" + sentVal);
//			System.out.println("\n");
//		}
		
		String modelPath = FileFactory.generateOrCreateFileInstance("sentimentModels" + File.separator + "corenlpTrainingDummy").getAbsolutePath();
		System.out.println(getSentiment("love great wonderful awesome cool nice good", "de", modelPath));
		System.out.println(getSentiment("shit fuck damn piss off screw bad angry", "de", modelPath));
		
		
		//String str = "1297685	24-03-2015	Debt collection	Cont'd attempts collect debt not owed	Debt is not mine	\"I find this medical debt reported on my credit report  but I do not remember ever owing a bill with a remaining balance of {$7.00}. It looks like the company dated the opening of this debt XX/XX/2010. I looked for the collection company on line  but it is as though it does n't exist. It appears to have impact on my credit score. I am willing to pay it if I could get an itemized bill showing the date of the procedure and when and what my medical insurance company reports about it. Also  if the company no longer exist  IT WILL NEED TO BE REMOVE FROM MY CREDIT REPORT. My complaint is that this company has not sent me a bill showing the details of my so called debt  but has reported to the credit bureau that I owe this debt. This is harming my credit score and it is not making it 's contact information available to me. How am I to clear up my credit report if this company does n't exist  but they have reported that I owe a debt that I have no explanation for? _\" Company chooses not to provide a public response \"Healthcare Collections-I";
		//int sentVal = getSentiment(str);
		//System.out.println("Sentiment value:" + sentVal);
		
		
		//TODO: keep this for somewhere, as it seems to be a neat way of changing the language/annotators in corenlp to german/anything else we included in the pom file
//		Properties germanProps = new Properties();
//		germanProps.setProperty("annotators", "tokenize, ssplit, parse");
//		germanProps.put("tokenize.language", "de");
//		//germanProps.put("parse.model", "edu/stanford/nlp/models/lexparser/germanFactored.ser.gz");
//		germanProps.put("parse.model", "edu/stanford/nlp/models/parser/nndep/UD_German.gz");
		
//		PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
//		
//		Date d1 = new Date();
//		
//		try {
//			String fileContent = readFile("C:\\Users\\pebo01\\Desktop\\data\\sentimentData\\sanders-twitter-0.2\\stuffDownloadedFromGithub\\testData.txt", StandardCharsets.UTF_8);
//			String[] lines = fileContent.split("\\n");
//			for (String l : lines){
//				String v = null;
//				Double polarity = getSentiment(l);
//				
//				if (polarity > 1){
//					v = "1";
//				}
//				else {
//					v = "0";
//				}
//				System.out.println(v + "\t" + l);
//				out.write(v + "\t" + l + "\n");
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		out.close();
//		Date d2 = new Date();
//		System.out.println("Start time:" + d1);
//		System.out.println("End time:" + d2);

	}
	
	
	public static Model getSentimentForModel(Model nifModel, boolean sentenceLevel, String language,  String modelPath) throws IOException {
		
		String s = NIFReader.extractIsString(nifModel);
		double sentVal = getSentiment(s,language, modelPath);
		NIFWriter.addSentimentAnnotation(nifModel, s, NIFReader.extractDocumentURI(nifModel), sentVal);
		
		if (sentenceLevel == true){ // also do sentence level annotations
			Span[] sentenceSpans = null;
			if (language.equals("en")){
			sentenceSpans = de.dkt.eservices.eopennlp.modules.SentenceDetector.detectSentenceSpans(s, "en-sent.bin"); 
			}
			else if (language.equals("de")){
			sentenceSpans = de.dkt.eservices.eopennlp.modules.SentenceDetector.detectSentenceSpans(s, "de-sent.bin"); 
			}else{
				//If we want more languages, add them here!
			}
			for (Span sp : sentenceSpans){
				String sent = s.substring(sp.getStart(), sp.getEnd());
				double sentValSentence = getSentiment(sent, language, modelPath);
				NIFWriter.addSentenceSentimentAnnotation(nifModel, sp.getStart(), sp.getEnd(), sentValSentence);
			}
		}
		
		return nifModel;
		
	}
	

	public static double getSentiment(String text, String lang, String modelPath) throws IOException {

		Properties props = new Properties();
		if (lang.equals("en")){
			props.setProperty("annotators", "tokenize, ssplit, parse, sentiment ");
		}
		else if(lang.equals("de")){
			props.setProperty("annotators", "tokenize, ssplit, parse, sentiment "); // TODO: change to german parser and tokeniyer etc here!!!
			props.setProperty("tokenize.language", "de");
			props.setProperty("parse.model", "edu/stanford/nlp/models/lexparser/germanFactored.ser.gz");
			props.setProperty("sentiment.model", modelPath);
		}
		
		//props.setProperty("sentiment.model", "C:\\Users\\Sabine\\git\\e-SentimentAnalysis\\target\\classes\\sentimentModels\\corenlpTrainingDummy");
		if (modelPath != null){
			String absPath = null;
			//absPath = FileFactory.generateOrCreateFileInstance("sentimentModels" + File.separator + modelPath).getAbsolutePath();
			absPath = modelPath;
			props.setProperty("sentiment.model", absPath);
		}
		
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
