package ir.fanap.samplesdk.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.fanap.samplesdk.R;
import ir.fanap.sdk.accountbill.AccountBillData;
import saman.zamani.persiandate.PersianDate;

public class AccountBillAdapter extends RecyclerView.Adapter<AccountBillAdapter.ViewHolder> {

    private List<AccountBillData.AccountBill> list;
    private LayoutInflater mInflater;

    public AccountBillAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void addList(List<AccountBillData.AccountBill> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rcv_account_bill_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountBillData.AccountBill accountBill = list.get(position);

        holder.lblDocumentNumber.setText("شماره سند: " + accountBill.getDocumentId());
        holder.lblDescription.setText(accountBill.getDescription());
        PersianDate persianDate = new PersianDate(accountBill.getIssuanceDate());
        holder.lblDateTime.setText(persianDate.getShYear() + "/" + persianDate.getShMonth() + "/" + persianDate.getShDay() + " " + persianDate.getHour() + ":" + persianDate.getMinute() + ":" + persianDate.getSecond());
        if (accountBill.getDebtor()) {
            holder.lblPrice.setText(String.format("%,d", accountBill.getAmount(), " " + accountBill.getCurrency().getName()));
            holder.lblPrice.setTextColor(Color.GREEN);
        } else {
            holder.lblPrice.setText(String.format("%,d", accountBill.getAmount() * -1, " " + accountBill.getCurrency().getName()));
            holder.lblPrice.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblDocumentNumber;
        TextView lblDateTime;
        TextView lblPrice;
        TextView lblDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblDocumentNumber = itemView.findViewById(R.id.lblDocumentNumber);
            lblDateTime = itemView.findViewById(R.id.lblDateTime);
            lblPrice = itemView.findViewById(R.id.lblPrice);
            lblDescription = itemView.findViewById(R.id.lblDescription);
        }

    }
}
