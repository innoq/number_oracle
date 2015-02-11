package com.innoq.numbergame.baseapi;

/**
   A plain vanilla class of immutable objects for oracle results.
 */
public final class OracleResult implements Comparable<OracleResult> {

    final private int fullMatchCount;
    final private int partialMatchCount;

    public OracleResult(int f, int p) {
        fullMatchCount = f;
        partialMatchCount = p;
    }

    public int getFullMatchCount() {return fullMatchCount;}

    public int getPartialMatchCount() {return partialMatchCount;}

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o != null && o.getClass().equals(OracleResult.class)) {
            OracleResult r = (OracleResult) o;
            return r.fullMatchCount == fullMatchCount &&
                r.partialMatchCount == partialMatchCount;
        } else {
            return false;
        }
    }

    public int compareTo(OracleResult o) {
        int result = o.fullMatchCount - fullMatchCount;
        if (0 == result) {
            result = o.partialMatchCount - partialMatchCount;
        }
        return result;
    }
}
