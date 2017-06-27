package de.dkt.eservices.esentimentanalysis;

import java.io.IOException;
import org.apache.jena.riot.RiotException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.exceptions.LoggedExceptions;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.tools.ParameterChecker;
import eu.freme.common.conversion.rdf.JenaRDFConversionService;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import de.dkt.eservices.esentimentanalysis.dfki.SentimentAnalyzer;
import de.dkt.eservices.esentimentanalysis.modules.CoreNLPSentimentAnalyzer;
/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 * The whole documentation about openNLP examples can be found in https://opennlp.apache.org/documentation/1.6.0/manual/opennlp.html
 *
 */

@Component
public class ESentimentAnalysisService {
    
	Logger logger = Logger.getLogger(ESentimentAnalysisService.class);

	@Autowired
	SentimentAnalyzer sa;

	RDFConversionService rdfConversionService = new JenaRDFConversionService();
	
	public ESentimentAnalysisService() {
	}
	
	public Model analyzeSentiment(String textToProcess, String languageParam, RDFConstants.RDFSerialization inFormat, String sentimentEngine, String prefix, boolean sentenceLevel)
					throws ExternalServiceFailedException, BadRequestException, IOException, Exception {
		ParameterChecker.checkNotNullOrEmpty(languageParam, "language", logger);
		
		try {
			Model nifModel = null;
			if (inFormat.equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
				nifModel = NIFWriter.initializeOutputModel();
				NIFWriter.addInitialString(nifModel, textToProcess, prefix);
			} else {
				try {
					nifModel = NIFReader.extractModelFromFormatString(textToProcess, inFormat);
				} catch (RiotException e) {
					throw new BadRequestException("Check the input format [" + inFormat + "]!!");
				}
			}
		
			if (languageParam.equals("en")) {
				if(sentimentEngine.equalsIgnoreCase("corenlp")){
					String defaultModel = null; //this is far from elegant, because in english case, it's not used (blame peter!)
					CoreNLPSentimentAnalyzer.getSentimentForModel(nifModel, sentenceLevel, languageParam, defaultModel);
				}
				else if(sentimentEngine.equalsIgnoreCase("dfki")){
					//SentimentAnalyzer sa = new SentimentAnalyzer();
					nifModel = sa.analyzeSentimentToModel(nifModel, sentenceLevel);
				}
				else {
					throw new BadRequestException("SentimentEngine value not supported");
				}
			} else if (languageParam.equals("de")) {// add clause for de here
													// when implemented
				if (sentimentEngine.equalsIgnoreCase("corenlp")) {
					String defaultModel = "corenlpTrainingDummyGermanBigger"; // if we also want to offer training as an endpoint, modelname should be a param, but for now it's just hardcoded
					
					CoreNLPSentimentAnalyzer.getSentimentForModel(nifModel, sentenceLevel, languageParam, defaultModel);
//				} else if (sentimentEngine.equalsIgnoreCase("dfki")) {
//					// SentimentAnalyzer sa = new SentimentAnalyzer();
//					nifModel = sa.analyzeSentimentToModel(nifModel, sentenceLevel);
				} else {
					throw new BadRequestException("SentimentEngine value not supported");
				}
			} else {
				logger.error("Unsupported language:" + languageParam);
				throw new BadRequestException("Unsupported language:" + languageParam);
			}
			
			
			return nifModel;
			
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (ExternalServiceFailedException e2) {
			logger.error(e2.getMessage());
			throw e2;
		}

	}
	
}
