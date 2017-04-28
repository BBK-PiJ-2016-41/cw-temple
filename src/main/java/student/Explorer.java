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
    Stack<Long> stack = new Stack<Long>();
    Long current = state.getCurrentLocation();
    stack.add(current);
    Stack<Long> visited = new Stack<Long>();
    Stack<Long> path = new Stack<Long>();
    path.add(current);
    visited.add(current);
    while (!stack.isEmpty() && state.getDistanceToTarget() != 0) {
      int distance = state.getDistanceToTarget();
      Collection<NodeStatus> neighbours = state.getNeighbours();
      Stream<NodeStatus> distanceStream = neighbours.stream().filter(n -> (!visited.contains(n.getId()) && (n.getDistanceToTarget() < distance)));
      if (distanceStream.count() > 0) {
        neighbours.stream().filter(n -> ((!visited.contains(n.getId())) && (n.getDistanceToTarget() < distance))).forEach(n -> stack.push(n.getId()));
        state.moveTo(stack.pop());
        visited.add(state.getCurrentLocation());
        path.add(state.getCurrentLocation());
      } else {
      Stream<NodeStatus> neighbourStream = neighbours.stream().filter(n -> (!visited.contains(n.getId())));
        if (neighbourStream.count() > 0) {
          neighbours.stream().filter((n) -> (!visited.contains(n.getId()))).forEach((n) -> stack.push(n.getId()));
          state.moveTo(stack.pop());
          visited.add(state.getCurrentLocation());
          path.add(state.getCurrentLocation());
        } else {
          if (path.peek() == state.getCurrentLocation()) {
            path.pop();
          }
          state.moveTo(path.peek());
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
    //add the distances
    Stack<Node> exitPath = dijkstra(nodes, start, exit);
    Set<Node> checkedGold = new HashSet<Node>();
    getGold(state);
    checkedGold.add(start);
    int totalCost = 4;
    int moves = 1;
    //move the explorer to the exit, picking up gold along the way
    while (exitPath.size() < 0.5 * state.getTimeRemaining()/(Math.floor(totalCost/moves)) && exitPath.size() > 10) {
      Set<Node> neighbours = state.getCurrentNode().getNeighbours();
      Optional<Node> first = neighbours.stream().filter(n -> !checkedGold.contains(n)).findAny();
      if (first.isPresent()) {
        int preTime = state.getTimeRemaining();
        Node moveTo = first.get();
        state.moveTo(moveTo);
        int postTime = state.getTimeRemaining();
        totalCost += (preTime - postTime);
        moves++;
        getGold(state);
        checkedGold.add(moveTo);
      } else {
        break;
      }
      exitPath = dijkstra(nodes, state.getCurrentNode(), exit);
    }
    this.traverse(exitPath, state);
    return;
  }

  private Stack<Node> dijkstra(Collection<Node> nodes, Node start, Node exit) {
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
        unvisited.remove(next);
        visited.add(next);
        current = next;
      }
    }
    Stack<Node> path = new Stack<Node>();
    Node pointer = exit;
    path.push(exit);
    while (pointer != start) {
      Node prev = predecessors.get(pointer);
      path.push(prev);
      pointer = prev;
    }
    return path;
  }

  private void traverse(Stack<Node> path, EscapeState state) {
    path.pop();
    while (!path.isEmpty()) {
      getGold(state);
      state.moveTo(path.pop());
    }
  }

  private void getGold(EscapeState state) {
    try {
      state.pickUpGold();
    } catch (Exception e) {
      //System.out.println("There is no gold here. Carry on.");
    }
  }
}
