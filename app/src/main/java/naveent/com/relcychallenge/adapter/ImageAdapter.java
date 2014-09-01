package naveent.com.relcychallenge.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

import naveent.com.relcychallenge.MainActivity;
import naveent.com.relcychallenge.R;

/**
 * Created by NaveenT on 8/28/14.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<String> urls;

    // Constructor
    public ImageAdapter(Context c, List<String> urls){
        mContext = c;
        inflater = LayoutInflater.from(c);
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.image_grid_item, parent, false);
            holder = new ImageHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ImageHolder) convertView.getTag();
        }
        String url = urls.get(position);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
        if (holder.imageView != null) {
            // Get and set profile picture
            if (!url.equals("")) {
                System.out.println(url);
                MainActivity.imageLoader.displayImage(url, holder.imageView, options);
            }
        }
        return convertView;
    }

    //View Holder
    class ImageHolder {
        public ImageView imageView;

        public ImageHolder(View convertView){
            // UI references
            imageView = (ImageView) convertView.findViewById(R.id.googleImage);
        }

    }
}