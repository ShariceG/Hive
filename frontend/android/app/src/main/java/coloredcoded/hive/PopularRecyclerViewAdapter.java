package coloredcoded.hive;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import coloredcoded.hive.client.Location;

public class PopularRecyclerViewAdapter
        extends RecyclerView.Adapter<PopularRecyclerViewAdapter.ViewHolder> {

    public interface Delegate {
        void onItemClick(View v);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            button = (Button) itemView;
        }
    }

    private ArrayList<Location> popularLocations;
    private Context context;
    private Delegate delegate;

    // data is passed into the constructor
    PopularRecyclerViewAdapter(Context c, ArrayList<Location> locations, Delegate d) {
        context = c;
        popularLocations = locations;
        delegate = d;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This doesn't have to be a button. It can be an inflated custom view.
        Button button = new Button(context);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.onItemClick(v);
            }
        });
        return new ViewHolder(button);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Location location = popularLocations.get(position);
        holder.button.setText(location.getArea().toString());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return popularLocations.size();
    }
}
