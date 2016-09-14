package de.dkt.eservices.esentimentanalysis.dfki.sentimentassigner;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.eservices.erattlesnakenlp.linguistic.SentimentScoper;
import de.dkt.eservices.esentimentanalysis.dfki.linguistic.LinguisticUnit;
import de.dkt.eservices.esentimentanalysis.dfki.linguistic.SpanText;
import de.dkt.eservices.esentimentanalysis.dfki.linguistic.SpanWord;
import de.dkt.eservices.esentimentanalysis.dfki.linguistic.Word;
import de.dkt.eservices.esentimentanalysis.dfki.values.SentimentValue;
import edu.stanford.nlp.ling.IndexedWord;

public class PoSSentimentAssigner implements ISentimentAssigner{

	static Logger logger = Logger.getLogger(PoSSentimentAssigner.class);

	private static Map<String, SentimentValue> values;
	private static String sentimentValuesDictionary = "sentimentDictionaries/sentimentvalues-en2.txt";

	private static Map<String, Double> negationValues;
	private static String negationValuesDictionary = "sentimentDictionaries/negationvalues-en.txt";
	
	public boolean loadValues(){
		try{
			values = new HashMap<String, SentimentValue>();
			BufferedReader br = FileFactory.generateBufferedReaderInstance(sentimentValuesDictionary, "utf-8");
			String line = br.readLine();
			while(line!=null){
				String parts[] = line.split("\t");
				values.put(parts[0].toLowerCase(), SentimentValue.fromValue(parts[1]));
				line = br.readLine();
			}
			br.close();
			return true;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			return false;
		}
	}
	
	
	public boolean loadNegationValues(){
		try{
			negationValues = new HashMap<String, Double>();
			BufferedReader br = FileFactory.generateBufferedReaderInstance(negationValuesDictionary, "utf-8");
			String line = br.readLine();
			while(line!=null){
				String parts[] = line.split("\t");
				negationValues.put(parts[0].toLowerCase(), Double.parseDouble(parts[1]));
				line = br.readLine();
			}
			br.close();
			return true;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			return false;
		}
	}

	public LinguisticUnit computeSentiment (LinguisticUnit lu){
		if(lu instanceof Word ){
			Word w = (Word)lu;
			double d = computeSentimentOfWord(w.getText());
			w.setSentimentValue(d);
			return w;
		}
		else if(lu instanceof SpanWord ){
			SpanWord w = (SpanWord)lu;
			double d = computeSentimentOfWord(w.getText());
			w.setSentimentValue(d);
			return w;
		}
		else{
			SpanText t = (SpanText) lu;
			List<LinguisticUnit> list = t.getChilds();
			List<Double> values = new LinkedList<Double>();
		
			List<LinguisticUnit> newlist = new LinkedList<LinguisticUnit>();
			List<String> sw = new LinkedList<>();
			for (LinguisticUnit lUnit : list) {
				LinguisticUnit unitAux = computeSentiment(lUnit);
				newlist.add(unitAux);
				if(unitAux.getSentimentValue()>0|unitAux.getSentimentValue()<0){
					values.add( unitAux.getSentimentValue() );
					sw.add(lUnit.getString());
					}
			}
			double value =0;
			for (double v : values){
				value += v;
			}
	
			//list = word of sentence
			//values = list of doubles that are sent.values for the words of the sentence
			
			//find which words of our sentiment lexicon are in the sentence
			//put those word in the list the scoper is going to look for
			//look if there is any negation for a word that is in the sentiment lexicon
			//if yes, invert the value
			//add everything up
			//walk trough sMap and filter stings starting with "negated_". Substract the "negated_" and get the values, *(-1) before adding,
			//then add the two values. 
			
			HashMap<String, List<IndexedWord>>  sMap = SentimentScoper.getScopeForSentiment(lu.getString(), sw);
			System.out.println(sMap);
			List<String> negatedWords = new LinkedList<>();
			for (String key : sMap.keySet()) {
			    if(key.contains("negated_")){
			    	key = key.replace("negated_", "");
			    	key = key.replaceAll("\\|.*", "");
			    	negatedWords.add(key);
			    }
			}
			double f =0;
			for(String word : negatedWords){
				Word wd = new Word(word);
				LinguisticUnit foo = computeSentiment(wd);
				f += foo.getSentimentValue()*(-2);				
			}
			double total = value+f;
			t.setSentimentValue(total);
			
			
			
//			return computeSentimentOfText(list,values);
			
//			List<LinguisticUnit> negatedList = applyNegation(newlist);
			
			double d = computeSentimentValueOfText(list,values);
			
			
			
			return t;
		}
	}
	
/*	private List<LinguisticUnit> applyNegation(List<LinguisticUnit> newlist) {
		for (LinguisticUnit lu : newlist) {
			//First, we should determine if it is a negation word.			
			
			if(lu instanceof Word ){
				Word w = (Word)lu;
				double d = computeNegationOfWord(w.getText());
				w.setSentimentValue(d);
				return w;
			}
			else if(lu instanceof SpanWord ){
				SpanWord w = (SpanWord)lu;
				double d = computeNegationOfWord(w.getText());
				w.setSentimentValue(d);
				return w;
			}
			else{
				SpanText t = (SpanText) lu;
				List<LinguisticUnit> list = t.getChilds();
				List<Double> values = new LinkedList<Double>();
				
				List<LinguisticUnit> newlist = new LinkedList<LinguisticUnit>();
				for (LinguisticUnit lUnit : list) {
					LinguisticUnit unitAux = computeSentiment(lUnit);
					newlist.add(unitAux);
					values.add( unitAux.getSentimentValue() );
				}
//				return computeSentimentOfText(list,values);
				
				List<LinguisticUnit> negatedList = applyNegation(newlist);
				
				double d = computeSentimentValueOfText(list,values);
				
				
				
				return t;
			}
		}

		
		
		return null;
	}*/

	public double computeNegationOfWord (String s) {
		if(negationValues==null){
			if(!loadNegationValues()){
				return 0;
			}
		}
		if(negationValues.containsKey(s)){
			double d = negationValues.get(s);
			return d;
		}
		return 0;
	}

	public double computeSentimentOfWord (String s) {
		if(values==null){
			if(!loadValues()){
				return 0;
			}
		}
		SentimentValue v = values.get(s);
//		System.out.println(s+"\t"+v);
		if(v==null){
			return 0;
		}
		return v.numericValue();
	}
	
	public double computeSentimentValueOfText (List<LinguisticUnit> units, List<Double> semUnits){
		double d = 0;
		for (double ad: semUnits) {
			d += ad;
		}

		return d;
	}

		
		
	public double computeSentimentOfText (List<LinguisticUnit> units, List<SentimentValue> semUnits){

		/**
		 * 
		 * TODO: consider NEGATION
		 * 
		 * TODO: consider IRONY
		 * 
		 * TODO: consider more complex approaches.
		 * 
		 * TODO: consider modificators for words. 
		 * 
		 */
		
		//Simpliest approach: add values and see polarity
		double d = 0;
		for (SentimentValue sv : semUnits) {
			d += sv.numericValue();
//			if(sv.equals(SentimentValue.NONE)){
//			}
//			else if(sv.equals(SentimentValue.VERYNEGATIV)){
//				d += -2;
//			}
//			else if(sv.equals(SentimentValue.NEGATIV)){
//				d += -1;
//			}
//			else if(sv.equals(SentimentValue.NEUTRUM)){
//			}
//			else if(sv.equals(SentimentValue.POSITIVE)){
//				d += 1;
//			}
//			else if(sv.equals(SentimentValue.VERYPOSITIVE)){
//				d += 2;
//			}
		}
		return d;
//		if(d<=-2){
//			return SentimentValue.VERYNEGATIV;
//		}
//		else if(d<0){
//			return SentimentValue.NEGATIV;
//		}
//		else if(d==0){
//			return SentimentValue.NEUTRUM;
//		}
//		else if(d<2){
//			return SentimentValue.POSITIVE;
//		}
//		else{
//			return SentimentValue.VERYPOSITIVE;
//		}
	}

	
}
