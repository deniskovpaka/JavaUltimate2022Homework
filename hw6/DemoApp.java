package com.bobocode;

import lombok.SneakyThrows;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.*;

public class DemoApp {
    public static void main(String[] args) {
//        var head = createLinkedList(4, 3, 9, 1);
//        printReversedRecursively(head);
//        printReversedUsingStack(head);

        List<Integer> arr = Arrays.asList(1, 5, 6, 2, 3, 9);
        DemoApp.sortList(arr);
        System.out.println(arr);
//        GreetingService helloService = createMethodLoggingProxy(GreetingService.class);
//        helloService.hello(); // logs nothing to the console
//        helloService.gloryToUkraine(); // logs method invocation to the console
    }

    /**
     * Creates a list of linked {@link Node} objects based on the given array of elements and returns a head of the list.
     *
     * @param elements an array of elements that should be added to the list
     * @param <T>      elements type
     * @return head of the list
     */
    public static <T> Node<T> createLinkedList(T... elements) {
        Objects.requireNonNull(elements);
        Node<T> head = new Node<>(elements[0]);
        Node<T> current = head;
        for (int i = 1; i < elements.length; ++i) {
            Node<T> newNode = new Node<>(elements[i]);
            current.next = newNode;
            current = newNode;
        }
        return head;
    }

    /**
     * Prints a list in a reserved order using a recursion technique. Please note that it should not change the list,
     * just print its elements.
     * <p>
     * Imagine you have a list of elements 4,3,9,1 and the current head is 4. Then the outcome should be the following:
     * 1 -> 9 -> 3 -> 4
     *
     * @param head the first node of the list
     * @param <T>  elements type
     */
    public static <T> void printReversedRecursively(Node<T> head) {
        Objects.requireNonNull(head);
        printReversed(head.next);
        System.out.println(head.element);
    }

    private static <T> void printReversed(Node<T> node) {
        if (node != null) {
            printReversed(node.next);
            System.out.print(node.element + " -> ");
        }
    }

    /**
     * Prints a list in a reserved order using a {@link java.util.Stack} instance. Please note that it should not change
     * the list, just print its elements.
     * <p>
     * Imagine you have a list of elements 4,3,9,1 and the current head is 4. Then the outcome should be the following:
     * 1 -> 9 -> 3 -> 4
     *
     * @param head the first node of the list
     * @param <T>  elements type
     */
    public static <T> void printReversedUsingStack(Node<T> head) {
        Objects.requireNonNull(head);
        var stack = new Stack<T>();
        while (head != null) {
            stack.push(head.element);
            head = head.next;
        }
        while (stack.size() > 1) {
            System.out.print(stack.pop() + " -> ");
        }
        System.out.println(stack.pop());
    }

    public static <T extends Comparable<T>> void sortList(List<T> list) {
        if (list.size() < 2) {
            return;
        }
        int mid = list.size() / 2;
        List<T> left = new ArrayList<>(list.subList(0, mid));
        sortList(left);
        List<T> right = new ArrayList<>(list.subList(mid, list.size()));
        sortList(right);

        merge(list, left, right);
    }

    private static <T extends Comparable<? super T>> void merge(List<T> list, List<T> left, List<T> right) {
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
    }

    /**
     * Creates a proxy of the provided class that logs its method invocations. If a method that
     * is marked with {@link LogInvocation} annotation is invoked, it prints to the console the following statement:
     * "[PROXY: Calling method '%s' of the class '%s']%n", where the params are method and class names accordingly.
     *
     * @param targetClass a class that is extended with proxy
     * @param <T>         target class type parameter
     * @return an instance of a proxy class
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T createMethodLoggingProxy(Class<T> targetClass) {
        Enhancer e = new Enhancer();
        e.setSuperclass(targetClass);
        e.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
                if (method.isAnnotationPresent(LogInvocation.class)) {
                    System.out.printf("[PROXY: Calling method '%s' of the class '%s']%n", method.getName(), targetClass.getTypeName());
                }
                return proxy.invokeSuper(obj, args);
            });
        return (T) e.create();
    }
}

class GreetingService {
    public void hello() {
        System.out.println("hello");
    }
    @LogInvocation
    public void gloryToUkraine() {
        System.out.println("Glory to Ukraine");
    }
}