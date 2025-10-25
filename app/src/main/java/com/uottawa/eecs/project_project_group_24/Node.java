package com.uottawa.eecs.project_project_group_24;

public class Node<E> {
    Node<E> next,previous;
    E element;
    public Node(E el, Node<E> p, Node<E> n)
    {
        element = el;
        next = n;
        previous = p;
    }

    public Node<E> getNext() {
        return next;
    }

    public Node<E> getPrevious() {
        return previous;
    }

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
        this.element = element;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }

    public void setPrevious(Node<E> previous) {
        this.previous = previous;
    }
}
