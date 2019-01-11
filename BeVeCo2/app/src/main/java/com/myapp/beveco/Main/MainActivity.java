package com.myapp.beveco.Main;

//안드로이드 라이브러리
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

//Tmap Api 라이브러리
import com.myapp.beveco.Model.Marker;
import com.myapp.beveco.R;
import com.myapp.beveco.View.Map;
import com.myapp.beveco.Model.ShortestPath;
import com.myapp.beveco.Model.Tmap;
import com.myapp.beveco.Model.ConnectTMap;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

//자바 라이브러리
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    //클래스
    private Map mapView = new Map();
    private static ShortestPath shortestPath;

    //객체
    private EditText searchText;
    private Button searchButton;
    private Button findShortest;
    private Button zoomIn;
    private Button zoomOut;
    private Button showLists;
    private Button reset;
    private Button backButton;
    private ListView searchListView;
    private ArrayAdapter<String> listAdapter;


    //선택된 포인트를 저장하는 배열
    private static TMapPoint[] _selectPoints = new TMapPoint[5];
    //검색로그에서 장소의 이름을 저장하는 리스트
    private List<String> list = new ArrayList<>();
    //검색로그에서 포인트를 저장하는 리스트
    private List<TMapPoint> tMapPoints = new ArrayList<>();
    //최단경로를 받아오는 리스트
    private static List<TMapPoint> shortestList = new ArrayList<>();

    //변수
    //여행지 입력 개수
    private static int input = 0;

    //setter/getter
    public static TMapPoint[] selectPoints(){
        return _selectPoints;
    }

    private void setSelectPoints(TMapPoint[] aSelectPoints){
        _selectPoints = aSelectPoints;
    }

    public static List<TMapPoint> ShortestPath(){
        return shortestList;
    }

    public static int getI(){
        return input;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mapView.setLayout((RelativeLayout) findViewById(R.id.main_layout_tmap));
        this.mapView.setTMapView(new TMapView(this));

        searchText =  findViewById(R.id.searchText);
        searchButton = findViewById(R.id.searchButton);
        searchListView =  findViewById(R.id.searchListView);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        findShortest = findViewById(R.id.findShortest);
        zoomIn = findViewById(R.id.zoomIn);
        zoomOut = findViewById(R.id.zoomOut);
        backButton = findViewById(R.id.backButton);
        reset =  findViewById(R.id.reset);
        showLists = findViewById(R.id.showLists);

        //지도 초기화
        findShortest.setVisibility(View.INVISIBLE);
        showLists.setVisibility(View.INVISIBLE);
        mapView.initTmap();


        searchText.setOnKeyListener(new View.OnKeyListener() {

            //searchText에서 엔터를 누를 시 검색 수행
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                    clickSearchButton();
                    return true;
                }
                return false;
            }
        });
        searchButton.setOnClickListener(this);

        findShortest.setOnClickListener(this);
        showLists.setOnClickListener(this);

        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);

        backButton.setOnClickListener(this);
        reset.setOnClickListener(this);

    }

    //버튼들을 클릭시 경우에 맞는 함수 작동
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton:
                clickSearchButton();
                break;
            case R.id.findShortest:
                clickFindShortest();
                break;
            case R.id.zoomIn:
                clickZoomIn();
                break;
            case R.id.zoomOut:
                clickZoomOut();
                break;
            case R.id.reset:
                clickReset();
                break;
            case R.id.backButton:
                clickBackbutton();
                break;
            case R.id.showLists:
                clickShowLists();
                break;
        }
    }

    //검색 버튼을 눌렀을 경우
    private void clickSearchButton() {

        String strData;
        strData = searchText.getText().toString();

        //처음 입력이 들어온 후에 바뀜
        searchText.setHint("여행지를 입력해 주세요");

        mapView.tMapData().findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {

            @Override
            public void onFindAllPOI(ArrayList poiItems) {
                //이전에 있던 값들을 전부 초기화
                tMapPoints.clear();
                list.clear();


                for (int i = 0; i < poiItems.size(); i++) {
                    TMapPOIItem item = (TMapPOIItem) poiItems.get(i);
//                    Log.d("", "POIName : " + item.getPOIName().toString() + "\n"); //DEBUG

                    //장소 이름 저장
                    list.add(item.getPOIName());
                    //포인트 저장
                    tMapPoints.add(item.getPOIPoint());
                }

//                //DEBUG 출력
//                for(int i = 0; i < list.size(); i++){
//                    System.out.println(i + " : " + list.get(i));
//                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapView.layout().setVisibility(View.GONE);
                        searchListView.setVisibility(View.VISIBLE);
                        listAdapter.clear();
                        listAdapter.addAll(list);

                        //실시간 업데이트
                        searchListView.setAdapter(listAdapter);
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        //검색 후 출력된 listView를 클릭했을 경우
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //여행지 입력이 다섯 개 이상일 경우
                if(input >= 5){
                    AlertDialog.Builder errorMsg = new AlertDialog.Builder(MainActivity.this);
                    errorMsg.setMessage("여행지는 5개까지만 입력할 수 있습니다.");
                    errorMsg.setIcon(R.mipmap.ic_launcher);
                    errorMsg.show();
                    mapView.layout().setVisibility(View.VISIBLE);
                    searchListView.setVisibility(View.GONE);
                    return;
                }

                //포인트를 저장해둔 리스트에서 포인트를 가져온다
                TMapPoint point = tMapPoints.get(position);

                //마커 객체 생성
                Marker marker = new Marker();

                //마커 객체에 포인트를 넣는다
                marker.marker().setTMapPoint(point);

                //포인트의 화면을 중점으로 잡음, zoomLevel 12
                mapView.setCenterView(point);
                mapView.setZoom(12);

                mapView.layout().setVisibility(View.VISIBLE);
                searchListView.setVisibility(View.GONE);

                //지정된 위치에 마커를 찍는다
                mapView.tMapView().addMarkerItem("marker" + input, marker.marker());
                selectPoints()[input] = point;
                input++;

                //입력이 2개 이상 들어왔을 때 shortestPath 실행 가능
                if(input >= 2){
                    findShortest.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //최단경로 찾기 버튼을 클릭했을 경우
    private void clickFindShortest(){

        findShortest.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);
        //지도의 마커를 모두 지운다
        mapView.tMapView().removeAllMarkerItem();
        //경유지 안내 버튼 생성
        showLists.setVisibility(View.VISIBLE);
        //shortestPath 객체 생성
        shortestPath = new ShortestPath(selectPoints(), input);
        shortestList.clear();

        for(int i = 0; i < input; i++){
            TMapPoint point = shortestPath.getI(i);

            //마커를 지도에 생성
            shortestList.add(point);
            TMapMarkerItem viewMarker = this.getMarker(point, i);
            mapView.tMapView().addMarkerItem("marker" + i, viewMarker);

        }

        mapView.layout().setVisibility(View.VISIBLE);
        searchListView.setVisibility(View.GONE);
    }

    //최단 경로 찾기 실행시 지도에 뜨는 마커를 표시하기 위한 함수
    private TMapMarkerItem getMarker(TMapPoint point, int Number){
        Bitmap bm;

        if(Number == 0) {
            Marker marker0 = new Marker();
            marker0.marker().setTMapPoint(point);
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.map1)).getBitmap();
            marker0.marker().setCalloutTitle("시작점");
            marker0.marker().setIcon(bm);
            marker0.marker().setVisible(TMapMarkerItem.VISIBLE);
            marker0.marker().setPosition((float)0.5,(float)1.0);
            return marker0.marker();

        }else if(Number == 1){
            Marker marker1 = new Marker();
            marker1.marker().setTMapPoint(point);
            bm =((BitmapDrawable) getResources().getDrawable(R.drawable.map2)).getBitmap();
            marker1.marker().setCalloutTitle("두번째 방문지");
            marker1.marker().setIcon(bm);
            marker1.marker().setVisible(TMapMarkerItem.VISIBLE);
            marker1.marker().setPosition((float)0.5,(float)1.0);
            return marker1.marker();

        }else if(Number == 2){
            Marker marker2 = new Marker();
            marker2.marker().setTMapPoint(point);
            bm =  ((BitmapDrawable) getResources().getDrawable(R.drawable.map3)).getBitmap();
            marker2.marker().setCalloutTitle("세번째 방문지");
            marker2.marker().setIcon(bm);
            marker2.marker().setVisible(TMapMarkerItem.VISIBLE);
            marker2.marker().setPosition((float)0.5,(float)1.0);
            return marker2.marker();

        }else if(Number == 3){
            Marker marker3 = new Marker();
            marker3.marker().setTMapPoint(point);
            bm =((BitmapDrawable) getResources().getDrawable(R.drawable.map4)).getBitmap();
            marker3.marker().setCalloutTitle("네번째 방문지");
            marker3.marker().setIcon(bm);
            marker3.marker().setVisible(TMapMarkerItem.VISIBLE);
            marker3.marker().setPosition((float)0.5,(float)1.0);
            return marker3.marker();

        }else{
            Marker marker4 = new Marker();
            marker4.marker().setTMapPoint(point);
            bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.map5)).getBitmap();
            marker4.marker().setCalloutTitle("다섯번째 방문지");
            marker4.marker().setIcon(bm);
            marker4.marker().setVisible(TMapMarkerItem.VISIBLE);
            marker4.marker().setPosition((float)0.5,(float)1.0);
            return marker4.marker();
        }

    }

    //'+'(zoomIn)버튼을 눌렀을 경우
    private void clickZoomIn(){
        //지도가 레벨 1씩 확대된다
        mapView.setZoom(this.mapView.zoom() + 1);
        mapView.zoom();
        mapView.layout().setVisibility(View.GONE);
        mapView.layout().setVisibility(View.VISIBLE);
    }

    //'-'(zoomOut)버튼을 눌렀을 경우
    private void clickZoomOut(){
        //지도가 레벨 1씩 축소된다
        mapView.setZoom(this.mapView.zoom() - 1);
        mapView.zoom();
        mapView.layout().setVisibility(View.GONE);
        mapView.layout().setVisibility(View.VISIBLE);
    }

    //'B'(backButton)을 눌렀을 경우
    private void clickBackbutton(){
        //현재 검색한 지점 중 가장 최근에 검색한 지점을 하나 삭제한다
        AlertDialog.Builder errorMsg = new AlertDialog.Builder(MainActivity.this);
        if (input >= 1) {
            errorMsg.setMessage(input + "번째 입력 여행지 삭제");
            errorMsg.setIcon(R.mipmap.ic_launcher);
            errorMsg.show();
            input--;
            if(input < 2){
                findShortest.setVisibility(View.INVISIBLE);
            }
            //마지막 좌표 마커 삭제
            mapView.tMapView().removeMarkerItem("marker" + input);

        }else{
            errorMsg.setMessage("삭제할 값이 없습니다.");
            errorMsg.setIcon(R.mipmap.ic_launcher);
            errorMsg.show();
        }
    }

    //'R'(reset)을 눌렀을 경우
    private void clickReset(){
        //현재 가지고 있는 모든 지점이 삭제된다
        AlertDialog.Builder errorMsg = new AlertDialog.Builder(MainActivity.this);
        errorMsg.setMessage("초기화 합니다");
        errorMsg.setIcon(R.mipmap.ic_launcher);
        errorMsg.show();
        input = 0;
        backButton.setVisibility(View.VISIBLE);
        findShortest.setVisibility(View.INVISIBLE);

        //모든 지점의 마커 지우기
        mapView.tMapView().removeAllMarkerItem();
        ShortestPath().clear();
        showLists.setVisibility(View.INVISIBLE);
    }

    //'길안내'(showLists)을 눌렀을 경우
    private void clickShowLists(){
        //최단 경로 찾기를 통해서 나온 포인트로 길찾기 안내
        Intent intent =  new Intent(getApplicationContext(), ConnectTMap.class);
        startActivity(intent);
    }

    //지점과 지점 사이의 라인을 그린다
//    public void pathLine(TMapPoint i, TMapPoint j) {
//        //현재 라인 하나씩 밖에 못 그린다
//
//        this.mapView.tMapData().findPathData(i, j, new TMapData.FindPathDataListenerCallback() {
//            @Override
//            public void onFindPathData(TMapPolyLine tMapPolyLine) {
//
//                mapView.tMapView().addTMapPolyLine("source" + input, tMapPolyLine);
//                tMapPolyLine.setLineColor(Color.YELLOW);
//                tMapPolyLine.setLineWidth(5);
//            }
//        });
//    }
}