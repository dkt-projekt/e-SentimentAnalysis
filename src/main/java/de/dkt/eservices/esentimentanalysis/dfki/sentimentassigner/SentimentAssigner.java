package de.dkt.eservices.esentimentanalysis.dfki.sentimentassigner;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.eservices.esentimentanalysis.dfki.linguistic.LinguisticUnit;
import de.dkt.eservices.esentimentanalysis.dfki.linguistic.SpanText;
import de.dkt.eservices.esentimentanalysis.dfki.linguistic.SpanWord;
import de.dkt.eservices.esentimentanalysis.dfki.linguistic.Word;
import de.dkt.eservices.esentimentanalysis.dfki.values.SentimentValue;

public class SentimentAssigner {

	static Logger logger = Logger.getLogger(SentimentAssigner.class);

	private static Map<String, SentimentValue> values;
	private static String sentimentValuesDictionary = "sentimentDictionaries/sentimentvalues-en.txt";
	
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
	
	public double computeSentiment (LinguisticUnit lu){
		if(lu instanceof Word ){
			Word w = (Word)lu;
			return computeSentimentOfWord(w.getText());
		}
		else if(lu instanceof SpanWord ){
			SpanWord w = (SpanWord)lu;
			return computeSentimentOfWord(w.getText());
		}
		else{
			SpanText t = (SpanText) lu;
			List<LinguisticUnit> list = t.getChilds();
			List<Double> values = new LinkedList<Double>();
			for (LinguisticUnit lUnit : list) {
				double d= computeSentiment(lUnit);
				values.add( d );
			}
//			return computeSentimentOfText(list,values);
			return computeSentimentValueOfText(list,values);
		}
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
			if(sv.equals(SentimentValue.NONE)){
			}
			else if(sv.equals(SentimentValue.VERYNEGATIV)){
				d += -2;
			}
			else if(sv.equals(SentimentValue.NEGATIV)){
				d += -1;
			}
			else if(sv.equals(SentimentValue.NEUTRUM)){
			}
			else if(sv.equals(SentimentValue.POSITIVE)){
				d += 1;
			}
			else if(sv.equals(SentimentValue.VERYPOSITIVE)){
				d += 2;
			}
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
