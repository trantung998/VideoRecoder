package example.tung.videorecoder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final long VIDEO_MAX_SIZE = 10 * 1024 * 1024; //30 MB
    private Uri fileUri; // file url to store image/video
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "MyTestCamera";
    private VideoView videoPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnRecordVideo = (Button) findViewById(R.id.button);
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDeviceSupportCamera()) {
                    takeVideo();
                }
            }
        });

        videoPreview = (VideoView)findViewById(R.id.videoView);
        videoPreview.setVisibility(View.GONE);
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void takeVideo() {
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, VIDEO_MAX_SIZE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        try {
            startActivityForResult(takeVideoIntent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String mCurrentVideoPath;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
            if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    // video successfully recorded
                    // preview the recorded video
                    previewVideo();
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled recording
                    Toast.makeText(getApplicationContext(),
                            "User cancelled video recording", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // failed to record video
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                            .show();
                }
            }
    }


    /**
     * Previewing recorded video
     */
    private void previewVideo() {
        try {
            videoPreview.setVisibility(View.VISIBLE);
            videoPreview.setVideoPath(fileUri.getPath());
            // start playing
            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
