package it.polimi.ingsw.Model.Utilities;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.TileColor;

import java.util.*;
import java.util.stream.Collectors;

public class IslandCounter {

        static final int R = Shelf.getRows();
        static final int C = Shelf.getColumns();

        static class pair {
            int first, second;

            public pair(int first, int second) {
                this.first = first;
                this.second = second;
            }
        }
        static class RefInteger {
            public Integer value;

            public RefInteger(Integer value)
            {
                this.value = value;
            }

            public void increment(){
                this.value++;
            }

            public int toInt(){
                return this.value;
            }

            @Override
            public String toString()
            {
                return String.valueOf(value);
            }
        }

        // A function to check if a given position
        // (i, j) is valid for the Shelf, and its tile
        // is of the same color of the initial cell
        static boolean isSafe(Shelf shelf, int i, int j,
                              boolean vis[][], TileColor color) {
            return (i >= 0) && (i < R) &&
                    (j >= 0) && (j < C) &&
                    (shelf.getTile(new Coordinates(i,j)) != null &&
                            shelf.getTile(new Coordinates(i,j)).getColor() == color &&
                            !vis[i][j]);
        }

        static void BFS(Shelf shelf, boolean vis[][], int si, int sj, RefInteger cont) {

            // These arrays are used to get row and
            // column numbers of 8 neighbours of
            // a given cell
            int row[] = {-1, -1, -1, 0, 0, 1, 1, 1};
            int col[] = {-1, 0, 1, -1, 1, -1, 0, 1};

            // Simple BFS first step, we enqueue
            // source and mark it as visited
            Queue<pair> q = new LinkedList<pair>();
            q.add(new pair(si, sj));
            vis[si][sj] = true;

            // Next step of BFS. We take out
            // items one by one from queue and
            // enqueue their unvisited adjacent
            while (!q.isEmpty()) {

                int i = q.peek().first;
                int j = q.peek().second;
                q.remove();

                // Go through all 8 adjacent
                for (int k = 0; k < 8; k++) {
                    if (isSafe(shelf, i + row[k],
                            j + col[k], vis, shelf.getTile(new Coordinates(i,j)).getColor())) {
                        vis[i + row[k]][j + col[k]] = true;
                        q.add(new pair(i + row[k], j + col[k]));
                        cont.increment();
                    }
                }
            }
        }

        // This function returns number islands (connected
        // components) in a graph. It simply works as
        // BFS for disconnected graph and returns count
        // of BFS calls.
        public static List<Integer> countIslands(Shelf shelf) {
            // Mark all cells as not visited
            boolean[][] vis = new boolean[R][C];

            // The value at i-th position tells us the
            // amount of tiles the island is made of
            List<RefInteger> islands = new ArrayList<RefInteger>();

            // Calls BFS for every unvisited vertex
            // Whenever it finds an unvisited vertex
            // a new island was found
            for (int i = 0; i < R; i++) {
                for (int j = 0; j < C; j++) {
                    if (shelf.getTile(new Coordinates(i,j)) != null && !vis[i][j]) {
                        RefInteger cont = new RefInteger(1);
                        BFS(shelf, vis, i, j,cont);
                        islands.add(cont);
                    }
                }
            }

            return islands.stream().map(n -> n.toInt()).
                    collect(Collectors.toCollection(ArrayList::new));
        }
    }