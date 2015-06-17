package com.jrmeza.spotstream.results;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jrmeza.spotstream.MainActivity;
import com.jrmeza.spotstream.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jrmeza on 6/13/15.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder> {
    LayoutInflater inflater;
    List<SearchResult> searchResults = Collections.emptyList();
    List<SearchViewHolder> searchViewHolders = new ArrayList<>();
    List<Bitmap> bitmaps = new ArrayList<>();
    Context context;
    public SearchResultAdapter(Context context, List<SearchResult> results){
        inflater = LayoutInflater.from( context );
        searchResults = results;
        this.context = context;
        for (int x = 0; x < searchResults.size(); x++){
            new ImageLoader().execute( searchResults.get( x ));
        }

    }
    @Override

    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate( R.layout.search_result, viewGroup , false);
        SearchViewHolder searchViewHolder = new SearchViewHolder( view );
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder searchViewHolder, int i) {
        final SearchResult result = searchResults.get(i);
        searchViewHolder.title.setText( result.title  );
        searchViewHolder.imageView.setImageBitmap( result.image );
        searchViewHolder.uri = result.URI;
        searchViewHolders.add( i , searchViewHolder );
    }


    @Override
    public int getItemCount() {
        return searchResults.size();
    }
    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView title;
        String uri;
        ImageButton playButton;
        public SearchViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.search_result_title);
            imageView = (ImageView) itemView.findViewById(R.id.result_image_view);
            playButton = (ImageButton) itemView.findViewById(R.id.playButton);
            playButton.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {
            MainActivity.mPlayer.play( uri );
        }
    }

    class ImageLoader extends AsyncTask<SearchResult, Void, Bitmap>{
        SearchResult result;
        @Override
        protected Bitmap doInBackground(SearchResult... sr) {
            result = sr[0];
            URL url = null;
            try {
                url = new URL( result.imageUrl );
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            try {
                 bitmap = BitmapFactory.decodeStream( url.openConnection().getInputStream());
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            result.image = bitmap;
            bitmaps.add( bitmap );
            for (int x = 0; x  <searchViewHolders.size(); x++){
                if( x < bitmaps.size() )searchViewHolders.get(x).imageView.setImageBitmap( searchResults.get(x).image);
            }
        }
    }

}
