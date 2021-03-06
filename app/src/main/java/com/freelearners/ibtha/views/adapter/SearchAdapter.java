package com.freelearners.ibtha.views.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.freelearners.ibtha.R;
import com.freelearners.ibtha.model.ProductModel;
import com.freelearners.ibtha.database.remote.server.Constants;
import com.freelearners.ibtha.views.ui.ProductActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;

    ArrayList<ProductModel> products;
    ArrayList<ProductModel> productsBackup;
    Context context;
    private static final String TAG = "ProductAdapter";

    public SearchAdapter(ArrayList<ProductModel> products, Context context) {
        this.products = products;
        this.context = context;
        this.productsBackup = products;
    }

    public void setProducts(ArrayList<ProductModel> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filter ;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence keyword) {

            ArrayList<ProductModel> filteredData = new ArrayList<>();
            if (keyword.toString().isEmpty()){
                filteredData.addAll(productsBackup);
            }else {
                for (ProductModel obj : products){
                    if (obj.getName().toString().toLowerCase().contains(keyword.toString().toLowerCase().trim()))
                        filteredData.add(obj);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            products.clear();
            products.addAll((ArrayList)results.values);
            Log.d(TAG, "publishResults: "+ results.values.toString());
            notifyDataSetChanged();
        }
    };

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.search_items, parent, false);
        if (viewType == TYPE_ITEM) {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_items, parent, false);
            return new SearchAdapter.ItemViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footer, parent, false);
            return new SearchAdapter.FooterViewHolder(itemView);
        } else
            return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchAdapter.FooterViewHolder) {
            SearchAdapter.FooterViewHolder footerHolder = (SearchAdapter.FooterViewHolder) holder;
//            footerHolder.footerText.setText("Footer View");
        } else if (holder instanceof SearchAdapter.ItemViewHolder) {
            SearchAdapter.ItemViewHolder itemViewHolder = (SearchAdapter.ItemViewHolder) holder;

            itemViewHolder.title.setText(products.get(position).getName());
            itemViewHolder.unit.setText(R.string.unit_gm);
            itemViewHolder.price.setText(Integer.toString(products.get(position).getPrice()));

            if (products.get(position).getProductPictures().get(0).getData() != null) {
                String url;
                url = products.get(position).getProductPictures().get(0).getData();
                Log.d(TAG, "onBindViewHolder: " + url);
                byte[] decodedString = Base64.decode(url, Base64.DEFAULT);
                Glide.with(context)
                        .asBitmap()
                        .load(decodedString)
                        .centerCrop()
                        .into(itemViewHolder.img);
            }

            itemViewHolder.searchCard.setOnClickListener(v -> {
                Log.d(TAG, "onBindViewHolder: " + "Item clicked " + products.get(position).toString());
                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("product", products.get(position));
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (this.products != null) {
            return products.size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == products.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView footerText;

        public FooterViewHolder(View view) {
            super(view);
            footerText = (TextView) view.findViewById(R.id.footer_text);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView searchCard;
        public ImageView img;
        public TextView title;
        public TextView unit;
        public TextView price;

        public ItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            searchCard = itemView.findViewById(R.id.search_card_view);
            title = itemView.findViewById(R.id.product_name_search);
            img = itemView.findViewById(R.id.product_img_search);
            unit = itemView.findViewById(R.id.product_quantity);
            price = itemView.findViewById(R.id.product_price_search);
        }
    }
}
