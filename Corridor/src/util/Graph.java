/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 only, as published by
 * the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License version 2 for
 * more details (a copy is included in the LICENSE file that accompanied this
 * code).
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this work; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class Graph<E> {
    private Set<Node<E>> nodes;

    public Graph() {
        nodes = new HashSet<Node<E>>();
    }
    public void addNode(E element) {
        nodes.add(new Node<E>(element));
    }


    private Node<E> node(Node<E> target) {
        for (Node<E> n : nodes) {
            if (n.equals(target)) {
                return n;
            }
        }
        return null;
    }


    public boolean contains(E element) {
        return nodes.contains(new Node<E>(element));
    }


    public Set<E> elements() {
        Set<E> elements = new HashSet<E>();
        for (Node<E> node: nodes) {
            elements.add(node.element());
        }
        return elements;
    }


    public void addEdge(E origin, E apex) {
        addEdge(origin, apex, 1d);
    }


    public void removeEdge(E origin, E apex) {
        removeEdge(origin, apex, 1d);
    }


    public void addEdge(E origin, E apex, double weight) {
        Node<E> nodeA = new Node<E>(origin);
        Node<E> nodeB = new Node<E>(apex);

        node(nodeA).addEdge(node(nodeA), node(nodeB), weight);
        node(nodeB).addEdge(node(nodeB), node(nodeA), weight);
    }


    public void removeEdge(E origin, E apex, double weight) {
        Node<E> nodeA = new Node<E>(origin);
        Node<E> nodeB = new Node<E>(apex);

        node(nodeA).removeEdge(node(nodeA), node(nodeB), weight);
        node(nodeB).removeEdge(node(nodeB), node(nodeA), weight);
    }


    public boolean containsEdge(E element1, E element2) {
        if (!contains(element1) || !contains(element2)) {
            return false;
        }

        for (E element: neighbors(element1)) {
            if (element.equals(element2)) {
                return true;
            }
        }

        return false;
    }


    public Set<E> neighbors(E element) {
        Set<E> neighbors = new HashSet<E>();
        Node<E> node = node(new Node<E>(element));
        for (Edge<E> edge: node.edges()) {
            neighbors.add(edge.apex().element());
        }
        return neighbors;
    }


    public Graph<E> clone() {
        Graph<E> clone = new Graph<E>();
        for (Node<E> node: nodes) {
            clone.addNode(node.element());
        }
        for (Node<E> node: nodes) {
            for (Edge<E> edge: node.edges()) {
                clone.addEdge(edge.origin().element(),
                        edge.apex().element(),
                        edge.weight());
            }
        }
        return clone;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Graph) {
            Graph<E> graph = (Graph<E>)object;
            if (!graph.elements().equals(elements())) {
                return false;
            }
            else {
                for (E element: elements()) {
                    if (!graph.neighbors(element).equals(neighbors(element))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<E> findPath(String algorithm, E initial, Set<E> goals) {
        List<E> path             = new ArrayList<E>();
        Double  pathSize         = Double.POSITIVE_INFINITY;
        List<E> shortestPath     = path;
        Double  shortestPathSize = pathSize;

        for (E goal: goals) {
            path     = findPath(algorithm, initial, goal);
            pathSize = Double.valueOf(path.size());

            if (pathSize > 0d && pathSize < shortestPathSize) {
                shortestPath     = path;
                shortestPathSize = pathSize;
            }
        }

        return shortestPath;
    }


    public List<E> findPath(String algorithm, E initial, E goal) {
        List<E> path = new ArrayList<E>();

        if (initial.equals(goal)) {
            path.add(initial);
            return path;
        }

        Node<E> initialNode = node(new Node<E>(initial));
        Node<E> goalNode    = node(new Node<E>(goal));

        if (algorithm.equals("breadth-first") ||
                algorithm.equals("depth-first")) {
            return findPath(path, algorithm, initialNode, goalNode);
        }

        return findPath(path, initialNode, goalNode);
    }


    private List<E> findPath(List<E> path,
                             String  algorithm,
                             Node<E> initialNode,
                             Node<E> goalNode)
    {
        Frontier<SearchNode<Node<E>>> frontier;
        NavigableSet<SearchNode<Node<E>>> explored;
        SearchNode<Node<E>> node;
        SearchNode<Node<E>> child;
        Stack<E> stack;

        frontier = new QueueFrontier<SearchNode<Node<E>>>();
        explored = new TreeSet<SearchNode<Node<E>>>();
        stack    = new Stack<E>();

        if (algorithm.equals("depth-first")) {
            frontier = new StackFrontier<SearchNode<Node<E>>>();
        }

        frontier.add(new SearchNode<Node<E>>(initialNode, // state
                null));      // parent

        while (!frontier.isEmpty()) {
            node = frontier.remove();
            explored.add(node);
            for (Edge<E> edge : node.state().edges()) {
                child = new SearchNode<Node<E>>(edge.apex(), // state
                        node);       // parent
                if (!explored.contains(child) && !frontier.contains(child)) {
                    if (child.state().equals(goalNode)) {
                        stack.push(child.state().element());
                        while (true) {
                            stack.push(node.state().element());
                            if (node.parent() == null) {
                                break;
                            }
                            node = node.parent();
                        }
                        while (!stack.isEmpty()) {
                            path.add(stack.pop());
                        }
                        return path;
                    }
                    frontier.add(child);
                }
            }
        }

        return path;
    }


    private List<E> findPath(List<E> path,
                             Node<E> initialNode,
                             Node<E> goalNode)
    {
        Frontier<SearchNode<Node<E>>> frontier;
        NavigableSet<SearchNode<Node<E>>> explored;
        SearchNode<Node<E>> node;
        SearchNode<Node<E>> child;
        Stack<E> stack;
        double f; // the value of the evaluation function for node, f(node)
        double g; // the value of the  path cost function for node, g(node)
        double h; // the value of the  heuristic function for node, h(node)

        frontier = new PriorityQueueFrontier<SearchNode<Node<E>>>(
                11, new SearchNodeComparator());
        explored = new TreeSet<SearchNode<Node<E>>>();
        stack    = new Stack<E>();

        frontier.add(new SearchNode<Node<E>>(initialNode, // state
                null,        // parent
                0d));        // f(initialNode)

        while (!frontier.isEmpty()) {
            node = frontier.remove();
            if (node.state().element().equals(goalNode)) {
                while (true) {
                    stack.push(node.state().element());
                    if (node.parent() == null) {
                        break;
                    }
                    node = node.parent();
                }
                while (!stack.isEmpty()) {
                    path.add(stack.pop());
                }
                break;
            }
            explored.add(node);
            for (Edge<E> edge : node.state().edges()) {
                g = node.value() + edge.weight();
                h = 0;
                f = g + h;
                child = new SearchNode<Node<E>>(edge.apex(), // state
                        node,        // parent
                        f);          // f(node)
                if (!explored.contains(child) && !frontier.contains(child)) {
                    frontier.add(child);
                }
            }
        }

        return path;
    }
}

class SearchNode<T> implements Comparable<SearchNode<T>> {
    private T state;
    private SearchNode<T> parent;
    private double value;
    public SearchNode(T state, SearchNode<T> parent) {
        this.state = state;
        this.parent = parent;
    }
    public SearchNode(T state, SearchNode<T> parent, double value) {
        this.state = state;
        this.parent = parent;
        this.value = value;
    }
    public T state() {
        return state;
    }
    public SearchNode<T> parent() {
        return parent;
    }
    public double value() {
        return value;
    }

    public int compareTo(SearchNode<T> anotherSearchNode) {
        if (this == anotherSearchNode) {
            return 0;
        }
        return state.toString().compareTo(
                anotherSearchNode.state().toString());
    }


    public boolean equals(Object anotherObject) {
        if (this == anotherObject) {
            return true;
        }
        if (anotherObject instanceof SearchNode) {
            SearchNode<T> anotherSearchNode = (SearchNode<T>)anotherObject;
            return (state.equals((anotherSearchNode).state()));
        }
        return false;
    }


    public int hashCode() {
        return state.hashCode();
    }
}


class SearchNodeComparator implements Comparator {


    public SearchNodeComparator() {

    }

    public int compare(Object o1, Object o2) {
        if (o1 == o2) {
            return 0;
        }
        SearchNode n1 = (SearchNode)o1;
        SearchNode n2 = (SearchNode)o2;
        return (int)(n1.value() - n2.value());
    }
}


class Node<E> {

    private E element;

    private Set<Edge<E>> edges;


    public Node() {
        this.element = null;
        edges = new HashSet<Edge<E>>();
    }


    public Node(E element) {
        this.element = element;
        edges = new HashSet<Edge<E>>();
    }


    public E element() {
        return element;
    }


    public Set<Edge<E>> edges() {
        return edges;
    }

    public void addEdge(Node<E> origin, Node<E> apex, double weight) {
        edges.add(new Edge<E>(origin, apex, weight));
    }


    public void removeEdge(Node<E> origin, Node<E> apex, double weight) {
        edges.remove(new Edge<E>(origin, apex, weight));
    }

    // Compares this node to the specified object.  Returns {@code true} if the

    public boolean equals(Object anotherObject) {
        if (this == anotherObject) {
            return true;
        }
        if (anotherObject instanceof Node) {
            Node<E> anotherNode = (Node<E>)anotherObject;
            return element.equals(anotherNode.element());
        }
        return false;
    }


    public int hashCode() {
        return element.hashCode();
    }
}


class Edge<E> {

    private Node<E> origin;


    private Node<E> apex;


    private double weight;


    public Edge(Node<E> origin, Node<E> apex) {
        this.origin = origin;
        this.apex = apex;
        this.weight = 1d;
    }


    public Edge(Node<E> origin, Node<E> apex, double weight) {
        this.origin = origin;
        this.apex = apex;
        this.weight = weight;
    }


    public Node<E> origin() {
        return origin;
    }


    public Node<E> apex() {
        return apex;
    }


    public double weight() {
        return weight;
    }

    public boolean equals(Object anotherObject) {
        if (this == anotherObject) {
            return true;
        }
        if (anotherObject instanceof Edge) {
            Edge<E> anotherEdge = (Edge<E>)anotherObject;
            if (origin.equals((anotherEdge).origin()) &&
                    apex.equals((anotherEdge).apex()))
            {
                return true;
            }
        }
        return false;
    }


    public int hashCode() {
        int hash = 1;
        hash = hash * 11 + origin.hashCode();
        hash = hash * 11 + apex.hashCode();
        return hash;
    }
}


interface Frontier<E> {


    public void add(E element);


    public E remove();


    public boolean contains(E element);

    public boolean isEmpty();
}


class QueueFrontier<E> implements Frontier<E> {


    private Queue<E> queue;
    public QueueFrontier() {
        queue = new LinkedList<E>();
    }

    public void add(E element) {
        queue.add(element);
    }

    public E remove() {
        return queue.remove();
    }

    public boolean contains(E element) {
        return queue.contains(element);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
class StackFrontier<E> implements Frontier<E> {

    private Stack<E> queue;
    public StackFrontier() {
        queue = new Stack<E>();
    }
    public void add(E element) {
        queue.push(element);
    }
    public E remove() {
        return queue.pop();
    }

    public boolean contains(E element) {
        return queue.contains(element);
    }

    public boolean isEmpty() {
        return queue.empty();
    }
}
class PriorityQueueFrontier<E> implements Frontier<E> {

    private PriorityQueue<E> queue;

    public PriorityQueueFrontier() {
        queue = new PriorityQueue<E>();
    }

    public PriorityQueueFrontier(int initialCapacity,
                                 Comparator<E> comparator) {
        queue = new PriorityQueue<E>(initialCapacity, comparator);
    }
    public void add(E element) {
        queue.add(element);
    }

    public E remove() {
        return queue.poll();
    }

    public boolean contains(E element) {
        return queue.contains(element);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
