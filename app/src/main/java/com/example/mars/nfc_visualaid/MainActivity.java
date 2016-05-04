package com.example.mars.nfc_visualaid;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


/**
 * Programmer: Marcio Castillo
 * Email: mecastillo00@gmail.com
 * Date: May 1, 2016
 *
 * Summary: This project is intended to aid the visually impaired students
 * of Brooklyn College and direct them to the right room. By using a android
 * phone's built in NFC sensor and placing NFC tags on classroom, bathroom
 * and offices this will help navigate them inside the campus.
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    NfcAdapter nfcAdapter;
    TextView tagText;
    TextToSpeech ttSpeech;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private ImageView mPointer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tagText = (TextView) findViewById(R.id.textView);

        //check if NFC is enabled
        if(!nfcAdapter.isEnabled())
            Toast.makeText(this, "Please enable NFC", Toast.LENGTH_SHORT).show();

        //setting up the speech function
        ttSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ttSpeech.setLanguage(Locale.UK);
                }
            }
        });

        //i slowed down the speech rate a little
        //the default one is a little too fast
        ttSpeech.setSpeechRate((float) .6);

        //This is for the compass
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.imageView);
    }

    //This is called whenever the sensor values have changed.
    //When values change, the direction of the arrow will also
    //change. This will always point to magnetic north.
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        }
        else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }

        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);
            ra.setFillAfter(true);

            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
    }


    //Doesn't do a anything at the moment, but it must be added in the code.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }


    //On a pause state it is best to turn off the senors that
    //are begin used. This also saves battery life.
    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    //When coming back from a paused state you must enable the senors
    //for it to work properly
    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    //Gives your current foreground activity priority in receiving NFC events over
    //all other actvities.
    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    //Disable priority for NFC events
    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    //This is somewhat like an action listener where the app will react
    //to any nearby nfc tag
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this, "NFC intent", Toast.LENGTH_SHORT).show();

            Parcelable[] parcelable = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(parcelable != null && parcelable.length > 0){
                readTextFromMessage((NdefMessage) parcelable[0]);
            }
            else{
                Toast.makeText(this, "No ndef message found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //This will set text on the screen and will call the
    //speech function to say the text from the NFC tag
    private void readTextFromMessage(NdefMessage msg) {
        NdefRecord[] ndefRecords = msg.getRecords();

        if(ndefRecords != null && ndefRecords.length > 0){
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            tagText.setText(tagContent);
            ttSpeech.speak(tagContent, TextToSpeech.QUEUE_FLUSH, null);
        }
        else{
            Toast.makeText(this, "No ndef records found", Toast.LENGTH_SHORT).show();
        }
    }

    //returns NFC tag data into a readable string
    public String getTextFromNdefRecord(NdefRecord ndefRecord){
        String tagContent = null;
        try{
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        }catch(Exception e){
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }
}
