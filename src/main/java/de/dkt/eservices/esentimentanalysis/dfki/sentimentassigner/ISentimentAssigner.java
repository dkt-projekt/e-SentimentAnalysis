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

public interface ISentimentAssigner {

//	public boolean loadValues();
	
	public LinguisticUnit computeSentiment (LinguisticUnit lu);

//	public double computeSentimentOfWord (String s);
	
//	public double computeSentimentValueOfText (List<LinguisticUnit> units, List<Double> semUnits);

		
		
//	public double computeSentimentOfText (List<LinguisticUnit> units, List<SentimentValue> semUnits);
	
}
