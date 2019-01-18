// FederalMoneyRules.aidl
package com.chandra.piyush.project3c.android.common;

// Declare any non-default types here with import statements

interface FederalMoneyRules {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    List monthlyAvgCash(int aYear);
    List dailyCash(int aYear, int aMonth, int aDay, int aNumber);
}
