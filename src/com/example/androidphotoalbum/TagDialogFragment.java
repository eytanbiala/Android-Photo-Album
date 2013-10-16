package com.example.androidphotoalbum;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author Eytan Biala
 */
public class TagDialogFragment extends DialogFragment {

	private Spinner type;
	private EditText value;
	private int mode;
	private String album;
	private String photo;
	private Controller ctrl;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		album = getArguments().getString(AppConstants.ALBUM_TITLE);
		photo = getArguments().getString(AppConstants.PHOTO_FILENAME);
		mode = getArguments().getInt(AppConstants.MODE);
		ctrl = Controller.getInstance(getActivity().getApplicationContext());
		int titleId = 0;
		int buttonId = 0;

		switch (mode) {
			case 1 : // new tag
				buttonId = R.string.ok;
				titleId = R.string.add_tag;
				break;
			case 2 : // edit tag
				buttonId = R.string.save;
				titleId = R.string.edit_tag;
				break;
		}

		List<String> tagTypes = new ArrayList<String>();
		tagTypes.add("Person");
		tagTypes.add("Location");

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.tag_dialog, null);

		type = (Spinner) v.findViewById(R.id.spinner1);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tagTypes);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type.setAdapter(dataAdapter);
		value = (EditText) v.findViewById(R.id.editText1);
		value.setHint("");
		value.requestFocus();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(titleId).setView(v)
				.setPositiveButton(buttonId, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						String val = value.getText().toString().trim();
						if (val.isEmpty()) {
							Toast.makeText(getActivity(), "Tag cannot be empty.", Toast.LENGTH_SHORT)
									.show();

						} else {
							switch (mode) {
								case 1 :
									// new
									String album = TagDialogFragment.this.album;
									String filename = TagDialogFragment.this.photo;
									String tagType = TagDialogFragment.this.type.getSelectedItem()
											.toString();
									String tagValue = TagDialogFragment.this.value.getText()
											.toString();
									if (ctrl.addTagToPhoto(album, filename, tagType, tagValue)) {
										TagDialogFragment.this.getDialog().cancel();
										Toast.makeText(getActivity().getApplicationContext(), "Added "
												+ tagType + ": " + tagValue + "", Toast.LENGTH_SHORT)
												.show();
									} else {
										Toast.makeText(getActivity().getApplicationContext(), "Could not add tag.", Toast.LENGTH_SHORT)
												.show();
									}
									break;
								case 2 :
									// rename
									if (ctrl.renameAlbum(TagDialogFragment.this.album, val)) {
										Toast.makeText(getActivity().getApplicationContext(), "Renamed "
												+ TagDialogFragment.this.album + " to " + val, Toast.LENGTH_SHORT)
												.show();
										TagDialogFragment.this.getDialog().cancel();
										Context ctx = getActivity().getApplicationContext();
										Intent i = new Intent(ctx, PhotoGridActivity.class);
										i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										i.putExtra(AppConstants.ALBUM_TITLE, val);
										getActivity().finish();
										ctx.startActivity(i);
									}
									break;
							}
						}
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						TagDialogFragment.this.getDialog().cancel();
					}
				});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
