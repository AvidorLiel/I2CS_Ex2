# Map2D Pathfinding and Image Processing Project

## Overview
This project, focuses on implementing a 2D map data structure and various algorithms for image processing and pathfinding.
The core of the project involves representing a grid-based map where each pixel has a color value,
and providing tools to manipulate this map, draw shapes, and calculate the shortest path between points.

## Features
- **Shape Drawing:** Implementation of algorithms to draw lines, rectangles, and circles on a 2D grid.
- **Area Fill:** A recursive or stack-based fill algorithm to color bounded areas.
- **Pathfinding (BFS):** Implementation of the Breadth-First Search algorithm to find the shortest path between two points while avoiding obstacles.
- **Map Serialization:** Tools to save map data to text files and load them back into the application.
- **GUI Visualization:** Integration with `StdDraw` to provide a real-time graphical representation of the maps and algorithms.

## Class Structure
- `Map.java`: The core implementation of the `Map2D` interface, handling the grid logic.
- `Index2D.java`: Represents a coordinate (x, y) on the map.
- `GUI.java`: Handles the visual rendering of the map and provides a `main` method for testing/demonstration.
- `Pixel2D.java` & `Map2D.java`: Interfaces defining the required functionality for pixels and map operations.

## Pathfinding Logic
The shortest path algorithm is based on **BFS (Breadth-First Search)**. It ensures:
1. The path found is the shortest possible in terms of steps.
2. Obstacles (defined by specific color values) are completely avoided.
3. Support for both 4-connectivity (up, down, left, right) and 8-connectivity (including diagonals).

## How to Run
1. **Prerequisites:** - Java JDK 8 or higher.
   - `StdDraw` library (included in the project dependencies).
2. **Compilation:**
   ```bash
   javac *.java


<img width="1197" height="1017" alt="image" src="https://github.com/user-attachments/assets/41b886e0-1e11-426c-ac2d-72b12d299b63" />

   <img width="961" height="1022" alt="image" src="https://github.com/user-attachments/assets/2454fbc6-3777-4ccc-8fb5-55f0b4e0255c" />
