package tipbox.com.tipbox;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created by dakaugu on 2/1/16.
 */

public class Result {

    public BigDecimal amountDue; //a
    public BigDecimal tip; //b
    public BigDecimal tipTotal; //c
    public BigDecimal amountTotal; //d
    public BigDecimal persons; //e
    public BigDecimal amountPerPerson; //f
    public BigDecimal tipPercent;

    public Result() {
        amountDue = new BigDecimal("0");
        tip = new BigDecimal("0.15");
        tipTotal = new BigDecimal("0");
        amountTotal = new BigDecimal("0");
        persons = new BigDecimal("1");
        amountPerPerson = new BigDecimal("0");
        tipPercent = new BigDecimal("15");
    }

    public void editAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
        calculateTotals();
    }

    public void editTipPercent(BigDecimal tip){
        this.tipPercent = tip;
        this.tip = tip.divide(BigDecimal.valueOf(100));
        calculateTotals();
    }

    public void editSplit(BigDecimal split) {
        this.persons = split;
        calculateAmountPerPerson();
    }

    public void editTiptotal(BigDecimal tipTotal) {
        this.tipTotal = tipTotal;
        calculateTipPercent();
        calculateTotals();
    }

    public void editTotal(BigDecimal amountTotal) {
        this.amountTotal = amountTotal;
        tipTotal = amountTotal.subtract(amountDue).setScale(2, RoundingMode.CEILING);
        calculateTipPercent();
        calculateTotals();
    }

    public void editPerPerson(BigDecimal amountPerPerson) {
        this.amountPerPerson = amountPerPerson;
        amountTotal = amountPerPerson.multiply(persons).setScale(2, RoundingMode.CEILING);
        editTotal(amountTotal);
    }

    private void calculateTotals(){
        tipTotal = amountDue.multiply(tip).setScale(2, RoundingMode.CEILING);
        amountTotal = amountDue.add(tipTotal).setScale(2, RoundingMode.CEILING);
        calculateAmountPerPerson();
    }

    private void calculateAmountPerPerson(){
        amountPerPerson = amountTotal.divide(persons, MathContext.DECIMAL32)
                .setScale(2, RoundingMode.CEILING); //hacky, idk
    }

    private void calculateTipPercent() {
        if(!String.valueOf(amountDue).equals("0")) {  //may not be the best way
            tip = tipTotal.divide(amountDue, MathContext.DECIMAL32)
                    .setScale(2, RoundingMode.CEILING);
            tipPercent = BigDecimal.valueOf(tip.multiply(BigDecimal.valueOf(100)).intValueExact());
        }
    }

}

