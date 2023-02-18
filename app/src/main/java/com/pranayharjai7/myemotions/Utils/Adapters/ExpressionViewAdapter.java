package com.pranayharjai7.myemotions.Utils.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pranayharjai7.myemotions.Database.LocalDatabase.Expression;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.databinding.ExpressionCardLayoutBinding;

import java.util.List;

public class ExpressionViewAdapter extends RecyclerView.Adapter<ExpressionViewAdapter.ExpressionViewHolder> {

    private List<Expression> expressions;

    public ExpressionViewAdapter(List<Expression> expressions) {
        this.expressions = expressions;
    }

    @NonNull
    @Override
    public ExpressionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expression_card_layout,parent,false);
        return new ExpressionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpressionViewHolder holder, int position) {
        holder.binding.dateTextView.setText(expressions.get(position).getDateTime());
        holder.binding.expressionsTextView.setText(expressions.get(position).getExpression());
    }

    @Override
    public int getItemCount() {
        return expressions.size();
    }

    public class ExpressionViewHolder extends RecyclerView.ViewHolder{
        ExpressionCardLayoutBinding binding;

        public ExpressionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ExpressionCardLayoutBinding.bind(itemView);
        }
    }
}
