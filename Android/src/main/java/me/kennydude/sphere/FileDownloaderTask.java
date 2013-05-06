package me.kennydude.sphere;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: kennydude
 * Date: 06/05/13
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public class FileDownloaderTask {

	ProgressDialog mDialog;
	Context mContext;
	TaskDoneCallback mCallback;

	public FileDownloaderTask(Context context, final String downloadUrl, TaskDoneCallback cback){
		mDialog = new ProgressDialog(context);
		mDialog.setMessage( context.getString(R.string.please_wait) );
		mDialog.setIndeterminate(false);
		mDialog.setMax(100);
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.show();

		mContext = context;
		mCallback = cback;

		try{
			new DownloadTask().execute( new URL(downloadUrl) );
		} catch (Exception e){
			e.printStackTrace();
			mCallback.onTaskFailure();
		}
	}

	public interface TaskDoneCallback{
		public void onTaskSuccessful();
		public void onTaskFailure();
	}

	public class DownloadTask extends AsyncTask<URL, Integer, Integer>{

		@Override
		protected Integer doInBackground(URL... urls) {
			// Now download the file to cache
			try{
				URL url = urls[0];
				URLConnection connection = url.openConnection();

				int fileLength = connection.getContentLength();

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream( new File( mContext.getCacheDir(), "temp") );

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				return 0;
			} catch (Exception e){
				e.printStackTrace();
				return -1;
			}
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			mDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			mDialog.hide();
			if(result == -1){
				mCallback.onTaskFailure();
			} else{
				mCallback.onTaskSuccessful();
			}
		}
	}

}
