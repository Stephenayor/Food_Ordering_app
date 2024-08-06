package com.example.yummy.utils.imagepicker;

import static com.github.florent37.runtimepermission.RuntimePermission.askPermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.yummy.utils.AppConstants;
import com.example.yummy.utils.Tools;
import com.github.florent37.runtimepermission.BuildConfig;
import com.github.florent37.runtimepermission.PermissionResult;
import com.github.florent37.runtimepermission.RuntimePermission;
import com.github.florent37.runtimepermission.callbacks.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.zelory.compressor.Compressor;

/**
 * Created by stephen on 03/08/2024.   dd/mm/yyyy
 */

public class ImagePickerHelper {

    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 3;
    public static final int LOAD_GALLERY = 2;
    public static final int compressionMaxWidth = 640;
    public static final int compressionMaxHeight = 480;
    public static final int compressionQuality = 75;
    public static final Bitmap.CompressFormat compressionFormat = Bitmap.CompressFormat.JPEG;
    private final String TAG = ImagePickerHelper.class.getSimpleName();
    private final ActivityResultLauncher<Intent> someActivityResultLauncher;
    private final ActivityResultLauncher<Uri> requestImageCaptureActivityResultLauncher;
    //private Uri photoURI;
    private final Context context;
    //    Context context;
    FragmentActivity activity;
    Fragment fragment;
    private Bitmap imageBitmap;
    private String imagePath = "";
    private File imageFile;
    private boolean frontCamera = false;
    private boolean isFromCamera = false;

    public ImagePickerHelper(FragmentActivity activity, ActivityResultLauncher<Intent> someActivityResultLauncher,
                             ActivityResultLauncher<Uri> requestImageCaptureActivityResultLauncher) {
        this.activity = activity;
        this.context = activity;
        this.someActivityResultLauncher = someActivityResultLauncher;
        this.requestImageCaptureActivityResultLauncher = requestImageCaptureActivityResultLauncher;
    }


    public ImagePickerHelper(Fragment fragment, ActivityResultLauncher<Intent> someActivityResultLauncher,
                             ActivityResultLauncher<Uri> requestImageCaptureActivityResultLauncher) {
        this.fragment = fragment;
        this.context = fragment.requireActivity();
        this.someActivityResultLauncher = someActivityResultLauncher;
        this.requestImageCaptureActivityResultLauncher = requestImageCaptureActivityResultLauncher;
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String imagePath,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        }
        return bm;
    }

    private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
        int rotation = 0;
        if (isCamera) {
//            rotation = getRotationFromCamera(context, imageUri);
        } else {
//            rotation = getRotationFromGallery(context, imageUri);
        }
//        MultiLog.e("", "Image rotation: " + rotation);
        return rotation;
    }

    public String getImageAsBase64String() {
        return Tools.Companion.convertBitmapToBase64(getImageBitmap());
    }

    public void setFrontCamera(boolean frontCamera) {
        this.frontCamera = frontCamera;
    }

    public void selectImageDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library"};

        Context context = null;
        if (activity != null) {
            //Toast.makeText(activity, "Activity context", Toast.LENGTH_SHORT).show();
            context = activity;
        } else if (fragment != null) {
            //Toast.makeText(fragment.getContext(), "fragment context", Toast.LENGTH_SHORT).show();
            context = fragment.getContext();
        }
        try {
            if (context != null) {
                //Toast.makeText(context, "Select image dialog 2, context not null", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setTitle("Add Photo");
                alertBuilder.setItems(items, (dialog, item) -> {
                    if (items[item].equals("Take Photo")) {
                        //take photo
                        takePhoto();
                    } else if (items[item].equals("Choose from Library")) {
                        loadFromGallery();

                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                });
                if ((activity != null && !activity.isFinishing()) || (fragment != null && !fragment.isRemoving())) {
                    // Show your dialog here
                    alertBuilder.show();
                }


            }
        } catch (Exception e) {
            //Toast.makeText(context, "Select image dialog 2,exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public File getImageFile() {
        return imageFile;
    }

    public void selectImageFromGallery() {
        loadFromGallery();
    }

    private boolean greaterThanApiLevel32() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    private boolean greaterThanApiLevel28() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    private void showPermissionDeniedDialog() {
//        Tools.openSuccessErrorDialog(activity, "You have denied the permission to use this feature.",
//                "Permission Denied", false);
    }

    private void performTakePhotoAction() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
//
            //Log.d("ImagePickerDelly", "==>"+photoFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(activity,
                    BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            try {
                if (requestImageCaptureActivityResultLauncher != null)
                    requestImageCaptureActivityResultLauncher.launch(photoURI);
            } catch (Exception e) {
                e.printStackTrace();
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "" + e.getCause());
                }
            }
        }

    }

    private void triggerGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                .Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);
    }

    public void takePhoto() {
//        if (fragment != null) {
        if (greaterThanApiLevel32()) {
            requestTakePhotoPermissions(fragment, activity, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA);
        } else {
            if (greaterThanApiLevel28()) {
                requestTakePhotoPermissions(fragment, activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                /**
                 askPermission(fragment, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                 .ask(new PermissionListener() {
                @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                performTakePhotoAction();
                //                                File photoFile = null;
                //                                try {
                //                                    photoFile = createImageFile();
                //                                    //Log.d("ImagePickerDelly", "==>"+photoFile);
                //                                } catch (IOException ex) {
                //                                    ex.printStackTrace();
                //                                }
                //                                // Continue only if the File was successfully created
                //                                if (photoFile != null) {
                //
                //                                    Uri photoURI = FileProvider.getUriForFile(fragment.getActivity(),
                //                                            BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                //                                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //                                    try {
                //                                        requestImageCaptureActivityResultLauncher.launch(photoURI);
                //                                    } catch (Exception e) {
                //                                        e.printStackTrace();
                //                                        if (BuildConfig.DEBUG) {
                //                                            Log.e(TAG, "" + e.getCause());
                //                                        }
                //                                    }
                //                                }
                }

                @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                showPermissionDeniedDialog();
                }
                });**/

            } else {
                requestTakePhotoPermissions(fragment, activity, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                /**
                 askPermission(fragment, Manifest.permission.READ_EXTERNAL_STORAGE,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                 .ask(new PermissionListener() {
                @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                performTakePhotoAction();
                //                                File photoFile = null;
                //                                try {
                //                                    photoFile = createImageFile();
                //                                    //Log.d("ImagePickerDelly", "==>"+photoFile);
                //                                } catch (IOException ex) {
                //                                    ex.printStackTrace();
                //                                }
                //                                // Continue only if the File was successfully created
                //                                if (photoFile != null) {
                //
                //                                    Uri photoURI = FileProvider.getUriForFile(fragment.getActivity(),
                //                                            BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                //                                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //                                    try {
                //                                        requestImageCaptureActivityResultLauncher.launch(photoURI);
                //                                    } catch (Exception e) {
                //                                        e.printStackTrace();
                //                                        if (BuildConfig.DEBUG) {
                //                                            Log.e(TAG, "" + e.getCause());
                //                                        }
                //                                    }
                //                                }
                }

                @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                showPermissionDeniedDialog();
                }
                });
                 }
                 }
                 } else if (activity != null) {
                 if (greaterThanApiLevel32()) {
                 requestTakePhotoPermissions(fragment, activity, Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES);
                 /**
                 askPermission(activity, Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
                 .ask(new PermissionListener() {
                @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                performTakePhotoAction();
                }

                @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                showPermissionDeniedDialog();
                }
                });
                 } else {

                 if (greaterThanApiLevel28()) {
                 requestTakePhotoPermissions(fragment, activity, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);
                 /**
                 askPermission(activity, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                 .ask(new PermissionListener() {
                @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                performTakePhotoAction();
                //                                File photoFile = null;
                //                                try {
                //                                    photoFile = createImageFile();
                //                                    //Log.d("ImagePickerDelly", "==>"+photoFile);
                //                                } catch (IOException ex) {
                //                                    ex.printStackTrace();
                //                                }
                //                                // Continue only if the File was successfully created
                //                                if (photoFile != null) {
                //
                //                                    Uri photoURI = FileProvider.getUriForFile(activity,
                //                                            BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                //                                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //                                    try {
                //                                        requestImageCaptureActivityResultLauncher.launch(photoURI);
                //                                    } catch (Exception e) {
                //                                        e.printStackTrace();
                //                                        if (BuildConfig.DEBUG) {
                //                                            Log.e(TAG, "" + e.getCause());
                //                                        }
                //                                    }
                //                                }
                }

                @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                System.out.println("Got here");
                for (int i = 0; i < denied.size(); i++) {
                System.out.println("Denied " + i + " => " + denied.get(i));
                }
                for (int i = 0; i < foreverDenied.size(); i++) {
                System.out.println("ForeverDenied " + i + " => " + foreverDenied.get(i));
                }
                showPermissionDeniedDialog();
                }
                });
                 } else {
                 requestTakePhotoPermissions(fragment, activity, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE);
                 /**
                 askPermission(activity, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE)
                 .ask(new PermissionListener() {
                @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                performTakePhotoAction();
                //                                File photoFile = null;
                //                                try {
                //                                    photoFile = createImageFile();
                //                                    //Log.d("ImagePickerDelly", "==>"+photoFile);
                //                                } catch (IOException ex) {
                //                                    ex.printStackTrace();
                //                                }
                //                                // Continue only if the File was successfully created
                //                                if (photoFile != null) {
                //
                //                                    Uri photoURI = FileProvider.getUriForFile(activity,
                //                                            BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                //                                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //                                    try {
                //                                        requestImageCaptureActivityResultLauncher.launch(photoURI);
                //                                    } catch (Exception e) {
                //                                        e.printStackTrace();
                //                                        if (BuildConfig.DEBUG) {
                //                                            Log.e(TAG, "" + e.getCause());
                //                                        }
                //                                    }
                //                                }
                }

                @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                for (int i = 0; i < denied.size(); i++) {
                System.out.println("Denied " + i + " => " + denied.get(i));
                }
                for (int i = 0; i < foreverDenied.size(); i++) {
                System.out.println("ForeverDenied " + i + " => " + foreverDenied.get(i));
                }
                showPermissionDeniedDialog();
                }
                });
                 }**/
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image imageFile name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    public Bitmap getBitmapFromPhotoPath() {
        if (imagePath == null) return null;

        imageFile = getCompressedImage(imagePath);
        if (imageFile == null) return null;

        imagePath = imageFile.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        return imageBitmap = BitmapFactory.decodeFile(imagePath, options);

    }

    /*
        matrix.postRotate(90F);

            matrix.setRotate(90F);
     */

    // TODO: 19/10/2017 Handle the request permission callback from the calling activity
    public void loadFromGallery() {
//        if (fragment != null) {

        if (greaterThanApiLevel32()) {
            requestGalleryPermissions(fragment, activity, Manifest.permission.READ_MEDIA_IMAGES);
            /**askPermission(fragment, Manifest.permission.READ_MEDIA_IMAGES)
             .ask(new PermissionListener() {
            @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
            triggerGalleryIntent();
            //  fragment.startActivityForResult(intent, LOAD_GALLERY);
            }

            @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
            showPermissionDeniedDialog();
            }
            });**/
        } else {
            if (greaterThanApiLevel28()) {
                requestGalleryPermissions(fragment, activity, Manifest.permission.READ_EXTERNAL_STORAGE);
                /**askPermission(fragment, Manifest.permission.READ_EXTERNAL_STORAGE)
                 .ask(new PermissionListener() {
                @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                triggerGalleryIntent();
                //                                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                //                                        .Images.Media.EXTERNAL_CONTENT_URI);
                //                                someActivityResultLauncher.launch(intent);
                //  fragment.startActivityForResult(intent, LOAD_GALLERY);
                }

                @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                showPermissionDeniedDialog();
                //                                Tools.openSuccessErrorDialog(fragment.getActivity(), "You have denied the permission to use this feature.",
                //                                        "Permission Denied", false);
                }
                });**/
            } else {
                requestGalleryPermissions(fragment, activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                /**
                 askPermission(fragment, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                 .ask(new PermissionListener() {
                @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                triggerGalleryIntent();
                //                                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                //                                        .Images.Media.EXTERNAL_CONTENT_URI);
                //                                someActivityResultLauncher.launch(intent);
                //  fragment.startActivityForResult(intent, LOAD_GALLERY);
                }

                @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                showPermissionDeniedDialog();
                //                                Tools.openSuccessErrorDialog(fragment.getActivity(), "You have denied the permission to use this feature.",
                //                                        "Permission Denied", false);
                }
                });**/
            }
        }/**
         } else if (activity != null) {
         if (greaterThanApiLevel32()) {
         requestGalleryPermissions(fragment, activity, Manifest.permission.READ_MEDIA_IMAGES);
         /** askPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
         .ask(new PermissionListener() {
        @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
        triggerGalleryIntent();
        // activity.startActivityForResult(intent, LOAD_GALLERY);
        }

        @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
        showPermissionDeniedDialog();
        }
        });
         } else {
         if (greaterThanApiLevel28()) {
         requestGalleryPermissions(fragment, activity, Manifest.permission.READ_EXTERNAL_STORAGE);
         /**askPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
         .ask(new PermissionListener() {
        @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
        triggerGalleryIntent();
        //                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore
        //                                    .Images.Media.EXTERNAL_CONTENT_URI);
        //
        //                            someActivityResultLauncher.launch(intent);
        // activity.startActivityForResult(intent, LOAD_GALLERY);
        }

        @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
        //                            Tools.openSuccessErrorDialog(activity, "You have denied the permission to use this feature.",
        //                                    "Permission Denied", false);
        showPermissionDeniedDialog();
        }
        });
         } else {
         requestGalleryPermissions(fragment, activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
         /**askPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
         .ask(new PermissionListener() {
        @Override public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
        triggerGalleryIntent();
        //                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore
        //                                    .Images.Media.EXTERNAL_CONTENT_URI);
        //
        //                            someActivityResultLauncher.launch(intent);
        // activity.startActivityForResult(intent, LOAD_GALLERY);
        }

        @Override public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
        //                            Tools.openSuccessErrorDialog(activity, "You have denied the permission to use this feature.",
        //                                    "Permission Denied", false);
        showPermissionDeniedDialog();
        }
        });
         }
         }
         }**/
    }

    public Bitmap processImage(int intentResultCode, int intentRequestCode, Intent data) {
        BitmapFactory.Options bitmapOptions = null;

        if (intentResultCode == Activity.RESULT_OK) {


            if (intentRequestCode == REQUEST_IMAGE_CAPTURE) {
                isFromCamera = true;

                bitmapOptions = new BitmapFactory.Options();

                bitmapOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, bitmapOptions);

                // Calculate inSampleSize
                bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions, 700, 1000);

                // Decode bitmap with inSampleSize set
                bitmapOptions.inJustDecodeBounds = false;
                if (imagePath != null) {
                    imageBitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
                }
//                   f.delete();
                try {
                    //Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);
                    imagePath = imageFile.getPath();
//                    File imageFile = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    if (imageFile != null) {
                        try (OutputStream outFile = new FileOutputStream(imageFile)) {
                            if (outFile != null) {
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outFile);

                                outFile.flush();
                            }
                        }
                        // outFile.close();  // Not necessary since we are using a try-with-resources block
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "" + npe.getCause());
                    }
                    npe.printStackTrace();
                }

            } else if (intentRequestCode == LOAD_GALLERY) {

                try {
                    Context context = null;
                    if (activity != null) {
                        context = activity;
                    } else if (fragment != null) {
                        context = fragment.getContext();
                    }

                    Uri pickedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = null;
                    if (context != null) {
                        if (pickedImage != null) {
                            cursor = context.getContentResolver().query(pickedImage, filePath,
                                    null, null, null);
                        }
                    }
                    if (cursor != null) {
                        cursor.moveToFirst();
                        imagePath = cursor.getString(cursor.getColumnIndexOrThrow(filePath[0]));

                        bitmapOptions = new BitmapFactory.Options();
                        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        bitmapOptions.inJustDecodeBounds = true;

                        // Decode bitmap with inSampleSize set
                        BitmapFactory.decodeFile(imagePath, bitmapOptions);
                        bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions, 700, 1000);
                        bitmapOptions.inJustDecodeBounds = false;
                        if (imagePath != null) {
                            imageBitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
                        }
                        // At the end remember to close the cursor or you will end with the RuntimeException!
                        cursor.close();
                    }
                } catch (OutOfMemoryError | NullPointerException ome) {
//                    MultiLog.debug("ImagePickerHelper", "This can be optimised in the future", ome);
                }
            }
        }

        // creating a new compressed image file and decoding a bitmap from the compressed image
        if (imagePath != null) {
            imageFile = getCompressedImage(new File(imagePath).getAbsolutePath());

            imagePath = imageFile.getAbsolutePath();
            imageBitmap = BitmapFactory.decodeFile(imagePath);
        }
        return imageBitmap;
    }

    public Bitmap resizedBitmap(int newWidth, int newHeight, Bitmap imageBitmap, boolean isNecessaryToKeepOrig) {
        if (imageBitmap != null) {
            ////Log.d("kolo", "imageBitmap not null");

            int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP 45* 4
//            matrix.postRotate(180);
//            matrix.setRotate(180);
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, width, height, matrix,
                    true);
            if (!isNecessaryToKeepOrig) {
                imageBitmap.recycle();
                imageBitmap = null;
            }

            return resizedBitmap;
        }
        ////Log.d("kolo", "imageBitmap null");

        return null;
    }

    public Bitmap getResizedBitmap(int newWidth, int newHeight, boolean isNecessaryToKeepOrig, boolean isCamera) {
        if (imageBitmap != null) {
            ////Log.d("kolo", "imageBitmap not null");

            int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
//            matrix.postRotate(90);
//            matrix.setRotate(90);
            matrix.postScale(scaleWidth, scaleHeight);

            if (isCamera) {
                matrix.postRotate(90F);
                matrix.setRotate(90F);
            }

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, width, height, matrix,
                    false);
            if (!isNecessaryToKeepOrig) {
                imageBitmap.recycle();
                imageBitmap = null;
            }

            return resizedBitmap;
        }
        ////Log.d("kolo", "imageBitmap null");

        return null;
    }

    public Bitmap getResizedBitmap(int newWidth, int newHeight, boolean isNecessaryToKeepOrig) {
        if (imageBitmap != null) {
            ////Log.d("kolo", "imageBitmap not null");

            int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
//            matrix.postRotate(90);
//            matrix.setRotate(90);
            matrix.postScale(scaleWidth, scaleHeight);


            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, width, height, matrix,
                    false);
            if (!isNecessaryToKeepOrig) {
                imageBitmap.recycle();
                imageBitmap = null;
            }

            return resizedBitmap;
        }
        ////Log.d("kolo", "imageBitmap null");

        return null;
    }

    public Bitmap processIntentData(Intent data) {

        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(data.getData());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //
    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getImagePath() {
        return imagePath;
    }

    //    /**
//     * @param context
//     * @param imageFile
//     * @return
//     */
    /*private static int getRotationFromCamera(Context context, Uri imageFile) {
        int rotate = 0;
        try {

            context.getContentResolver().notifyChange(imageFile, null);

            InputStream in = context.getApplicationContext().getContentResolver().openInputStream(imageFile);

            if (in != null){
                ExifInterface exifInterface = new ExifInterface(in);

                ExifInterface exif = new ExifInterface(imageFile.getPath());
                int orientation = exif.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
                in.close();
            }else {
                MultiLog.e("ImagePicker Helper", "Input Stream Is Null");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static int getRotationFromGallery(Context context, Uri imageUri) {
        int result = 0;
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception e) {
            //Do nothing
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }//End of try-catch block
        return result;
    }


    private static int getOrientation(Context context, Uri imageUri) {
        int orientation = 0;
        try {

            ExifInterface exif = new ExifInterface(imageUri.getPath());
            orientation = exif.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        } catch (Exception e) { e.printStackTrace(); }
        return orientation;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public File getCompressedImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            // Handle the case where imagePath is null or empty
            throw new IllegalArgumentException("Image path is null or empty");
        }

        File actualImage = new File(imagePath);

        try {
            return new Compressor(context)
                    .setMaxWidth(compressionMaxWidth)
                    .setMaxHeight(compressionMaxHeight)
                    .setQuality(compressionQuality)
                    .setCompressFormat(compressionFormat)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(actualImage);

        } catch (IOException e) {
            // Handle IOException (e.g., log error, return original file)
            e.printStackTrace();
            return actualImage;
        } catch (NullPointerException e) {
            // Handle NullPointerException (e.g., log error, return original file)
            e.printStackTrace();
            return actualImage;
        }
    }

    private void requestGalleryPermissions(Fragment fragment, FragmentActivity activity, String... permissions) {
        System.out.println("requestGalleryPermissions: gallery");
        if (fragment != null) {
            askPermission(fragment, permissions)
                    .ask(new PermissionListener() {
                        @Override
                        public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                            triggerGalleryIntent();
                            //  fragment.startActivityForResult(intent, LOAD_GALLERY);
                        }

                        @Override
                        public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                            showPermissionDeniedDialog();
                        }
                    });
        } else if (activity != null) {
            askPermission(activity, permissions)
                    .ask(new PermissionListener() {
                        @Override
                        public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                            triggerGalleryIntent();
                            //  fragment.startActivityForResult(intent, LOAD_GALLERY);
                        }

                        @Override
                        public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                            showPermissionDeniedDialog();
                        }
                    });
        }

    }

    private void requestTakePhotoPermissions(Fragment fragment, FragmentActivity activity, String... permissions) {
        // Define the permission listener that will be used for both cases
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                // Permission granted, proceed with taking the photo
                performTakePhotoAction();
            }

            @Override
            public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                // Permission denied, show a dialog
                showPermissionDeniedDialog();
            }
        };

        // Check whether the request is coming from a Fragment or an Activity
        if (fragment != null) {
            // Request permission using EasyPermissions for Fragment
            askPermission(fragment, permissions).ask(permissionListener);
        } else if (activity != null) {
            // Request permission using EasyPermissions for Activity
            askPermission(activity, permissions).ask(permissionListener);
        }
    }


//    private void requestTakePhotoPermissions(Fragment fragment, FragmentActivity activity, String... permissions) {
//        if (fragment != null) {
//            askPermission(fragment, permissions)
//                    .ask(new PermissionListener() {
//                        @Override
//                        public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
//                            // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            performTakePhotoAction();
//                        }
//
//                        @Override
//                        public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
//                            showPermissionDeniedDialog();
//                        }
//                    });
//        } else if (activity != null) {
//            askPermission(activity, permissions)
//                    .ask(new PermissionListener() {
//                        @Override
//                        public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
//                            // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            performTakePhotoAction();
//                        }
//
//                        @Override
//                        public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
//                            showPermissionDeniedDialog();
//                        }
//                    });
//        }
//    }

}
