package de.dkt.eservices.esentimentanalysis;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.common.feedback.InteractionManagement;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.tools.ParameterChecker;
import de.dkt.common.tools.ResponseGenerator;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;

@RestController
public class ESentimentAnalysisServiceStandAlone extends BaseRestController {
	
	Logger logger = Logger.getLogger(ESentimentAnalysisServiceStandAlone.class);

	@Autowired
	ESentimentAnalysisService service;

	@RequestMapping(value = "/e-sentimentanalysis/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
            @RequestBody(required = false) String postBody) throws Exception {
    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
	}
	
	@RequestMapping(value = "/e-sentimentanalysis", method = {
            RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> analyseSentiments(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestParam(value = "sentimentEngine", required = false) String sentimentEngine,
			@RequestParam(value = "sentenceLevel", required = false) boolean sentenceLevel,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,
            @RequestBody(required = false) String postBody) throws Exception {
        
		// Check the language parameter.
		ParameterChecker.checkInList(language, "en;de", "language", logger);
        
		if(allParams.get("input")==null){
        	allParams.put("input", input);
        }
        if(allParams.get("informat")==null){
        	allParams.put("informat", informat);
        }
        if(allParams.get("outformat")==null){
        	allParams.put("outformat", outformat);
        }
        if(allParams.get("prefix")==null){
        	allParams.put("prefix", prefix);
        }
        if (input == null) {
			input = i;
		}
		if (informat == null) {
			informat = f;
		}
		if (outformat == null) {
			outformat = o;
		}
		if (prefix == null) {
			prefix = p;
		}
        if (prefix == null || prefix.equalsIgnoreCase("")){
			prefix = DKTNIF.getDefaultPrefix();
		}
        
        NIFParameterSet nifParameters = this.normalizeNif(postBody, acceptHeader, contentTypeHeader, allParams, false);
        
        Model inModel = ModelFactory.createDefaultModel();
        
        String textForProcessing = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
        	// input is sent as value of the input parameter
            textForProcessing = nifParameters.getInput();
            //rdfConversionService.plaintextToRDF(inModel, textForProcessing,language, nifParameters.getPrefix());
        } else {
            //inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
        	textForProcessing = nifParameters.getInput();
        	if (textForProcessing == null){
        		textForProcessing = postBody;
        	}
        	//textForProcessing = postBody;
            if (textForProcessing == null) {
    			String msg = "No text to process.";
    			logger.error(msg);
    			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "/e-nlp/SentimentAnalysis", msg, 
    					"", "Exception", msg, "");
    			throw new BadRequestException(msg);
            }
        }
        
        
        try {
        	Model outModel = service.analyzeSentiment(textForProcessing, language, nifParameters.getInformat(),sentimentEngine,prefix, sentenceLevel);
            outModel.add(inModel);
            // remove unwanted info
            //NOTE: don't know why this is here. Commenting it out
//            outModel.removeAll(null, RDF.type, OWL.ObjectProperty);
//            outModel.removeAll(null, RDF.type, OWL.DatatypeProperty);
//            outModel.removeAll(null, RDF.type, OWL.Class);
//            outModel.removeAll(null, RDF.type, OWL.Class);
//            ResIterator resIter = outModel.listResourcesWithProperty(RDF.type, outModel.getResource("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#Entry"));
//            while (resIter.hasNext()) {
//                Resource res = resIter.next();
//                outModel.removeAll(res, null, (RDFNode) null);
//            }
    		InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "/e-nlp/SentimentAnalysis", "Success", "", "", "", "");

            return createSuccessResponse(outModel, nifParameters.getOutformat());
            
        } catch (BadRequestException e) {
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "/e-nlp/SentimentAnalysis", e.getMessage(), "", "Exception", e.getMessage(), "");
        	logger.error(e.getMessage());
            throw e;
        } catch (ExternalServiceFailedException e) {
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "/e-nlp/SentimentAnalysis", e.getMessage(), "", "Exception", e.getMessage(), "");
        	logger.error(e.getMessage());
            throw e;
        }
    }
	
}
