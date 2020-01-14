package edu.neu.cs5520.liferecorder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class AddPlaceDialogFragment extends DialogFragment{

    private Location location;

    public AddPlaceDialogFragment(Location location){
        this.location = location;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));


        builder.setTitle("Add places from")
                .setItems(new String[]{"","search"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){

                        }else{

                        }
                    }
                });
        return builder.create();
    }
}

