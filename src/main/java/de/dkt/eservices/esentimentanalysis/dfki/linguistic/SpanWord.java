package de.dkt.eservices.esentimentanalysis.dfki.linguistic;

public class SpanWord implements LinguisticUnit {

	protected String text;
	protected int startSpan;
	protected int endSpan;
	
	protected double sentimentValue;

	public SpanWord(String text) {
		super();
		this.text = text;
	}
	
	public SpanWord(String text, int startSpan, int endSpan) {
		super();
		this.text = text;
		this.startSpan = startSpan;
		this.endSpan = endSpan;
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
	

	public int getStartSpan() {
		return startSpan;
	}

	public void setStartSpan(int startSpan) {
		this.startSpan = startSpan;
	}

	public int getEndSpan() {
		return endSpan;
	}

	public void setEndSpan(int endSpan) {
		this.endSpan = endSpan;
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
