package me.kennydude.sphere;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created with IntelliJ IDEA.
 * User: kennydude
 * Date: 06/05/13
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: something better
		setContentView(R.layout.main);

		Button mButton = (Button) findViewById(R.id.remote);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, SphereViewer.class);
				i.setData(Uri.parse("http://kennydude.github.io/photosphere/stest.jpg"));
				startActivity(i);
			}
		});
	}
}