package com.mungwaagu.quizapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mungwaagu.quizapp.main.QuizActivity;
import com.mungwaagu.quizapp.R;
import com.mungwaagu.quizapp.models.CategoryModel;

import java.util.List;
import java.util.Random;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    Context context;
    List<CategoryModel> categoryModelList;


    //For Random Category Background Colors
    public String[] random_bg_colors = {
            "EF5350", "F44336", "E53935",        //reds
            "AB47BC", "9C27B0", "8E24AA",        //purples
            "7E57C2", "673AB7", "5E35B1",        //deep purples
            "5C6BC0", "3F51B5", "3949AB",        //indigo
            "42A5F5", "2196F3", "1E88E5",        //blue
            "FFA726", "FF9800", "FB8C00",        //orange
            "FF7043", "FF5722", "F4511E",        //deep orange
            "8D6E63", "795548", "6D4C41",        //brown
    };

    public CategoryAdapter(Context context, List<CategoryModel> categoryModelList){
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.top_layout.setBackgroundColor(Color.parseColor("#"+ random_bg_colors[new Random().nextInt(20)]));
        holder.category_name.setText(categoryModelList.get(position).getCategory_name());
        holder.category_questions_total.setText(categoryModelList.get(position).getCategory_total_questions());

        holder.category_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                Intent intent = new Intent(context, QuizActivity.class);
                intent.putExtra("quizCategory", categoryModelList.get(position).getCategory_name());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        public TextView category_name, category_questions_total;
        public CardView category_card;
        RelativeLayout top_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            category_name = (TextView) itemView.findViewById(R.id.category_name);
            category_questions_total = (TextView) itemView.findViewById(R.id.total_questions);
            category_card = (CardView) itemView.findViewById(R.id.category_card);
            top_layout = itemView.findViewById(R.id.top_layout);

        }
    }

}
