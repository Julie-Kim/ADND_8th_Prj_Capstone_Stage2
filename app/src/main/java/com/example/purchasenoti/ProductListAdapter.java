package com.example.purchasenoti;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purchasenoti.databinding.ProductListItemBinding;
import com.example.purchasenoti.model.Product;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListAdapterViewHolder> {
    private static final String TAG = ProductListAdapter.class.getSimpleName();

    private final ProductOnClickHandler mOnClickHandler;

    public interface ProductOnClickHandler {
        void onClick(Product product);
    }

    private ArrayList<Product> mProductList = new ArrayList<>();

    public ProductListAdapter(ProductOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    public class ProductListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProductListItemBinding mBinding;

        public ProductListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Product product = mProductList.get(getAdapterPosition());
            Log.d(TAG, "onClick(), clicked product: " + product.toString());    //TODO

            mOnClickHandler.onClick(product);
        }

        void showOrHideLoadingIndicator(boolean show) {
            mBinding.pbImageLoadingIndicator.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @NonNull
    @Override
    public ProductListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_list_item, parent, false);
        return new ProductListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapterViewHolder holder, int position) {
        Product product = mProductList.get(position);
        Log.d(TAG, "onBindViewHolder() product: " + product.toString());

        String productTitle = product.getTitle();
        holder.mBinding.tvProductName.setText(productTitle);
        holder.mBinding.tvPrice.setText(product.getPrice());

        holder.showOrHideLoadingIndicator(true);
        Picasso.get()
                .load(product.getImage())
                .noPlaceholder()
                .error(R.drawable.error_image)
                .into(holder.mBinding.ivProductImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.showOrHideLoadingIndicator(false);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.showOrHideLoadingIndicator(false);
                            }
                        }
                );

        holder.mBinding.ivProductImage.setContentDescription(productTitle);

        setRatingStarView(holder, Math.round(product.getRating()));
    }

    private void setRatingStarView(@NonNull ProductListAdapterViewHolder holder, int rating) {
        AppExecutors.getInstance().mainThread().execute(() -> {
            holder.mBinding.ivRatingStar1.setImageResource(1 <= rating ?
                    R.drawable.ic_star_red_32dp : R.drawable.ic_star_border_red_32dp);
            holder.mBinding.ivRatingStar2.setImageResource(2 <= rating ?
                    R.drawable.ic_star_red_32dp : R.drawable.ic_star_border_red_32dp);
            holder.mBinding.ivRatingStar3.setImageResource(3 <= rating ?
                    R.drawable.ic_star_red_32dp : R.drawable.ic_star_border_red_32dp);
            holder.mBinding.ivRatingStar4.setImageResource(4 <= rating ?
                    R.drawable.ic_star_red_32dp : R.drawable.ic_star_border_red_32dp);
            holder.mBinding.ivRatingStar5.setImageResource(5 <= rating ?
                    R.drawable.ic_star_red_32dp : R.drawable.ic_star_border_red_32dp);
        });
    }

    @Override
    public int getItemCount() {
        if (mProductList.isEmpty()) {
            return 0;
        }
        return mProductList.size();
    }

    public void setProductList(ArrayList<Product> productList) {
        mProductList.clear();
        mProductList.addAll(productList);

        notifyDataSetChanged();
    }
}
