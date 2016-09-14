package de.dkt.eservices.esentimentanalysis.dfki.linguistic;

public class Word implements LinguisticUnit {

	protected String text;
	protected double sentimentValue;

	
	public Word(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void indentedPrintToScreen(String indent){
		System.out.println(indent+text);
	}
	public double getSentimentValue() {
		return sentimentValue;
	}
	
	public void setSentimentValue(double sentimentValue) {
		this.sentimentValue = sentimentValue;
	}
	
	public String getString(){
		return text;
	}


}
