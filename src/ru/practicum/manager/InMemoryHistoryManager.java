package ru.practicum.manager;

import ru.practicum.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        public Task task;
        public Node prev;
        public Node next;

        public Node(Task task) {
            this.task = task;
        }
    }

    private Node head;
    private Node tail;

    private final Map<Integer, Node> historyMap = new HashMap<>();

    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        linkLast(task);

        if (historyMap.size() > MAX_HISTORY_SIZE) {
            if (head != null) {
                removeNode(head);
            }
        }
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);

        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    private void removeNode(Node node) {

        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }

        historyMap.remove(node.task.getId());
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node current = head;

        while (current != null) {
            historyList.add(current.task);
            current = current.next;
        }

        return historyList;
    }
}
