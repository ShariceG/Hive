package coloredcoded.hive;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import coloredcoded.hive.client.HiveLocation;

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

    private ArrayList<HiveLocation> popularHiveLocations;
    private Context context;
    private Delegate delegate;

    // data is passed into the constructor
    PopularRecyclerViewAdapter(Context c, ArrayList<HiveLocation> hiveLocations, Delegate d) {
        context = c;
        popularHiveLocations = hiveLocations;
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
        HiveLocation hiveLocation = popularHiveLocations.get(position);
        holder.button.setText(hiveLocation.getArea().toString());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return popularHiveLocations.size();
    }
}
