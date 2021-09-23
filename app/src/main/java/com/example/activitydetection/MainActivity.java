package com.example.activitydetection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor mAcc;
    private Sensor mGyro;
    private Sensor mMeganut;
    private Sensor mLacc;
    private double[] data = new double[12];
    private boolean[] i = new boolean[4];
    private J48 model;
    private DataInstance ins;
    private ArrayList<String> list = new ArrayList<>();

    private SensorEventListener mAccListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            data[0] = sensorEvent.values[0];
            data[1] = sensorEvent.values[1];
            data[2] = sensorEvent.values[2];
            i[0] = true;
            boolean isNotFull = false;
            for (boolean j:i) {
                if(!j){
                    isNotFull = true;
                }
            }
            if(!isNotFull){
                try {
                    list.add(ins.classify(ins.createInstance(data),model));
                    if (list.size() == 100){
                        System.out.println(mostCommon(list));
                        list = new ArrayList<>();
                    }
                    data = new double[12];
                    clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

                }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    public static <String> String mostCommon(List<String> list) {
        Map<String, Integer> map = new HashMap<>();

        for (String t : list) {
            Integer integer = map.get(t);
            map.put(t, integer == null ? 1 : integer + 1);
        }

        Map.Entry<String, Integer> max = null;

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }

    private SensorEventListener mGyroListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            data[6] = sensorEvent.values[0];
            data[7] = sensorEvent.values[1];
            data[8] = sensorEvent.values[2];
            i[2] = true;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    private SensorEventListener mMeganutListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            data[9] = sensorEvent.values[0];
            data[10] = sensorEvent.values[1];
            data[11] = sensorEvent.values[2];
            i[3] = true;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    private SensorEventListener mLaccListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            data[3] = sensorEvent.values[0];
            data[4] = sensorEvent.values[1];
            data[5] = sensorEvent.values[2];
            i[1] = true;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clear();
        ins = new DataInstance();
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        System.out.println("a");
        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMeganut = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mLacc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        InputStream in = getResources().openRawResource(R.raw.newmodeljfe);
        try {
            model = (J48) (new ObjectInputStream(in)).readObject();
            System.out.println("model created");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(mGyroListener, mGyro, 20000, 20000);
        sensorManager.registerListener(mAccListener, mAcc, 20000, 20000);
        sensorManager.registerListener(mMeganutListener, mMeganut, 20000, 20000);
        sensorManager.registerListener(mLaccListener, mLacc, 20000, 20000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(mGyroListener);
        sensorManager.unregisterListener(mAccListener);
        sensorManager.unregisterListener(mMeganutListener);
        sensorManager.unregisterListener(mLaccListener);
    }

    public void clear(){
        for (int j = 0; j < i.length; j++) {
            i[j] =false;
        }
    }
}

    class DataInstance{
        private Attribute ax, ay, az;
        private Attribute lax, lay, laz;
        private Attribute gx, gy, gz;
        private Attribute mx, my, mz;
        private Attribute activity;
        private ArrayList classval;
        private ArrayList attributes;
        private Instances dataRaw;
        //Time,Ax,Ay,Az,Lax,Lay,Laz,Gx,Gy,Gz,Mx,My,Mz,Activity
        public DataInstance(){
            ax = new Attribute("Ax");
            ay = new Attribute("Ay");
            az = new Attribute("Az");
            lax = new Attribute("Lax");
            lay = new Attribute("Lay");
            laz = new Attribute("Laz");
            gx = new Attribute("Gx");
            gy = new Attribute("Gy");
            gz = new Attribute("Gz");
            mx = new Attribute("Mx");
            my = new Attribute("My");
            mz = new Attribute("Mz");

            attributes = new ArrayList();
            classval = new ArrayList();
            classval.add("walking");
            classval.add("standing");
            classval.add("jogging");
            classval.add("sitting");
            classval.add("biking");
            classval.add("upstairs");
            classval.add("downstairs");

            attributes.add(ax);
            attributes.add(ay);
            attributes.add(az);
            attributes.add(lax);
            attributes.add(lay);
            attributes.add(laz);
            attributes.add(gx);
            attributes.add(gy);
            attributes.add(gz);
            attributes.add(mx);
            attributes.add(my);
            attributes.add(mz);
            attributes.add(new Attribute("activity", classval));

            dataRaw = new Instances("Test", attributes, 0);
            dataRaw.setClassIndex(dataRaw.numAttributes() -1);

        }
        public Instances createInstance(double[] data){
            dataRaw.clear();
            dataRaw.add(new DenseInstance(1.0, data));
            return dataRaw;
        }
        public String classify(Instances ins, J48 model) throws Exception {
            return (String) classval.get((int) model.classifyInstance(ins.firstInstance()));
        }


    }