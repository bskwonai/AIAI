package com.example.aiai.service;

import java.util.List;

public final class VectorMath {

    private VectorMath() {
    }

    public static double cosineSimilarity(List<Double> a, List<Double> b) {
        if (a.size() != b.size()) {
            return -1;
        }
        double dot = 0d;
        double na = 0d;
        double nb = 0d;
        for (int i = 0; i < a.size(); i++) {
            double av = a.get(i);
            double bv = b.get(i);
            dot += av * bv;
            na += av * av;
            nb += bv * bv;
        }
        if (na == 0 || nb == 0) {
            return -1;
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
