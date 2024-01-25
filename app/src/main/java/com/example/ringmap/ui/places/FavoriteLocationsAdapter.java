package com.example.ringmap.ui.places;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ringmap.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoriteLocationsAdapter extends RecyclerView.Adapter<FavoriteLocationsAdapter.LocationViewHolder> {

    private List<FavoriteLocation> favoriteLocations;



    public FavoriteLocationsAdapter() {
        this.favoriteLocations = new ArrayList<>();
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setFavoriteLocations(List<FavoriteLocation> favoriteLocations) {
        this.favoriteLocations = favoriteLocations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        FavoriteLocation location = favoriteLocations.get(position);
        holder.bind(location);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(location, "open");
            }
        });
        holder.bt_delete.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(location, "delete");
            }
        });
        holder.bt_edit.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(location, "open");
            }
        });

        }

    @Override
    public int getItemCount() {
        return favoriteLocations.size();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {

        private TextView locationNameTextView;
        private TextView coordinatesTextView;
        private AppCompatButton bt_delete, bt_edit;
        LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.locationNameTextView);
            coordinatesTextView = itemView.findViewById(R.id.coordinatesTextView);
            bt_delete = itemView.findViewById(R.id.bt_delete);
            bt_edit = itemView.findViewById(R.id.bt_edit);
        }

        void bind(FavoriteLocation location) {

            locationNameTextView.setText(location.getLocationName());
            String coordinatesText = String.format(Locale.getDefault(),
                    "Lat: %f, Lon: %f, R: %d", location.getLocationPoint().getLatitude(), location.getLocationPoint().getLongitude(), location.getRadius());
            coordinatesTextView.setText(coordinatesText);

        }
    }
    public interface OnItemClickListener {
        void onItemClick(FavoriteLocation favoriteLocation, String action);
    }

}
