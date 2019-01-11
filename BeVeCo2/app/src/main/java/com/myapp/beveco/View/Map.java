package com.myapp.beveco.View;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;


public class Map {
    //지도 상에서 보이는 부분들을 모아놓은 클래스

    //지도를 생성할 수 있는 APPKey
    private String TMAP_API_KEY = "069315f9-df48-4a79-ae5e-4dd624e50495";

    //인스턴스 변수
    private RelativeLayout _layout; //지도 레이아웃 객체
    private TMapView _tMapView; //지도를 보여주는 TMapView 객체
    private TMapData _mapData; //지도의 데이터를 사용할 수 있는 TMapData 객체


    //getter/setter
    public RelativeLayout layout() {
        return this._layout;
    }

    public void setLayout(RelativeLayout newLayout) {
        this._layout = newLayout;
    }

    public TMapView tMapView() {
        return this._tMapView;
    }

    public void setTMapView(TMapView newTMapView) {
        this._tMapView = newTMapView;
    }

    public TMapData tMapData() {
        return this._mapData;
    }

    public void setTMapData(TMapData newTMapData) {
        this._mapData = newTMapData;
    }


    //생성자
    public Map() {
        this.setTMapView(null);
        this.setLayout(null);
        this.setTMapData(new TMapData());
    }

    //AppKey를 설정하는 함수
    private void AppKey() {
        this.tMapView().setSKTMapApiKey(TMAP_API_KEY);
    }


    public void initTmap() {
        /* 초기화면
         * 지도를 띄운다. */

        this.AppKey();

        //처음으로 보일 지점 지정 (충남대학교 줌 레벨 8.5로)
        double lat = 36.23; //위도
        double lng = 127.19; //경도
        this.setCenterView(lng, lat); //화면의 중심(보이는 곳)
        this.setZoom((float) 8.50);

        this.layout().addView(this.tMapView());
    }

    //포인트의 지점을 화면의 중심으로 잡는다
    public void setCenterView(TMapPoint aPoint) {
        this.tMapView().setCenterPoint(aPoint.getLongitude(), aPoint.getLatitude());
    }

    private void setCenterView(double aLng, double aLat) {
        this.tMapView().setCenterPoint(aLng, aLat);
    }

    //화면의 줌 레벨을 설정한다
    public void setZoom(float i) {
        this.tMapView().setZoom(i);
    }

    //줌 레벨 getter
    public float zoom() {
        System.out.println(this.tMapView().getZoomLevel());
        return this.tMapView().getZoomLevel();
    }

}