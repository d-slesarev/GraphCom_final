package graph.algorithm;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableGraph;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Checker {

    /**
     * Метод проверяет корректность параметра ?
     * @param myGraph Проверяемый граф
     * @return Возвращает true, если параметр ? корректен, если иначе — false*/
    private static boolean isCorrectMU(MutableGraph<String> myGraph) {
        ArrayList<Integer> numberOfNeighborsOfNonAdjacentVertices = new ArrayList<>();
        ArrayList<String> nonAdjacentPeaks;
        boolean isCorrect = true;
        Integer numCommonNeighbors = 0;

        for (String peak : myGraph.nodes()){
            nonAdjacentPeaks = new ArrayList<>(myGraph.nodes().stream().toList());

            if (nonAdjacentPeaks.removeAll(myGraph.adjacentNodes(peak))){
                nonAdjacentPeaks.remove(peak);
                for (String nonPeak : nonAdjacentPeaks){
                    for (String adjNonPeak : myGraph.adjacentNodes(nonPeak)){
                        if (myGraph.adjacentNodes(peak).contains(adjNonPeak)){
                            numCommonNeighbors++;
                        }
                    }
                    numberOfNeighborsOfNonAdjacentVertices.add(numCommonNeighbors);
                    numCommonNeighbors = 0;
                }
            }
        }

        for (Integer neighbors : numberOfNeighborsOfNonAdjacentVertices){
            if (!numberOfNeighborsOfNonAdjacentVertices.get(0).equals(neighbors)){
                isCorrect = false;
            }
        }

        return isCorrect;
    }
    /**
     * Метод проверяет корректность параметра ?
     * @param myGraph Проверяемый граф
     * @return Возвращает true, если параметр ? корректен, если иначе — false*/
    private static boolean isCorrectLambda(@NotNull MutableGraph<String> myGraph) {
        ArrayList<Integer> numberOfAdjacentVertexNeighbors = new ArrayList<>();
        boolean isCorrect = true;
        Integer numCommonNeighbors = 0;

        for (EndpointPair<String> rib : myGraph.edges()){
            for (String adjPeak : myGraph.adjacentNodes(rib.nodeU())) {
                if (myGraph.adjacentNodes(rib.nodeV()).contains(adjPeak)) {
                    numCommonNeighbors++;
                }
            }
            numberOfAdjacentVertexNeighbors.add(numCommonNeighbors);
            numCommonNeighbors = 0;
        }

        for (Integer neighbors : numberOfAdjacentVertexNeighbors){
            if (!numberOfAdjacentVertexNeighbors.get(0).equals(neighbors)){
                isCorrect = false;
            }
        }

        return isCorrect;
    }
    /**
     * Метод проверяет являться ли переданный граф myGraph сильно регулярным
     * @param myGraph Проверяемый граф
     * @return Возвращает true, если параметр истина, если иначе — false*/
    public static boolean isStronglyRegularGraph(MutableGraph<String> myGraph){
        boolean isCorrect = false;

        if (isCorrectMU(myGraph) && isCorrectLambda(myGraph)){
            isCorrect = true;
        }

        return isCorrect;
    }

}