package com.uottawa.eecs.project_project_group_24;

public class Queue<E> {
    Node<E> leader,tail;
    int size;
    public Queue()
    {}
    public Node<E> Enqueue(E el)
    {
        Node<E> tmp = new Node<>(el,null,null);
        if(leader==null)
        {
            leader = tmp;
        }
        else
        {
            tail.setNext(tmp);
            tail = tmp;
        }
            size++;
            return tmp;
    }

    public Node<E> Dequeue()
    {
        Node<E> tmp = leader;
        leader = leader.getNext();
        leader.setPrevious(null);
        return tmp;
    }

    public Node<E> First() {
        return leader;
    }

}
