/*
 * Licensed Materials - Property of IBM
 * 5737-I23 5900-AUD
 * Copyright IBM Corp. 2018 - 2026. All Rights Reserved.
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
*/

package com.ibm.di.samples;

import java.util.Random;

import javax.json.JsonObject;

public class InputData {
    private int creditScore;
    private int yearlyIncome;
    private String zipCode;
    private int amount;
    private int duration;

    private int initialCreditScore;
    private int initialYearlyIncome;
    private String initialZipCode;
    private int initialAmount;
    private int initialDuration;

    private int crediScoreIncrement = 50;
    private int yearlyIncomeIncrement = 1000;
    private int amountIncrement = 10000;
    private int durationIncrement = 6;
    private Random random;
    private String[] zips = {"06560", "83700", "75013", "94200", "45678", "69000", "13240", "0623"};
    private int maxRandom = 8;
 
    public InputData(JsonObject payload) {
        creditScore = payload.getInt("credit-score");
		yearlyIncome = payload.getInt("yearly-income");
        zipCode = payload.getString("zipCode");
        amount = payload.getInt("amount");
		duration = payload.getInt("duration");	
        initialCreditScore = creditScore;
		initialYearlyIncome = yearlyIncome;
        initialZipCode = zipCode;
        initialAmount = amount;
		initialDuration = duration;	
        random = new Random();				
    }

    private void initialize() {
        creditScore = initialCreditScore;
        yearlyIncome = initialYearlyIncome;
        amount = initialAmount;
        duration = initialDuration;
        zipCode = initialZipCode;
        }

    public void generateData(int i, int max) {
        if (i ==0 || max < 2) return;
        if (i == max - 1) {
           initialize();
           return; 
        } 
        int factor = random.nextInt(maxRandom);
        int sign = 1;
        if (i % 2 == 1) sign = -1;
        creditScore = initialCreditScore + (factor * sign * crediScoreIncrement);
        yearlyIncome = initialYearlyIncome + ( sign  * random.nextInt(maxRandom) * yearlyIncomeIncrement);
        amount = initialAmount + ( sign  * random.nextInt(maxRandom) * amountIncrement);
        duration = initialDuration + ( sign  * random.nextInt(maxRandom) * durationIncrement);
        changeZipCode(factor);
        checkData();
    }

    private void checkData() {
        if (creditScore < 50) creditScore = 50;
        if (amount < 10000) amount = 10000;
        if (duration < 12) duration = 12;
        if (yearlyIncome < 1000) yearlyIncome = 1000;
    }

    private void changeZipCode(int factor) {
        if (factor == 3) zipCode = initialZipCode;
        zipCode = zips[factor];
    }

    public int getCreditScore() {
        return creditScore;
    }

    public int getYearlyIncome() {
        return yearlyIncome;
    }

   public int getAmount() {
        return amount;
    }
    public String getZipCode() {
        return zipCode;
    }
   public int getDuration() {
        return duration;
    }


}
