package com.myapp.beveco.Model;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.myapp.beveco.Main.MainActivity;
import com.myapp.beveco.R;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectTMap extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //인스턴스 변수
    private Tmap tMap = new Tmap();
    private ListView findPathListView;
    private ArrayAdapter listAdapter;
    //최단경로찾기로 완성된 포인트의 리스트
    private List<TMapPoint> getPoint = MainActivity.ShortestPath();
    //화면에 띄울 정보를 가지는 리스트
    public ArrayList<String> list = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        //리스트에 화면에 띄울 정보 (n번째 방문지)를 삽입
        for (int i = 0; i < getPoint.size(); i++) {
            list.add(i + 1 + "번째 방문지");
        }
        list.add("총 방문지 안내");

        //리스트 뷰의 실시간 업데이트
        findPathListView = (ListView) findViewById(R.id.findPathListView);
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        findPathListView.setAdapter(listAdapter);

        //listView의 목록을 클릭했을 때 실행되는 함수
        findPathListView.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }

    //listView의 목록을 클릭했을 경우
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //Tmap 앱과 연동되어 길안내를 수행할 수 있게 한다

        tMap.setTMapTapi(new TMapTapi(this));
        tMap.setSKTMap();

        boolean isTmapApp = tMap.tMapTapi().isTmapApplicationInstalled();

        //Tmap이 설치했는지 확인
        if (isTmapApp) {
            if (position < getPoint.size()) {
                //설치 되었다면 listView의 목록 중 클릭된 부분의 포인트를 가져와 길찾기 수행
                //현재위치에서 n번째 위치로 길안내
                tMap.tMapTapi().invokeRoute("dest", (float) getPoint.get(position).getLongitude(), (float) getPoint.get(position).getLatitude());
            } else if(position == getPoint.size()){
                //현재 위치에서 마지막위치까지로 경유지 포함 길안내
                HashMap pathInfo = new HashMap();
                if(getPoint.size()==5){//입력값이 4개 이하일때만 정상적인 길안내가 가능, 에러처리
                    AlertDialog.Builder errorMsg = new AlertDialog.Builder(this);
                    errorMsg.setMessage("입력한 좌표가 5개 이상이라서, 5번째 방문지는 안내되지 않습니다.");//에러메시지 출력
                    errorMsg.setIcon(R.mipmap.ic_launcher);
                    errorMsg.show();
                    pathInfo.put("rGoName", "도착지");//도착지를 4번째 방문지로 설정
                    pathInfo.put("rGoX", Double.toString(getPoint.get(getPoint.size()-2).getLongitude()));
                    pathInfo.put("rGoY", Double.toString(getPoint.get(getPoint.size()-2).getLatitude()));
                }
                else {//4개 이하일경우
                    pathInfo.put("rGoName", "도착지");//도착지를 마지막 방문지로 설정
                    pathInfo.put("rGoX", Double.toString(getPoint.get(getPoint.size() - 1).getLongitude()));
                    pathInfo.put("rGoY", Double.toString(getPoint.get(getPoint.size() - 1).getLatitude()));
                }
                pathInfo.put("rStName", "출발지");//출발지 설정, 맨 첫번째 방문지로
                pathInfo.put("rStX", Double.toString(getPoint.get(0).getLongitude()));
                pathInfo.put("rStY", Double.toString(getPoint.get(0).getLatitude()));
                if(getPoint.size() == 5){//입력값이 5개라면 4번째까지만 반복해야함
                    for(int i = 1; i < getPoint.size()-2; i++) {
                        pathInfo.put("rV"+i+"Name", (i+1)+"번째 방문지");
                        pathInfo.put("rV"+i+"X", Double.toString(getPoint.get(i).getLongitude()));
                        pathInfo.put("rV"+i+"Y", Double.toString(getPoint.get(i).getLatitude()));
                    }
                }else {//입력값이 4개이하일경우
                    for (int i = 1; i < getPoint.size() - 1; i++) {
                        pathInfo.put("rV" + i + "Name", (i + 1) + "번째 방문지");
                        pathInfo.put("rV" + i + "X", Double.toString(getPoint.get(i).getLongitude()));
                        pathInfo.put("rV" + i + "Y", Double.toString(getPoint.get(i).getLatitude()));
                    }
                }
                tMap.tMapTapi().invokeRoute(pathInfo);

            }
        }else {
            //어플이 설치되지 않았을 경우 오류 메세지 출력
            AlertDialog.Builder errorMsg = new AlertDialog.Builder(this);
            errorMsg.setMessage("TMap이 설치되어 있지 않아서, 길을 찾을 수 없습니다");
            errorMsg.setIcon(R.mipmap.ic_launcher);
            errorMsg.show();
        }

    }
}


