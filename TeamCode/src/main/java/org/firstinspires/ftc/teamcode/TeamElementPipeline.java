package org.firstinspires.ftc.teamcode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.opencv.core.Core.divide;

import static org.opencv.core.Core.subtract;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.approxPolyDP;
import static org.opencv.imgproc.Imgproc.arcLength;

import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cornerEigenValsAndVecs;
import static org.opencv.imgproc.Imgproc.drawContours;

import static org.opencv.imgproc.Imgproc.drawMarker;
import static org.opencv.imgproc.Imgproc.threshold;

import org.opencv.core.Core;


import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvPipeline;
import org.opencv.core.Mat;


public class TeamElementPipeline extends OpenCvPipeline {
    public static String LEFT = "Left";
    public static String RIGHT = "Right";
    ArrayList<Mat> channels = new ArrayList<>(3);
    List<MatOfPoint> filteredContours = new ArrayList<>();
    List<MatOfPoint> contours = new ArrayList<>();
    Mat r = new Mat();
    Mat grayImg = new Mat();
    Mat hierarchy = new Mat();
    MatOfPoint2f newContour;

    String Position = "Not Found";

    Scalar white = new Scalar(255,255,255);
    Scalar red = new Scalar(255,0,0);

    public Mat processFrame(Mat input){
        grayImg.release();
        hierarchy.release();
        r.release();
        Core.split(input,channels);
        r = channels.get(0);
        divide(channels.get(1),new Scalar(2),channels.get(1));
        divide(channels.get(2),new Scalar(2),channels.get(2));
        subtract(r,channels.get(1),r);
        subtract(r,channels.get(2),r);

        threshold(r,grayImg,80d,255,THRESH_BINARY);

        Imgproc.findContours(grayImg,contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

        for(MatOfPoint contour : contours){
            if(contour.toArray().length > 0){
                newContour = new MatOfPoint2f(contour.toArray());
                approxPolyDP(newContour, newContour, (0.0001 * arcLength(newContour,true)), true);
                if(newContour.toArray().length >= 60){
                    filteredContours.add(new MatOfPoint(contour.toArray()));
                    break;
                }
                newContour.release();
                contour.release();
            }
        }

        drawContours(r,filteredContours,-1, white,2);
        if (filteredContours.size() > 0){
            Moments M = Imgproc.moments(filteredContours.get(0));

            double cx = M.get_m10()/M.get_m00();
            double cy = M.get_m01()/M.get_m00();

            Point Center = new Point(cx,cy);

            circle(r,Center,10,red);

            if (cx < r.width()/2.0) {
                Position = LEFT;
            }
            else {
                Position = RIGHT;
            }
        }
        else {
            Position = null;
        }

        for (MatOfPoint m : filteredContours) {
            m.release();
        }

        for (MatOfPoint m : contours){
            m.release();
        }

        channels.clear();
        contours.clear();
        filteredContours.clear();

        return r;
    }


    public String getPosition(){
        if (Position != null) {
            return Position;
        }
        else {
            return null;
        }
    }
}
