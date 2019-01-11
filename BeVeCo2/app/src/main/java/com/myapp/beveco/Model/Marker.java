package com.myapp.beveco.Model;

import com.skt.Tmap.TMapMarkerItem;

public class Marker {

    private TMapMarkerItem _marker; //지도에서 마커를 띄울 수 있는 객체

    //getter/setter
    public TMapMarkerItem marker(){
        return this._marker;
    }

    private void setMarker(TMapMarkerItem aMarker){
        this._marker = aMarker;
    }

    //생성자
    public Marker(){
        this.setMarker(new TMapMarkerItem());
    }

}
