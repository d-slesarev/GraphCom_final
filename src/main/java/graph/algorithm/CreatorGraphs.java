package graph.algorithm;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.guava.MutableGraphAdapter;

import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class CreatorGraphs {

    public enum Source {
        FROM_CONTIGUITY_MATRIX,
        FROM_INCIDENCE_MATRIX,
        FROM_GRAPHICAL_VIEW,
        FROM_MFI
    }

    private static final Logger logger = Logger.getLogger(CreatorGraphs.class.getName());
    private final ArrayList<ArrayList<Integer>> Matrix;
    private final List<String> GraphVertices;
    private final mxGraph Graph;
    private final Integer[] G;
    private final Integer[] P;

    private CreatorGraphs(Builder builder) {
        Matrix = builder.Matrix;
        GraphVertices = builder.GraphVertices;
        Graph = builder.Graph;
        G = builder.G;
        P = builder.P;
    }

    /**
     * ����� ������� GUAVA-���� �� ���������� ���������
     * @param mySource �������� �������� GUAVA-�����
     * @return ���������� GUAVA-����*/
    public MutableGraph<String> getGraphGUAVA(@NotNull Source mySource){
        switch (mySource){
            case FROM_CONTIGUITY_MATRIX : {
                if (Matrix != null && GraphVertices != null){
                    return createFromContigTable();
                } else {
                    return null;
                }
            }

            case FROM_INCIDENCE_MATRIX : {
                if (Matrix != null && GraphVertices != null){
                    return createFromIncidentTable();
                } else {
                    return null;
                }
            }

            case FROM_GRAPHICAL_VIEW : {
                if (Graph != null){
                    return createFromGraphicalView();
                } else {
                    return null;
                }
            }

            case FROM_MFI : {
                if (G.length != 0 && P.length != 0){
                    return createFromMFI();
                } else {
                    return null;
                }
            }

            default:{
                return null;
            }
        }
    }
    /**
     * ����� ������� JGraph-���� �� GUAVA-�����
     * @param myGraph GUAVA-����
     * @return ���������� JGraph-����*/
    public mxGraph getJGraph(@NotNull MutableGraph<String> myGraph){
        MutableGraphAdapter<String> jgrapht = new MutableGraphAdapter<>(myGraph);

        JGraphXAdapter adapter = new JGraphXAdapter(jgrapht);
        mxGraph graph = new mxGraph(adapter.getModel());

        Object[] arrObj = graph.getChildCells(graph.getDefaultParent());


        for (Object obj : arrObj) {
            if (obj instanceof mxCell) {
                mxCell cell = (mxCell) obj;
                if (cell.isVertex()) {
                    graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", new Object[]{cell});
                    graph.setCellStyles(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE, new Object[]{cell});
                    graph.setCellStyles(mxConstants.STYLE_OPACITY, "100", new Object[]{cell});
                    graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "red", new Object[]{cell});
                    cell.getGeometry().setHeight(18);
                    cell.getGeometry().setWidth(18);
                }
                if (cell.isEdge()) {
                    graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "black", new Object[]{cell});
                    graph.setCellStyles(mxConstants.STYLE_ENDARROW, mxConstants.NONE, new Object[]{cell});
                    cell.setValue("");
                }
            }
        }
        logger.info("�������� ������ JGraph-����� �� GUAVA-�����");
        return graph;
    }
    /**
     * ����� ������� GUAVA-���� �� ������� ���������
     * @return ���������� GUAVA-����*/
    private MutableGraph<String> createFromContigTable(){
        MutableGraph<String> myGraph = GraphBuilder.undirected().build();

        for (int i = 0; i < GraphVertices.size(); i++) {
            myGraph.addNode(GraphVertices.get(i));
        }

        for (int i = 0; i < Matrix.size(); i++) {
            for (int j = 0; j < Matrix.get(i).size(); j++) {
                if (Matrix.get(i).get(j).equals(1)){
                    myGraph.putEdge(GraphVertices.get(i), GraphVertices.get(j));
                }
            }
        }
        logger.info("�������� ������ GUAVA-����� �� ������� ���������");
        return myGraph;
    }
    /**
     * ����� ������� GUAVA-���� �� ������� �������������
     * @return ���������� GUAVA-����*/
    private MutableGraph<String> createFromIncidentTable(){
        MutableGraph<String> myGraph = GraphBuilder.undirected().build();
        ArrayList<Integer> indexPeaks = new ArrayList<>();

        for (int i = 0; i < GraphVertices.size(); i++) {
            myGraph.addNode(GraphVertices.get(i));
        }

        for (int i = 0; i < Matrix.size(); i++) {
            for (int j = 0; j < Matrix.get(i).size(); j++) {
                if (Matrix.get(i).get(j).equals(1)){
                    indexPeaks.add(j);
                }
            }
            myGraph.putEdge(GraphVertices.get(indexPeaks.get(0)), GraphVertices.get(indexPeaks.get(1)));
            indexPeaks.clear();
        }
        logger.info("�������� ������ GUAVA-����� �� ������� �������������");
        return myGraph;
    }
    /**
     * ����� ������� GUAVA-���� �� JGraph-�����
     * @return ���������� GUAVA-����*/
    private MutableGraph<String> createFromGraphicalView(){
        MutableGraph<String> myGraph = GraphBuilder.undirected().build();
        Object[] arrObj = Graph.getChildCells(Graph.getDefaultParent());

        mxCell cell;

        for(Object obj : arrObj){
            if (obj instanceof mxCell){
                cell = (mxCell) obj;
                if (cell.isVertex()){
                    myGraph.addNode(cell.getValue().toString());
                }
            }
        }

        for(Object obj : arrObj){
            if (obj instanceof mxCell){
                cell = (mxCell) obj;
                if (cell.isEdge()){
                    myGraph.putEdge(cell.getSource().getValue().toString(), cell.getTarget().getValue().toString());
                }
            }
        }


        return myGraph;
    }
    /**
     * ����� ������� GUAVA-���� �� MFI
     * @return ���������� GUAVA-����*/
    private MutableGraph<String> createFromMFI(){
        int index = 0;// ������ ��� ������� G
        Integer box;
        MutableGraph<String> myGraph = GraphBuilder.undirected().build();
        HashSet<Integer> idPeak = new HashSet<>();


        for (Integer someID : G){
            idPeak.add(someID);
        }

        idPeak.stream().sorted();

        for(Integer someName : idPeak){
            myGraph.addNode(someName.toString());
        }


        for (int i = 0; i < P.length; i++) {
            metka:
            {
                while (true){
                    if (P[i] < index){
                        break metka;
                    }
                    box = i;
                    box++;
                    myGraph.putEdge(box.toString(), G[index].toString());
                    if (index < G.length - 1){
                        index++;
                    }
                    else {
                        break metka;
                    }
                }
            }
        }
        logger.info("�������� ������ GUAVA-����� �� MFI");

        return myGraph;
    }

    public static class Builder{

        private ArrayList<ArrayList<Integer>> Matrix = null;
        private List<String> GraphVertices = null;
        private mxGraph Graph = null;
        private Integer[] G;
        private Integer[] P;

        public Builder matrix(ArrayList<ArrayList<Integer>> matrix){
            Matrix = matrix;
            return this;
        }

        public Builder graphVertices(List<String> graphVertices){
            GraphVertices = graphVertices;
            return this;
        }

        public Builder graph(mxGraph graph){
            Graph = graph;
            return this;
        }

        public Builder G(Integer[] G){
            this.G = G;
            return this;
        }

        public Builder P(Integer[] P){
            this.P = P;
            return this;
        }

        public CreatorGraphs build(){
            return new CreatorGraphs(this);
        }
    }

}
