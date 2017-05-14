package org.technocracy.app.kleos.api;


public class Question {
    private String questionPart1;
    private String questionPart2;
    private double ratio1;
    private double ratio2;
    private double ratio3;

    public Question(String questionPart1, String questionPart2, double ratio1, double ratio2, double ratio3){
        this.questionPart1 = questionPart1;
        this.questionPart2 = questionPart2;
        this.ratio1 = ratio1;
        this.ratio2 = ratio2;
        this.ratio3 = ratio3;
    }

    public String getQuestionPart1(){
        return questionPart1;
    }

    public void setQuestionPart1(String questionPart1){
        this.questionPart1 = questionPart1;
    }

    public String getQuestionPart2(){
        return questionPart2;
    }

    public void setQuestionPart2(String questionPart2){
        this.questionPart2 = questionPart2;
    }

    public double getRatio1(){
        return ratio1;
    }

    public void setRatio1(double ratio1){
        this.ratio1 = ratio1;
    }

    public double getRatio2(){
        return ratio2;
    }

    public void setRatio2(double ratio2){
        this.ratio2 = ratio2;
    }

    public double getRatio3(){
        return ratio3;
    }

    public void setRatio3(double ratio3){
        this.ratio3 = ratio3;
    }
}
