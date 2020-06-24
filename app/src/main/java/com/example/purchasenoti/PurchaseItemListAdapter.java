package com.example.purchasenoti;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purchasenoti.databinding.PurchaseListItemBinding;
import com.example.purchasenoti.model.PurchaseItem;
import com.example.purchasenoti.utilities.DateUtils;

import java.util.ArrayList;
import java.util.Locale;

public class PurchaseItemListAdapter extends RecyclerView.Adapter<PurchaseItemListAdapter.PurchaseItemListAdapterViewHolder> {
    private static final String TAG = PurchaseItemListAdapter.class.getSimpleName();

    private final PurchaseItemOnClickHandler mOnClickHandler;

    public interface PurchaseItemOnClickHandler {
        void onItemClick(PurchaseItem purchaseItem);
        void onEdit(PurchaseItem purchaseItem);
        void onDelete(PurchaseItem purchaseItem);
    }

    private ArrayList<PurchaseItem> mPurchaseItemList = new ArrayList<>();

    public PurchaseItemListAdapter(PurchaseItemOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    public class PurchaseItemListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private PurchaseListItemBinding mBinding;

        public PurchaseItemListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PurchaseItem purchaseItem = mPurchaseItemList.get(getAdapterPosition());
            Log.d(TAG, "onClick(), clicked purchase item: " + purchaseItem.toString());

            mOnClickHandler.onItemClick(purchaseItem);
        }
    }

    @NonNull
    @Override
    public PurchaseItemListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.purchase_list_item, parent, false);
        return new PurchaseItemListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseItemListAdapterViewHolder holder, int position) {
        PurchaseItem item = mPurchaseItemList.get(position);
        Log.d(TAG, "onBindViewHolder() item: " + item.toString());

        holder.mBinding.tvPurchaseItemName.setText(item.getItemName());

        String nextDate = DateUtils.getNextDate(item.getLastPurchasedDate(),
                item.getPurchaseTermYear(), item.getPurchaseTermMonth(), item.getPurchaseTermDay());
        holder.mBinding.tvNextPurchaseDate.setText(String.format(Locale.getDefault(), "%s  %s",
                nextDate, DateUtils.getDDayString(nextDate)));

        holder.mBinding.ivEdit.setOnClickListener(v -> mOnClickHandler.onEdit(item));
        holder.mBinding.ivDelete.setOnClickListener(v -> mOnClickHandler.onDelete(item));
    }

    @Override
    public int getItemCount() {
        if (mPurchaseItemList.isEmpty()) {
            return 0;
        }
        return mPurchaseItemList.size();
    }

    public void setPurchaseItemList(ArrayList<PurchaseItem> purchaseItemList) {
        mPurchaseItemList.clear();
        mPurchaseItemList.addAll(purchaseItemList);

        notifyDataSetChanged();
    }
}
