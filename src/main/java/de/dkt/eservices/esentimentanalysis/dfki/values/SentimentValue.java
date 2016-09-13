package de.dkt.eservices.esentimentanalysis.dfki.values;

public enum SentimentValue {
    NONE(Double.MIN_VALUE),
    EXTREMELYNEGATIV(-10),
    VERYNEGATIV(-5),
    NEGATIV(-1),
    NEUTRUM(0),
    POSITIVE(1),
    VERYPOSITIVE (5),
    EXTREMELYPOSITIVE (10);

    private final double numericValue;   // in kilograms
    SentimentValue(double numericValue) {
        this.numericValue = numericValue;
    }
    public double numericValue() { return numericValue; }

    public static SentimentValue fromValue(String s) {
    	if(s.equalsIgnoreCase("none") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase("-INF")){
    		return SentimentValue.NONE;
    	}
    	else if(s.equalsIgnoreCase("extremelybad") || s.equalsIgnoreCase("extremelynegative") || s.equalsIgnoreCase("eb") || s.equalsIgnoreCase("-10")){
    		return SentimentValue.EXTREMELYNEGATIV;
    	}
    	else if(s.equalsIgnoreCase("verybad") || s.equalsIgnoreCase("verynegative") || s.equalsIgnoreCase("vb") || s.equalsIgnoreCase("-5")){
    		return SentimentValue.VERYNEGATIV;
    	}
    	else if(s.equalsIgnoreCase("bad") || s.equalsIgnoreCase("negative") || s.equalsIgnoreCase("b") || s.equalsIgnoreCase("-1")){
    		return SentimentValue.NEGATIV;
    	}
    	else if(s.equalsIgnoreCase("neutrum") || s.equalsIgnoreCase("neutral") ||s.equalsIgnoreCase("n") || s.equalsIgnoreCase("0")){
    		return SentimentValue.NEUTRUM;
    	}
    	else if(s.equalsIgnoreCase("good") || s.equalsIgnoreCase("positive") || s.equalsIgnoreCase("g") || s.equalsIgnoreCase("1")){
    		return SentimentValue.POSITIVE;
    	}
    	else if(s.equalsIgnoreCase("verygood") || s.equalsIgnoreCase("verypositive") || s.equalsIgnoreCase("vg") || s.equalsIgnoreCase("5")){
    		return SentimentValue.VERYPOSITIVE;
    	}
    	else if(s.equalsIgnoreCase("extremelygood") || s.equalsIgnoreCase("extremelypositive") || s.equalsIgnoreCase("eg") || s.equalsIgnoreCase("10")){
    		return SentimentValue.EXTREMELYPOSITIVE;
    	}
    	throw new IllegalArgumentException("Relevance Value not valid!!!");
    }

}
