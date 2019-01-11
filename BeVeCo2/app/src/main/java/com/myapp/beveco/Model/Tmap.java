package com.myapp.beveco.Model;

import com.skt.Tmap.TMapTapi;

public class Tmap {
    //Tmap과 연동하는 함수를 모아놓은 클래스

    //앱 키 상수
    private final String TMAP_API_KEY = "069315f9-df48-4a79-ae5e-4dd624e50495";

    //인스턴스 변수
    private TMapTapi _TMapTapi;

    //setter/getter
    public void setTMapTapi(TMapTapi aTApi){
        this._TMapTapi = aTApi;
    }

    public TMapTapi tMapTapi(){
        return this._TMapTapi;
    }

    //생성자
    public Tmap(){
        this.setTMapTapi(null);
    }

    //Tmap연동시 필요한 앱키를 설정하는 함수
    public void setSKTMap(){
        this.tMapTapi().setSKTMapAuthentication(TMAP_API_KEY);
    }
}