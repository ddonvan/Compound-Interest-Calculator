public class CompoundInterest {
    private Integer year;
    private double openingBalance;
    private double interestRate;
    private double interest;
    private Integer frequency;
    private double closingBalance;

    public CompoundInterest (Integer year, double openingBalance, double interestRate, Integer frequency){
        this.year = year;
        this.openingBalance = openingBalance;
        this.interestRate = interestRate;
        this.interest = (openingBalance * Math.pow((1 + (interestRate/frequency)), (frequency)) - openingBalance);
        this.frequency = frequency;
        this.closingBalance = openingBalance + interest;
    }


    public Integer getYear() {
        return year;
    }

    public double getOpeningBalance() {return openingBalance;}

    public double getInterestRate() {
        return interestRate;
    }

    public double getInterest() {
        return interest;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public double getClosingBalance() {
        return closingBalance;
    }

}
