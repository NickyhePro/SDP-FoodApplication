package com.nickhe.reciperescue;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaExtractor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileFragment extends Fragment {

    User user;
    ImageView profileImageView;
    ListView listView;
    FakeRecipeRepository fakeRecipeRepository;
    public final int READ_IMAGE_PERMISSION = 0;
    public final int PICK_IMAGE_RESULT = 1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public void loadUserInfo()
    {

    }

    public void initializeView(User user)
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_IMAGE_PERMISSION);
        }

        profileImageView = view.findViewById(R.id.profileImageView);
        listView = view.findViewById(R.id.profile_recipeList);
        fakeRecipeRepository = FakeRecipeRepository.getFakeRecipeRepository(getActivity());
        RecipeListAdapter recipeListAdapter = new RecipeListAdapter(getActivity(), fakeRecipeRepository.getFakeRepo());
        listView.setAdapter(recipeListAdapter);
        ListViewProcessor.setListViewHeightBasedOnChildren(listView);

        //Set clickListener to allow users to select image from their phone as the profile image
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent , 1);
            }
        });

        //To allow users to be able to open a recipe and review that
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Recipe recipe = fakeRecipeRepository.getFakeRepo().get(position);

                startRecipeViewActivity(recipe);
            }
        });

         TextView newRecipeButton = view.findViewById(R.id.newRecipeButton);
         newRecipeButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getActivity(), CreateRecipeActivity.class));
             }
         });

    }

    /**
     *
     * @param recipe
     */
    private void startRecipeViewActivity(Recipe recipe)
    {
        Intent i = new Intent(getActivity().getBaseContext(), RecipeViewActivity.class);
        i.putExtra("recipe", recipe);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case READ_IMAGE_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getActivity(), "Permission granted!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode)
        {
            case PICK_IMAGE_RESULT:
                if(resultCode == Activity.RESULT_OK)
                {
                    Uri selectedImg = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver()
                            .query(selectedImg, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    profileImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                }

        }
    }

}
