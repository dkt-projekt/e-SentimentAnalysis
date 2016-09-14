package de.dkt.eservices.esentimentanalysis.dfki.linguistic;

public interface LinguisticUnit {

	public void indentedPrintToScreen(String indent);

	public String getString();

	public double getSentimentValue();

	public void setSentimentValue(double sentimentValue);

}
