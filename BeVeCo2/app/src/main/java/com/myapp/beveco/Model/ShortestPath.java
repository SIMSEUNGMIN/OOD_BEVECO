package com.myapp.beveco.Model;

import com.myapp.beveco.Main.MainActivity;
import com.myapp.beveco.View.Map;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;

public class ShortestPath {

    //상수
    private final static double DEFAULT_MAX_SIZE = Integer.MAX_VALUE;

    //인스턴스 변수
    private Map _mapView;
    private TMapPoint[] _finalPoints;
    private double[][] _length;
    private boolean[] _visit;
    private int _input;
    //최단경로 찾기의 결과 포인트가 들어가는 배열
    private TMapPoint[] _shortestPaths;


    //getter/setter
    private  Map mapView(){
        return this._mapView;
    }

    private  void setMapView(Map aMap) {
        this._mapView = aMap;
    }

    private TMapPoint[] finalPoint(){
        return this._finalPoints;
    }

    private void setFinalPoint(TMapPoint[] aFinalPoint){
        this._finalPoints = aFinalPoint;
    }

    private  double[][] length(){
        return this._length;
    }

    private  void setLength(double[][] aLength){
        this._length = aLength;
    }

    private  boolean[] visit(){
        return this._visit;
    }

    private  void setVisit(boolean[] aVisit){
        this._visit = aVisit;
    }

    private  int input(){
        return this._input;
    }

    private  void setInput(int aInput){
        this._input = aInput;
    }

    private   TMapPoint[] shortestPaths(){
        return this._shortestPaths;
    }

    private void setShortestPaths(TMapPoint[] aShortestPaths){
        this._shortestPaths = aShortestPaths;
    }

    //생성자
    public ShortestPath(TMapPoint[] finalPoints, int input) {
        this.setMapView(new Map());
        this.setFinalPoint(finalPoints);
        this.setLength(null);
        this.setVisit(null);
        this.setInput(input);
        this.setShortestPaths(new TMapPoint[this.input()]);
        findShortestPaths();
    }

    //받아온 포인트에 대해서 방문 여부 초기화
    private  void initVisit() {
        this.setVisit(new boolean[this.finalPoint().length]);
        for (int i = 0; i < this.finalPoint().length; i++) {
            this.visit()[i] = false;
        }
    }

    //받아온 포인트들로 나올 수 있는 모든 거리를 삽입하여 [input][input]배열 초기화
    private  void initLength() {
        this.setLength(new double[this.input()][this.input()]);

        for (int i = 0; i < this.input(); i++) {
            for (int j = 0; j < this.input(); j++) {
                if (i == j) {
                    this.length()[i][j] = 0;
                } else {
                    this.length()[i][j] = this.findPathDistance(this.finalPoint()[i], this.finalPoint()[j]);
                }
            }
        }
    }

    //최단 경로를 찾는다 (최단 경로 순으로 포인트를 _shortestPaths에 집어넣음)
    private void findShortestPaths() {
        int length = this.input();
        initLength();
        initVisit();
        this.visit()[0] = true;
        int visit = 0;
        this.shortestPaths()[0] = this.finalPoint()[0];
        for (int i = 1; i < length; i++) {
            double shortest = DEFAULT_MAX_SIZE;
            int shortestIndex = i;
            for (int j = 0; j < length; j++) {
                if (visit != j) {
                    if (this.length()[visit][j] < shortest && !this.visit()[j]) {
                        shortest = this.length()[visit][j];
                        shortestIndex = j;
                    }
                }
            }
            this.shortestPaths()[i] = this.finalPoint()[shortestIndex];
            this.visit()[shortestIndex] = true;
            visit = shortestIndex;
        }

    }

    //최단거리 순대로 삽입되어 있는 지점 중 j에 맞춰서 반환
    public TMapPoint getI(int j) {
        return this.shortestPaths()[j];
    }

    //해당하는 두 지점의 거리를 구한다
    private double findPathDistance(TMapPoint start, TMapPoint end) {

        this.mapView().setTMapData(new TMapData());

        TMapPolyLine tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.addLinePoint(start);
        tMapPolyLine.addLinePoint(end);
        return tMapPolyLine.getDistance();

    }
}

