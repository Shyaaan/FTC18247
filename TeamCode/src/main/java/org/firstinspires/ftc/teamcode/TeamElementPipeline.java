package org.firstinspires.ftc.teamcode;
import java.util.ArrayList;
import java.util.List;


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
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvPipeline;
import org.opencv.core.Mat;


public class TeamElementPipeline extends OpenCvPipeline {
    public static String LEFT = "Left";
    OpenCvCamera camera;
    public static final String RIGHT = "Right";
    public static final String CENTER = "Center";
    public static final String RED = "Red";
    public static final String BLUE = "Blue";
    ArrayList<Mat> channels = new ArrayList<>(3);
    List<MatOfPoint> filteredContours = new ArrayList<>();
    List<MatOfPoint> contours = new ArrayList<>();
    Mat c = new Mat();
    Mat grayImg = new Mat();
    Mat hierarchy = new Mat();
    MatOfPoint2f newContour;

    String Position = null;
    String Color = null;

    Scalar white = new Scalar(255,255,255);
    Scalar red = new Scalar(255,0,0);
    private void cameraClosed(){
        ;
    }
    public Mat processFrame(Mat input){
        if (!Vision.getInstance().OpmodeStatus()){
            camera.closeCameraDeviceAsync(this::cameraClosed);
            return input;
        }
        grayImg.release();
        hierarchy.release();
        c.release();

        if (Color != null){
            ProcessChannel(Color, input);
        }
        else {
            List<MatOfPoint> RedResults = ProcessChannel(RED, input);
            List<MatOfPoint> BlueResults = ProcessChannel(BLUE,input);
        }
        drawContours(c,filteredContours,-1, white,2);
        if (filteredContours.size() > 0){
            Moments M = Imgproc.moments(filteredContours.get(0));

            double cx = M.get_m10()/M.get_m00();
            double cy = M.get_m01()/M.get_m00();

            Point Center = new Point(cx,cy);

            circle(c,Center,100,red);

            Position = CENTER;
        }
        else {
            Position = null;
        }
        filteredContours.clear();
        contours.clear();
        return c;
    }


    public String getPosition(){
        return Position;
    }
    public void setColor(String C){
        Color = C;
    }
    public String getColor(){
        return Color;
    }
    public List<MatOfPoint> ProcessChannel(String Color, Mat input){
        Core.split(input,channels);
        int ColorChannel;
        int OtherChannel;
        if (Color == RED){
            ColorChannel = 0;
        }
        else {
            ColorChannel = 2;
        }
        if (ColorChannel == 0){
            OtherChannel = 2;
        }
        else {
            OtherChannel = 0;
        }
        c = channels.get(ColorChannel);
        divide(channels.get(1),new Scalar(2),channels.get(1));
        divide(channels.get(OtherChannel),new Scalar(2),channels.get(OtherChannel));
        subtract(c,channels.get(1),c);
        subtract(c,channels.get(OtherChannel),c);

        threshold(c,grayImg,80d,255,THRESH_BINARY);

        Imgproc.findContours(grayImg,contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

        for(MatOfPoint contour : contours){
            if(contour.toArray().length > 0){
                newContour = new MatOfPoint2f(contour.toArray());
                approxPolyDP(newContour, newContour, (0.0001 * arcLength(newContour,true)), true);
                if(newContour.toArray().length >= 200){
                    filteredContours.add(new MatOfPoint(contour.toArray()));
                    break;
                }
                newContour.release();
                contour.release();
            }
        }
        return filteredContours;
    }
    public void setCamera(OpenCvCamera c){
        camera = c;
    }
}
