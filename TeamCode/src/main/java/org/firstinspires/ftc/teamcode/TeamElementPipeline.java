package org.firstinspires.ftc.teamcode;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.divide;

import static org.opencv.core.Core.subtract;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.approxPolyDP;
import static org.opencv.imgproc.Imgproc.arcLength;

import static org.opencv.imgproc.Imgproc.drawContours;

import static org.opencv.imgproc.Imgproc.threshold;

import org.opencv.core.Core;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
import org.opencv.core.Mat;


public class TeamElementPipeline extends OpenCvPipeline {
    ArrayList<Mat> channels = new ArrayList<>(3);
    List<MatOfPoint> filteredContours = new ArrayList<>();
    List<MatOfPoint> contours = new ArrayList<>();
    Mat r = new Mat();
    Mat grayImg = new Mat();
    Mat hierarchy = new Mat();
    MatOfPoint2f newContour;

    Scalar white = new Scalar(255,255,255);

    //int nonZeroPixels = 0;
    public Mat processFrame(Mat input){
        Core.split(input,channels);
        r = channels.get(0);
        divide(channels.get(1),new Scalar(2),channels.get(1));
        divide(channels.get(2),new Scalar(2),channels.get(2));
        subtract(r,channels.get(1),r);
        subtract(r,channels.get(2),r);

        threshold(r,grayImg,80d,255,THRESH_BINARY);
        Imgproc.findContours(grayImg,contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        for(MatOfPoint contour : contours){
            if(contour.toArray().length > 60){
                newContour = new MatOfPoint2f(contour.toArray());
                approxPolyDP(newContour, newContour, (0.03 * arcLength(newContour,true)), true);
                if(newContour.toArray().length >= 60){
                    filteredContours.add(new MatOfPoint(contour.toArray()));
                }

            }
        }
        drawContours(r,filteredContours,-1, white,2);
        channels.clear();
        contours.clear();
        filteredContours.clear();
        return r;
    }
}
