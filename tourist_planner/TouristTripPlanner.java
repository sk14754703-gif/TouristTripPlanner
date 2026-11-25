
import java.util.*;

public class TouristTripPlanner {

    static class Edge {
        String destination;
        int time;

        Edge(String destination, int time) {
            this.destination = destination;
            this.time = time;
        }
    }

    static class Result {
        ArrayList<String> route;
        int totalTime;
        int totalRating;

        Result(ArrayList<String> route, int time, int rating) {
            this.route = new ArrayList<>(route);
            this.totalTime = time;
            this.totalRating = rating;
        }
    }

    static HashMap<String, ArrayList<Edge>> graph =
            new HashMap<>();

    static HashMap<String, Integer> ratings =
            new HashMap<>();

    static void addLocation(String name, int rating) {
        graph.put(name, new ArrayList<>());
        ratings.put(name, rating);
    }

    static void addRoad(String source, String destination, int time) {
        graph.get(source).add(new Edge(destination, time));
        graph.get(destination).add(new Edge(source, time));
    }

    static void createGraph() {

        addLocation("Hotel", 0);
        addLocation("Fort", 8);
        addLocation("Museum", 5);
        addLocation("Lake", 4);
        addLocation("Temple", 6);

        addRoad("Hotel", "Fort", 2);
        addRoad("Hotel", "Lake", 1);
        addRoad("Lake", "Museum", 1);
        addRoad("Temple", "Fort", 2);
        addRoad("Temple", "Museum", 1);
    }

    static void sortByRating(ArrayList<Edge> neighbours) {

        for (int i = 0; i < neighbours.size() - 1; i++) {

            for (int j = 0; j < neighbours.size() - i - 1; j++) {

                int rating1 = ratings.get(
                        neighbours.get(j).destination);

                int rating2 = ratings.get(
                        neighbours.get(j + 1).destination);

                if (rating1 < rating2) {

                    Edge temp = neighbours.get(j);

                    neighbours.set(j, neighbours.get(j + 1));

                    neighbours.set(j + 1, temp);
                }
            }
        }
    }
    static Result greedy(int budget) {

        ArrayList<String> route = new ArrayList<>();

        HashSet<String> visited = new HashSet<>();

        String current = "Hotel";

        int totalTime = 0;
        int totalRating = 0;

        route.add(current);
        visited.add(current);

        while (true) {

            ArrayList<Edge> neighbours =
                    new ArrayList<>(graph.get(current));


            sortByRating(neighbours);

            boolean found = false;

            for (Edge edge : neighbours) {

                String next = edge.destination;

                if (!visited.contains(next)
                        && totalTime + edge.time <= budget) {

                    current = next;

                    visited.add(current);
                    route.add(current);

                    totalTime += edge.time;
                    totalRating += ratings.get(current);

                    found = true;
                    break;
                }
            }

        
            if (!found) {
                break;
            }
        }

        return new Result(route, totalTime, totalRating);
    }

    static Result bestResult;

    static void backtrack(
            String current,
            int budget,
            int totalTime,
            int totalRating,
            HashSet<String> visited,
            ArrayList<String> route) {

        if (bestResult == null
                || totalRating > bestResult.totalRating
                || (totalRating == bestResult.totalRating
                && totalTime < bestResult.totalTime)) {

            bestResult = new Result(
                    route, totalTime, totalRating);
        }

    
        for (Edge edge : graph.get(current)) {

            String next = edge.destination;

            if (!visited.contains(next)
                    && totalTime + edge.time <= budget) {

                // Visit location
                visited.add(next);
                route.add(next);

                // Recursive call
                backtrack(
                        next,
                        budget,
                        totalTime + edge.time,
                        totalRating + ratings.get(next),
                        visited,
                        route
                );

                // Backtracking
                route.remove(route.size() - 1);
                visited.remove(next);
            }
        }
    }

    // Display route details
    static void displayResult(String title, Result result) {

        System.out.println("\n" + title);

        System.out.println(
                "Route: " + String.join(" -> ", result.route));

        System.out.println(
                "Travel Time: " + result.totalTime + " hours");

        System.out.println(
                "Total Rating: " + result.totalRating);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        createGraph();

        System.out.println("==================================");
        System.out.println("       TOURIST TRIP PLANNER");
        System.out.println("==================================");

        System.out.print("Enter your travel budget (hours): ");

        if (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            sc.close();
            return;
        }

        int budget = sc.nextInt();

        if (budget <= 0) {
            System.out.println(
                    "Invalid budget. Hours must be greater than 0.");
            sc.close();
            return;
        }

        Result greedyResult = greedy(budget);

    
        bestResult = null;

        HashSet<String> visited = new HashSet<>();
        ArrayList<String> route = new ArrayList<>();

        visited.add("Hotel");
        route.add("Hotel");

        backtrack(
                "Hotel",
                budget,
                0,
                0,
                visited,
                route
        );

        displayResult("GREEDY PLAN", greedyResult);

        displayResult("BEST PLAN (BACKTRACKING)", bestResult);

        System.out.println("\n==================================");
        System.out.println("         ROUTE COMPARISON");
        System.out.println("==================================");

        if (bestResult.totalRating > greedyResult.totalRating) {

            System.out.println(
                    "Backtracking found a better-rated route.");

        } else if (bestResult.totalRating
                == greedyResult.totalRating) {

            System.out.println(
                    "Both algorithms achieved the same rating.");

        } else {

            System.out.println(
                    "Greedy found a better-rated route.");
        }

        System.out.println("\nThank you for using Tourist Trip Planner!");

        sc.close();
    }
}
