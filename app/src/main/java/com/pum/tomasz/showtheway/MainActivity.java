package com.pum.tomasz.showtheway;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnTouchListener,TextWatcher{

    public static final int MAX_LATITUDE_VALUE = 90;
    public static final int MAX_LONGITUDE_VALUE = 180;
    private MyCompass compassView;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private String prevLatitudeString;
    private String prevLongitudeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        compassView = (MyCompass) findViewById(R.id.compass_view);
        compassView.setOnTouchListener(this);

        latitudeEditText = (EditText) findViewById(R.id.latitude_edit_text);
        longitudeEditText = (EditText) findViewById(R.id.longitude_edit_text);
        latitudeEditText.addTextChangedListener(this);
        longitudeEditText.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        compassView.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compassView.close();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                confirmNewDestinationCoordinates();
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        confirmNewDestinationCoordinates();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmNewDestinationCoordinates(){
        String latString = latitudeEditText.getText().toString();
        String longString = longitudeEditText.getText().toString();

        //Update destination when any of text is not empty and if one of them changed
        if( (!latString.isEmpty() && !longString.isEmpty()) &&
                (!latString.equals(prevLatitudeString) || !longString.equals(prevLongitudeString))){
            try {
                float latitude = new Float(latString);
                float longitude = new Float(longString);

                if(latitude> MAX_LATITUDE_VALUE || latitude<-MAX_LATITUDE_VALUE ||
                        longitude> MAX_LONGITUDE_VALUE || longitude<-MAX_LONGITUDE_VALUE){
                    Toast.makeText(this, R.string.coordinates_out_of_range_msg,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d("Tomek", "Update new destination: "+ latString+","+longString);
                Location destLoc = new Location("destination");
                destLoc.setLatitude(latitude);
                destLoc.setLongitude(longitude);
                compassView.setDestinationLocation(destLoc);
            }catch (NumberFormatException e){
                Log.d("Tomek","Wrong value formatting");
                Toast.makeText(this, R.string.new_msg_wrong_value_format,
                        Toast.LENGTH_LONG).show();
            }
            prevLatitudeString = latString;
            prevLongitudeString = longString;
        }
    }

}
