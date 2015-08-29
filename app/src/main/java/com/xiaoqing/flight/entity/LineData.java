package com.xiaoqing.flight.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QingYang on 15/8/10.
 */
public class LineData {

    private List<Float> datasY ;
    private ArrayList<Float> datasX;

    public List<Float> getDatasY() {
        return datasY;
    }

    public void setDatasY(List<Float> datasY) {
        this.datasY = datasY;
    }

    public ArrayList<Float> getDatasX() {
        return datasX;
    }

    public void setDatasX(ArrayList<Float> datasX) {
        this.datasX = datasX;
    }
}
