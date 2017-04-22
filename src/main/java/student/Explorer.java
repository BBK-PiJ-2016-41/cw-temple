package student;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;
import game.Node;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.*;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Set;
import java.util.Optional;


public class Explorer {

  /**
   * Explore the cavern, trying to find the orb in as few steps as possible.
   * Once you find the orb, you must return from the function in order to pick
   * it up. If you continue to move after finding the orb rather
   * than returning, it will not count.
   * If you return from this function while not standing on top of the orb,
   * it will count as a failure.
   *
   * <p>There is no limit to how many steps you can take, but you will receive
   * a score bonus multiplier for finding the orb in fewer steps.</p>
   *
   * <p>At every step, you only know your current tile's ID and the ID of all
   * open neighbor tiles, as well as the distance to the orb at each of these tiles
   * (ignoring walls and obstacles).</p>
   *
   * <p>To get information about the current state, use functions
   * getCurrentLocation(),
   * getNeighbours(), and
   * getDistanceToTarget()
   * in ExplorationState.
   * You know you are standing on the orb when getDistanceToTarget() is 0.</p>
   *
   * <p>Use function moveTo(long id) in ExplorationState to move to a neighboring
   * tile by its ID. Doing this will change state to reflect your new position.</p>
   *
   * <p>A suggested first implementation that will always find the orb, but likely won't
   * receive a large bonus multiplier, is a depth-first search.</p>
   *
   * @param state the information available at the current state
   */
  public void explore(ExplorationState state) {
    //System.out.println("Current Location: " + state.getCurrentLocation());
    //System.out.println("Distance to Orb: " + state.getDistanceToTarget());
    Stack<Long> visited = new Stack<Long>();
    Stack<Long> eliminated = new Stack<Long>();
    Stack<Long> next = new Stack<Long>();
    visited.push(state.getCurrentLocation());
    while (state.getDistanceToTarget() > 0) {
      //visited.push(state.getCurrentLocation());
      boolean moved = false;
      if (!next.isEmpty()) {
        while (!moved && !next.isEmpty()) {
          try {
            state.moveTo(next.pop());
            moved = true;
          } catch (IllegalArgumentException e) {
            System.out.println("That square was not a neighbour. Trying again...");
            continue;
          } catch (EmptyStackException e) {
            System.out.println("The stack of potential squares is empty.");
            continue;
          }
        }
      }
      if (state.getDistanceToTarget() == 0) {
        break;
      }
      long currentLocation = state.getCurrentLocation();
      int distance = state.getDistanceToTarget();
      if (moved) {
        visited.push(currentLocation);
      }
      Collection<NodeStatus> neighbours = state.getNeighbours();
      Stream<NodeStatus> neighbourStream = neighbours.stream().filter((n) -> (n.getDistanceToTarget() <= distance && (!(visited.contains(n.getId()))) && (!(eliminated.contains(n.getId())))));
      if (neighbourStream.count() > 0) {
        neighbours.stream().filter((n) -> (n.getDistanceToTarget() <= distance && (!(visited.contains(n.getId()))) && (!(eliminated.contains(n.getId()))))).forEach((n) -> next.push(n.getId()));
      } else {
        Stream<NodeStatus> nonDistanceStream = neighbours.stream().filter((n) -> (!(visited.contains(n.getId())) && !(eliminated.contains(n.getId()))));
        if (nonDistanceStream.count() > 0) {
          neighbours.stream().filter((n) -> (!visited.contains(n.getId()) && !eliminated.contains(n.getId()))).forEach((n) -> next.push(n.getId()));
        } else {
          eliminated.push(currentLocation);
          next.clear();
          if (moved) {
            visited.pop();
          }
          try {
            state.moveTo(visited.pop());
          } catch (IllegalArgumentException e) {
            //try more than once?
            System.out.println("That square was not a neighbour.");
          } catch (EmptyStackException e) {
            System.out.println("Let's start again from here.");
            eliminated.clear();
          }
        }
      }
    }
    return;
  }

  /**
   * Escape from the cavern before the ceiling collapses, trying to collect as much
   * gold as possible along the way. Your solution must ALWAYS escape before time runs
   * out, and this should be prioritized above collecting gold.
   *
   * <p>You now have access to the entire underlying graph, which can be accessed
   * through EscapeState.
   * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
   * will return a collection of all nodes on the graph.</p>
   *
   * <p>Note that time is measured entirely in the number of steps taken, and for each step
   * the time remaining is decremented by the weight of the edge taken. You can use
   * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
   * on your current tile (this will fail if no such gold exists), and moveTo() to move
   * to a destination node adjacent to your current node.</p>
   *
   * <p>You must return from this function while standing at the exit. Failing to do so before time
   * runs out or returning from the wrong location will be considered a failed run.</p>
   *
   * <p>You will always have enough time to escape using the shortest path from the starting
   * position to the exit, although this will not collect much gold.</p>
   *
   * @param state the information available at the current state
   */
  public void escape(EscapeState state) {
    Node start = state.getCurrentNode();
    Node exit = state.getExit();
    Collection<Node> nodes = state.getVertices();
    System.out.println("Vertices: " + nodes.size());
    //add the distances
    Map<Node, Integer> distances = new HashMap<Node, Integer>();
    nodes.stream().filter(n -> n != start).forEach(n -> distances.put(n, 10000000));
    distances.put(start, 0);
    //create the visited set
    Set<Node> visited = new HashSet<Node>();
    visited.add(start);
    //create the unvisited set
    Set<Node> unvisited = new HashSet<Node>();
    nodes.stream().filter(n -> n != start).forEach(n -> unvisited.add(n));
    //create a map for predecessors
    Map<Node, Node> predecessors = new HashMap<Node, Node>();
    //Create a pointer node
    Node current = start;
    while (!visited.contains(exit)) {
      Set<Node> neighbours = current.getNeighbours();
      int distFromCurrent = distances.get(current);
      final Node copy = current;
      neighbours.stream().filter(n -> !visited.contains(n) && (distFromCurrent + 1 < distances.get(n))).forEach(n -> {
        distances.replace(n, distances.get(n), distFromCurrent + 1);
        if (predecessors.containsKey(n)) {
          predecessors.replace(n, predecessors.get(n), copy);
        } else {
          predecessors.put(n, copy);
        }
      });
      Optional<Entry<Node, Integer>> optionalNode = distances.entrySet().stream().filter(n -> unvisited.contains(n.getKey())).min(Comparator.comparingInt(Entry::getValue));
      if (optionalNode.isPresent()) {
        Entry<Node, Integer> entry = optionalNode.get();
        Node next = entry.getKey();
        System.out.println(next.getId());
        unvisited.remove(next);
        visited.add(next);
        current = next;
      }
    }
    Stack<Node> path = new Stack<Node>();
    Node pointer = exit;
    path.push(exit);
    while (pointer != start) {
      System.out.println(path.size());
      Node prev = predecessors.get(pointer);
      path.push(prev);
      pointer = prev;
    }
    //move the explorer to the exit, picking up gold along the way
    path.pop();
    while (!path.isEmpty()) {
      try {
        state.pickUpGold();
      } catch (Exception e) {
        System.out.println("There is no gold here. Carry on.");
      }
      state.moveTo(path.pop());
    }
    return;
  }

}
