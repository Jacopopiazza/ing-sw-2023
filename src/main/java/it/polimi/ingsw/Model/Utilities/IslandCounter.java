package it.polimi.ingsw.Model.Utilities;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

class IslandCounter {

        static final int R = 6;
        static final int C = 5;

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
        static boolean isSafe(TileColor mat[][], int i, int j,
                              boolean vis[][], TileColor color) {
            return (i >= 0) && (i < R) &&
                    (j >= 0) && (j < C) &&
                    (mat[i][j] == color && !vis[i][j]);
        }

        static void BFS(TileColor mat[][], boolean vis[][],
                        int si, int sj,RefInteger compostaDa) {

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
                    if (isSafe(mat, i + row[k],
                            j + col[k], vis, mat[i][j])) {
                        vis[i + row[k]][j + col[k]] = true;
                        q.add(new pair(i + row[k], j + col[k]));
                        compostaDa.increment();
                    }
                }
            }
        }

        // This function returns number islands (connected
        // components) in a graph. It simply works as
        // BFS for disconnected graph and returns count
        // of BFS calls.
        static List<Integer> countIslands(TileColor mat[][]) {
            // Mark all cells as not visited
            boolean[][] vis = new boolean[R][C];

            // Each element of the list is and island
            // the value at i-th position tell us the
            // amount of tiles the island is made of
            List<RefInteger> islands = new ArrayList<RefInteger>();

            // Call BFS for every unvisited vertex
            // Whenever it finds an unvisited vertex
            // it means there is a new island
            for (int i = 0; i < R; i++) {
                for (int j = 0; j < C; j++) {
                    if (mat[i][j] != null && !vis[i][j]) {
                        RefInteger compostaDa = new RefInteger(1);
                        BFS(mat, vis, i, j,compostaDa);
                        islands.add(compostaDa);
                    }
                }
            }

            return islands.stream().map(n -> n.toInt()).
                    collect(Collectors.toCollection(ArrayList::new));
        }
    }