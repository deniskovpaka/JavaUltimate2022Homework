package com.bobocode;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class DemoApp {
    public static void main(String[] args) {
        List<Integer> arr = Arrays.asList(1, 5, 6, 2, 3, 9);
        List<Integer> res = new RecursiveMergeSort<>(arr).compute();
        System.out.println(res);
    }
}

class RecursiveMergeSort<T extends Comparable<? super T>> extends RecursiveTask<List<T>> {
    private final List<T> list;

    public RecursiveMergeSort(@NonNull List<T> list) {
        this.list = list;
    }

    @Override
    protected List<T> compute() {
        if (list.size() < 2) {
            return list;
        }
        int mid = list.size() / 2;
        List<T> left = new ArrayList<>(list.subList(0, mid));
        List<T> right = new ArrayList<>(list.subList(mid, list.size()));
        RecursiveMergeSort<T> recursiveMergeSortLeft = new RecursiveMergeSort<>(left);
        RecursiveMergeSort<T> recursiveMergeSortRight = new RecursiveMergeSort<>(right);

        recursiveMergeSortRight.fork();
        return merge(recursiveMergeSortLeft.compute(), recursiveMergeSortRight.join());
    }

    private List<T> merge(List<T> left, List<T> right) {
        int leftArrSize = left.size();
        int rightArrSize = right.size();
        int leftIndex = 0;
        int rightIndex = 0;
        int listIndex = 0;
        while (leftIndex < leftArrSize && rightIndex < rightArrSize) {
            if (left.get(leftIndex).compareTo(right.get(rightIndex)) <= 0) {
                list.set(listIndex++, left.get(leftIndex++));
            } else {
                list.set(listIndex++, right.get(rightIndex++));
            }
        }

        while (leftIndex < leftArrSize) {
            list.set(listIndex++, left.get(leftIndex++));
        }
        while (rightIndex < rightArrSize) {
            list.set(listIndex++, right.get(rightIndex++));
        }
        return list;
    }
}