package com.zhqz.faces.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhqz.faces.R;
import com.zhqz.faces.data.model.FaceUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FaceUserAdapter extends RecyclerView.Adapter<FaceUserAdapter.ViewHolder> {


    private Context context;
    private List<FaceUser> faceUsers;
    private CallBack mCallback;

    public FaceUserAdapter(Context context, List<FaceUser> faceUser, CallBack cb) {
        this.context = context;
        this.faceUsers = faceUser;
        this.mCallback = cb;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.face_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FaceUser faceUser = faceUsers.get(position);
        holder.face_name.setText(faceUser.name);
        holder.face_number.setText(faceUser.number);
        holder.face_status.setText(faceUser.status + "");

        holder.setItem(faceUser);
    }

    @Override
    public int getItemCount() {
        return faceUsers != null ? faceUsers.size() : 0;
    }

    public void setFaceUserData(List<FaceUser> faces) {
        this.faceUsers = faces;
        notifyDataSetChanged();
    }

    public interface CallBack {
        void onFaceUserItemClicked(FaceUser faceUser);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.face_name)
        TextView face_name;

        @BindView(R.id.face_number)
        TextView face_number;

        @BindView(R.id.face_status)
        TextView face_status;


        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        FaceUser item;

        public void setItem(FaceUser item) {
            this.item = item;
        }

        @OnClick(R.id.item_LL)
        void onItemClicked() {
            if (mCallback != null && item != null) {
                mCallback.onFaceUserItemClicked(item);
            }

        }
    }
}
