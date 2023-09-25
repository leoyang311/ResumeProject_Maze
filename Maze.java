import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import tester.Tester;

import java.awt.*;
import java.util.*;

//represents the class of Node
class Node {
  int row;
  int col;
  Node parent;
  int rank;
  boolean leftWall;
  boolean rightWall;
  boolean topWall;
  boolean bottomWall;
  Node left;
  Node right;
  Node top;
  Node bottom;
  Color color;

  // constructor of Node
  Node(int row, int col) {
    this.row = row;
    this.col = col;
    this.parent = this;
    this.rank = 0;
    this.color = Color.white;
    this.leftWall = true;
    this.rightWall = true;
    this.topWall = true;
    this.bottomWall = true;
  }

  // removes the top black lines above the node by giving the scene
  void removeTop(WorldScene scene) {
    int deltaX = Maze.CELL_LEN * col;
    int deltaY = Maze.CELL_LEN * row;
    scene.placeImageXY(new LineImage(new Posn(Maze.CELL_LEN, 0),
        Color.white).movePinhole(-Maze.CELL_LEN / 2 - deltaX, 0 - deltaY), 0, 0);
  }

  // removes the bottom black lines above the node by giving the scene
  void removeBottom(WorldScene scene) {
    int deltaX = Maze.CELL_LEN * col;
    int deltaY = Maze.CELL_LEN * row;
    scene.placeImageXY(new LineImage(new Posn(Maze.CELL_LEN, 0),
        Color.white).movePinhole(-Maze.CELL_LEN / 2 - deltaX, -Maze.CELL_LEN - deltaY), 0, 0);
  }

  // removes the right black lines above the node by giving the scene
  void removeRight(WorldScene scene) {
    int deltaX = Maze.CELL_LEN * col;
    int deltaY = Maze.CELL_LEN * row;
    scene.placeImageXY(new LineImage(new Posn(0, Maze.CELL_LEN),
        Color.white).movePinhole(-Maze.CELL_LEN - deltaX, -Maze.CELL_LEN / 2 - deltaY), 0, 0);
  }

  // removes the left black lines above the node by giving the scene
  void removeLeft(WorldScene scene) {
    int deltaX = Maze.CELL_LEN * col;
    int deltaY = Maze.CELL_LEN * row;
    scene.placeImageXY(new LineImage(new Posn(0, Maze.CELL_LEN),
        Color.white).movePinhole(0 - deltaX, -Maze.CELL_LEN / 2 - deltaY), 0, 0);
  }

  // finds the adjacentNodes for nodes in the arraylist of arraylist of nodes
  void findAdjacentNodes(ArrayList<ArrayList<Node>> nodes) {
    if (this.row > 0) {
      this.top = nodes.get(this.row - 1).get(this.col);
    }
    if (this.row < nodes.size() - 1) {
      this.bottom = nodes.get(this.row + 1).get(this.col);
    }
    if (this.col > 0) {
      this.left = nodes.get(this.row).get(this.col - 1);
    }
    if (this.col < nodes.get(0).size() - 1) {
      this.right = nodes.get(this.row).get(this.col + 1);
    }
  }

  // the method to find the most parent of one Node
  Node find() {
    if (this.parent == this) {
      return this;
    }
    return this.parent.find();
  }

  // the method to union the Node with another according to their rank
  void union(Node other) {
    Node thisRoot = this.find();
    Node otherRoot = other.find();
    if (thisRoot != otherRoot) {
      if (thisRoot.rank > otherRoot.rank) {
        otherRoot.parent = thisRoot;
      } else {
        thisRoot.parent = otherRoot;
        if (thisRoot.rank == otherRoot.rank) {
          otherRoot.rank++;
        }
      }
    }
  }

  // overrides the toString method
  @Override
  public String toString() {
    return "(" + this.row + ", " + this.col + ")";
  }

  // method to draw the node
  public WorldImage nodeImage() {
    return new RectangleImage(Maze.CELL_LEN - 2, Maze.CELL_LEN - 2, "solid", this.color)
        .movePinhole(-Maze.CELL_LEN / 2, -Maze.CELL_LEN / 2);
  }
}

// represents the class of Player
class Player {
  static final Color PLAYER_COLOR = new Color(61, 118, 204);
  static final Color PATH_COLOR = new Color(145, 184, 242);
  Node node;
  int steps;
  ArrayList<Node> path;

  // constructor of Player
  Player(Node node) {
    this.path = new ArrayList<Node>();
    this.node = node;
    node.color = PLAYER_COLOR;
    this.steps = 0;
  }

  // moves the player to the given node
  void moveTo(Node node) {
    this.path.add(this.node);
    this.node.color = PATH_COLOR;
    this.node = node;
    this.node.color = PLAYER_COLOR;
    this.steps++;
  }

  // moves the player to the left
  void moveLeft() {
    if (this.node.left != null && !this.node.leftWall) {
      moveTo(this.node.left);
    }
  }

  // moves the player to the right
  void moveRight() {
    if (this.node.right != null && !this.node.rightWall) {
      moveTo(this.node.right);
    }
  }

  // moves the player to the top
  void moveTop() {
    if (this.node.top != null && !this.node.topWall) {
      moveTo(this.node.top);
    }
  }

  // moves the player to the bottom
  void moveBottom() {
    if (this.node.bottom != null && !this.node.bottomWall) {
      moveTo(this.node.bottom);
    }
  }

  // method to draw the player
  WorldImage playerImage() {
    return new RectangleImage(Maze.CELL_LEN - 2, Maze.CELL_LEN - 2, "solid", Color.blue)
        .movePinhole(-Maze.CELL_LEN / 2, -Maze.CELL_LEN / 2);
  }
}

// represents Edges
class Edge implements Comparable<Edge> {
  Node node1;
  Node node2;
  int weight;

  // constructor of edge
  Edge(Node node1, Node node2, int weight) {
    this.node1 = node1;
    this.node2 = node2;
    this.weight = weight;
  }

  // overrides the compareTo method
  @Override
  public int compareTo(Edge other) {
    return Integer.signum(this.weight - other.weight);
  }

  // overrides the toString method
  @Override
  public String toString() {
    return "(" + this.node1 + ", " + this.node2 + ")";
  }
}

// represents the Maze
class Maze extends World {
  static final int CELL_LEN = 30;
  ArrayList<ArrayList<Node>> nodes;
  ArrayList<Edge> edges;
  ArrayList<Edge> minimumSpanningTree;
  int heightSize;
  int widthSize;
  Player player;
  boolean isWinning;
  ArrayList<Node> solutionPath;
  ArrayList<Node> dfsPath;
  ArrayList<Node> bfsPath;
  ArrayList<Node> visitedPath;
  ArrayList<Edge> removedWalls;
  int wrongSteps;
  boolean showSolutionPath;
  boolean findSolutionPath;

  // constructor of Maze
  Maze(int widthSize, int heightSize) {
    this.heightSize = heightSize;
    this.widthSize = widthSize;
    this.initGame();
  }

  // constructor of Maze for testing
  Maze(int widthSize, int heightSize, ArrayList<ArrayList<Node>> nodes, ArrayList<Edge> edges) {
    this.heightSize = heightSize;
    this.widthSize = widthSize;
    this.nodes = nodes;
    this.edges = edges;
    this.minimumSpanningTree = new ArrayList<>();
    this.player = new Player(this.nodes.get(0).get(0));
    this.isWinning = false;
    this.solutionPath = new ArrayList<>();
    this.dfsPath = new ArrayList<>();
    this.bfsPath = new ArrayList<>();
    this.visitedPath = new ArrayList<>();
    this.removedWalls = new ArrayList<>();
    this.wrongSteps = 0;
    this.showSolutionPath = false;
    this.findSolutionPath = false;
  }

  // method to show the node of a path one by one
  void showPathNode(ArrayList<Node> path, Color color) {
    if (path.size() > 0) {
      Node node = path.remove(0);
      node.color = color;
    }
  }

  // method to clear the path
  void clearPath() {
    this.resetNodesColor();
    this.visitedPath = new ArrayList<Node>();
    this.solutionPath = new ArrayList<Node>();
    this.dfsPath = new ArrayList<Node>();
    this.bfsPath = new ArrayList<Node>();
  }

  // method to reset nodes color to white
  void resetNodesColor() {
    for (ArrayList<Node> row : this.nodes) {
      for (Node node : row) {
        node.color = Color.white;
      }
    }
  }

  // method to create nodes
  void createNodes() {
    this.nodes = new ArrayList<ArrayList<Node>>();
    for (int i = 0; i < this.heightSize; i++) {
      this.nodes.add(new ArrayList<Node>());
      for (int j = 0; j < this.widthSize; j++) {
        this.nodes.get(i).add(new Node(i, j));
      }
    }
  }

  // method to create edges
  void createEdges() {
    this.edges = new ArrayList<Edge>();
    this.removedWalls = new ArrayList<Edge>();
    this.minimumSpanningTree = new ArrayList<Edge>();
    for (int i = 0; i < this.heightSize; i++) {
      for (int j = 0; j < this.widthSize; j++) {
        if (i < this.heightSize - 1) {
          this.edges.add(new Edge(this.nodes.get(i).get(j), this.nodes.get(i + 1).get(j),
              new Random().nextInt(50)));
        }
        if (j < this.widthSize - 1) {
          this.edges.add(new Edge(this.nodes.get(i).get(j), this.nodes.get(i).get(j + 1),
              new Random().nextInt(50)));
        }
        this.nodes.get(i).get(j).findAdjacentNodes(nodes);
      }
    }
  }

  // methods to init game
  void initGame() {
    this.wrongSteps = 0;
    this.isWinning = false;
    this.findSolutionPath = false;
    this.showSolutionPath = false;
    this.createNodes();
    this.createEdges();
    this.clearPath();
    this.player = new Player(this.nodes.get(0).get(0));
    setStartEndColor();
    kruskal();
  }

  // method to set the start and end color
  void setStartEndColor() {
    this.nodes.get(0).get(0).color = Color.green;
    this.nodes.get(this.heightSize - 1).get(this.widthSize - 1).color = Color.magenta;
  }

  // method to draw all the nodes
  void drawAllNodes(WorldScene scene) {
    for (int i = 0; i < this.heightSize; i++) {
      for (int j = 0; j < this.widthSize; j++) {
        scene.placeImageXY(this.nodes.get(i).get(j).nodeImage(), CELL_LEN * j, CELL_LEN * i);
      }
    }
  }

  // methods to utilize kruskal algo
  void kruskal() {
    PriorityQueue<Edge> pq = new PriorityQueue<Edge>(this.edges);
    while (!pq.isEmpty()) {
      Edge edge = pq.poll();
      Node root1 = edge.node1.find();
      Node root2 = edge.node2.find();
      if (root1 != root2) {
        this.minimumSpanningTree.add(edge);
        edge.node1.union(edge.node2);
      }
    }
  }

  // Breadth-First Search
  ArrayList<Node> bfs() {
    Node startNode = this.nodes.get(0).get(0);
    Node endNode = this.nodes.get(this.heightSize - 1).get(this.widthSize - 1);

    Queue<Node> queue = new LinkedList<>();
    Set<Node> visited = new HashSet<>();
    Map<Node, Node> parentMap = new HashMap<>();

    queue.add(startNode);
    visited.add(startNode);

    while (!queue.isEmpty()) {
      Node currentNode = queue.poll();
      this.bfsPath.add(currentNode);
      this.visitedPath.add(currentNode);
      if (currentNode == endNode) {
        break;
      }

      for (Node neighbor : getNeighbors(currentNode)) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          parentMap.put(neighbor, currentNode);
          queue.add(neighbor);
        }
      }
    }

    return buildPath(parentMap, startNode, endNode);
  }

  // Depth-First Search
  ArrayList<Node> dfs() {
    Node startNode = this.nodes.get(0).get(0);
    Node endNode = this.nodes.get(this.heightSize - 1).get(this.widthSize - 1);

    Stack<Node> stack = new Stack<>();
    Set<Node> visited = new HashSet<>();
    Map<Node, Node> parentMap = new HashMap<>();

    stack.push(startNode);
    visited.add(startNode);

    while (!stack.isEmpty()) {
      Node currentNode = stack.pop();
      this.dfsPath.add(currentNode);
      this.visitedPath.add(currentNode);
      if (currentNode == endNode) {
        break;
      }

      for (Node neighbor : getNeighbors(currentNode)) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          parentMap.put(neighbor, currentNode);
          stack.push(neighbor);
        }
      }
    }

    return buildPath(parentMap, startNode, endNode);
  }

  // Helper method to get valid neighbors of a node
  ArrayList<Node> getNeighbors(Node node) {
    ArrayList<Node> neighbors = new ArrayList<>();

    if (node.left != null && !node.leftWall) {
      neighbors.add(node.left);
    }
    if (node.right != null && !node.rightWall) {
      neighbors.add(node.right);
    }
    if (node.top != null && !node.topWall) {
      neighbors.add(node.top);
    }
    if (node.bottom != null && !node.bottomWall) {
      neighbors.add(node.bottom);
    }

    return neighbors;
  }

  // Helper method to build path from start to end node using the parent map
  ArrayList<Node> buildPath(Map<Node, Node> parentMap, Node startNode, Node endNode) {
    ArrayList<Node> path = new ArrayList<>();
    Node currentNode = endNode;

    while (currentNode != startNode) {
      path.add(0, currentNode);
      currentNode = parentMap.get(currentNode);
    }

    path.add(0, startNode);
    return path;
  }

  // method to remove wall
  void removeWall(Edge edge, WorldScene scene) {
    if (edge.node1.left == edge.node2) {
      edge.node1.removeLeft(scene);
      edge.node1.leftWall = false;
      edge.node2.rightWall = false;
    }
    if (edge.node1.right == edge.node2) {
      edge.node1.removeRight(scene);
      edge.node1.rightWall = false;
      edge.node2.leftWall = false;
    }
    if (edge.node1.top == edge.node2) {
      edge.node1.removeTop(scene);
      edge.node1.topWall = false;
      edge.node2.bottomWall = false;
    }
    if (edge.node1.bottom == edge.node2) {
      edge.node1.removeBottom(scene);
      edge.node1.bottomWall = false;
      edge.node2.topWall = false;
    }
  }

  // method to remove walls
  void removeWallsEdge(WorldScene scene) {
    Edge edge = this.minimumSpanningTree.remove(0);
    this.removeWall(edge, scene);
    this.removedWalls.add(edge);
  }

  // method to remove all walls
  void removeAllWalls(WorldScene scene) {
    for (Edge edge : removedWalls) {
      this.removeWall(edge, scene);
    }
  }

  // method to draw nodes outline
  void drawNodesOutline(WorldScene scene) {
    for (int i = 0; i < this.heightSize; i++) {
      for (int j = 0; j < this.widthSize; j++) {
        scene.placeImageXY(new RectangleImage(CELL_LEN, CELL_LEN,
            OutlineMode.OUTLINE, Color.BLACK),
            CELL_LEN * j + CELL_LEN / 2, CELL_LEN * i + CELL_LEN / 2);
      }
    }
  }

  // method to count wrong steps
  void countWrongSteps() {
    this.wrongSteps = 0;
    for (Node node : this.visitedPath) {
      if (!this.solutionPath.contains(node)) {
        this.wrongSteps += 1;
      }
    }
  }

  // method to draw winning text image
  void drawWinningText(WorldScene scene) {
    TextImage win = new TextImage("You Win!", CELL_LEN * 5, Color.red);
    scene.placeImageXY(win, widthSize * CELL_LEN / 2, heightSize * CELL_LEN / 2);
  }

  // method to draw menu text image
  void drawMenuText(WorldScene scene) {
    TextImage textImage1 = new TextImage(
        "Press 'd' for DFS or 'b' for BFS to find a solution.", 20, Color.black);
    TextImage textImage2 = new TextImage(
        "To restart a random maze, press 'r' key!", 20, Color.black);
    TextImage textImage3 = new TextImage(
        "Wrong steps: " + this.wrongSteps, 20,
        Color.blue);
    scene.placeImageXY(textImage1, 220, heightSize * CELL_LEN + 15);
    scene.placeImageXY(textImage2, 330, heightSize * CELL_LEN + 35);
    scene.placeImageXY(textImage3, 75, heightSize * CELL_LEN + 35);
  }

  // method to override makeScene method
  @Override
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(widthSize * CELL_LEN, heightSize * CELL_LEN);
    drawNodesOutline(scene);
    drawAllNodes(scene);

    // animate the walls knocking down
    if (this.minimumSpanningTree.size() > 0) {
      removeWallsEdge(scene);
    }
    removeAllWalls(scene);

    if (this.isWinning) {
      drawWinningText(scene);
    }
    drawMenuText(scene);
    return scene;
  }

  // method to override onKeyEvent method
  @Override
  public void onKeyEvent(String key) {
    if (!this.isWinning) {
      if (key.equals("up")) {
        this.player.moveTop();
      } else if (key.equals("down")) {
        this.player.moveBottom();
      } else if (key.equals("left")) {
        this.player.moveLeft();
      } else if (key.equals("right")) {
        this.player.moveRight();
      }
      if (this.solutionPath.size() == 0 && !this.findSolutionPath) {
        this.solutionPath = dfs();
        this.visitedPath = new ArrayList<>();
        this.findSolutionPath = true;
      }
      this.visitedPath.add(this.player.node);
    }

    if (key.equals("b")) {
      this.showSolutionPath = true;
      this.clearPath();
      this.solutionPath = bfs();
    } else if (key.equals("d")) {
      this.showSolutionPath = true;
      this.clearPath();
      this.solutionPath = dfs();
    } else if (key.equals("r")) {
      this.initGame();
    }
    if (this.player.node == this.nodes.get(this.heightSize - 1)
        .get(this.widthSize - 1)) {
      this.isWinning = true;
    }
    this.countWrongSteps();
  }

  // method to override the onTick method to animate the path
  @Override
  public void onTick() {
    if (this.showSolutionPath) {
      if (this.solutionPath.size() > 0 && this.dfsPath.size() == 0 && this.bfsPath.size() == 0) {
        this.showPathNode(this.solutionPath, Color.orange);
      }
      if (this.dfsPath.size() > 0) {
        this.showPathNode(this.dfsPath, Color.yellow);
      }
      if (this.bfsPath.size() > 0) {
        this.showPathNode(this.bfsPath, Color.pink);
      }
    }
    if (this.solutionPath.size() == 0 && this.dfsPath.size() == 0 && this.bfsPath.size() == 0) {
      this.setStartEndColor();
    }
  }

  // click here to play the game
  public static void main(String[] args) {
    Maze maze = new Maze(20, 10);
    int height = maze.heightSize * CELL_LEN + 50;
    int width = maze.widthSize * CELL_LEN > 550
        ? maze.widthSize * CELL_LEN
        : 550;
    maze.bigBang(width, height, 0.01);
  }
}

// represents the example to test
class ExamplesMaze {
  Node n1;
  Node n2;
  Node n3;
  Node n4;
  Node n5;
  Node n6;
  Node n7;
  Node n8;
  ArrayList<ArrayList<Node>> nodes;

  Edge e1;
  Edge e2;
  Edge e3;
  Edge e4;
  Edge e5;
  Edge e6;
  Edge e7;
  Edge e8;
  Edge e9;
  Edge e10;
  ArrayList<Edge> edges;
  Maze maze;

  void testGame(Tester t) {
    Maze maze = new Maze(20, 10);
    int height = maze.heightSize * Maze.CELL_LEN + 50;
    int width = maze.widthSize * Maze.CELL_LEN > 550
        ? maze.widthSize * Maze.CELL_LEN
        : 550;
    maze.bigBang(width, height, 0.01);
  }

  // initialize the test
  void initTest() {
    this.n1 = new Node(0, 0);
    this.n2 = new Node(0, 1);
    this.n3 = new Node(0, 2);
    this.n4 = new Node(0, 3);
    this.n5 = new Node(1, 0);
    this.n6 = new Node(1, 1);
    this.n7 = new Node(1, 2);
    this.n8 = new Node(1, 3);
    this.nodes = new ArrayList<ArrayList<Node>>(
        Arrays.asList(new ArrayList<Node>(Arrays.asList(n1, n2, n3, n4)),
            new ArrayList<Node>(Arrays.asList(n5, n6, n7, n8))));

    this.e1 = new Edge(n1, n2, 1);
    this.e2 = new Edge(n1, n5, 2);
    this.e3 = new Edge(n2, n3, 3);
    this.e4 = new Edge(n2, n6, 4);
    this.e5 = new Edge(n3, n4, 5);
    this.e6 = new Edge(n3, n7, 6);
    this.e7 = new Edge(n4, n8, 7);
    this.e8 = new Edge(n5, n6, 8);
    this.e9 = new Edge(n6, n7, 9);
    this.e10 = new Edge(n7, n8, 10);
    this.edges = new ArrayList<Edge>(
        Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10));
    this.maze = new Maze(4, 2, this.nodes, this.edges);
  }

  // test the method to find the adjacent nodes
  void testFindAdjacentNodes(Tester t) {
    this.initTest();
    // find the adjacent nodes for each node
    for (ArrayList<Node> row : this.maze.nodes) {
      for (Node node : row) {
        node.findAdjacentNodes(nodes);
      }
    }
    t.checkExpect(this.n1.left, null);
    t.checkExpect(this.n1.right, this.n2);
    t.checkExpect(this.n1.top, null);
    t.checkExpect(this.n1.bottom, this.n5);
    t.checkExpect(this.n2.left, this.n1);
    t.checkExpect(this.n2.right, this.n3);
    t.checkExpect(this.n2.top, null);
    t.checkExpect(this.n2.bottom, this.n6);
    t.checkExpect(this.n3.left, this.n2);
    t.checkExpect(this.n3.right, this.n4);
    t.checkExpect(this.n3.top, null);
    t.checkExpect(this.n3.bottom, this.n7);
    t.checkExpect(this.n4.left, this.n3);
    t.checkExpect(this.n4.right, null);
    t.checkExpect(this.n4.top, null);
    t.checkExpect(this.n4.bottom, this.n8);
    t.checkExpect(this.n5.left, null);
    t.checkExpect(this.n5.right, this.n6);
    t.checkExpect(this.n5.top, this.n1);
    t.checkExpect(this.n5.bottom, null);
    t.checkExpect(this.n6.left, this.n5);
    t.checkExpect(this.n6.right, this.n7);
    t.checkExpect(this.n6.top, this.n2);
    t.checkExpect(this.n6.bottom, null);
    t.checkExpect(this.n7.left, this.n6);
    t.checkExpect(this.n7.right, this.n8);
    t.checkExpect(this.n7.top, this.n3);
    t.checkExpect(this.n7.bottom, null);
    t.checkExpect(this.n8.left, this.n7);
    t.checkExpect(this.n8.right, null);
    t.checkExpect(this.n8.top, this.n4);
    t.checkExpect(this.n8.bottom, null);
  }

  // Test the find method
  void testFind(Tester t) {
    this.initTest();
    t.checkExpect(this.n1.find(), this.n1);
    this.n1.parent = this.n2;
    t.checkExpect(this.n1.find(), this.n2);
    this.n1.parent = this.n3;
    t.checkExpect(this.n1.find(), this.n3);
    this.n1.parent = this.n4;
    t.checkExpect(this.n1.find(), this.n4);
    this.n1.parent = this.n5;
    t.checkExpect(this.n1.find(), this.n5);
  }

  // Test the union method
  void testUnion(Tester t) {
    this.initTest();
    this.n1.union(this.n2);
    t.checkExpect(this.n1.find(), this.n2);
    this.n1.union(this.n3);
    t.checkExpect(this.n1.find(), this.n2);
    this.n2.union(this.n4);
    t.checkExpect(this.n1.find(), this.n2);
  }

  // Test the method of kruskal
  void testKruskal(Tester t) {
    this.initTest();
    this.maze.kruskal();
    t.checkExpect(this.maze.minimumSpanningTree.size(), 7);
    t.checkExpect(this.maze.minimumSpanningTree.get(0).node1, this.n1);
    t.checkExpect(this.maze.minimumSpanningTree.get(0).node2, this.n2);
    t.checkExpect(this.maze.minimumSpanningTree.get(1).node1, this.n1);
    t.checkExpect(this.maze.minimumSpanningTree.get(1).node2, this.n5);
    t.checkExpect(this.maze.minimumSpanningTree.get(2).node1, this.n2);
    t.checkExpect(this.maze.minimumSpanningTree.get(2).node2, this.n3);
    t.checkExpect(this.maze.minimumSpanningTree.get(3).node1, this.n2);
    t.checkExpect(this.maze.minimumSpanningTree.get(3).node2, this.n6);
    t.checkExpect(this.maze.minimumSpanningTree.get(4).node1, this.n3);
    t.checkExpect(this.maze.minimumSpanningTree.get(4).node2, this.n4);
    t.checkExpect(this.maze.minimumSpanningTree.get(5).node1, this.n3);
    t.checkExpect(this.maze.minimumSpanningTree.get(5).node2, this.n7);
    t.checkExpect(this.maze.minimumSpanningTree.get(6).node1, this.n4);
    t.checkExpect(this.maze.minimumSpanningTree.get(6).node2, this.n8);
  }

  // Test the method of getNeighbors
  void testGetNeighbors(Tester t) {
    this.initTest();
    for (ArrayList<Node> row : this.nodes) {
      for (Node node : row) {
        node.findAdjacentNodes(nodes);
      }
    }
    this.maze.kruskal();
    WorldScene scene = this.maze.makeScene();
    this.maze.removedWalls = this.maze.minimumSpanningTree;
    this.maze.removeAllWalls(scene);
    ArrayList<Node> n1Neighbors = this.maze.getNeighbors(this.n1);
    ArrayList<Node> n2Neighbors = this.maze.getNeighbors(this.n2);
    ArrayList<Node> n3Neighbors = this.maze.getNeighbors(this.n3);
    ArrayList<Node> n4Neighbors = this.maze.getNeighbors(this.n4);
    ArrayList<Node> n5Neighbors = this.maze.getNeighbors(this.n5);
    ArrayList<Node> n6Neighbors = this.maze.getNeighbors(this.n6);
    ArrayList<Node> n7Neighbors = this.maze.getNeighbors(this.n7);
    ArrayList<Node> n8Neighbors = this.maze.getNeighbors(this.n8);
    t.checkExpect(n1Neighbors, new ArrayList<Node>(Arrays.asList(this.n2, this.n5)));
    t.checkExpect(n2Neighbors, new ArrayList<Node>(Arrays.asList(this.n1, this.n3, this.n6)));
    t.checkExpect(n3Neighbors, new ArrayList<Node>(Arrays.asList(this.n2, this.n4, this.n7)));
    t.checkExpect(n4Neighbors, new ArrayList<Node>(Arrays.asList(this.n3, this.n8)));
    t.checkExpect(n5Neighbors, new ArrayList<Node>(Arrays.asList(this.n1)));
    t.checkExpect(n6Neighbors, new ArrayList<Node>(Arrays.asList(this.n2)));
    t.checkExpect(n7Neighbors, new ArrayList<Node>(Arrays.asList(this.n3)));
    t.checkExpect(n8Neighbors, new ArrayList<Node>(Arrays.asList(this.n4)));
  }

  // Test the method of dfs
  void testDfs(Tester t) {
    this.initTest();
    for (ArrayList<Node> row : this.nodes) {
      for (Node node : row) {
        node.findAdjacentNodes(nodes);
      }
    }
    this.maze.kruskal();
    WorldScene scene = this.maze.makeScene();
    this.maze.removedWalls = this.maze.minimumSpanningTree;
    this.maze.removeAllWalls(scene);
    this.maze.solutionPath = this.maze.dfs();
    t.checkExpect(this.maze.visitedPath.size(), 8);
    t.checkExpect(this.maze.solutionPath.size(), 5);

    t.checkExpect(this.maze.visitedPath,
        new ArrayList<Node>(
            Arrays.asList(this.n1, this.n5, this.n2, this.n6, this.n3, this.n7, this.n4, this.n8)));
    t.checkExpect(this.maze.solutionPath,
        new ArrayList<Node>(Arrays.asList(this.n1, this.n2, this.n3, this.n4, this.n8)));
  }

  // Test the method of bfs
  void testBfs(Tester t) {
    this.initTest();
    for (ArrayList<Node> row : this.nodes) {
      for (Node node : row) {
        node.findAdjacentNodes(nodes);
      }
    }
    this.maze.kruskal();
    WorldScene scene = this.maze.makeScene();
    this.maze.removedWalls = this.maze.minimumSpanningTree;
    this.maze.removeAllWalls(scene);
    this.maze.solutionPath = this.maze.bfs();
    t.checkExpect(this.maze.visitedPath.size(), 8);
    t.checkExpect(this.maze.solutionPath.size(), 5);

    t.checkExpect(this.maze.visitedPath,
        new ArrayList<Node>(
            Arrays.asList(this.n1, this.n2, this.n5, this.n3, this.n6, this.n4, this.n7, this.n8)));
    t.checkExpect(this.maze.solutionPath,
        new ArrayList<Node>(Arrays.asList(this.n1, this.n2, this.n3, this.n4, this.n8)));
  }

  // Test the method of countWrongSteps
  void testCountWrongSteps(Tester t) {
    this.initTest();
    for (ArrayList<Node> row : this.nodes) {
      for (Node node : row) {
        node.findAdjacentNodes(nodes);
      }
    }
    this.maze.kruskal();
    WorldScene scene = this.maze.makeScene();
    this.maze.removedWalls = this.maze.minimumSpanningTree;
    this.maze.removeAllWalls(scene);
    this.maze.solutionPath = this.maze.dfs();
    this.maze.countWrongSteps();
    t.checkExpect(this.maze.wrongSteps, 3);
  }
}