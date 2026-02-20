package com.example.aiai;

import com.example.aiai.service.VectorMath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VectorMathTest {

    @Test
    void cosineSimilarityShouldBeHighForCloseVectors() {
        double sim = VectorMath.cosineSimilarity(List.of(1.0, 0.0, 1.0), List.of(0.9, 0.1, 0.95));
        assertTrue(sim > 0.95);
    }
}
