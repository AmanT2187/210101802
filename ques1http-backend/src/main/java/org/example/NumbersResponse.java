package org.example;

import java.util.Set;

public class NumbersResponse {
    private Set<Integer> numbers;

    public NumbersResponse() {
    }

    public NumbersResponse(Set<Integer> numbers) {
        this.numbers = numbers;
    }

    public Set<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(Set<Integer> numbers) {
        this.numbers = numbers;
    }
}
