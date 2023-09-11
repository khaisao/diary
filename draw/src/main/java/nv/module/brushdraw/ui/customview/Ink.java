package nv.module.brushdraw.ui.customview;


import java.util.ArrayList;
import java.util.List;

public class Ink {
    private List<PointXY> points;
    private List<Float> times;


    public Ink() {
        this.points = new ArrayList<>();
        this.times = new ArrayList<>();
    }

    public Ink(List<PointXY> points, List<Float> times) {
        this.points = points;
        this.times = times;
    }

    public List<PointXY> getPoints() {
        return points;
    }

    public void setPoints(List<PointXY> points) {
        this.points = points;
    }

    public List<Float> getTimes() {
        return times;
    }

    public void setTimes(List<Float> times) {
        this.times = times;
    }

    public void clearPoints() {
        if (points != null) points.clear();
    }

    public void addPoint(float x, float y){
        points.add(new PointXY(x,y));
    }

    public void addTime(float time){
        times.add(time);
    }

    public void clear(){
        points.clear();
        times.clear();
    }
}
