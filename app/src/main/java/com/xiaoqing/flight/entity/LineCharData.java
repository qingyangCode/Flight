package com.xiaoqing.flight.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by QingYang on 15/10/13.
 */
public class LineCharData implements Serializable{

    private ArrayList<WeightLimitData> weightLimitDatas;
    private ArrayList<WeightData> weightDatas;

    private float maxFlyweight;//最大起飞重量
    private float maxLandWeight;//最大着陆重量
    private float maxNofuleWeight;//最大无油重量


    public static class WeightLimitData implements Serializable {
        private float weight;
        private float weightCg1;
        private float weightCg2;

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public float getWeightCg1() {
            return weightCg1;
        }

        public void setWeightCg1(float weightCg1) {
            this.weightCg1 = weightCg1;
        }

        public float getWeightCg2() {
            return weightCg2;
        }

        public void setWeightCg2(float weightCg2) {
            this.weightCg2 = weightCg2;
        }
    }

    public static class WeightData implements Serializable {
        private float weight;
        private float weightCg;

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public float getWeightCg() {
            return weightCg;
        }

        public void setWeightCg(float weightCg) {
            this.weightCg = weightCg;
        }
    }

    public ArrayList<WeightLimitData> getWeightLimitDatas() {
        return weightLimitDatas;
    }

    public void setWeightLimitDatas(ArrayList<WeightLimitData> weightLimitDatas) {
        this.weightLimitDatas = weightLimitDatas;
    }

    public ArrayList<WeightData> getWeightDatas() {
        return weightDatas;
    }

    public void setWeightDatas(ArrayList<WeightData> weightDatas) {
        this.weightDatas = weightDatas;
    }

    public float getMaxFlyweight() {
        return maxFlyweight;
    }

    public void setMaxFlyweight(float maxFlyweight) {
        this.maxFlyweight = maxFlyweight;
    }

    public float getMaxLandWeight() {
        return maxLandWeight;
    }

    public void setMaxLandWeight(float maxLandWeight) {
        this.maxLandWeight = maxLandWeight;
    }

    public float getMaxNofuleWeight() {
        return maxNofuleWeight;
    }

    public void setMaxNofuleWeight(float maxNofuleWeight) {
        this.maxNofuleWeight = maxNofuleWeight;
    }
}
