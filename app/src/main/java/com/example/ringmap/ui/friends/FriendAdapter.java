package com.example.ringmap.ui.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ringmap.R;
import com.example.ringmap.ui.places.FavoriteLocation;
import com.example.ringmap.ui.places.FavoriteLocationsAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendAdapterHold> {

    private List<String> friendList;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FriendAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(FriendAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public FriendAdapter() {
        this.friendList = new ArrayList<>();
    }

    public void setFriendAdapters(List<String> friendList) {
        this.friendList = friendList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendAdapterHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friends, parent, false);
        return new FriendAdapterHold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapterHold holder, int position) {
        String user_id = friendList.get(position);
        holder.bind(user_id);
        holder.bt_delete.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(user_id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    static class FriendAdapterHold extends RecyclerView.ViewHolder {

        private TextView userIdTextView;
        private AppCompatButton bt_delete;
        FriendAdapterHold(@NonNull View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.friendID);
            bt_delete = itemView.findViewById(R.id.bt_delete_friend);

        }

        void bind(String userID) {
            DocumentReference documentReference = db.collection("usuarios").document(userID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if(documentSnapshot != null) {
                        userIdTextView.setText(documentSnapshot.getString("nome"));
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(String userId);
    }
}
