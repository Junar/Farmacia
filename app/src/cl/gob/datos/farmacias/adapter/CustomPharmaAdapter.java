package cl.gob.datos.farmacias.adapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.fragment.PharmaDetailActivity;
import cl.gob.datos.farmacias.helpers.Utils;

import com.google.android.gms.maps.model.LatLng;
import com.junar.searchpharma.Pharmacy;

public class CustomPharmaAdapter extends ArrayAdapter<Pharmacy> implements
        Filterable {

    private Context context;
    private int layoutResourceId;
    private List<Pharmacy> filteredList;
    private List<Pharmacy> originalList;
    private Filter filter = new CustomFilter();
    private LatLng currentLocation;

    public CustomPharmaAdapter(Context context, int layoutResourceId,
            List<Pharmacy> data, LatLng location) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        originalList = data;
        filteredList = data;
        currentLocation = location;
    }

    private class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            final List<Pharmacy> list = originalList;
            int count = list.size();
            final ArrayList<Pharmacy> dataFiltered = new ArrayList<Pharmacy>(
                    count);

            constraint = constraint.toString().toLowerCase();
            for (Pharmacy phar : list) {
                if (phar.toString().toLowerCase().contains(constraint)) {
                    dataFiltered.add(phar);
                }
            }

            results.values = dataFiltered;
            results.count = dataFiltered.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                FilterResults results) {
            filteredList = (ArrayList<Pharmacy>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PharmaHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PharmaHolder();
            holder.detailPharmacy = (LinearLayout) row
                    .findViewById(R.id.detailPharmacy);
            holder.txtTitle = (TextView) row.findViewById(R.id.pharma_name);
            holder.txtAddress = (TextView) row
                    .findViewById(R.id.pharmacy_address);
            holder.txtSchedule = (TextView) row
                    .findViewById(R.id.pharmacy_schedule);

            holder.btnRoute = (Button) row.findViewById(R.id.btn_pharmacy_ruta);
            holder.btnShare = (Button) row
                    .findViewById(R.id.btn_pharmacy_share);
            row.setTag(holder);
        } else {
            holder = (PharmaHolder) row.getTag();
        }
        final Pharmacy pharma = filteredList.get(position);
        holder.txtTitle.setText(pharma.getName());
        holder.txtAddress.setText(pharma.getAddress());
        holder.txtSchedule.setText(pharma.getSchedule());

        if (pharma.getLatitude() != 0 && pharma.getLongitude() != 0
                && currentLocation != null) {
            holder.btnRoute.setVisibility(View.VISIBLE);
            float[] results = new float[1];
            Location.distanceBetween(currentLocation.latitude,
                    currentLocation.longitude, pharma.getLatitude(),
                    pharma.getLongitude(), results);

            String distanceStr = (long) results[0] + " Mts";
            if (results[0] > 1000) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                distanceStr = nf.format(results[0] / 1000) + " Kms";
            }

            holder.btnRoute.setText(distanceStr);
        } else {
            holder.btnRoute.setVisibility(View.GONE);
        }

        holder.btnRoute.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = "http://maps.google.com/maps?daddr="
                        + pharma.getLatitude() + "," + pharma.getLongitude();
                context.startActivity(new Intent("android.intent.action.VIEW",
                        Uri.parse(str)));
            }
        });

        holder.btnShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                context.startActivity(Utils.createShareIntent(context, pharma));
            }
        });

        holder.detailPharmacy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent pharmaDetail = new Intent(context,
                        PharmaDetailActivity.class);
                pharmaDetail.putExtra("id", pharma.getId());
                context.startActivity(pharmaDetail);
            }
        });

        return row;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Pharmacy getItem(int position) {
        return filteredList.get(position);
    }

    static class PharmaHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtAddress;
        TextView txtSchedule;
        Button btnRoute;
        Button btnShare;
        Button btnMapMode;
        LinearLayout detailPharmacy;
    }
}
